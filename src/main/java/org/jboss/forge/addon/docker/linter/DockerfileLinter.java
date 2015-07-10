package org.jboss.forge.addon.docker.linter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.forge.addon.docker.resource.DockerFileResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.URLResource;
import org.yaml.snakeyaml.Yaml;

public class DockerfileLinter
{
   private ResourceFactory resourceFactory;

   private Resource<?> baseRuleFile = null;

   public DockerfileLinter(ResourceFactory resourceFactory)
   {
      this.resourceFactory = resourceFactory;
   }

   /**
    * Return the {@link DockerfileLintResult} ,lint the underlying Dockerfile against a set of preset base rules.
    * 
    * @param dockerfile The {@link DockerFileResource} to be linted.
    * @return The result of validation containing errors, warnings and info.
    */
   public DockerfileLintResult lint(DockerFileResource dockerfile)
   {
      return lint(dockerfile, null);
   }

   /**
    * Return the {@link DockerfileLintResult} ,lint the underlying Dockerfile against the given rule file.
    * 
    * @param dockerfile The {@link DockerFileResource} to be linted.
    * @param ruleFile The {@link Resource} which is the abstraction for YAML rule file used to lint against.
    * @return The result of validation containing errors, warnings and info.
    */
   public DockerfileLintResult lint(DockerFileResource dockerfile, Resource<?> ruleFile)

   {
      if (baseRuleFile == null)
      {
         baseRuleFile = getBaseRules();
      }

      InputStream bis = baseRuleFile.getResourceInputStream();
      Yaml baseYaml = new Yaml();
      @SuppressWarnings("unchecked")
      Map<String, Object> baseRuleFileObject = (Map<String, Object>) baseYaml.load(bis);
      Map<String, Object> object = baseRuleFileObject;

      // Load the rule file YAML object.
      if (ruleFile != null)
      {
         InputStream is = ruleFile.getResourceInputStream();
         Yaml yaml = new Yaml();
         @SuppressWarnings("unchecked")
         Map<String, Object> ruleFileObject = (Map<String, Object>) yaml.load(is);
         try
         {
            is.close();
         }
         catch (IOException e)
         {
         }
         object = getCombinedRules(ruleFileObject, baseRuleFileObject);
      }

      // Make global
      // @SuppressWarnings("unchecked")
      // Map<String, String> profile = (Map<String, String>) object.get("profile");

      // Map representing the "line_rules" section of the rule file.
      @SuppressWarnings("unchecked")
      Map<String, Object> linerules = (Map<String, Object>) object.get("line_rules");

      // Map representing the "required_instructions" section of the rule file.
      @SuppressWarnings("unchecked")
      List<Object> reqinst = (List<Object>) object.get("required_instructions");

      // Map representing the "general" section of the rule file.
      @SuppressWarnings("unchecked")
      Map<String, Object> general = (Map<String, Object>) object.get("general");

      String multiLineRegex = (String) general.get("multiline_regex");
      String instructionRegex = (String) general.get("instruction_regex");
      String ignoreRegex = (String) general.get("ignore_regex");
      String validInstructionRegex = (String) general.get("valid_instruction_regex");

      Map<String, Integer> requiredInstructions = setupReqInstExists(reqinst);

      List<String> lines;
      String df = dockerfile.getContents().trim();
      String[] l = df.split("\\r?\\n");
      lines = Arrays.asList(l);

      Iterator<String> i = lines.iterator();
      boolean fromCheck = false;
      int currentLineIndex = 0;

      DockerfileLintResult dockerfileLintResult = new DockerfileLintResult();

      while (i.hasNext())
      {

         String currentLine = i.next();
         currentLineIndex++;

         // ignore blank lines.
         if (currentLine == null || currentLine.length() == 0)
         {
            continue;
         }

         // ignore comments.
         if (currentLine.charAt(0) == '#')
         {
            continue;
         }
         // ignore if "ignoreRegex" matches the current line.
         if (ignoreRegex != null)
         {
            if (currentLine.matches(ignoreRegex))
               continue;
         }

         // join multiple lines into one using the "multiLineRegex" match on the current line.
         if (multiLineRegex != null)
         {
            while (currentLine.matches(multiLineRegex))
            {
               currentLine = currentLine.replaceFirst(multiLineRegex, " ");
               currentLine += i.next();
               i.remove();
               currentLineIndex++;
            }
         }

         // Check if the presence the required "FROM" instruction has been checked.
         if (!fromCheck)
         {
            fromCheck = true;

            if (currentLine.toUpperCase().indexOf("FROM") != 0)
            {
               dockerfileLintResult
                        .addError("FROM statement missing or misplaced", currentLine, currentLineIndex);
            }

         }

         // Add error if the current line does not contain the "instructionRegex" defined in the rule file.
         if (instructionRegex != null)
         {
            if (!currentLine.matches(instructionRegex))
            {
               dockerfileLintResult.addError("Not a Instruction", currentLine, currentLineIndex);
               continue;
            }
         }

         // Add error if the current line does not contain a Valid Instruction.
         if (validInstructionRegex != null)
         {
            if (!Pattern.compile(validInstructionRegex).matcher(currentLine).find())
            {
               dockerfileLintResult.addError("Not a Valid Instruction", currentLine, currentLineIndex);
               continue;
            }
         }

         String instruction = null;

         Pattern pattern = Pattern.compile(validInstructionRegex);
         Matcher matcher = pattern.matcher(currentLine);

         if (matcher.find())
         {
            instruction = matcher.group(1);
         }

         if (instruction == null)
         {
            continue;
         }

         // Reduce the number of occurrences required of the current instruction by one because one has been just found.
         if (requiredInstructions.get(instruction) != null)
         {
            requiredInstructions.put(instruction, requiredInstructions.get(instruction) - 1);
         }

         @SuppressWarnings("unchecked")
         List<Object> instructionRules = (List<Object>) ((Map<String, Object>) linerules.get(instruction)).get("rules");

         // check the line rules for the instruction found.
         checkLineRules(instruction, currentLine, instructionRules, dockerfileLintResult, currentLineIndex);

         @SuppressWarnings("unchecked")
         String parameterSyntaxRegex = (String) ((Map<String, String>) linerules.get(instruction))
                  .get("paramSyntaxRegex");

         // Extract the command parameters.
         String parameters = currentLine.substring(matcher.end());

         // check the parameter rules for the instruction found by matching the "paramSyntaxRegex" to the parameter.
         if (parameterSyntaxRegex != null)
         {
            if (!parameters.matches(parameterSyntaxRegex))
            {
               dockerfileLintResult.addError("Bad Parameters", currentLine, currentLineIndex);
               continue;
            }
         }
      }

      // Check whether all instructions defined in the "required_instructions" section of the rule file exist.
      checkRequiredInstructions(requiredInstructions, dockerfileLintResult, reqinst);

      return dockerfileLintResult;
   }

   /**
    * Return the {@link Map} ,representing the Dockerfile Command name as the key and the number of command occurrences
    * required by the lint file as values.
    * 
    * @param requiredInstructions The {@link List},representing "required_instructions" section of the YAML rule file.
    * @return Map result of parsing the "required_instructions" section of the YAML rule file as Dockerfile Command name
    *         as the key and the number of command occurrences required by the lint file as values.
    */
   private Map<String, Integer> setupReqInstExists(List<Object> requiredInstructions)
   {

      Map<String, Integer> reqInst = new LinkedHashMap<>();
      for (Object o : requiredInstructions)
      {
         @SuppressWarnings("unchecked")
         Map<String, Object> map = ((Map<String, Object>) o);
         reqInst.put((String) map.get("instruction"), (Integer) map.get("count"));
      }

      return reqInst;
   }

   /**
    * Validate and add messages defined in YAML rule file to the {@link DockerfileLintResult} based on the problem found
    * in the linting process. Checks that all the required instructions exist in the Dockerfile.
    * 
    * @param dockerfileLintResult The {@link DockerfileLintResult} representing the Lint result being constructed.
    * @param reqinst The {@link List} representing the "required_instructions" section of rule file.
    * @param requiredInstructions, representing the Dockerfile Command name as the key and the number of command
    *           occurrences required by the lint file as values.
    */
   private void checkRequiredInstructions(Map<String, Integer> requiredInstructions,
            DockerfileLintResult dockerfileLintResult,
            List<Object> reqinst)
   {
      for (Object o : reqinst)
      {
         @SuppressWarnings("unchecked")
         Map<String, Object> map = ((Map<String, Object>) o);

         String str = map.get("instruction").toString();

         if (requiredInstructions.get(str) != 0)
         {

            StringBuilder sb = new StringBuilder();
            sb.append(map.get("message")).append(".");
            sb.append(map.get("description")).append(".");
            @SuppressWarnings("unchecked")
            List<String> referenceUrl = (List<String>) map.get("reference_url");
            sb.append("\nReference --> \n");
            for (String reference : referenceUrl)
            {
               sb.append(reference);
            }

            addLintResult(dockerfileLintResult, (String) map.get("level"), sb.toString());
         }

      }

   }

   /**
    * Validate and add messages defined in YAML rule file to the {@link DockerfileLintResult} based on the problem found
    * in the linting process for individual Dockerfile instruction.
    * 
    * @param dockerfileLintResult The {@link DockerfileLintResult} representing the Lint result being constructed.
    * @param instructionRules The {@link List} representing the "rules" section of the YAML file.
    * @param instruction The {@link String} representing the current Dockerfile instruction.
    * @param currentLine The {@link String} representing the current Dockerfile line.
    * @param currentLineIndex The {@link Integer} representing the index of the current Dockerfile line.
    */
   private void checkLineRules(String instruction, String currentLine, List<Object> instructionRules,
            DockerfileLintResult dockerfileLintResult,
            int currentLineIndex)
   {

      if (instructionRules != null)
      {

         for (Object o : instructionRules)
         {
            @SuppressWarnings("unchecked")
            Map<String, String> rule = (Map<String, String>) o;

            boolean invr = false;
            if (rule.containsKey("inverse_rule"))
            {
               invr = Boolean.valueOf(rule.get("inverse_rule"));
            }

            if (rule.containsKey("regex"))
            {
               if (currentLine.matches(rule.get("regex")) && !invr)
               {
                  addLintResult(dockerfileLintResult, rule.get("level"), rule.get("message"), currentLine,
                           currentLineIndex);
               }

               if (!currentLine.matches(rule.get("regex")) && invr)
               {
                  addLintResult(dockerfileLintResult, rule.get("level"), rule.get("message"), currentLine,
                           currentLineIndex);
               }

            }

         }
      }

   }

   /**
    * Return the {@link Map} ,which represents the merged rules of the rule file and the based rule file.
    * 
    * @param ruleFileObject The {@link Map} representing the rules in YAML rule file used to lint against.
    * @param baseFileObject The {@link Map} representing the rules in YAML base rule file used to lint against.
    * @return Map containing rule names as keys and the corresponding regexes as values.
    */
   @SuppressWarnings("unchecked")
   private Map<String, Object> getCombinedRules(Map<String, Object> ruleFileObject,
            Map<String, Object> baseRuleFileObject)
   {

      if (ruleFileObject.get("general") == null)
         ruleFileObject.put("general", baseRuleFileObject.get("general"));

      if (ruleFileObject.get("line_rules") == null)
         ruleFileObject.put("line_rules", baseRuleFileObject.get("line_rules"));

      if (ruleFileObject.get("required_instructions") == null)
         ruleFileObject.put("required_instructions", baseRuleFileObject.get("required_instructions"));

      Map<String, Object> general = (Map<String, Object>) ruleFileObject.get("general");

      if (general.get("multiline_regex") == null)
         general.put("multiline_regex", baseRuleFileObject.get("multiline_regex"));

      if (general.get("instruction_regex") == null)
         general.put("instruction_regex", baseRuleFileObject.get("instruction_regex"));

      if (general.get("ignore_regex") == null)
         general.put("ignore_regex", baseRuleFileObject.get("ignore_regex"));

      if (general.get("valid_instruction_regex") == null)
         general.put("valid_instruction_regex", baseRuleFileObject.get("valid_instruction_regex"));

      if (general.get("ref_url_base") == null)
         general.put("ref_url_base", baseRuleFileObject.get("ref_url_base"));

      if (general.get("valid_instructions") == null)
         general.put("valid_instructions", baseRuleFileObject.get("valid_instructions"));

      Map<String, Object> ruleFileLineRules = (Map<String, Object>) ruleFileObject.get("line_rules");
      Map<String, Object> baseRuleFileLineRules = (Map<String, Object>) baseRuleFileObject.get("line_rules");
      List<Object> validInstructions = (List<Object>) general.get("valid_instructions");

      for (Object obj : validInstructions)
      {
         String instruction = (String) obj;

         if (ruleFileLineRules.get(instruction) == null)
         {
            ruleFileLineRules.put(instruction, baseRuleFileLineRules.get(instruction));
         }

         else
         {
            Map<String, Object> ruleFileInstructionRules = (Map<String, Object>) ruleFileLineRules.get(instruction);
            Map<String, Object> baseRuleFileInstructionRules = (Map<String, Object>) baseRuleFileLineRules
                     .get(instruction);

            if (ruleFileInstructionRules.get("paramSyntaxRegex") == null)
               ruleFileInstructionRules.put("paramSyntaxRegex", baseRuleFileInstructionRules.get("paramSyntaxRegex"));

            if (ruleFileInstructionRules.get("rules") == null)
               ruleFileInstructionRules.put("rules", baseRuleFileInstructionRules.get("rules"));
         }

      }

      if (ruleFileObject.get("required_instructions") == null)
         ruleFileObject.put("required_instructions", baseRuleFileObject.get("required_instructions"));

      return ruleFileObject;

   }

   public void setBaseRuleFile(FileResource<?> baseRuleFile)
   {
      this.baseRuleFile = baseRuleFile;
   }

   /**
    * Return the {@link FileResource} ,for the base rule file.
    * 
    * @return The Base Rule file FileResource containing basic set of lint rules for Dockerfiles.
    */

   public Resource<?> getBaseRules()
   {
      return resourceFactory.create(URLResource.class, getClass().getResource("base_rules.yaml"));
   }

   /**
    * Add messages defined in YAML rule file to the {@link DockerfileLintResult} based on the problem found in the
    * linting process.
    * 
    * @param dockerfileLintResult The {@link DockerfileLintResult} representing the Lint result being constructed.
    * @param type The {@link String} representing the type of problem found in the linting process.
    * @param message The {@link String} representing the message corresponding to type of problem found in the linting
    *           process.
    * @param line The {@link String} representing the statement in the Dockerfile where the problem was found in the
    *           linting process.
    * @param lineNumber The {@link Integer} representing the line number of the statement in the Dockerfile where the
    *           problem was found in the linting process.
    */
   private void addLintResult(DockerfileLintResult dockerfileLintResult, String type, String message,
            String line, Integer lineNumber)
   {

      if (type != null)
      {
         if (type.toUpperCase().equals("ERROR"))
         {
            dockerfileLintResult.addError(message, line, lineNumber);
         }
         else if (type.toUpperCase().equals("WARN"))
         {
            dockerfileLintResult.addWarn(message, line, lineNumber);
         }
         else if (type.toUpperCase().equals("INFO"))
         {
            dockerfileLintResult.addInfo(message, line, lineNumber);
         }
      }
   }

   /**
    * Add messages defined in YAML rule file to the {@link DockerfileLintResult} based on the problem found in the
    * linting process.
    * 
    * @param dockerfileLintResult The {@link DockerfileLintResult} representing the Lint result being constructed.
    * @param type The {@link String} representing the type of problem found in the linting process.
    * @param message The {@link String} representing the message corresponding to type of problem found in the linting
    *           process.
    */
   private void addLintResult(DockerfileLintResult dockerfileLintResult, String type, String message)
   {
      addLintResult(dockerfileLintResult, type, message, "", -1);

   }

}

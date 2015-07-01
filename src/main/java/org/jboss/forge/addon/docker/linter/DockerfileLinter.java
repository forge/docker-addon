package org.jboss.forge.addon.docker.linter;

import java.io.File;
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
import org.yaml.snakeyaml.Yaml;

public class DockerfileLinter
{
   // TODO add support for JavaScript regexes.
   // TODO add support for case insensitive regexes.

   private ResourceFactory resourceFactory;

   private FileResource<?> baseRuleFile = null;

   public DockerfileLinter(ResourceFactory resourceFactory)
   {
      this.resourceFactory = resourceFactory;
   }

   @SuppressWarnings("unchecked")
   public FileResource<?> getBaseRules()
   {
      InputStream ist = getClass().getResourceAsStream("base_rules.yaml");
      File file = null;
      try
      {
         file = File.createTempFile("fileresourcetest", ".yaml");
      }
      catch (IOException e1)
      {
      }
      file.deleteOnExit();
      FileResource<?> fileResource = resourceFactory.create(FileResource.class, file);
      fileResource.setContents(ist);
      try
      {
         ist.close();
      }
      catch (IOException e)
      {
      }
      return fileResource;
   }

   public DockerfileLintResult lint(DockerFileResource dockerfile)
   {
      return lint(dockerfile, null);
   }

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

      @SuppressWarnings("unchecked")
      Map<String, Object> linerules = (Map<String, Object>) object.get("line_rules");

      @SuppressWarnings("unchecked")
      List<Object> reqinst = (List<Object>) object.get("required_instructions");

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

         if (currentLine == null || currentLine.length() == 0)
         {
            continue;
         }

         if (currentLine.charAt(0) == '#')
         {
            continue;
         }

         if (ignoreRegex != null && testRegex(ignoreRegex, currentLine))
         {
            continue;
         }

         while (multiLineRegex != null && testRegex(multiLineRegex, currentLine))
         {
            currentLine = currentLine.replaceFirst(multiLineRegex, " ");
            currentLine += i.next();
            i.remove();
            currentLineIndex++;
         }

         if (!fromCheck)
         {
            fromCheck = true;

            if (currentLine.toUpperCase().indexOf("FROM") != 0)
            {
               dockerfileLintResult
                        .addError("FROM statement missing or misplaced", currentLine, currentLineIndex);
            }

         }

         if (instructionRegex != null && !testRegex(instructionRegex, currentLine))
         {
            dockerfileLintResult.addError("Not a Instruction", currentLine, currentLineIndex);
            continue;
         }

         if (validInstructionRegex != null && !testRegex(validInstructionRegex, currentLine))
         {
            dockerfileLintResult.addError("Not a Valid Instruction", currentLine, currentLineIndex);
            continue;
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

         if (requiredInstructions.get(instruction) != null)
         {
            requiredInstructions.put(instruction, requiredInstructions.get(instruction) - 1);
         }

         @SuppressWarnings("unchecked")
         List<Object> instructionRules = (List<Object>) ((Map<String, Object>) linerules.get(instruction)).get("rules");

         checkLineRules(instruction, currentLine, instructionRules, dockerfileLintResult, currentLineIndex);

         @SuppressWarnings("unchecked")
         String parameterSyntaxRegex = (String) ((Map<String, String>) linerules.get(instruction))
                  .get("paramSyntaxRegex");
         String parameters = currentLine.substring(matcher.end());

         if (parameterSyntaxRegex != null && !testRegex(parameterSyntaxRegex, parameters))
         {
            dockerfileLintResult.addError("Bad Parameters", currentLine, currentLineIndex);
            continue;
         }
      }

      checkRequiredInstructions(requiredInstructions, dockerfileLintResult, reqinst);

      return dockerfileLintResult;
   }

   private boolean testRegex(String regex, String source)
   {
      return (Pattern.compile(regex).matcher(source).find());

   }

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
               if (testRegex(rule.get("regex"), currentLine) && !invr)
               {
                  addLintResult(dockerfileLintResult, rule.get("level"), rule.get("message"), currentLine,
                           currentLineIndex);
               }

               if (!testRegex(rule.get("regex"), currentLine) && invr)
               {
                  addLintResult(dockerfileLintResult, rule.get("level"), rule.get("message"), currentLine,
                           currentLineIndex);
               }

            }

         }
      }

   }

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

   private void addLintResult(DockerfileLintResult dockerfileLintResult, String type, String message)
   {
      addLintResult(dockerfileLintResult, type, message, "", -1);

   }

}

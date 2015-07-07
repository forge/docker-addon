package org.jboss.forge.addon.docker.linter;

import java.util.ArrayList;
import java.util.List;

public class DockerfileLintResult
{

   final private List<DockerfileLineLintResult> lintResults = new ArrayList<>();
   final private List<String> errorList = new ArrayList<>();
   final private List<String> warnList = new ArrayList<>();
   final private List<String> infoList = new ArrayList<>();
   private int errors = 0;
   private int info = 0;
   private int warn = 0;

   public void addError(String message, String line, Integer lineNumber)
   {
      errors++;
      DockerfileLineLintResult result = new DockerfileLineLintResult(DockerfileLintResultType.ERROR, message, line,
               lineNumber);
      lintResults.add(result);
      errorList.add(result.toString());
   }

   public void addWarn(String message, String line, Integer lineNumber)
   {
      warn++;
      DockerfileLineLintResult result = new DockerfileLineLintResult(DockerfileLintResultType.WARN, message, line,
               lineNumber);
      lintResults.add(result);
      warnList.add(result.toString());
   }

   public void addInfo(String message, String line, Integer lineNumber)
   {
      info++;
      DockerfileLineLintResult result = new DockerfileLineLintResult(DockerfileLintResultType.INFO, message, line,
               lineNumber);
      lintResults.add(result);
      infoList.add(result.toString());

   }

   public int getErrors()
   {
      return errors;
   }

   public int getInfo()
   {
      return info;
   }

   public int getWarn()
   {
      return warn;
   }

   public List<String> getErrorList()
   {
      return errorList;
   }

   public List<String> getWarnList()
   {
      return warnList;
   }

   public List<String> getInfoList()
   {
      return infoList;
   }

   public List<DockerfileLineLintResult> getLintResults()
   {
      return lintResults;
   }

   public List<DockerfileLineLintResult> getResult()
   {
      return lintResults;
   }

   @Override
   public String toString()
   {
      StringBuilder lintResult = new StringBuilder();
      lintResult.append("Validation Results\n");
      lintResult.append("Errors: ").append(errors).append("\n");
      lintResult.append("Warn: ").append(warn).append("\n");
      lintResult.append("Info: ").append(info).append("\n");

      for (DockerfileLineLintResult dlvr : lintResults)
      {
         lintResult.append(dlvr.toString());
      }

      return lintResult.toString();

   }

}

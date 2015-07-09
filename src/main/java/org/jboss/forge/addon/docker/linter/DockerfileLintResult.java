package org.jboss.forge.addon.docker.linter;

import java.util.ArrayList;
import java.util.List;

public class DockerfileLintResult
{

   final private List<DockerfileLineLintResult> lintResults = new ArrayList<>();

   private int errors = 0;
   private int info = 0;
   private int warn = 0;

   public void addError(String message, String line, Integer lineNumber)
   {
      errors++;
      DockerfileLineLintResult result = new DockerfileLineLintResult(DockerfileLintResultType.ERROR, message, line,
               lineNumber);
      lintResults.add(result);
   }

   public void addWarn(String message, String line, Integer lineNumber)
   {
      warn++;
      DockerfileLineLintResult result = new DockerfileLineLintResult(DockerfileLintResultType.WARN, message, line,
               lineNumber);
      lintResults.add(result);
   }

   public void addInfo(String message, String line, Integer lineNumber)
   {
      info++;
      DockerfileLineLintResult result = new DockerfileLineLintResult(DockerfileLintResultType.INFO, message, line,
               lineNumber);
      lintResults.add(result);
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

   public List<DockerfileLineLintResult> getLintResults()
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

package org.jboss.forge.addon.docker.linter;

import java.util.ArrayList;
import java.util.List;

public class DockerfileLintResult
{

   final private List<DockerfileLineLintResult> lintResults = new ArrayList<>();
   private int errors = 0;
   private int info = 0;
   private int warn = 0;

   public List<DockerfileLineLintResult> getResult()
   {
      return lintResults;
   }

   public void addError(String message, String line, Integer lineNumber)
   {
      errors++;
      lintResults.add(new DockerfileLineLintResult(DockerfileLintResultType.ERROR, message, line, lineNumber));

   }

   public void addWarn(String message, String line, Integer lineNumber)
   {
      warn++;
      lintResults.add(new DockerfileLineLintResult(DockerfileLintResultType.WARN, message, line, lineNumber));

   }

   public void addInfo(String message, String line, Integer lineNumber)
   {
      info++;
      lintResults.add(new DockerfileLineLintResult(DockerfileLintResultType.INFO, message, line, lineNumber));

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

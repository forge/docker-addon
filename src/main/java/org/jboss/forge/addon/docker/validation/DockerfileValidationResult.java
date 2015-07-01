package org.jboss.forge.addon.docker.validation;

import java.util.ArrayList;
import java.util.List;

public class DockerfileValidationResult
{

   final private List<DockerfileLineValidationResult> result = new ArrayList<>();
   private int errors = 0;
   private int info = 0;
   private int warn = 0;

   public List<DockerfileLineValidationResult> getResult()
   {
      return result;
   }

   public void addError(String message, String line, Integer lineNumber)
   {
      errors++;
      result.add(new DockerfileLineValidationResult(DockerfileValidationResultType.ERROR, message, line, lineNumber));

   }

   public void addWarn(String message, String line, Integer lineNumber)
   {
      warn++;
      result.add(new DockerfileLineValidationResult(DockerfileValidationResultType.WARN, message, line, lineNumber));

   }

   public void addInfo(String message, String line, Integer lineNumber)
   {
      info++;
      result.add(new DockerfileLineValidationResult(DockerfileValidationResultType.INFO, message, line, lineNumber));

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
      StringBuilder validationResult = new StringBuilder();
      validationResult.append("Validation Results\n");
      validationResult.append("Errors: ").append(errors).append("\n");
      validationResult.append("Warn: ").append(warn).append("\n");
      validationResult.append("Info: ").append(info).append("\n");

      for (DockerfileLineValidationResult dlvr : result)
      {
         validationResult.append(dlvr.toString());
      }

      return validationResult.toString();

   }

}

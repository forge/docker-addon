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

   void addValidationResult(String type, String message, String line, Integer lineNumber)
   {

      if (type.equals("error"))
      {
         addError(message, line, lineNumber);
      }
      else if (type.equals("warn"))
      {
         addWarn(message, line, lineNumber);
      }
      else if (type.equals("info"))
      {
         addInfo(message, line, lineNumber);
      }
      else
      {
         result.add(new DockerfileLineValidationResult(DockerfileValidationResultType.valueOf(type), message, line,
                  lineNumber));
      }

   }

   void addError(String message, String line, Integer lineNumber)
   {
      errors++;
      result.add(new DockerfileLineValidationResult(DockerfileValidationResultType.error, message, line, lineNumber));

   }

   void addWarn(String message, String line, Integer lineNumber)
   {
      warn++;
      result.add(new DockerfileLineValidationResult(DockerfileValidationResultType.warn, message, line, lineNumber));

   }

   void addInfo(String message, String line, Integer lineNumber)
   {
      info++;
      result.add(new DockerfileLineValidationResult(DockerfileValidationResultType.info, message, line, lineNumber));

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

   public void addValidationResult(String type, String message)
   {
      addValidationResult(type, message, "", -1);

   }

}

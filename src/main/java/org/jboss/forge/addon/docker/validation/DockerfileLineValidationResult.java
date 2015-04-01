package org.jboss.forge.addon.docker.validation;

public class DockerfileLineValidationResult
{

   private final DockerfileValidationResultType type;
   private final String message;
   private final String line;
   private final Integer lineNumber;

   public DockerfileLineValidationResult(DockerfileValidationResultType type)
   {
      this(type, null, null, -1);
   }

   public DockerfileLineValidationResult(DockerfileValidationResultType type, String message, String line,
            Integer lineNumber)
   {
      this.type = type;
      this.message = message;
      this.line = line;
      this.lineNumber = lineNumber;
   }

   public DockerfileValidationResultType getType()
   {
      return type;
   }

   public String getMessage()
   {
      return message;
   }

   public String getLine()
   {
      return line;
   }

   public Integer getLineNumber()
   {
      return lineNumber;
   }

   @Override
   public String toString()
   {
      if (lineNumber != -1)
      {
         return "\nType: " + type + "\nMessage: " + message + "\nLine=" + line
                  + "\nLine Number:" + lineNumber + "\n";
      }
      else
      {
         return "\nType: " + type + "\nMessage: " + message + "\n";
      }
   }

}

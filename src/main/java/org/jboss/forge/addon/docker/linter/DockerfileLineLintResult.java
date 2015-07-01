package org.jboss.forge.addon.docker.linter;

class DockerfileLineLintResult
{

   private final DockerfileLintResultType type;
   private final String message;
   private final String line;
   private final Integer lineNumber;

   public DockerfileLineLintResult(DockerfileLintResultType type)
   {
      this(type, null, null, -1);
   }

   public DockerfileLineLintResult(DockerfileLintResultType type, String message, String line,
            Integer lineNumber)
   {
      this.type = type;
      this.message = message;
      this.line = line;
      this.lineNumber = lineNumber;
   }

   public DockerfileLintResultType getType()
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

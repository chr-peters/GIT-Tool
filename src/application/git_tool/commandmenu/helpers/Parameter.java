//!important!
package application.git_tool.commandmenu.helpers;

public class Parameter{
  private String defaultText;
  private String paramName;
  private boolean hasArg;

  public Parameter(String paramName, String defaultText){
    this.defaultText = defaultText;
    this.paramName = paramName;
    hasArg = true;
  }

  public Parameter(String paramName, boolean hasArg){
    defaultText = "";
    this.paramName = paramName;
    this.hasArg = hasArg;
  }

  public Parameter(String paramName){
    defaultText = "";
    this.paramName = paramName;
    hasArg = false;
  }

  public String getDefaultText(){
    return defaultText;
  }
  public String getName(){
    return paramName;
  }
  public boolean hasArg(){
    return hasArg;
  }
}

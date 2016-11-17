//!important!
package application.git_tool.commandmenu.helpers;

public class Parameter{
  private String paramName;
  private boolean hasArg;

  public Parameter(String paramName, boolean hasArg){
    this.paramName = paramName;
    this.hasArg = hasArg;
  }

  public Parameter(String paramName){
    this.paramName = paramName;
    hasArg = false;
  }
}

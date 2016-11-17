//!important!
package application.git_tool.commandmenu.helpers;

public class Command{
  private String cmdName;
  private Parameter[] params;

  public Command(String cmdName, Parameter[] params){
    this.cmdName = cmdName;
    this.params = params;
  }

public Command(String cmdName){
  this.cmdName = cmdName;
  params = null;
}

  public String getName(){
    return cmdName;
  }
  public Parameter[] getParams(){
    return params;
  }
}

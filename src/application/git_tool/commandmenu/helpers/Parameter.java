package application.git_tool.commandmenu.helpers;

public class Parameter{

  private String defaultText;
  private String paramName;
  private boolean isNecessary;

  public Parameter(String paramName, String defaultText, boolean isNecessary){
    this.paramName = paramName;
    this.defaultText = defaultText;
    this.isNecessary = isNecessary;
  }

  public Parameter(String paramName, String defaultText){
    this.paramName = paramName;
    this.defaultText = defaultText;
    isNecessary = false;
  }

  public Parameter(String paramName){
    this.paramName = paramName;
    this.defaultText = "";
    isNecessary = false;
  }

  public String getDefaultText(){
    return defaultText;
  }
  public String getName(){
    return paramName;
  }
  public boolean isNecessary(){
    return isNecessary;
  }
  public boolean hasArg(){
    return !defaultText.isEmpty();
  }
}

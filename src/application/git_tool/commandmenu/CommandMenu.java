//!important!
package application.git_tool.commandmenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import net.miginfocom.layout.*;
import net.miginfocom.swing.*;

import application.git_tool.commandmenu.helpers.Command;
import application.git_tool.commandmenu.helpers.Parameter;
import application.git_tool.gitcommandexecutor.GITCommandExecutor;
import application.git_tool.GITTool;

public class CommandMenu extends JPanel {
  private Command[] commands;
  private GITCommandExecutor gitCmdExec;
  private GITTool gitTool;
  private List errors;
  private JButton bHelp, bExec;
  private JCheckBox[] paramBoxes;
  private JComboBox cmdList;
  private JLabel currCmdText;
  private JTextField[] paramTexts;
  private final static int maxParams = 5; //TODO wichtige Zeile

  public CommandMenu(GITTool gitTool){ //KONSTRUKTOR//////////////////////////////////////////////
    this.gitTool = gitTool;
    this.gitCmdExec = new GITCommandExecutor(this.gitTool.getProcessBuilder());
    this.setLayout(new MigLayout());
    this.init();
    cmdList.addActionListener(new ActionListener(){
       public void actionPerformed(ActionEvent e){
         int index = CommandMenu.this.cmdList.getSelectedIndex();
         int numParams = CommandMenu.this.commands[index].getParams().length;
         CommandMenu.this.setParams(numParams);
       }
     });
  }//KONSTRUKTOR ENDE//////////////////////////////////////////////////////////////////////////


  //Zeigt oder versteckt die ersten num Parameter////////
  public void setParams(int num){
    for(int i = 0; i < Math.min(num, maxParams); i++){
      paramBoxes[i].setSelected(false);
      paramBoxes[i].setText(getSelectedCommand().getParams()[i].getName());
      paramBoxes[i].setVisible(true);

      paramTexts[i].setText(getSelectedCommand().getParams()[i].getDefaultText());
      if(getSelectedCommand().getParams()[i].hasArg()) paramTexts[i].setVisible(true);
      else paramTexts[i].setVisible(false);
    }
    for(int i = num; i < maxParams; i++){
      paramBoxes[i].setVisible(false);

      paramTexts[i].setText("");
      paramTexts[i].setVisible(false);
    }
  }
  //////////////////////////////////////////////////////


  //Gibt das aktuell oben ausgewaehlte Kommando zurueck
  public Command getSelectedCommand(){
    return commands[cmdList.getSelectedIndex()];
  }////////////////////////////////////////////////////

  public void execute(){
    switch(cmdList.getSelectedIndex()){
      /*add*/case 0: errors = gitCmdExec.add(paramBoxes[1].isSelected(), paramBoxes[2].isSelected(), paramBoxes[3].isSelected(),
                            paramBoxes[4].isSelected(), paramTexts[0].getText());break;
   /*branch*/case 1: if(!paramBoxes[1].isSelected() && !paramBoxes[2].isSelected()){
                        if(paramTexts[0].getText().trim().equals(""))
                          errors = gitCmdExec.listBranches();
                        else
                          errors = gitCmdExec.createBranch(paramTexts[0].getText());
                     }
                     else if(paramBoxes[1].isSelected() && !paramBoxes[2].isSelected())
                        errors = gitCmdExec.deleteBranch(paramTexts[1].getText());
                     else if(paramBoxes[2].isSelected() && !paramBoxes[1].isSelected())
                        errors = gitCmdExec.renameBranch(paramTexts[0].getText(), paramTexts[2].getText());
                     break;
    }
    System.out.println(errors);
    System.out.println("Gelesen: " + paramTexts[0].getText().trim());
  }

  public void init(){
    //Erzeugung der einzelnen Befehle/////////////////////////////////////////////////
    Parameter[] addParams = {new Parameter("pathspec", true),
                            new Parameter("--force"), new Parameter("--update"),
                            new Parameter("--all"), new Parameter("--ignore-errors")};
    Command add = new Command("add", addParams);

    Parameter[] branchParams = {new Parameter("name", true), new Parameter("-d","*branch to delete*"),
                               new Parameter("-m", "*new name*")};
    Command branch = new Command("branch", branchParams);

    Parameter[] checkoutParams = {new Parameter("branch/commit", "*name*")};
    Command checkout = new Command("checkout", checkoutParams);

    Parameter[] cloneParams = {new Parameter("repository", true),
                              new Parameter("directory", true)};
    Command clone = new Command("clone", cloneParams);

    Parameter[] commitParams =  {new Parameter("-m", "*message*"), new Parameter("file", true),
                                new Parameter("--all"), new Parameter("--amend")};
    Command commit = new Command("commit", commitParams);

    Command fetch = new Command("fetch", new Parameter[0]);

    Parameter[] initParams = {new Parameter("--bare")};
    Command init = new Command("init", initParams);

    Parameter[] mergeParams = {new Parameter("branch/commit", true)};
    Command merge = new Command("merge", mergeParams);

    Parameter[] pullParams = {new Parameter("repository", true), new Parameter("refspec", true)};
    Command pull = new Command("pull", pullParams);

    Parameter[] pushParams = {new Parameter("repository", true), new Parameter("refspec", true)};
    Command push = new Command("push", pushParams);

    Parameter[] resetParams = {new Parameter("tree-ish", true), new Parameter("paths", true)};
    Command reset = new Command("reset", resetParams);

    Parameter[] rmParams = {new Parameter("file", true), new Parameter("--force"),
                           new Parameter("-r"), new Parameter("--cached")};
    Command rm = new Command("rm", rmParams);

    Parameter[] tagParams = {new Parameter("name", "*name of new tag*"), new Parameter("-d", "*tag to delete*")};
    Command tag = new Command("tag", tagParams);
    /////////////////////////////////////////////////////////////////////////////////

    //Liste der git-Befehle////////////////////////////
    Command[] tmpCommands  = { add, branch, checkout, clone, commit, fetch, init, merge,
                              pull, push, reset, rm, tag};
    commands = tmpCommands;
    String[] strCommands = new String[commands.length];
    for(int i = 0; i < strCommands.length; i++){
      strCommands[i] = commands[i].getName();
    } ///////////////////////////////////////////////////

    //Dropdown-Menue/////////////////////////////
    cmdList = new JComboBox<String>(strCommands);
    this.add(cmdList, "width 75%,  height 5%");
    /////////////////////////////////////////////

    //Hilfe-Button////////////////////////////////
    bHelp = new JButton("Help");
    this.add(bHelp, "width 25%, height 5%, growx, wrap");
    //////////////////////////////////////////////

    //Anzeige der Befehlsparameter////////////////////////////////////////////////
    int scalHeight = Math.min(90/maxParams, 10);
    paramBoxes = new JCheckBox[maxParams];
    paramTexts = new JTextField[maxParams];
    for(int i = 0; i < maxParams; i++){
      paramBoxes[i] = new JCheckBox();
      this.add(paramBoxes[i], "height "+scalHeight+"%"+", width 20%");
      paramTexts[i] = new JTextField();
      this.add(paramTexts[i], "height "+scalHeight/2+"%"+", width 80%, growx, wrap");
    }
    ///////////////////////////////////////////////////////////////////////////////

    //Anzeige des aktuellen Befehls///////////////////////
    currCmdText = new JLabel("HIER STEHT SP�TER WAT :O"); //TODO
    this.add(currCmdText, "growx, wrap");
    //////////////////////////////////////////////////////

    //Button zum Absetzen des Befehls
    bExec = new JButton("Execute");
    bExec.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
         execute();
       }
    });
    this.add(bExec, "growx, spanx, wrap, height 5%");
    /////////////////////////////////

    //Setze Startzustand
    setParams(commands[cmdList.getSelectedIndex()].getParams().length);
  }
}

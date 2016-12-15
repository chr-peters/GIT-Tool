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
import application.git_tool.commandmenu.helpers.MyTextField;

import application.git_tool.gitcommandexecutor.GITCommandExecutor;
import application.git_tool.gitcommandexecutor.MergeResponse;
import application.git_tool.GITTool;

public class CommandMenu extends JPanel {//Class CommandMenu/////////////////////////////////////////////////////////////

  private Command[] commands;
  private GITCommandExecutor gitCmdExec;
  private GITTool gitTool;
  private List errors;
  private JButton bHelp, bExec;
  private JCheckBox[] paramBoxes;
  private JComboBox cmdList;
  private JLabel currCmdText;
  private JLabel[] paramNames;
  private MyTextField[] paramTexts;
  private final static int maxParams = 5;


  public CommandMenu(GITTool gitTool){ //Konstruktor//////////////////////////////////////////////////////////////////
    this.gitTool = gitTool;
    this.gitCmdExec = new GITCommandExecutor(this.gitTool, this.gitTool.getProcessBuilder());
    this.setLayout(new MigLayout());
    this.init();
    addListenerToMenu();
    for(int i = 0; i < maxParams; i++){
      final int j = i; //In der Implementierung eines ActionListeners darf nur mit final Parametern gearbeitet werden.
      addListenerToBox(j);
    }
  }///////////////////////////////////////////////////////////////////////////////////////////////////////////////////


  private void init(){ //INITIALISIERUNG/////////////////////////////////////////////////////////////////////////////

    //Erzeugung der einzelnen Befehle////////////////////////////////////////////////////////////////////////
    Parameter[] addParams = {new Parameter("pathspec", "*file/directory*"),
                            new Parameter("--force"), new Parameter("--update"),
                            new Parameter("--all"), new Parameter("--ignore-errors")};
    Command add = new Command("add", addParams);

    Parameter[] createBranchParams = {new Parameter("name", "*name*", true)};
    Command createBranch = new Command("branch(create)", createBranchParams);

    Parameter[] deleteBranchParams = {new Parameter("-d","*branchname*", true)};
    Command deleteBranch = new Command("branch(delete)", deleteBranchParams);

    Command listBranches = new Command("branch(list)", new Parameter[0]);

    Parameter[] renameBranchParams = {new Parameter("old name", "*old name*", true), new Parameter("new name", "*old name*", true)};
    Command renameBranch = new Command("branch(rename)", renameBranchParams);

    Parameter[] checkoutParams = {new Parameter("branch/commit", "*name*", true)};
    Command checkout = new Command("checkout", checkoutParams);

    Parameter[] cloneParams = {new Parameter("repository", "*repository*", true),
                              new Parameter("directory", "*directory*", true)};
    Command clone = new Command("clone", cloneParams);

    Parameter[] commitParams =  {new Parameter("-m", "*message*", true), new Parameter("file", "*filename*"),
                                new Parameter("--all"), new Parameter("--amend")};
    Command commit = new Command("commit", commitParams);

    Command fetch = new Command("fetch", new Parameter[0]);

    Parameter[] initParams = {new Parameter("--bare")};
    Command init = new Command("init", initParams);

    Parameter[] mergeParams = {new Parameter("branch/commit", "PFLICHTPAREMETER?")};
    Command merge = new Command("merge", mergeParams);

    Parameter[] pullParams = {new Parameter("repository", "*repository*"), new Parameter("refspec", "TODO")};
    Command pull = new Command("pull", pullParams);

    Parameter[] pushParams = {new Parameter("repository", "*repository*"), new Parameter("refspec", "TODO")};
    Command push = new Command("push", pushParams);

    Parameter[] resetParams = {new Parameter("tree-ish", "TODO"), new Parameter("paths", "TODO")};
    Command reset = new Command("reset", resetParams);

    Parameter[] rmParams = {new Parameter("file", "*filename*", true), new Parameter("--force"),
                           new Parameter("-r"), new Parameter("--cached")};
    Command rm = new Command("rm", rmParams);

    Parameter[] createTagParams = {new Parameter("tagname", "*name*", true), new Parameter("message", "*message*"),
                                  new Parameter("commit", "*commit*")};
    Command createTag = new Command("tag(create)", createTagParams);

    Parameter[] deleteTagParams = {new Parameter("name", "*to delete*", true)};
    Command deleteTag = new Command("tag(delete)", deleteTagParams);

    Command listTags = new Command("tag(list)", new Parameter[0]);
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    //Liste der GIT-Befehle/////////////////////////////////////////////////////////////////////////////////////////
    Command[] tmpCommands  = { add, createBranch, deleteBranch, listBranches, renameBranch ,checkout, clone, commit,
                              fetch, init, merge, pull, push, reset, rm, createTag, deleteTag, listTags};
    commands = tmpCommands;
    String[] strCommands = new String[commands.length];
    for(int i = 0; i < strCommands.length; i++){
      strCommands[i] = commands[i].getName();
    } //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //Dropdown-Menü//////////////////////////////
    cmdList = new JComboBox<String>(strCommands);
    this.add(cmdList, "width 75%,  height 5%");
    /////////////////////////////////////////////

    //Hilfe-Button//////////////////////////////////////////////
    bHelp = new JButton("Help");
    this.add(bHelp, "width 25%, height 5%, growx, spanx, wrap");
    ////////////////////////////////////////////////////////////

    //Anzeige der Befehlsparameter///////////////////////////////////////////////////
    int scalHeight = Math.min(90/maxParams, 10);
    paramBoxes = new JCheckBox[maxParams];
    paramNames = new JLabel[maxParams];
    paramTexts = new MyTextField[maxParams];
    for(int i = 0; i < maxParams; i++){
      paramNames[i] = new JLabel();
      this.add(paramNames[i], "height "+scalHeight+"%"+", width 18%");
      paramBoxes[i] = new JCheckBox();
      this.add(paramBoxes[i], "height "+scalHeight+"%"+", width 2%");
      paramTexts[i] = new MyTextField();
      this.add(paramTexts[i], "height "+scalHeight/2+"%"+", width 80%, growx, wrap");
    }
    /////////////////////////////////////////////////////////////////////////////////

    //Anzeige des aktuellen Befehls//////
    currCmdText = new JLabel(); //TODO
    this.add(currCmdText, "growx, wrap");
    /////////////////////////////////////

    //Button zum Absetzen des Befehls////////////////
    bExec = new JButton("Execute");
    bExec.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
         execute();
       }
    });
    this.add(bExec, "growx, spanx, wrap, height 5%");
    /////////////////////////////////////////////////

    //Startzustand
    setParams();

  }//INITIALISIERUNG ENDE///////////////////////////////////////////////////////////////////////////////////////////


  //Anzeige der Parameter, bezogen auf den ausgewählten Befehl
  private void setParams(){
    int numParams = getSelectedCommand().getParams().length;
    Parameter param;
    for(int i = 0; i < Math.min(numParams, maxParams); i++){
      param = getSelectedCommand().getParams()[i];

      paramNames[i].setText(param.getName());
      paramBoxes[i].setSelected(false);
      paramTexts[i].setText(param.getDefaultText());
      if(param.hasArg()) paramTexts[i].setVisible(true);
      else paramTexts[i].setVisible(false);

      if(param.isNecessary()){
        paramTexts[i].setEnabled(true);
        paramBoxes[i].setVisible(false);
      }
      else {
        paramTexts[i].setEnabled(false);
        paramBoxes[i].setVisible(true);
      }
    }
    for(int i = numParams; i < maxParams; i++){
      paramNames[i].setText("");
      paramBoxes[i].setVisible(false);
      paramTexts[i].setVisible(false);
    }
  }///////////////////////////////////////////////////////////


  //Rückgabe des aktuell ausgewählten Kommandos/
  private Command getSelectedCommand(){
    return commands[cmdList.getSelectedIndex()];
  }/////////////////////////////////////////////


  //Ausführung des ausgewählten Kommands mit den vom Nutzer angegebenen Parametern///////////////////////////////////////
  private void execute(){
    switch(cmdList.getSelectedIndex()){
      //add
      case 0: errors = gitCmdExec.add(paramBoxes[1].isSelected(), paramBoxes[2].isSelected(), paramBoxes[3].isSelected(),
                            paramBoxes[4].isSelected(), paramTexts[0].getText()); break;
      //create branch
      case 1: errors = gitCmdExec.createBranch(paramTexts[0].getText()); break;
      //delete branch
      case 2: errors = gitCmdExec.deleteBranch(paramTexts[0].getText()); break;
      //list branches
      case 3: errors = gitCmdExec.listBranches(); break;
      //rename branch
      case 4: errors = gitCmdExec.renameBranch(paramTexts[0].getText(), paramTexts[1].getText()); break;
      //checkout
      case 5: errors = gitCmdExec.checkout(paramTexts[0].getText()); break;
      //clone
      case 6: errors = gitCmdExec.clone(paramTexts[0].getText(), paramTexts[1].getText()); break;
      //commit
      case 7: errors = gitCmdExec.commit(paramBoxes[2].isSelected(), paramBoxes[3].isSelected(),
                            paramTexts[0].getText(), paramTexts[1].getText()); break;
      //fetch
      case 8: errors = gitCmdExec.fetch(); break;
      //init
      case 9: errors = gitCmdExec.init(paramBoxes[0].isSelected()); break;
      //merge
      case 10: errors = gitCmdExec.merge(paramTexts[0].getText()).getOutputLines(); break;
      //pull
      case 11: errors = gitCmdExec.pull(paramTexts[0].getText(), paramTexts[1].getText()); break;
      //push
      case 12: errors = gitCmdExec.push(paramTexts[0].getText(), paramTexts[1].getText()); break;
      //reset
      case 13: errors = gitCmdExec.reset(paramTexts[0].getText(), paramTexts[1].getText()); break;
      //rm
      case 14: errors = gitCmdExec.reset(paramTexts[0].getText(), paramTexts[1].getText()); break;
      //create tag
      case 15: errors = gitCmdExec.createTag(paramTexts[1].getText(), paramTexts[0].getText(),
                            paramTexts[2].getText()); break;
      //delete tag
      case 16: errors = gitCmdExec.deleteTag(paramTexts[0].getText()); break;
      //list tags
      case 17: errors = gitCmdExec.listTags(); break;
    }

    if(!errors.isEmpty()){ //TODO dat wird die Rückmeldung, Kolleche

    }
    System.out.println(errors);
    System.out.println("Gelesen: " + paramTexts[0].getText().trim());
  }//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


  //ActionListener für das Dropdown-Menü/////////////////////////////////////
  private void addListenerToMenu(){
    cmdList.addActionListener(new ActionListener(){
      @Override
       public void actionPerformed(ActionEvent e){
         CommandMenu.this.setParams();
       }
    });
  }//////////////////////////////////////////////////////////////////////////


  //ActionListener für die CheckBoxen/////////////////////////////////////////
  private void addListenerToBox(final int index){
    paramBoxes[index].addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e){
        if(paramBoxes[index].isSelected()) paramTexts[index].setEnabled(true);
        else paramTexts[index].setEnabled(false);
      }
    });
  }///////////////////////////////////////////////////////////////////////////

}//Class CommandMenu/////////////////////////////////////////////////////////////////////////////////////////////////////

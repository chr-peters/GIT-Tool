package application.git_tool.commandmenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
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
  private List res;
  private JButton bHelp, bExec;
  private JCheckBox[] paramBoxes;
  private JComboBox cmdList;
  private JLabel successMessage;
  private JLabel[] paramNames;
  private MyTextField[] paramTexts;
  private static String[] helpTexts;
  private final static int maxParams = 5;

  public CommandMenu(GITTool gitTool){ //Konstruktor//////////////////////////////////////////////////////////////////
    this.gitTool = gitTool;
    this.gitCmdExec = new GITCommandExecutor(this.gitTool.getProcessBuilder());
    this.setLayout(new MigLayout());
    this.setBorder(BorderFactory.createTitledBorder("Command Menu"));
    this.init();
  }///////////////////////////////////////////////////////////////////////////////////////////////////////////////////


  private void init(){ //INITIALISIERUNG/////////////////////////////////////////////////////////////////////////////

    //Erzeugung der einzelnen Befehle///////////////////////////////////////////////////////////////////////////////////////////////
    Parameter[] addParams = {new Parameter("pathspec", "*file/directory*"),
                            new Parameter("--force"), new Parameter("--update"),
                            new Parameter("--all"), new Parameter("--ignore-errors")};
    Command add = new Command("add", addParams);

    Parameter[] createBranchParams = {new Parameter("name", "*name*", true)};
    Command createBranch = new Command("branch(create)", createBranchParams);

    Parameter[] deleteBranchParams = {new Parameter("-d","*branchname*", true)};
    Command deleteBranch = new Command("branch(delete)", deleteBranchParams);

    Parameter[] renameBranchParams = {new Parameter("old name", "*old name*", true), new Parameter("new name", "*new name*", true)};
    Command renameBranch = new Command("branch(rename)", renameBranchParams);

    Parameter[] checkoutParams = {new Parameter("branch/commit", "*name*"), new Parameter("path", "*path*")};
    Command checkout = new Command("checkout", checkoutParams);

    Parameter[] cloneParams = {new Parameter("repository", "*from*", true),
                              new Parameter("directory", "*into*")};
    Command clone = new Command("clone", cloneParams);

    Parameter[] commitParams =  {new Parameter("-m", "*message*", true), new Parameter("file", "*filename*"),
                                new Parameter("--all"), new Parameter("--amend")};
    Command commit = new Command("commit", commitParams);

    Parameter[] fetchParams = {new Parameter("remote", "*url*")};
    Command fetch = new Command("fetch", fetchParams);

    Parameter[] initParams = {new Parameter("--bare")};
    Command init = new Command("init", initParams);

    Parameter[] mergeParams = {new Parameter("branch/commit", "PFLICHTPAREMETER?")};
    Command merge = new Command("merge", mergeParams);

    Parameter[] pullParams = {new Parameter("repository", "*from*"), new Parameter("refspec", "*what to pull*")};
    Command pull = new Command("pull", pullParams);

    Parameter[] pushParams = {new Parameter("repository", "*into*"), new Parameter("refspec", "*what to push*")};
    Command push = new Command("push", pushParams);

    Parameter[] resetParams = {new Parameter("tree-ish", "TODO"), new Parameter("paths", "TODO")};
    Command reset = new Command("reset", resetParams);

    Parameter[] rmParams = {new Parameter("file", "*filename*", true), new Parameter("--force"),
                           new Parameter("-r"), new Parameter("--cached")};
    Command rm = new Command("rm", rmParams);

    Parameter[] createTagParams = {new Parameter("tagname", "*name*", true), new Parameter("message", "*message*", true),
                                  new Parameter("commit", "*commit*")};
    Command createTag = new Command("tag(create)", createTagParams);

    Parameter[] deleteTagParams = {new Parameter("name", "*to delete*", true)};
    Command deleteTag = new Command("tag(delete)", deleteTagParams);
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //Liste der GIT-Befehle/////////////////////////////////////////////////////////////////////////////////////////
    Command[] tmpCommands  = { add, createBranch, deleteBranch, renameBranch ,checkout, clone, commit,
                              fetch, init, merge, pull, push, reset, rm, createTag, deleteTag};
    commands = tmpCommands;
    String[] strCommands = new String[commands.length];
    for(int i = 0; i < strCommands.length; i++){
      strCommands[i] = commands[i].getName();
    } //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //Erzeugung der Hilfe
    helpTexts = new String[commands.length];
    String tmpLine = "";
    try{
      FileReader fr = new FileReader("./src/data/help.txt");
      BufferedReader br = new BufferedReader(fr);
      for(int i = 0; i < commands.length; i++){
        helpTexts[i] = "";
        while(tmpLine != null){
          tmpLine = br.readLine();
          if (tmpLine == null || tmpLine.equals("##")) break;
          else helpTexts[i] += tmpLine + "\n";
        }
      }
    }
    catch(FileNotFoundException e){
      System.err.println("help.txt could not be found.");
      System.exit(1);
    }
    catch(IOException e){
      System.err.println("An IOException occured. Check help.txt.");
      System.exit(1);
    }
    /////////////////////

    //Dropdown-Menue//////////////////////////////
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

    //Button zum Absetzen des Befehls////////////////
    bExec = new JButton("Execute");
    bExec.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
         execute();
       }
    });
    this.add(bExec, "growx, spanx, wrap, height 5%");
    /////////////////////////////////////////////////

    //Anzeige des aktuellen Befehls//////
    successMessage = new JLabel();
    this.add(successMessage, "growx, wrap");
    /////////////////////////////////////


    //Startzustand
    setParams();
    addActionListeners();

  }//INITIALISIERUNG ENDE///////////////////////////////////////////////////////////////////////////////////////////


  //Anzeige der Parameter, bezogen auf den ausgew??hlten Befehl
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


  //R??ckgabe des aktuell ausgew??hlten Kommandos/
  private Command getSelectedCommand(){
    return commands[cmdList.getSelectedIndex()];
  }/////////////////////////////////////////////


  //Ausfuehrung des ausgewaehlten Kommands mit den vom Nutzer angegebenen Parametern///////////////////////////////////////
  private void execute(){
    //create command string
    StringBuilder cmdString = new StringBuilder();
    successMessage.setVisible(false);
    boolean error = false;

    switch(cmdList.getSelectedIndex()){
        //add
        case 0: res = gitCmdExec.add(paramBoxes[1].isSelected(), paramBoxes[2].isSelected(), paramBoxes[3].isSelected(),
                            paramBoxes[4].isSelected(), paramTexts[0].getText());
            if(commandSuccessful(res)){
              cmdString.append("git add");
              if(paramBoxes[1].isSelected())
                  cmdString.append(" --force");
              if(paramBoxes[2].isSelected())
                  cmdString.append(" --update");
              if(paramBoxes[3].isSelected())
                  cmdString.append(" --all");
              if(paramBoxes[4].isSelected())
                  cmdString.append(" --ignore-errors");
              cmdString.append(" "+paramTexts[0].getText());
                successMessage.setText("Added "+paramTexts[0].getText()+".");
            }
            else {
              gitTool.errorMessage(res, "Error");
              error = true;
            }
            break;

        //create branch
        case 1: res = gitCmdExec.createBranch(paramTexts[0].getText());
            if(commandSuccessful(res)){
              cmdString.append("git branch");
              cmdString.append(" "+paramTexts[0].getText());
              successMessage.setText("Branch "+paramTexts[0].getText()+"\nhas been created.");
            }
            else {
              gitTool.errorMessage(res, "Error");
              error = true;
            }
            break;

        //delete branch
        case 2: res = gitCmdExec.deleteBranch(paramTexts[0].getText());
            if(commandSuccessful(res)){
                cmdString.append("git branch -d");
                cmdString.append(" "+paramTexts[0].getText());
                successMessage.setText("Branch "+paramTexts[0].getText()+"\nhas been deleted.");
            }
            else {
              gitTool.errorMessage(res, "Error");
              error = true;
            }
            break;

        //rename branch
        case 3: res = gitCmdExec.renameBranch(paramTexts[0].getText(), paramTexts[1].getText());
            if(commandSuccessful(res)){
              cmdString.append("git branch -m");
              cmdString.append(" "+paramTexts[0].getText());
              cmdString.append(" "+paramTexts[1].getText());
              successMessage.setText("Branch "+paramTexts[0].getText()+"\n has been renamed to\n"+paramTexts[0].getText()+".");
            }
            else {
              gitTool.errorMessage(res, "Error");
              error = true;
            }
            break;

        //checkout
        case 4: res = gitCmdExec.checkout(paramTexts[0].getText(), paramTexts[1].getText());
            cmdString.append("git checkout");
            if(commandSuccessful(res)){
              if(!paramTexts[0].getText().isEmpty()) cmdString.append(" "+paramTexts[0].getText());
              if(!paramTexts[1].getText().isEmpty()){
                cmdString.append(" "+paramTexts[1].getText());
                if(!cmdString.equals("git checkout")) successMessage.setText(paramTexts[1].getText()+"\nfrom branch "+paramTexts[0].getText()+"\nhas been checked out.");
                else successMessage.setText(paramTexts[1].getText()+" has been checked out.");
              }
              if(cmdString.toString().equals("git checkout")){
                if(!paramTexts[0].getText().isEmpty()) successMessage.setText("Switched branch to "+paramTexts[0].getText()+".");
                else successMessage.setText("Nothing happened."); //TODO
              }
            }
            else {
              gitTool.errorMessage(res, "Error");
              error = true;
            }
            break;

        //clone
        case 5: res = gitCmdExec.clone(paramTexts[0].getText(), paramTexts[1].getText());
            if(commandSuccessful(res)){
              cmdString.append("git clone");
              if(!paramTexts[0].getText().isEmpty()) cmdString.append(" "+paramTexts[0].getText());
              if(!paramTexts[1].getText().isEmpty()) {
                cmdString.append(" "+paramTexts[1].getText());
                successMessage.setText("The repository "+paramTexts[0].getText()+ " has been cloned into "+paramTexts[1].getText()+".");
              }
              else successMessage.setText("The repository "+paramTexts[0].getText()+ "\nhas been cloned into the current directory.");
            }
            else {
              gitTool.errorMessage(res, "Error");
              error = true;
            }
            break;

        //commit
        case 6: res = gitCmdExec.commit(paramBoxes[2].isSelected(), paramBoxes[3].isSelected(),
                            paramTexts[0].getText(), paramTexts[1].getText());
            if(commandSuccessful(res)){
              cmdString.append("git commit");
              if(paramBoxes[2].isSelected()) cmdString.append(" --all");
              if(paramBoxes[3].isSelected()) cmdString.append(" --amend");
              if(paramBoxes[1].isSelected()) cmdString.append(" "+paramTexts[1]);
              cmdString.append(" -m "+paramTexts[0]);
              successMessage.setText("Commit successful.");
            }
            else {
              gitTool.errorMessage(res, "Error");
              error = true;
            }
            break;

        //fetch
        case 7: res = gitCmdExec.fetch(paramTexts[0].getText());
            if(commandSuccessful(res)){
              cmdString.append("git fetch");
              if(!paramTexts[0].getText().isEmpty()) cmdString.append(" "+paramTexts[0].getText());
              successMessage.setText("Fetch successful.");
            }
            else {
              gitTool.errorMessage(res, "Error");
              error = true;
            }
            break;

        //init
        case 8: res = gitCmdExec.init(paramBoxes[0].isSelected());
            if(commandSuccessful(res)){
              cmdString.append("git init");
              if(paramBoxes[0].isSelected()) cmdString.append("--bare");
              successMessage.setText("An empty "+( cmdString.toString().equals("git init") ? "" : "base " )+"Git-Repository has been created");
            }
            else {
              gitTool.errorMessage(res, "Error");
              error = true;
            }
            break;

        //merge TODO successMeldung + mergeResponse
        case 9: res = gitCmdExec.merge(paramTexts[0].getText()).getOutputLines();
            cmdString.append("git merge");
            if(!paramTexts[0].getText().isEmpty()) cmdString.append(" "+paramTexts[0].getText());
            break;

        //pull TODO successMeldung
        case 10: res = gitCmdExec.pull(paramTexts[0].getText(), paramTexts[1].getText());
            if(commandSuccessful(res)){
              cmdString.append("git pull");
              if(!paramTexts[0].getText().isEmpty()) cmdString.append(" "+paramTexts[0].getText());
              if(!paramTexts[1].getText().isEmpty()) cmdString.append(" "+paramTexts[1].getText());
              successMessage.setText("Pull successful.");
            }
            else {
              gitTool.errorMessage(res, "Error");
              error = true;
            }
            break;

        //push TODO successMeldung
        case 11: res = gitCmdExec.push(paramTexts[0].getText(), paramTexts[1].getText());
            if(commandSuccessful(res)){
              cmdString.append("git push");
              if(!paramTexts[0].getText().isEmpty()) cmdString.append(" "+paramTexts[0].getText());
              if(!paramTexts[1].getText().isEmpty()) cmdString.append(" "+paramTexts[1].getText());
              successMessage.setText("Push successful.");
            }
            else {
              gitTool.errorMessage(res, "Error");
              error = true;
            }
            break;

        case 12: res = gitCmdExec.reset(paramTexts[0].getText(), paramTexts[1].getText());
            if(commandSuccessful(res)){
              cmdString.append("git reset");
              if(!paramTexts[0].getText().isEmpty()) cmdString.append(" "+paramTexts[0].getText());
              if(!paramTexts[1].getText().isEmpty()) cmdString.append(" "+paramTexts[1].getText());
              successMessage.setText(""); //TODO successMeldung + Hilfe sollte noch angepasst/verstanden werden
            }
            else {
              gitTool.errorMessage(res, "Error");
              error = true;
            }
            break;

        //rm TODO successMeldung
        case 13: res = gitCmdExec.rm(paramBoxes[1].isSelected(), paramBoxes[2].isSelected(),
                            paramBoxes[3].isSelected(), paramTexts[0].getText());
            cmdString.append("git rm");
            if(commandSuccessful(res)){
              if(paramBoxes[1].isSelected()) cmdString.append(" --force");
              if(paramBoxes[2].isSelected()) cmdString.append(" -r");
              if(paramBoxes[3].isSelected()) cmdString.append(" --cached");
              if(!paramTexts[0].getText().isEmpty()) cmdString.append(" "+paramTexts[0].getText());
            }
            else {
              gitTool.errorMessage(res, "Error");
              error = true;
            }
            break;

        //create tag
        case 14: res = gitCmdExec.createTag(paramTexts[1].getText(), paramTexts[0].getText(),
                            paramTexts[2].getText());
            if(commandSuccessful(res)){
              cmdString.append("git tag");
              if(!paramTexts[1].getText().isEmpty()) cmdString.append(" -m "+paramTexts[1].getText());
              if(!paramTexts[0].getText().isEmpty()) cmdString.append(" "+paramTexts[0].getText());
              if(!paramTexts[2].getText().isEmpty()) cmdString.append(" "+paramTexts[2].getText());
              successMessage.setText("Tag "+paramTexts[0].getText()+ " has been created.");
            }
            else {
              gitTool.errorMessage(res, "Error");
              error = true;
            }
            break;
    }
    if(!error){
      successMessage.setVisible(true);
      this.gitTool.getHistory().addCommand(new application.git_tool.history.Command(cmdString.toString()));
      //refresh the gittool
      this.gitTool.refresh();
      if(!res.isEmpty()) gitTool.infoMessage(res, "Info");
    }
  }//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


  public boolean commandSuccessful(List <String> output){
    if(!output.isEmpty() && output.get(0).equals("ERRORERRORERROR")){
      output.remove(0);
      return false;
    }
    return true;
  }


  private void addActionListeners(){
    addListenerToMenu();
    addListenerToHelp();
    for(int i = 0; i < maxParams; i++){
      final int j = i; //In der Implementierung eines ActionListeners darf nur mit final Parametern gearbeitet werden.
      addListenerToBox(j);
    }
  }


  //ActionListener fuer das Dropdown-Menue/////////////////////////////////////
  private void addListenerToMenu(){
    cmdList.addActionListener(new ActionListener(){
      @Override
        public void actionPerformed(ActionEvent e){
          CommandMenu.this.setParams();
        }
    });
  }//////////////////////////////////////////////////////////////////////////

  private void addListenerToHelp(){
    bHelp.addActionListener(new ActionListener(){
      @Override
        public void actionPerformed(ActionEvent e){
          gitTool.infoMessage(helpTexts[cmdList.getSelectedIndex()], getSelectedCommand().getName());
        }
    });
  }

  //ActionListener fuer die CheckBoxen/////////////////////////////////////////
  private void addListenerToBox(final int index){
    paramBoxes[index].addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e){
        if(paramBoxes[index].isSelected()) paramTexts[index].setEnabled(true);
        else {
          paramTexts[index].setEnabled(false);
          paramTexts[index].setText("");
        }
      }
    });
  }///////////////////////////////////////////////////////////////////////////

}//Class CommandMenu/////////////////////////////////////////////////////////////////////////////////////////////////////

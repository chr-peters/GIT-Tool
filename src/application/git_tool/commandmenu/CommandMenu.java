//!important!
package application.git_tool.commandmenu;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import javax.swing.*;
import net.miginfocom.layout.*;
import net.miginfocom.swing.*;

import application.git_tool.commandmenu.helpers.Command;
import application.git_tool.commandmenu.helpers.Parameter;

public class CommandMenu extends JPanel {
  private JComboBox cmdList;
  private JButton bHelp;
  private Command[] commands;
  private JCheckBox[] paramBoxes;
  private JTextField[] paramTexts;
  private final static int maxParams = 5; //TODO wichtige Zeile

  public CommandMenu(){ //KONSTRUKTOR///////////////////////////////////////////////////////////
    //Setzen des Layouts////////////
    this.setLayout(new MigLayout());
    ////////////////////////////////

    init();








    ActionListener menuListener = new ActionListener(){
      public void actionPerformed(ActionEvent e){
        int index = cmdList.getSelectedIndex();
        int numParams = commands[index].getParams().length;
        setParams(numParams);
      }
    };
    cmdList.addActionListener(menuListener);

  }//KONSTRUKTOR ENDE//////////////////////////////////////////////////////////////////////////

  //Zeigt oder versteckt die ersten num Parameter////////
  public void setParams(int num){
    for(int i = 0; i < Math.min(num, maxParams); i++){
      paramBoxes[i].setText(getSelectedCommand().getParams()[i].getName());
      paramBoxes[i].setVisible(true);
      if(getSelectedCommand().getParams()[i].hasArg()) paramTexts[i].setVisible(true);
      else paramTexts[i].setVisible(false);
    }
    for(int i = num; i < maxParams; i++){
      paramBoxes[i].setVisible(false);
      paramTexts[i].setVisible(false);
    }
  }
  ///////////////////////////////////////////////////////

  //Gibt das aktuell oben ausgewaehlte Kommando zurueck
  public Command getSelectedCommand(){
    return commands[cmdList.getSelectedIndex()];
  }

  public void init(){
    //Erzeugung der einzigen Befehle/////////////////////////////////////////////////
    Parameter[] addParams = {new Parameter("pathspec", true),
                            new Parameter("--force"), new Parameter("--update"),
                            new Parameter("--all"), new Parameter("--ignore-errors")};
    Command add = new Command("add", addParams);

    Parameter[] initParams = {new Parameter("--bare")};
    Command init = new Command("init", initParams);

    Parameter[] rmParams = {new Parameter("file", true), new Parameter("--force"),
                           new Parameter("-r"), new Parameter("--cached")};
    Command rm = new Command("rm", rmParams);
    //////////////////////////////////////////////////////////////////////////////////


    //Liste der git-Befehle////////////////////////////
    Command[] tmpCommands  = { add, init, rm};
    commands = tmpCommands;
    String[] strCommands = new String[commands.length];
    for(int i = 0; i < strCommands.length; i++){
      strCommands[i] = commands[i].getName();
    }
    ///////////////////////////////////////////////////

    //Dropdown-Menue/////////////////////////////
    cmdList = new JComboBox<String>(strCommands);
    this.add(cmdList, "width 75%,  height 5%");
    /////////////////////////////////////////////

    //Hilfe-Button////////////////////////////////
    bHelp = new JButton("Help");
    this.add(bHelp, "width 25%, height 5%, wrap");
    //////////////////////////////////////////////

    //Anzeige der Befehlsparameter//////////////////////////
    int scalHeight = Math.min(90/maxParams, 10);
    paramBoxes = new JCheckBox[maxParams];
    paramTexts = new JTextField[maxParams];
    for(int i = 0; i < maxParams; i++){
      paramBoxes[i] = new JCheckBox();
      this.add(paramBoxes[i], "height "+scalHeight+"%"+", width 30%");
      paramTexts[i] = new JTextField();
      this.add(paramTexts[i], "height "+scalHeight+"%"+", width 70%, growx, wrap");
    }
    /////////////////////////////////////////////////////////
  }
}

//!important!
package application.git_tool.commandmenu;

import java.awt.Dimension;
import java.util.Arrays;
import javax.swing.*;
import net.miginfocom.layout.*;
import net.miginfocom.swing.*;

import application.git_tool.commandmenu.helpers.Command;
import application.git_tool.commandmenu.helpers.Parameter;

public class CommandMenu extends JPanel {
  private JComboBox cmdList;
  private JButton bHelp;
  private final static int maxRButtons = 5;

  public CommandMenu(){
    //Setzen des Layouts////////////
    this.setLayout(new MigLayout());
    ////////////////////////////////


    //Erzeugung der einzigen Befehle/////////////////////////////////////////////////
    Parameter[] addParams = {new Parameter("pathspec", true),
                            new Parameter("--force"), new Parameter("--update"),
                            new Parameter("--all"), new Parameter("--ignore-errors")};
    Command add = new Command("add", addParams);

    Parameter[] checkoutParams = {new Parameter("file", true), new Parameter("--force"),
                           new Parameter("-r"), new Parameter("--cached")};
    Command checkout = new Command("checkout", checkoutParams);

    Parameter[] initParams = {new Parameter("--bare")};
    Command init = new Command("init", initParams);
    //////////////////////////////////////////////////////////////////////////////////


    //Liste der git-Befehle////////////////////////////////////////////////
    Command[] commands = { add, init, checkout};
    String[] strCommands = new String[commands.length];
    for(int i = 0; i < strCommands.length; i++){
      strCommands[i] = commands[i].getName();
    }
    /////////////////////////////////////////////////////////////////////////


    //Dropdown-Menue///////////////////////////////
    cmdList = new JComboBox<String>(strCommands);
    this.add(cmdList, "width 75%,  height 5%");
    ///////////////////////////////////////////////


    //Hilfe-Button///////////////////////////////////////
    bHelp = new JButton("Help");
    this.add(bHelp, "width 25%, height 5%, wrap");
    //////////////////////////////////////////////


    //Anzeige der Befehlsparameter//////////////////////////////
    for(int i = 0; i < commands.length; i++){
      JRadioButton rbutton = new JRadioButton(commands[i].getName()); //TODO hier muss noch einiges getan werden
      this.add(rbutton, "height 15%, width 30%");
      //if(rbutton.isSelected()){
        JTextField text = new JTextField("Argument");
        this.add(text, "height 5%, width 70%, growx, wrap");
      //}
    }
    ////////////////////////////////////////////////////////////
  }
}

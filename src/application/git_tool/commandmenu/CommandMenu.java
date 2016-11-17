//!important!
package application.git_tool.commandmenu;

import java.awt.Dimension;
import javax.swing.*;

import net.miginfocom.layout.*;
import net.miginfocom.swing.*;

public class CommandMenu extends JPanel {
  public CommandMenu(){
    //Liste der git-Kommandos////////////////////////////////////////////////
    String[] commands = { "add", "branch", "checkout", "clone", "commit",
    "diff", "fetch", "grep", "init", "log", "merge", "pull", "push", "reset",
    "rm", "status", "tag"};
    /////////////////////////////////////////////////////////////////////////

    this.setLayout(new MigLayout());

    //Dropdown-Menue///////////////////////////////
    JComboBox cmdList = new JComboBox(commands);
    this.add(cmdList, "width 75%, spany 1, growx, growy, height 5%");//TODO
    ///////////////////////////////////////////////

    //Hilfe
    JButton bHelp = new JButton("Help");
    this.add(bHelp, "width 10%, height 5%");
    //
  }
}

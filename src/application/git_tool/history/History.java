//!important!
package application.git_tool.history;

import application.git_tool.GITTool;

import net.miginfocom.layout.*;
import net.miginfocom.swing.*;

import javax.swing.*;

public class History extends JPanel {

    private GITTool gitTool;
    private int maxCommands;
    //remove this as it is stored in the linkedlist
    private int size;
    
    public History (GITTool gitTool){
        this.gitTool = gitTool;
        this.maxCommands = 10;
        this.size = 10;
        
        this.setLayout(new MigLayout("debug, fillx"));
        
        this.drawCommands();
    }
    
    private void drawCommands() {
        //remove all components to redraw the history
        this.removeAll();
        //headline
        this.add(new JLabel("Command History:"), "spanx 2, center, wrap");
        //draw the commands
        for(int i=0; i<size; i++){
            this.add(new JLabel("Date: "), "");
            this.add(new JLabel("Command"), "wrap");
        }
    }
    
    public void refresh () {
    
    }
    
}
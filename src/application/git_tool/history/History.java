//!important!
package application.git_tool.history;

import application.git_tool.GITTool;

import net.miginfocom.layout.*;
import net.miginfocom.swing.*;

import javax.swing.*;

import java.awt.Color;

import java.util.LinkedList;

public class History extends JPanel {

    private GITTool gitTool;
    //maximum number of commands to be stored
    private int maxCommands;
    //list to store the commands
    private LinkedList<Command> commands;
    
    public History (GITTool gitTool, int maxCommands){
        this.gitTool = gitTool;
        this.maxCommands = maxCommands;
        this.commands = new LinkedList<Command>();
        
        //init GUI
        this.setLayout(new MigLayout("fillx"));
        this.setBorder(BorderFactory.createTitledBorder("Command History"));
    }
    
    private void drawCommands() {
        //remove all components to redraw the history
        this.removeAll();
        //draw the commands
        for(int i=0; i<commands.size(); i++){
            this.add(new JLabel(commands.get(i).printDate("dd.MM.yyyy' - 'HH:mm:ss")), "");
            this.add(new JLabel(commands.get(i).getText()), "wrap");
        }
        this.revalidate();
        this.repaint();
    }
    
    /**
    * Adds a command to the command history.
    * <p>
    * No need to call refresh afterwards as the history redraws itself
    * automatically.
    *
    * @param command A container for the relevant information regarding a command.
    *                See it's documentation for more details.
    */
    public void addCommand(Command command) {
        if(commands.size() < maxCommands) {
            commands.addFirst(command);
        } else {
            commands.pollLast();
            commands.addFirst(command);
        }
        this.drawCommands();
    }
    
    /**
    * Refreshes the command history.
    * <p>
    * At the moment, this method does not do anything, as the history redraws
    * itself everytime a new command is added.
    */
    public void refresh () {
        this.drawCommands();
    }
    
}
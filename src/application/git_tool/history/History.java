//!important!
package application.git_tool.history;

import application.git_tool.GITTool;

import net.miginfocom.layout.*;
import net.miginfocom.swing.*;

import javax.swing.*;

import java.awt.Color;

import java.text.SimpleDateFormat;
import java.text.ParseException;

import java.io.*;

import java.util.LinkedList;

public class History extends JPanel {

    private GITTool gitTool;
    //maximum number of commands to be stored
    private int maxCommands;
    //list to store the commands
    private LinkedList<Command> commands;
    
    private final String dateFormat = "dd.MM.yyyy' - 'HH:mm:ss";
    private final File commandLog = new File("src/data/commands.log");
    private final String separator = ",";
    private final String escapeSequence = "\\";
    
    public History (GITTool gitTool, int maxCommands){
        this.gitTool = gitTool;
        this.maxCommands = maxCommands;
        this.commands = new LinkedList<Command>();
        
        //init GUI
        this.setLayout(new MigLayout("fillx"));
        
        //parse the commands from the log file and draw them
        this.parseCommands();
        this.drawCommands();
        
        //add shutdown routine
        Runtime.getRuntime().addShutdownHook(new Thread (new Runnable () {
            public void run () {
                try {
                    //if the directory of the commandlog file does not exist, create it
                    if(!History.this.commandLog.getParentFile().exists()){
                        History.this.commandLog.getParentFile().mkdirs();
                    }
                    //write the last commands into the commands.log file
                    BufferedWriter writer = new BufferedWriter(new FileWriter(History.this.commandLog, false));
                    for(Command c: History.this.commands) {
                        //escape the separator sequence in case it is part of the command text or date
                        writer.write(c.printDate(History.this.dateFormat).replace(History.this.separator, History.this.escapeSequence+History.this.separator) 
                                        + History.this.separator + c.getText().replace(History.this.separator, History.this.escapeSequence+History.this.separator));
                        writer.newLine();
                    }
                    writer.close();
                } catch (IOException e) {
                    System.err.println("Could not save the last commands to the log file.");
                }
            }
        }));
    }
    
    private void drawCommands() {
        //remove all components to redraw the history
        this.removeAll();
        //draw the commands
        for(int i=0; i<commands.size(); i++){
            this.add(new JLabel(commands.get(i).printDate(dateFormat)), "");
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
    
    //parse the last commands from the log file
    private void parseCommands() {
        try {
            //if the directory of the commandlog file does not exist, create it
            if(!this.commandLog.getParentFile().exists()){
                this.commandLog.getParentFile().mkdirs();
            }
            BufferedReader reader = new BufferedReader(new FileReader(this.commandLog));
            String line = "";
            while ((line=reader.readLine()) != null && commands.size() < this.maxCommands){
                try {
                    //split the line along the unescaped separator sequence
                    String components [] = line.split("[^"+(this.escapeSequence=="\\"?"\\":"")+this.escapeSequence+"]"+this.separator);
                    //replace the escaped separator sequences with the standard separator sequences
                    String dateString = components[0].replace(this.escapeSequence+this.separator, this.separator);
                    String textString = components[1].replace(this.escapeSequence+this.separator, this.separator);
                    //parse the date format
                    SimpleDateFormat dateParser = new SimpleDateFormat(this.dateFormat);
                    this.commands.add(new Command(textString, dateParser.parse(dateString)));
                } catch (Exception e) {
                
                }
            }
            reader.close();
        } catch (IOException e) {

        }
    }
}

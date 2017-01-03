//!important!
package application.git_tool.commandline;

import application.git_tool.*;
import application.git_tool.unixcommandexecutor.*;

import net.miginfocom.layout.*;
import net.miginfocom.swing.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.*;
import java.awt.Color;

import java.awt.event.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import java.io.File;

public class CommandLine extends JPanel {

    private GITTool gitTool;
    private ProcessBuilder processBuilder;
    private UnixCommandExecutor commandExecutor;

    //components
    private JTextPane outputPane;
    private StyledDocument outputDoc;
    private JTextField inputField;

    public CommandLine (GITTool gitTool){
        this.gitTool = gitTool;
        this.processBuilder = new ProcessBuilder();
        this.processBuilder.redirectErrorStream(true);
        this.processBuilder.directory(gitTool.getProcessBuilder().directory());
        this.commandExecutor = new UnixCommandExecutor(this.processBuilder);

        this.setLayout(new MigLayout("gap 0, insets 0, fillx, filly"));

        //initialize the output field for the terminal
        this.outputPane = new JTextPane();
        this.outputPane.setBorder(BorderFactory.createEmptyBorder());
        this.outputDoc = outputPane.getStyledDocument();
        //set the style of the ouput field
        Style style = this.outputPane.addStyle("style", null);
        this.outputPane.setBackground(Color.BLACK);
        StyleConstants.setForeground(style, Color.GREEN);
        this.outputPane.setEditable(false);

        //initialize the input field for the terminal
        this.inputField = new JTextField();
        this.inputField.setBorder(BorderFactory.createEmptyBorder());
        this.inputField.setBackground(Color.BLACK);
        this.inputField.setForeground(Color.GREEN);
        this.inputField.setCaretColor(Color.GREEN);

        //initialize the $ sign
        JTextField lineStart = new JTextField("$", 1);
        lineStart.setBorder(BorderFactory.createEmptyBorder());
        lineStart.setBackground(Color.BLACK);
        lineStart.setForeground(Color.GREEN);
        lineStart.setCaretColor(Color.GREEN);
        lineStart.setEditable(false);

        JScrollPane outPutPaneScroll = new JScrollPane(outputPane);
        outPutPaneScroll.setBorder(BorderFactory.createEmptyBorder());
        this.add(outPutPaneScroll, "width 100%, growy, pushy, spanx 2,  wrap");
        this.add(lineStart, "height pref");
        this.add(inputField, "height pref, growx");

        //add an action listener
        this.inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                String line = CommandLine.this.inputField.getText().trim();
                CommandLine.this.appendOutput("$ "+line);

                //if the line was not empty
                if(!line.equals("")){
                    String fragments [] = line.split("\\s+");
                    //if the user wants to change the directory
                    if(fragments[0].equalsIgnoreCase("cd")){
                        if(fragments.length > 1){
                            File destination;
                            //test if an absolute path was given
                            if(fragments[1].charAt(0) == '/'){
                                destination = new File(fragments[1]);
                            } else {
                                destination = new File(CommandLine.this.processBuilder.directory().getPath()+"/"+fragments[1]);
                            }
                            if(destination.isDirectory()){
                                CommandLine.this.processBuilder.directory(destination);
                            } else {
                                CommandLine.this.appendOutput(fragments[1]+": No such file or directory");
                            }
                        }
                    } else {
                        List<String> command = CommandLine.this.commandExecutor.splitIntoComponents(line);
                        List<String> commandOutput = CommandLine.this.commandExecutor.executeCommand(command);
                        CommandLine.this.appendOutput(commandOutput);
                    }
                }
                CommandLine.this.inputField.setText("");
                CommandLine.this.gitTool.refresh();
            }
        });

    }

    private void appendOutput(String output) {
        List<String> tmp = new ArrayList<String>(1);
        tmp.add(output);
        this.appendOutput(tmp);
    }

    private void appendOutput(List<String> lines) {
        StringBuilder text = new StringBuilder();
        if(!outputPane.getText().equals(""))
            text.append(System.getProperty("line.separator"));
        for(int i = 0; i<lines.size(); ++i){
            text.append(lines.get(i)+(i==lines.size()-1?"":System.getProperty("line.separator")));
        }
        try {
            outputDoc.insertString(outputDoc.getLength(), text.toString(), outputDoc.getStyle("style"));
        } catch (BadLocationException e) {
            System.err.println("Could not append command output to terminal.");
        }
        this.outputPane.setCaretPosition(outputDoc.getLength());
    }

    public void refresh () {

    }
}

//!important!
package application.git_tool.commandline;

import application.git_tool.GITTool;

import net.miginfocom.layout.*;
import net.miginfocom.swing.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.*;
import java.awt.Color;

public class CommandLine extends JPanel {
    
    private GITTool gitTool;
    
    //components
    private JTextPane outputPane;
    private StyledDocument outputDoc;
    private JTextField inputField;
    
    public CommandLine (GITTool gitTool){
        this.gitTool = gitTool;
        
        this.setLayout(new MigLayout("gap rel 0, fillx, filly"));
        
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
        
        JScrollPane outPutPaneScroll = new JScrollPane(outputPane);
        outPutPaneScroll.setBorder(BorderFactory.createEmptyBorder());
        this.add(outPutPaneScroll, "width 100%, growy, pushy,  wrap");
        this.add(inputField, "height pref, growx");
    }
    
    public void refresh () {
    
    }
}
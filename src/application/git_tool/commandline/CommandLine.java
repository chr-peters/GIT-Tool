//!important!
package application.git_tool.commandline;

import application.git_tool.GITTool;

import net.miginfocom.layout.*;
import net.miginfocom.swing.*;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.Color;

public class CommandLine extends JPanel {
    
    private GITTool gitTool;
    
    //components
    private JTextPane outputPane;
    private StyledDocument outputDoc;
    private JTextField inputField;
    
    public CommandLine (GITTool gitTool){
        this.gitTool = gitTool;
        
        this.setLayout(new MigLayout("debug, fillx, filly"));
        this.outputPane = new JTextPane();
        this.outputDoc = outputPane.getStyledDocument();
        Style style = this.outputPane.addStyle("style", null);
        this.outputPane.setBackground(Color.BLACK);
        StyleConstants.setForeground(style, Color.GREEN);
        this.outputPane.setEditable(false);
        
        this.add(new JScrollPane(outputPane), "width 100%, growy");
    }
    
    public void refresh () {
    
    }
}
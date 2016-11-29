//!important!
package application.git_tool.infomenu;

import application.git_tool.GITTool;
import application.git_tool.gitcommandexecutor.GITCommandExecutor;

import net.miginfocom.layout.*;
import net.miginfocom.swing.*;

import javax.swing.*;

public class InfoMenu extends JPanel {

    private GITTool gitTool;
    private GITCommandExecutor cmdExec;
    
    public InfoMenu (GITTool gitTool){
        this.gitTool = gitTool;
        this.cmdExec = new GITCommandExecutor(this.gitTool.getProcessBuilder());
        this.setLayout(new MigLayout());
    }
    
    public void refresh () {
    
    }
}

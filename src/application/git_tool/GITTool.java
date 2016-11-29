package application.git_tool;

import application.git_tool.filebrowser.*;
import application.git_tool.commandmenu.*;
import application.git_tool.commandline.*;
import application.git_tool.infomenu.*;

import net.miginfocom.layout.*;
import net.miginfocom.swing.*;

import javax.swing.*;

import java.io.File;

public class GITTool {

    //root frame
    private JFrame frame;
    //container of all components such as filebrowser, commandmenu,...
    private JPanel rootContainer;

    //components
    private FileBrowser fileBrowser;
    private CommandMenu commandMenu;
    private CommandLine commandLine;
    private InfoMenu infoMenu;

    //central instance of the process-builder to perform tasks within the system
    private ProcessBuilder processBuilder;

    public GITTool (){

        //set up process builder in the current directory
        this.processBuilder = new ProcessBuilder();
        //this way error output and standard output are merged together in the subprocesses
        this.processBuilder.redirectErrorStream(true);
        this.processBuilder.directory(new File("."));

        //create JFrame
        this.frame = new JFrame("GIT Tool");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //create layout and assign it to rootcontainer
        MigLayout layout = new MigLayout("debug");
        this.rootContainer = new JPanel(layout);

        //create components
        this.fileBrowser = new FileBrowser(this);
        this.commandMenu = new CommandMenu(this);
        this.commandLine = new CommandLine(this);
        this.infoMenu = new InfoMenu(this);

        //set up layout
        rootContainer.add(commandMenu, "width 25%, height 75%");
        rootContainer.add(fileBrowser, "width 50%, spany 2, growx, growy, height 100%");
        rootContainer.add(infoMenu, "width 25%, spany 3, growx, growy, wrap");
        rootContainer.add(new JLabel("History"), "spany 2, growx, growy, height 25%,wrap");
        rootContainer.add(commandLine, "growx, growy, skip, skip, skip");

        //set up JFrame
        frame.getContentPane().add(rootContainer);
        frame.setSize(1280, 720);
        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main (String args []){
        new GITTool();
    }

    //returns the instance of the process builder
    public ProcessBuilder getProcessBuilder(){
        return this.processBuilder;
    }
    
    public void refresh() {
        this.fileBrowser.refresh();
        //this.commandMenu.refresh();
        this.commandLine.refresh();
        this.infoMenu.refresh();
    }
}

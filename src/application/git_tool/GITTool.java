package application.git_tool;

import application.git_tool.filebrowser.*;
import application.git_tool.commandmenu.*;
import application.git_tool.commandline.*;
import application.git_tool.infomenu.*;
import application.git_tool.history.*;

import net.miginfocom.layout.*;
import net.miginfocom.swing.*;

import javax.swing.*;

import java.io.*;

import java.util.List;
import java.util.ArrayList;

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
    private History history;

    //central instance of the process-builder to perform tasks within the system
    private ProcessBuilder processBuilder;

    //this file contains the information about the current working directory
    private final File workingDirectoryInfo = new File("src/data/workingdirectory.info");

    //is the terminal currently enabled?
    private boolean terminalActive;

    public GITTool (){

        //set up process builder in the current directory
        this.processBuilder = new ProcessBuilder();
        //this way error output and standard output are merged together in the subprocesses
        this.processBuilder.redirectErrorStream(true);
        this.processBuilder.directory(getWorkingDirectory());

        //create JFrame
        this.frame = new JFrame("GIT Tool");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //create layout and assign it to rootcontainer
        MigLayout layout = new MigLayout();
        this.rootContainer = new JPanel(layout);

        //create components
        this.infoMenu = new InfoMenu(this);
        this.fileBrowser = new FileBrowser(this);
        this.commandMenu = new CommandMenu(this);
        this.commandLine = new CommandLine(this);
        this.history = new History(this, 10);

        //the terminal is disabled on startup
        this.terminalActive = false;

        //paint the components to the root container
        this.paintComponents();

        //set up JFrame
        frame.getContentPane().add(rootContainer);
        frame.setSize(1280, 720);
        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        //check if all necessary components are installed
        this.checkComponents();

        //add shutdown routine
        Runtime.getRuntime().addShutdownHook(new Thread (new Runnable () {
            public void run () {
                try {
                    //if the directory of the info file does not exist, create it
                    if(!GITTool.this.workingDirectoryInfo.getParentFile().exists()){
                        GITTool.this.workingDirectoryInfo.getParentFile().mkdirs();
                    }
                    //write the current working directory to the workingDirectoryInfo file
                    BufferedWriter writer = new BufferedWriter(new FileWriter(workingDirectoryInfo, false));
                    writer.write(GITTool.this.getProcessBuilder().directory().getAbsolutePath());
                    writer.close();
                } catch (IOException e) {
                    //printing to stderr because this is a shutdown routine and the user won't be
                    //able to respond in time
                    System.err.println("Could not save the current working directory.");
                }
            }
        }));
    }

    public static void main (String args []){
        new GITTool();
    }

    private void checkComponents(){
        //try to execute the git --version command and check the exit code
        CommandExecutor c = new CommandExecutor(this, this.processBuilder);
        List<String> command = new ArrayList<String>(2);
        command.add("git");
        command.add("--version");
        c.executeCommand(command);
        int exitCode = c.getLastExitCode();
        if(exitCode != 0){
            this.errorMessage("Git is not installed on your system.", "Error");
        }

    }

    private void paintComponents(){
        rootContainer.removeAll();
        //set up layout
        if(this.terminalActive){
            rootContainer.add(commandMenu, "width 25%, height 75%");
            rootContainer.add(fileBrowser, "width 50%, spany 2, growx, growy, height 100%");
            rootContainer.add(infoMenu, "width 25%, spany 3, growx, growy, wrap");
            rootContainer.add(new JScrollPane(history), "spany 2, growx, growy, height 25%,wrap");
            rootContainer.add(commandLine, "growx, growy, skip, skip, skip");
        } else {
            rootContainer.add(commandMenu, "width 25%, height 75%");
            rootContainer.add(fileBrowser, "width 50%, spany 2, growx, growy, height 100%");
            rootContainer.add(infoMenu, "width 25%, spany 2, growx, growy, wrap");
            rootContainer.add(new JScrollPane(history), "growx, growy, height 25%");
        }
        rootContainer.revalidate();
        rootContainer.repaint();
    }

    /**
    * Toggles the terminal.
    */
    public void toggleTerminal(){
        this.terminalActive = !this.terminalActive;
        this.paintComponents();
    }

    /**
    * returns a reference to the process builder
    */
    public ProcessBuilder getProcessBuilder(){
        return this.processBuilder;
    }

    /**
    * Refreshes every component of the git-tool.
    */
    public void refresh() {
        this.fileBrowser.refresh();
        //this.commandMenu.refresh();
        this.commandLine.refresh();
        this.infoMenu.refresh();
        this.history.refresh();
    }

    /**
    * Displays an error message to the user.
    *
    * @param message The message to be shown. Use \n to break lines.
    * @param title The title of the message.
    */
    public void errorMessage(String message, String title) {
        JOptionPane.showMessageDialog(this.frame, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
    * Displays an error message to the user
    *
    * @param message The lines of the message to be shown. The line separator
    *                is added after each line automatically
    * @param title The title of the message
    */
    public void errorMessage(List<String> message, String title) {
        StringBuilder msg = new StringBuilder();
        for(String line: message){
            msg.append(line+System.getProperty("line.separator"));
        }
        this.errorMessage(msg.toString(), title);
    }

    /**
    * Displays an information message to the user
    *
    * @param message The message to be shown. Use \n to break lines.
    * @param title The title of the message.
    */
    public void infoMessage(String message, String title) {
        JOptionPane.showMessageDialog(this.frame, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
    * Displays an information message to the user
    *
    * @param message The lines of the message to be shown.
    * @param title The title of the message
    */
    public void infoMessage(List<String> message, String title) {
        StringBuilder msg = new StringBuilder();
        for(String line: message){
            msg.append(line+System.getProperty("line.separator"));
        }
        this.infoMessage(msg.toString(), title);
    }

    /**
    * Returns a reference to the command history.
    * <p>
    * Enables other components to access the history conveniently.
    */
    public History getHistory(){
        return this.history;
    }

    /**
    * Returns a reference to the info menu.
    * <p>
    * Enables other components to access the info menu conveniently.
    */
    public InfoMenu getInfoMenu(){
        return this.infoMenu;
    }

    //retrieve the last working directory from the workingDirectoryInfo file
    private File getWorkingDirectory() {
        try {
            //if the directory of the info file does not exist, create it
            if(!this.workingDirectoryInfo.getParentFile().exists()){
                this.workingDirectoryInfo.getParentFile().mkdirs();
            }
            BufferedReader reader = new BufferedReader(new FileReader(this.workingDirectoryInfo));
            String workingDirectory = reader.readLine();
            reader.close();
            File f = new File(workingDirectory);
            if(f.isDirectory())
                return f;
            return new File(".");
        } catch (IOException e) {
            return new File(".");
        }
    }
}

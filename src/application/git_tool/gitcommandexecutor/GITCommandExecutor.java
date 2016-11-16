package application.git_tool.gitcommandexecutor;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class GITCommandExecutor {

    //instance of the process-builder
    private ProcessBuilder processBuilder;

    public GITCommandExecutor(ProcessBuilder p){
        this.processBuilder = p;
    }
    
    public static void main(String args []) {
        ProcessBuilder p = new ProcessBuilder();
        p.directory(new File("../TestRepository"));
        GITCommandExecutor commandExecutor = new GITCommandExecutor(p);
        System.out.println(commandExecutor.init(false));
        System.out.println(commandExecutor.add(false, false, false, false, "addtest.txt"));
    }
    
    /**
    * Performs the "git init --quiet" command in the current directory of the processBuilder.
    * --quiet ensures that only error and warning messages are printed
    * 
    * @param bare true, if the --bare option is to be added
    * @return Error or warning-message
    */
    public String init(boolean bare){
        //generate the command from the options
        List<String> command = new ArrayList<String>(8);
        command.add("git");
        command.add("init");
        if(bare)
            command.add("--bare");
        command.add("--quiet");
        
        //execute the command
        try {
            this.processBuilder.command(command);
            Process p = this.processBuilder.start();
            
            //read the output
            return getProcessOutput(p);

        }
        catch (IOException e){
            return e.getMessage();
        }
    }
    
    /**
    * Performs the "git add" command with the given options in the current directory of the processBuilder
    *
    * @param force true, if the --force option is to be added
    * @param update true, if the --update option is to be added
    * @param all true, if the --all option is to be added
    * @param ignore_errors true, if the --ignore-errors option is to be added
    * @param pathspec files to add content from. See Git-Documentation for more details
    * @return Any output of the command
    */
    public String add(boolean force, boolean update, boolean all, boolean ignore_errors, String pathspec){
        //generate the command from the options
        List<String> command = new ArrayList<String>(8);
        command.add("git");
        command.add("add");
        if(force)
            command.add("--force");
        if(update)
            command.add("--update");
        if(all)
            command.add("--all");
        if(ignore_errors)
            command.add("--ignore-errors");
        command.add("--");
        command.add(pathspec);
        
        //execute the command
        try {
            this.processBuilder.command(command);
            Process p = this.processBuilder.start();
            
            //read the output
            return getProcessOutput(p);
        } catch (IOException e) {
            return e.getMessage();
        }
    }
    
    //returns the output of a process
    private String getProcessOutput(Process p) throws IOException{
        InputStream output = p.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(output));
        StringBuilder res = new StringBuilder();
        String line = "";
        while ((line=reader.readLine()) != null){
            res.append(line);
        }
        
        //if any errors or warnings occured, the output is returned
        return res.toString();
    }

}
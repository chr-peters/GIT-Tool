package application.git_tool.gitcommandexecutor;

import java.io.*;

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
        System.out.println(commandExecutor.init());
    }
    
    /**
    * Performs the "git init -q" command in the current directory of the processBuilder.
    * -q makes sure that only error and warning messages are be printed
    * 
    * @return Error or warning-message
    */
    public String init(){
        try {
            //perform the command
            this.processBuilder.command("git", "init", "-q");
            Process p = this.processBuilder.start();
            
            //read the output
            InputStream output = p.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(output));
            StringBuilder res = new StringBuilder();
            String line = "";
            //TODO use StringBuilder
            while ((line=reader.readLine()) != null){
                res.append(line);
            }
            
            //if any errors or warnings occured, the output is returned
            return res.toString();
        }
        catch (IOException e){
            return e.getMessage();
        }
    }

}
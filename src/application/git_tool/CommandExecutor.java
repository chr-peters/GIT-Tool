package application.git_tool;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class CommandExecutor {

    //instance of the process-builder
    private ProcessBuilder processBuilder;

    //a flag for the last exit code of a command
    private int lastExitCode;

    public CommandExecutor(ProcessBuilder p){
        this.processBuilder = p;
        this.lastExitCode = 0;
    }
    
    //use this to get the last exit code as it resets it afterwards
    public int getLastExitCode(){
        int exitCode = this.lastExitCode;
        this.lastExitCode = 0;
        return exitCode;
    }

    //executes a given command in the local processBuilder
    public List<String> executeCommand(List<String> params) {
        try {
            this.processBuilder.command(params);
            Process p = this.processBuilder.start();
            //set the last exit code
            this.lastExitCode = p.waitFor();

            //read the output
            return getProcessOutput(p);
        } catch (IOException e) {
            List<String> res = new ArrayList<String>();
            res.add(e.getMessage());
            return res;
        } catch (InterruptedException e){
            List<String> res = new ArrayList<String>();
            res.add(e.getMessage());
            return res;
        }
    }

    //returns the lines of the output of a process
    private List<String> getProcessOutput(Process p) throws IOException{
        InputStream output = p.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(output));
        List<String> res = new ArrayList<String>();
        String line = "";
        while ((line=reader.readLine()) != null){
            res.add(line);
        }
        return res;
    }
}
package application.git_tool;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

import application.git_tool.GITTool;
import application.git_tool.history.*;

public class CommandExecutor {

    private GITTool gitTool;
    //instance of the process-builder
    protected ProcessBuilder processBuilder;
    //a flag for the last exit code of a command
    protected int lastExitCode;

    public CommandExecutor(GITTool gitTool, ProcessBuilder p){
        this.gitTool = gitTool;
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
      StringBuilder cmdString = new StringBuilder();
      for(String s: params) {
          cmdString.append(s+" ");
      }
      this.gitTool.getHistory().addCommand(new Command(cmdString.toString()));
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

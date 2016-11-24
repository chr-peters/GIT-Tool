package application.git_tool.unixcommandexecutor;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class UnixCommandExecutor {
    private ProcessBuilder processBuilder;
    
    public UnixCommandExecutor(ProcessBuilder p) {
        this.processBuilder = p;
    }
    
    public List<String> chmod(String rights, List<File> files) {
        ArrayList<String> command = new ArrayList<String>();
        command.add("chmod");
        command.add(rights.trim());
        command.add("--");
        for(File f: files) {
            command.add(f.getName());
        }
        return executeCommand(command);
    }
    
    public List<String> cp(String dest, List<File> files) {
        ArrayList<String> command = new ArrayList<String>();
        command.add("cp");
        command.add("-r");
        command.add("--");
        for(File f: files) {
            command.add(f.getName());
        }
        command.add(dest.trim());
        return executeCommand(command);
    }
    
    //splits the given string into its components
    private List<String> splitIntoComponents(String inputString) {
        ArrayList<String> components = new ArrayList<String>();
        Matcher m = Pattern.compile("(\"(\\\\\"|.)*?\"|'(\\\\\'|.)*?'|(\\\\ |\\S)*)").matcher(inputString);
        while(m.find()) {
            if(m.group(0).length()>0) {
                components.add(m.group(0));
            }
        }
        return components;
    }
    
    //executes a given command in the local processBuilder
    private List<String> executeCommand(List<String> params) {
        try {
            this.processBuilder.command(params);
            Process p = this.processBuilder.start();
            
            //read the output
            return getProcessOutput(p);
        } catch (IOException e) {
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

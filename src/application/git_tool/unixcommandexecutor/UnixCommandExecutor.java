package application.git_tool.unixcommandexecutor;

import application.git_tool.GITTool;
import application.git_tool.CommandExecutor;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class UnixCommandExecutor extends CommandExecutor {
    
    public UnixCommandExecutor(ProcessBuilder p) {
        super(p);
    }
    
    public List<String> chmod(String rights, List<File> files) {
        List<String> command = new ArrayList<String>();
        command.add("chmod");
        for(String s: this.splitIntoComponents(rights)) {
            if(!s.equals("chmod")) {
                command.add(s);
            }
        }
        for(File f: files) {
            command.add(f.getName());
        }
        List<String> res = executeCommand(command);
        if(this.getLastExitCode()!=0) {
            res.add(0, "ERRORERRORERROR");
        }
        return res;
    }
    
    public List<String> cp(String dest, List<File> files) {
        List<String> command = new ArrayList<String>();
        command.add("cp");
        command.add("-r");
        List<String> splitted = this.splitIntoComponents(dest);
        for(int i=0; i<splitted.size(); i++) {
            if(!splitted.get(i).equals("cp") && !splitted.get(i).equals("-r") && (i!=splitted.size()-1 || command.contains("-t"))) {
                command.add(splitted.get(i));
            }
        }
        for(File f: files) {
            command.add(f.getName());
        }
        if(!command.contains("-t") && splitted.size()!=0) {
            command.add(splitted.get(splitted.size()-1));
        }
        List<String> res = executeCommand(command);
        if(this.getLastExitCode()!=0) {
            res.add(0, "ERRORERRORERROR");
        }
        return res;
    }
    
    public List<String> find(String pattern) {
        List<String> command = new ArrayList<String>();
        command.add("find");
        for(String s: this.splitIntoComponents(pattern)) {
            if(!s.equals("find")) {
                command.add(s);
            }
        }
        List<String> res = executeCommand(command);
        if(this.getLastExitCode()!=0) {
            res.add(0, "ERRORERRORERROR");
        }
        return res;
    }
    
    public List<String> mkdir(String name) {
        List<String> command = new ArrayList<String>();
        command.add("mkdir");
        for(String s: this.splitIntoComponents(name)) {
            if(!s.equals("mkdir")) {
                command.add(s);
            }
        }
        List<String> res = executeCommand(command);
        if(this.getLastExitCode()!=0) {
            res.add(0, "ERRORERRORERROR");
        }
        return res;
    }
    
    public List<String> mv(String dest, List<File> files) {
        List<String> command = new ArrayList<String>();
        command.add("mv");
        List<String> splitted = this.splitIntoComponents(dest);
        for(int i=0; i<splitted.size(); i++) {
            if(!splitted.get(i).equals("mv") && (i!=splitted.size()-1 || command.contains("-t"))) {
                command.add(splitted.get(i));
            }
        }
        for(File f: files) {
            command.add(f.getName());
        }
        if(!command.contains("-t") && splitted.size()!=0) {
            command.add(splitted.get(splitted.size()-1));
        }
        List<String> res = executeCommand(command);
        if(this.getLastExitCode()!=0) {
            res.add(0, "ERRORERRORERROR");
        }
        return res;
    }
    
    public List<String> rm(List<File> files) {
        List<String> command = new ArrayList<String>();
        command.add("rm");
        command.add("-r");
        command.add("-f");
        for(File f: files) {
            command.add(f.getName());
        }
        List<String> res = executeCommand(command);
        if(this.getLastExitCode()!=0) {
            res.add(0, "ERRORERRORERROR");
        }
        return res;
    }
    
    public List<String> rmdir(List<File> files) {
        List<String> command = new ArrayList<String>();
        command.add("rmdir");
        for(File f: files) {
            command.add(f.getName());
        }
        List<String> res = executeCommand(command);
        if(this.getLastExitCode()!=0) {
            res.add(0, "ERRORERRORERROR");
        }
        return res;
    }
    
    public List<String> tar(String name, List<File> files) {
        List<String> command = new ArrayList<String>();
        command.add("tar");
        for(String s: this.splitIntoComponents(name)) {
            if(!s.equals("tar")) {
                command.add(s);
            }
        }
        for(File f: files) {
            command.add(f.getName());
        }
        List<String> res = executeCommand(command);
        if(this.getLastExitCode()!=0) {
            res.add(0, "ERRORERRORERROR");
        }
        return res;
    }
    
    public List<String> touch(String name, List<File> files) {
        List<String> command = new ArrayList<String>();
        command.add("touch");
        for(String s: this.splitIntoComponents(name)) {
            if(!s.equals("touch")) {
                command.add(s);
            }
        }
        for(File f: files) {
            command.add(f.getName());
        }
        List<String> res = executeCommand(command);
        if(this.getLastExitCode()!=0) {
            res.add(0, "ERRORERRORERROR");
        }
        return res;
    }
    
    public List<String> wget(String address) {
        List<String> command = new ArrayList<String>();
        command.add("wget");
        for(String s: this.splitIntoComponents(address)) {
            if(!s.equals("wget")) {
                command.add(s);
            }
        }
        List<String> res = executeCommand(command);
        if(this.getLastExitCode()!=0) {
            res.add(0, "ERRORERRORERROR");
        }
        return res;
    }
    
    //splits the given string into its components
    public List<String> splitIntoComponents(String inputString) {
        List<String> components = new ArrayList<String>();
        Matcher m = Pattern.compile("(\"(\\\\\"|.)*?\"|'(\\\\\'|.)*?'|(\\\\ |\\S)*)").matcher(inputString);
        while(m.find()) {
            if(m.group(0).length()>0) {
                components.add(m.group(0));
            }
        }
        return components;
    }
}

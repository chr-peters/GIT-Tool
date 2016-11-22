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
        System.out.println(commandExecutor.reset("", ""));
        //System.out.println(commandExecutor.commit(false, false, "", ""));
    }
    
    /**
    * Performs the "git init --quiet" command in the current directory of the processBuilder.
    * --quiet ensures that only error and warning messages are printed
    * 
    * @param bare true, if the --bare option is to be added
    * @return Error or warning-message, "" if the execution was successful
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
        return executeCommand(command);
    }
    
    /**
    * Performs the "git add" command with the given options in the current directory of the processBuilder.
    *
    * @param force allow adding otherwise ignored files
    * @param update updates changes made to files that already existed but does not add new files
    * @param all add, modify or remove every index entry that does not match the working tree
    * @param ignore_errors proceed adding subsequent files even if errors occured
    * @param pathspec files to add content from. See Git-Documentation for more details
    * @return Any output of the command, "" if the execution was successful
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
        //separate file names from option names
        command.add("--");
        command.add(pathspec);
        
        //execute the command
        return executeCommand(command);
    }
    
    /**
    * Performs the "git rm --quiet" command with the given options in the current directory of the processBuilder.
    * --quiet ensures that only error and warning messages are printed
    *
    * @param force don't check if the files in the working tree are up-to-date with the tip of the branch and just delete them
    * @param r allow recursive removal when a leading directory name is given
    * @param cached only remove the files from the index but not from the working tree
    * @param file files to remove
    * @return Any output of the command, "" if the execution was successful
    */
    public String rm(boolean force, boolean r, boolean cached, String file) {
        //generate the command from the options
        List<String> command = new ArrayList<String>(8);
        command.add("git");
        command.add("rm");
        if(force)
            command.add("--force");
        if(r)
            command.add("-r");
        if(cached)
            command.add("--cached");
        command.add("--quiet");
        //separate file names from option names
        command.add("--");
        command.add(file);
        
        //execute the command
        return executeCommand(command);
    }
    
    /**
    * Performs the "git checkout --quiet" command to switch to the given branch or the given commit
    *
    * @param branchOrcommit the branch or commit that is checked out
    * @return any output of the command, "" if the execution was successful
    */
    public String checkout(String branchOrCommit) {
        //generate the command from the options
        List<String> command = new ArrayList<String>(4);
        command.add("git");
        command.add("checkout");
        command.add("--quiet");
        command.add(branchOrCommit);
        
        //execute the command
        return executeCommand(command);
    }
    
    /**
    * Performs the "git clone --quiet" command to clone an existing repository into a directory
    *
    * @param repository the (possibly remote) and correctly specified repository to clone from
    * @param diretory the name of a new directory to clone into. If the repository is to be cloned
    *                 into the current directory, just leave the string empty
    * @return any output of the command, "" if the execution was successful
    */
    public String clone(String repository, String directory) {
        //generate the command from the options
        List<String> command = new ArrayList<String>(6);
        command.add("git");
        command.add("clone");
        command.add("--quiet");
        command.add("--");
        command.add(repository);
        if (!directory.equals(""))
            command.add(directory);
        
        //execute the command
        return executeCommand(command);
    }
    
    /**
    * Performs the "git commit --quiet" command to store the current contents of the index in a new commit with a log message
    *
    * @param all automatically stage all files that have been modified or deleted
    * @param amend amend new changes to the last commit instead of creating a new one
    * @param commitMessage the corresponding log message
    * @param file (optional, just enter "" if not desired) only commit and stage the most recent contents of the specified files
    * @return any output of the command, "" if the execution was successful
    */
    public String commit(boolean all, boolean amend, String commitMessage, String file) {
        //generate the command from the options
        List<String> command = new ArrayList<String>(9);
        command.add("git");
        command.add("commit");
        command.add("--quiet");
        if(all)
            command.add("--all");
        if(amend)
            command.add("--amend");
        command.add("-m");
        if(commitMessage.equals(""))
            return "No commit-message specified!";
        command.add(commitMessage);
        if(!file.equals("")){
            command.add("--");
            command.add(file);
        }
        
        //execute the command
        return executeCommand(command);
    }
    
    /**
    * Performs the "git reset --quiet" command to reset the current HEAD to the specified state
    *
    * @param treeish something treeish, even a commit works fine. Leave at "" for HEAD
    * @param paths all directories which will be affected by the reset. Leave at "" for every directory. But be careful:
    *              specifying this option together with a commit will most likely lead to errors.
    * @return any output of the command, "" if the execution was successful
    */
    public String reset(String treeish, String paths) {
        //generate the command from the options
        List<String> command = new ArrayList<String>(6);
        command.add("git");
        command.add("reset");
        command.add("--quiet");
        if(!treeish.equals(""))
            command.add(treeish);
        if(!paths.equals("")){
            command.add("--");
            command.add(paths);
        }
        
        //execute the command
        return executeCommand(command);
    }
    
    /**
    * Performs the "git fetch --quiet" command to download objects and refs from another repository
    *
    * @return any output of the command, "" if the execution was successful
    */
    public String fetch() {
        //generate the command from the options
        List<String> command = new ArrayList<String>(3);
        command.add("git");
        command.add("fetch");
        command.add("--quiet");
        
        //execute the command
        return executeCommand(command);
    }
    
    //executes a given command in the local processBuilder
    private String executeCommand(List<String> params) {
        try {
            this.processBuilder.command(params);
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
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
    * @return Lines of the process-output. The list is empty, if everything worked well.
    */
    public List<String> init(boolean bare){
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
    * @return Lines of the process-output. The list is empty, if everything worked well.
    */
    public List<String> add(boolean force, boolean update, boolean all, boolean ignore_errors, String pathspec){
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
    * @return Lines of the process-output. The list is empty, if everything worked well.
    */
    public List<String> rm(boolean force, boolean r, boolean cached, String file) {
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
    * @return Lines of the process-output. The list is empty, if everything worked well.
    */
    public List<String> checkout(String branchOrCommit) {
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
    * @return Lines of the process-output. The list is empty, if everything worked well.
    */
    public List<String> clone(String repository, String directory) {
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
    * @return Lines of the process-output. The list is empty, if everything worked well.
    */
    public List<String> commit(boolean all, boolean amend, String commitMessage, String file) {
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
        if(commitMessage.equals("")) {
            List<String> res = new ArrayList<String>();
            res.add("No commit-message specified!");
            return res;
        }
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
    * @return Lines of the process-output. The list is empty, if everything worked well.
    */
    public List<String> reset(String treeish, String paths) {
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
    * @return Lines of the process-output. The list is empty, if everything worked well.
    */
    public List<String> fetch() {
        //generate the command from the options
        List<String> command = new ArrayList<String>(3);
        command.add("git");
        command.add("fetch");
        command.add("--quiet");
        
        //execute the command
        return executeCommand(command);
    }
    
    /**
    * Performs the "git push --quiet" command to update remote refs along with associated objects
    *
    * @param repository The repository that is pushed into. Usually a remote name is given, if left empty,
    *                   it defaults to origin.
    * @param refspec    Specification of what to push where. See the documentation for more details. It can be left empty.
    * @return Lines of the process-output. The list is empty, if everything worked well.
    */
    public List<String> push(String repository, String refspec) {
        //generate the command from the options
        List<String> command = new ArrayList<String>(5);
        command.add("git");
        command.add("push");
        command.add("--quiet");
        if(!repository.equals(""))
            command.add(repository);
        if(!refspec.equals(""))
            command.add(refspec);
        
        //execute the command
        return executeCommand(command);
    }
    
    /**
    * Performs the "git pull --quiet" command to fetch information from another repository and incorporate them into the current branch
    *
    * @param repository Name of the remote repository. If left empty, it defaults to origin.
    * @param refspec    Name of a remote reference that is pulled from which is usually master. It can be left empty.
    *
    * @return Lines of the process-output. The list is empty, if everything worked well.
    */
    public List<String> pull(String repository, String refspec) {
        //generate the command from the options
        List<String> command = new ArrayList<String>(5);
        command.add("git");
        command.add("pull");
        command.add("--quiet");
        if(!repository.equals(""))
            command.add(repository);
        if(!refspec.equals(""))
            command.add(refspec);
        
        //execute the command
        return executeCommand(command);
    }
    
    /**
    * Performs the "git merge --no-edit --quiet" command to join two development histories together
    * 
    * --no-edit ensures that the merge-message is created automatically
    *
    * @param commit Usually the name of another branch that is merged from. If left empty, the corresponding
    *               remote-tracking branch is used as the source.
    * @return The MergeResponse object stores all details about the merge. See its documentation for further details.
    */
    public MergeResponse merge(String commit) {
        //generate the command from the options
        List<String> command = new ArrayList<String>(5);
        command.add("git");
        command.add("merge");
        command.add("--no-edit");
        command.add("--quiet");
        if(!commit.equals(""))
            command.add(commit);
        
        //execute the command
        List<String> res = executeCommand(command);
        
        if (res.size() == 0){
            //the merge was fast forward
            return new MergeResponse(MergeResponse.States.FAST_FORWARD, res);
        } else {
            //the merge was not fast forward
            
            //test if the merge was successful
            for (String line: res){
                if(!line.startsWith("Auto-merging")) {
                    //there is a conflict somewhere
                    return new MergeResponse(MergeResponse.States.OPEN_CONFLICTS, res);
                }
            }
            //git was able to auto-merge every file without conflict
            return new MergeResponse(MergeResponse.States.RESOLVED_CONFLICTS, res);
        }
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
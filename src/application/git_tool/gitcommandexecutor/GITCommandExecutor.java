package application.git_tool.gitcommandexecutor;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class GITCommandExecutor {

    //instance of the process-builder
    private ProcessBuilder processBuilder;

    //a flag for the last exit code of a command
    private int lastExitCode;

    public GITCommandExecutor(ProcessBuilder p){
        this.processBuilder = p;
        this.lastExitCode = 0;
    }

    public static void main(String args []) {
        ProcessBuilder p = new ProcessBuilder();
        p.redirectErrorStream(true);
        p.directory(new File("../TestRepository"));
        GITCommandExecutor commandExecutor = new GITCommandExecutor(p);
//         System.out.println(commandExecutor.init(false));
//         System.out.println(commandExecutor.add(false, false, false, false, "addtest.txt"));
//         System.out.println(commandExecutor.reset("", ""));
        //System.out.println(commandExecutor.commit(false, false, "", ""));
        try {
            StatusContainer res = commandExecutor.status();
            System.out.println(res);
        } catch (GitCommandException e) {
            System.out.println(e.getMessage());
        }
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
    * @param force allows adding otherwise ignored files
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
        if(!pathspec.equals("")){
            command.add("--");
            command.add(pathspec);
        }
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

    /**
    * Performs the "git branch --quiet" command to create a new branch
    *
    * @param branchName the name of the new branch that is to be created
    *
    * @return Lines of the process-output. The list is empty, if everything worked well.
    */
    public List<String> createBranch (String branchName) {
        //generate the command from the options
        List<String> command = new ArrayList<String>(4);
        command.add("git");
        command.add("branch");
        command.add("--quiet");
        command.add(branchName);

        //execute the command
        return executeCommand(command);
    }

    /**
    * Performs the "git branch -d --quiet" command to delete a branch
    *
    * @param branchName the name of the branch that is to be deleted
    *
    * @return Lines of the process-output. The list is empty, if everything worked well.
    */
    public List<String> deleteBranch (String branchName) {
        //generate the command from the options
        List<String> command = new ArrayList<String>(5);
        command.add("git");
        command.add("branch");
        command.add("-d");
        command.add("--quiet");
        command.add(branchName);

        //execute the command
        return executeCommand(command);
    }

    /**
    * Performs the "git branch -m --quiet" command to rename oldbranch to newbranch
    *
    * @param oldBranch the name of the branch that is to be renamed. leave at "" to rename the current branch
    * @param newBranch the new name for the branch
    *
    * @return Lines of the process-output. The list is empty, if everything worked well.
    */
    public List<String> renameBranch (String oldBranch, String newBranch) {
        //generate the command from the options
        List<String> command = new ArrayList<String>(6);
        command.add("git");
        command.add("branch");
        command.add("-m");
        command.add("--quiet");
        if(!oldBranch.equals(""))
            command.add(oldBranch);
        command.add(newBranch);

        //execute the command
        return executeCommand(command);
    }

    /**
    * Performs the "git branch --all" command to list all branches
    * <p>
    * --all ensures that the remote tracking branches are listed as well
    *
    * @return One branchname per list-element.
    */
    public List<String> listBranches() {
        //generate the command from the options
        List<String> command = new ArrayList<String>(3);
        command.add("git");
        command.add("branch");
        command.add("--all");

        //execute the command
        return executeCommand(command);
    }

    /**
    * Performs the "git tag --annotate" command to create an annotated tag with a given message
    *
    * @param message the mandatory tag message
    * @param tagName the name of the tag that is to be created
    * @param commit the commit the tag will refer to. Leave at "" for default behavior.
    *
    * @return Lines of the process-output. The list is empty, if everything worked well.
    */
    public List<String> createTag(String message, String tagName, String commit){
        //generate the command from the options
        List<String> command = new ArrayList<String>(7);
        command.add("git");
        command.add("tag");
        command.add("--annotate");
        command.add("-m");
        if(message.equals("")){
            List<String> res = new ArrayList<String>(1);
            res.add("Error: No tag-message specified");
            return res;
        }
        command.add(message);
        command.add(tagName);
        if(!commit.equals(""))
            command.add(commit);

        //execute the command
        return executeCommand(command);
    }

    /**
    * Performs the "git tag -d" command to delete a given tag
    *
    * @param tagName the name of the tag that is to be deleted
    *
    * @return Lines of the process-output. The list is empty, if everything worked well.
    */
    public List<String> deleteTag(String tagName){
        //generate the command from the options
        List<String> command = new ArrayList<String>(4);
        command.add("git");
        command.add("tag");
        command.add("-d");
        command.add(tagName);

        //execute the command
        List<String> res = executeCommand(command);

        //check if everything went well
        for (String line: res){
            //if a line of this output does not start with "Deleted", something went wrong
            if(!line.startsWith("Deleted"))
                return res;
        }

        //everything went well, so just return an empty list
        res.clear();
        return res;
    }

    /**
    * Performs the "git tag" command to list all tags
    *
    * @return One tagname per list-element.
    */
    public List<String> listTags() {
        //generate the command from the options
        List<String> command = new ArrayList<String>(2);
        command.add("git");
        command.add("tag");

        //execute the command
        return executeCommand(command);
    }
    /**
    * Performs the "git --no-pager log" command to list all the commits for a given path
    *
    * @param path the path for which to list the commits. Leave at "" to list all commits for the project
    *
    * @throws GitLogException if an error occured, see the message of the gitlogexception
    *
    * @return a list of all the commits
    */
    public List<Commit> log(String path) throws GitCommandException{
        //generate the command from the options
        List<String> command = new ArrayList<String>(4);
        command.add("git");
        command.add("--no-pager");
        command.add("log");
        if(!path.equals(""))
            command.add(path);

        //execute the command
        List<String> res = executeCommand(command);

        //the endline character of the current system
        String n = System.getProperty("line.separator");

        //test if something went wrong
        if (getLastExitCode() != 0) {
            //something went wrong, so just put the output into the GitCommandException
            StringBuilder msg = new StringBuilder();
            for(String line: res){
                msg.append(line+n);
            }
            throw new GitCommandException(msg.toString());
        }

        //everything went well, so just parse the commits
        List<Commit> commits = new ArrayList<Commit>();
        String curHash;
        String curAuthor;
        String curDate;
        StringBuilder curMessage;
        String curMerge;
        String curLine;
        while (res.size() > 0) {
            //get hash
            curHash = (res.remove(0).split("\\s+")[1]).trim();
            //test if merge attribute exists and if so, add it
            curLine = res.remove(0);
            if(curLine.startsWith("Merge:")){
                curMerge = (curLine.split("Merge:")[1]).trim();
                curAuthor = (res.remove(0).split("Author:")[1]).trim();
            } else {
                curMerge = "";
                curAuthor = (curLine.split("Author:")[1]).trim();
            }
            //get the date
            curDate = (res.remove(0).split("Date:")[1]).trim();

            //reset the old commit message
            curMessage = new StringBuilder();
            //get the commit message
            while(res.size() > 0 && !(curLine = res.get(0)).startsWith("commit")) {
                curLine = res.remove(0).trim();
                if(!curLine.equals(""))
                    curMessage.append(curLine+n);
            }
            //add the commit-object to the list
            commits.add(new Commit(curHash, curAuthor, curDate, curMerge, curMessage.toString().trim()));
        }
        return commits;
    }
    
    /**
    * Performs the "git status --porcelain" command to fetch information about the working tree's status
    * <p>
    * --porcelain makes the output of the command parseable
    *
    * @return A container for all the status information. See its documentation for more details.
    */
    public StatusContainer status() throws GitCommandException{
        //generate the command from the options
        List<String> command = new ArrayList<String>(3);
        command.add("git");
        command.add("status");
        command.add("--porcelain");
        
        //execute the command
        List<String> res = executeCommand(command);
        
        //the endline character of the current system
        String n = System.getProperty("line.separator");
        
        //test if something went wrong
        if (getLastExitCode() != 0) {
            //something went wrong, so just put the output into the GitCommandException
            StringBuilder msg = new StringBuilder();
            for(String line: res){
                msg.append(line+n);
            }
            throw new GitCommandException(msg.toString());
        }
        
        //initialize the containers
        StatusFiles staged = new StatusFiles();
        StatusFiles unstaged = new StatusFiles();
        for(String line: res){
            parseStatusCode(line.charAt(0), line.trim(), staged);
            parseStatusCode(line.charAt(1), line.trim(), unstaged);
        }
        
        //return the result
        return new StatusContainer(staged, unstaged);
    }
    
    //more convenient parsing of the status codes
    private void parseStatusCode(char code, String line, StatusFiles dest) {
        //split the line into its core components
        String [] parts = line.split("\\s+");
        //check the status code
        switch ( code ) {
            case 'M': {
                //the file was modified
                dest.getModifiedFiles().add(parts[1]);
                break;
            } case 'A': {
                //the file was added
                dest.getNewFiles().add(parts[1]);
                break;
            } case 'D': {
                //the file was deleted
                dest.getDeletedFiles().add(parts[1]);
                break;
            } case 'R': {
                //the file was renamed
                dest.getRenamedFiles().add(parts[1] + " -> " + parts[3]);
                break;
            } case 'C': {
                //the file was copied
                dest.getCopiedFiles().add(parts[1]);   
                break;
            } case 'U': {
                //the file was updated but unmerged
                dest.getUpdatedFiles().add(parts[1]);  
                break;
            } case '?': {
                //the file was untracked
                dest.getUntrackedFiles().add(parts[1]);  
                break;
            } default: {
                break;
            }
        }
    }
    
    //use this to get the last exit code as it resets it afterwards
    private int getLastExitCode(){
        int exitCode = this.lastExitCode;
        this.lastExitCode = 0;
        return exitCode;
    }

    //executes a given command in the local processBuilder
    private List<String> executeCommand(List<String> params) {
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

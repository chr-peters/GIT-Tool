//!important!
package application.git_tool.infomenu;

import application.git_tool.GITTool;
import application.git_tool.gitcommandexecutor.*;

import net.miginfocom.layout.*;
import net.miginfocom.swing.*;

import java.io.*;
import java.util.*;

import javax.swing.*;

public class InfoMenu extends JPanel {

    private GITTool gitTool;
    private GITCommandExecutor executor;
    private int lastExitCode;
    private JLabel name;
    private JTextArea remote;
    private JLabel branch;
    private JTextArea commits;
    
    public InfoMenu (GITTool gitTool){
        this.gitTool = gitTool;
        this.executor = new GITCommandExecutor(this.gitTool.getProcessBuilder());
        this.lastExitCode = 0;
        this.name = new JLabel();
        this.remote = new JTextArea();
        this.remote.setBackground(this.getBackground());
        this.remote.setEditable(false);
        this.branch = new JLabel();
        this.commits = new JTextArea();
        this.commits.setBackground(this.getBackground());
        this.commits.setEditable(false);
        this.setLayout(new MigLayout());
        this.add(new JLabel("Repository:"), "growx, wrap");
        this.add(this.name, "growx, wrap");
        this.add(new JLabel("Remote:"), "growx, wrap");
        this.add(new JScrollPane(this.remote), "growx, wrap");
        this.add(new JLabel("Current Branch:"), "growx, wrap");
        this.add(this.branch, "growx, wrap");
        this.add(new JLabel("Last Commits:"), "growx, wrap");
        this.add(new JScrollPane(this.commits), "growx, wrap");
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
            this.gitTool.getProcessBuilder().command(params);
            Process p = this.gitTool.getProcessBuilder().start();
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
            res.add(0, line);
        }
        return res;
    }
    
    private String getRepoName() {
        List<String> nameCommand = new ArrayList<String>(3);
        nameCommand.add("git");
        nameCommand.add("rev-parse");
        nameCommand.add("--show-toplevel");
        List<String> res = this.executeCommand(nameCommand);
        if(this.getLastExitCode()!=0) {
            return "";
        }
        if(res.isEmpty()) {
            res.add(this.gitTool.getProcessBuilder().directory().getAbsolutePath());
        }
        String[] namePath = res.get(0).split(File.separator);
        return namePath[namePath.length-1].trim();
    }
    
    private List<String> getRemotes() {
        List<String> remoteCommand = new ArrayList<String>(3);
        remoteCommand.add("git");
        remoteCommand.add("remote");
        remoteCommand.add("-v");
        return this.executeCommand(remoteCommand);
    }
    
    private String getCurBranch() {
        List<String> branchCommand = new ArrayList<String>(2);
        branchCommand.add("git");
        branchCommand.add("branch");
        for(String s: this.executeCommand(branchCommand)) {
            if(s.startsWith("*")) {
                return s.replace("*", "").trim();
            }
        }
        return "";
    }
    
    private List<Commit> getCommits() throws GitCommandException {
        return this.executor.log("");
    }
    
    public void refresh () {
        this.name.setText("");
        this.remote.setText("");
        this.branch.setText("");
        this.commits.setText("");
        String repoName = this.getRepoName();
        if(!repoName.equals("")) {
            this.name.setText(repoName);
            List<String> remotes = this.getRemotes();
            for(int i=0; i<remotes.size(); i++) {
                this.remote.append(remotes.get(i).trim());
                if(i!=remotes.size()-1) {
                    this.remote.append("\n");
                }
            }
            this.branch.setText(this.getCurBranch());
            try {
                List<Commit> commit = this.getCommits();
                for(int i=0; i<Math.min(5, commit.size()); i++) {
                    this.commits.append(commit.get(i).toString().trim());
                    if(i!=Math.min(5, commit.size())-1) {
                        this.commits.append("\n");
                    }
                }
            } catch(GitCommandException e) {
            }
        }
        this.remote.setCaretPosition(0);
        this.commits.setCaretPosition(0);
    }
}

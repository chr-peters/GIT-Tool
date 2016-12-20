//!important!
package application.git_tool.infomenu;

import application.git_tool.GITTool;
import application.git_tool.gitcommandexecutor.*;

import net.miginfocom.layout.*;
import net.miginfocom.swing.*;

import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;

import javax.swing.*;

public class InfoMenu extends JPanel {

    private GITTool gitTool;
    private GITCommandExecutor executor;
    private int lastExitCode;
    private JLabel name;
    private JTextArea remote;
    private JTextArea branch;
    private JTextArea commits;
    private JTextArea status;
    private JTextArea tags;

    public InfoMenu (GITTool gitTool){
        this.gitTool = gitTool;
        this.executor = new GITCommandExecutor(this.gitTool.getProcessBuilder());
        this.lastExitCode = 0;
        this.name = new JLabel();
        this.remote = new JTextArea();
        this.remote.setBackground(this.getBackground());
        this.remote.setEditable(false);
        this.branch = new JTextArea();
        this.branch.setBackground(this.getBackground());
        this.branch.setEditable(false);
        this.commits = new JTextArea();
        this.commits.setBackground(this.getBackground());
        this.commits.setEditable(false);
        this.status = new JTextArea();
        this.status.setBackground(this.getBackground());
        this.status.setEditable(false);
        this.tags = new JTextArea();
        this.tags.setBackground(this.getBackground());
        this.tags.setEditable(false);
        this.setLayout(new MigLayout("fillx", "", "[][][][][][][][][][][][]push[]"));
        this.setBorder(BorderFactory.createTitledBorder("Info Menu"));
        JButton toggle = new JButton(new AbstractAction("Toggle Terminal") {
            @Override
            public void actionPerformed(ActionEvent e) {
                InfoMenu.this.gitTool.toggleTerminal();
            }
        });
        this.add(new JLabel("Repository:"), "growx, wrap");
        this.add(this.name, "growx, wrap");
        this.add(new JLabel("Remote:"), "growx, wrap");
        this.add(new JScrollPane(this.remote), "growx, wrap");
        this.add(new JLabel("Branches:"), "growx, wrap");
        this.add(new JScrollPane(this.branch), "growx, wrap");
        this.add(new JLabel("Last Commits:"), "growx, wrap");
        this.add(new JScrollPane(this.commits), "growx, wrap");
        this.add(new JLabel("Status:"), "growx, wrap");
        this.add(new JScrollPane(this.status), "growx, wrap");
        this.add(new JLabel("Tags:"), "growx, wrap");
        this.add(new JScrollPane(this.tags), "growx, wrap");
        this.add(toggle, "dock south");
    }

    private String getRepoName() {
        List<String> nameCommand = new ArrayList<String>(3);
        nameCommand.add("git");
        nameCommand.add("rev-parse");
        nameCommand.add("--show-toplevel");
        List<String> res = this.executor.executeCommand(nameCommand);
        if(this.executor.getLastExitCode()!=0) {
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
        return this.executor.executeCommand(remoteCommand);
    }

    private List<String> getBranches() {
        List<String> branchCommand = new ArrayList<String>(2);
        branchCommand.add("git");
        branchCommand.add("branch");
        return this.executor.executeCommand(branchCommand);
    }

    private List<Commit> getCommits() throws GitCommandException {
        return this.executor.log("");
    }

    private StatusContainer getStatus() throws GitCommandException {
        return this.executor.status();
    }
    
    private List<String> getTags() {
        return this.executor.listTags();
    }

    public void refresh () {
        this.name.setText("");
        this.remote.setText("");
        this.branch.setText("");
        this.commits.setText("");
        this.status.setText("");
        this.tags.setText("");
        String repoName = this.getRepoName();
        if(!repoName.equals("")) {
            this.name.setText(repoName);
            for(String s: this.getRemotes()) {
                this.remote.append(s.trim()+"\n");
            }
            for(String s: this.getBranches()) {
                this.branch.append(s.trim()+"\n");
            }
            try {
                List<Commit> commit = this.getCommits();
                for(int i=0; i<Math.min(5, commit.size()); i++) {
                    this.commits.append(commit.get(i).toString().trim()+"\n\n");
                }
            } catch(GitCommandException e) {
            
            }
            try {
                this.status.append(this.getStatus().toString().trim()+"\n");
            } catch(GitCommandException e) {
            
            }
            for(String s: this.getTags()) {
                this.tags.append(s.trim()+"\n");
            }
        }
        this.remote.setCaretPosition(0);
        this.branch.setCaretPosition(0);
        this.commits.setCaretPosition(0);
        this.status.setCaretPosition(0);
        this.tags.setCaretPosition(0);
    }
}

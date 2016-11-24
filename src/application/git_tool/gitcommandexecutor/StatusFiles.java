package application.git_tool.gitcommandexecutor;

import java.util.List;
import java.util.ArrayList;

/**
* A container to store all the relevant changes made to files.
*/
public class StatusFiles {
    
    private List<String> untrackedFiles;
    private List<String> newFiles;
    private List<String> modifiedFiles;
    private List<String> renamedFiles;
    private List<String> deletedFiles;
    private List<String> copiedFiles;
    private List<String> updatedFiles;
    
    public StatusFiles(){
        this.untrackedFiles = new ArrayList<String>();
        this.newFiles = new ArrayList<String>();
        this.modifiedFiles = new ArrayList<String>();
        this.renamedFiles = new ArrayList<String>();
        this.deletedFiles = new ArrayList<String>();
        this.copiedFiles = new ArrayList<String>();
        this.updatedFiles = new ArrayList<String>();
    }
    
    public List<String> getUntrackedFiles () {
        return this.untrackedFiles;
    }
    
    public List<String> getNewFiles () {
        return this.newFiles;
    }
    
    public List<String> getModifiedFiles () {
        return this.modifiedFiles;
    }
    
    public List<String> getRenamedFiles () {
        return this.renamedFiles;
    }
    
    public List<String> getDeletedFiles () {
        return this.deletedFiles;
    }
    
    public List<String> getCopiedFiles () {
        return this.copiedFiles;
    }
    
    public List<String> getUpdatedFiles () {
        return this.updatedFiles;
    }
    
    public String toString() {
        //the endline character of the current system
        String n = System.getProperty("line.separator");
        StringBuilder res = new StringBuilder();
        res.append("Untracked Files:"+n);
        for(String f: untrackedFiles){
            res.append(f+n);
        }
        res.append("New Files:"+n);
        for(String f: newFiles){
            res.append(f+n);
        }
        res.append("Modified Files:"+n);
        for(String f: modifiedFiles){
            res.append(f+n);
        }
        res.append("Deleted Files:"+n);
        for(String f: deletedFiles){
            res.append(f+n);
        }
        res.append("Renamed Files:"+n);
        for(String f: renamedFiles){
            res.append(f+n);
        }
        res.append("Copied Files:"+n);
        for(String f: copiedFiles){
            res.append(f+n);
        }
        res.append("Updated but unmerged Files:"+n);
        for(String f: updatedFiles){
            res.append(f+n);
        }
        return res.toString().trim();
    }
}
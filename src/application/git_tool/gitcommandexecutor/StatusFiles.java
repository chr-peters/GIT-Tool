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
}
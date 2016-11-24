package application.git_tool.gitcommandexecutor;

/**
* Contains all relevant information of a "git status"
*/
public class StatusContainer {

    //all changes staged for commit
    private StatusFiles stagedChanges;
    //all changes not staged for commit (not added to the index)
    private StatusFiles unstagedChanges;
    
    public StatusContainer(StatusFiles stagedChanges, StatusFiles unstagedChanges) {
        this.stagedChanges = stagedChanges;
        this.unstagedChanges = unstagedChanges;
    }
    
    /**
    * @return all changed files that are staged for commit
    */
    public StatusFiles getStagedChanges(){
        return this.stagedChanges;
    }
    
    /**
    * @return all changed files that are not yet staged for commit (not added to the index)
    */
    public StatusFiles getUnstagedChanges(){
        return this.unstagedChanges;
    }
}
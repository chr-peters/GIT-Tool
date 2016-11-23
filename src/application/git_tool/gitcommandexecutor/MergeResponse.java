package application.git_tool.gitcommandexecutor;

import java.util.List;

public class MergeResponse {
    public enum States {FAST_FORWARD, RESOLVED_CONFLICTS, OPEN_CONFLICTS};
    
    private States state;
    private List<String> outputLines;
    
    /**
    * Creates a new MergeResponse object.
    *
    * @param state The state of the merge. It can be one of the following values:
    *              FAST_FORWARD: Everything went well.
    *              RESOLVED_CONFLICTS: Some conflicts occured, but git was able to
    *                                  resolve them automatically.
    *              OPEN_CONFLICTS: Some conflicts occured which could not be resolved automatically.
    * @param outputLines Details about the merge.
    */
    public MergeResponse (States state, List<String> outputLines) {
        this.state = state;
        this.outputLines = outputLines;
    }
    
    /**
    * Returns the state of the merge.
    * <p>
    * Use this method to check if the merge was successful.
    *
    * @return The state of the merge.
    */
    public States getState () {
        return this.state;
    }
    
    /**
    * Returns the output lines of the merge command.
    *
    * @return Check this for details about the merge.
    */
    public List<String> getOutputLines () {
        return this.outputLines;
    }
}
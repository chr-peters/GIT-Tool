package application.git_tool.gitcommandexecutor;

import java.util.List;

public class MergeResponse {
    public enum States {FAST_FORWARD, RESOLVED_CONFLICTS, OPEN_CONFLICTS};
    
    private States state;
    private List<String> outputLines;
    
    public MergeResponse (States state, List<String> outputLines) {
        this.state = state;
        this.outputLines = outputLines;
    }
    
    public States getState () {
        return this.state;
    }
    
    public List<String> getOutputLines () {
        return this.outputLines;
    }
}
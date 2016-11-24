package application.git_tool.gitcommandexecutor;

public class GitCommandException extends Exception {
    public GitCommandException(String msg) {
        super(msg);
    }
    public GitCommandException(){
        super("Error: Could not execute the GIT-Command!");
    }
}
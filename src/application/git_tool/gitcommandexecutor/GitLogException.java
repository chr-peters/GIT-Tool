package application.git_tool.gitcommandexecutor;

public class GitLogException extends Exception {
    public GitLogException(String msg) {
        super(msg);
    }
    public GitLogException(){
        super("Error: Unable to list all commits!");
    }
}
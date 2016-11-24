package application.git_tool.gitcommandexecutor;

public class Commit {

    private String hash;
    private String author;
    private String date;
    private String merge;
    private String message;
    
    public Commit(String hash, String author, String date, String merge, String message){
        this.hash = hash;
        this.author = author;
        this.date = date;
        this.merge = merge;
        this.message = message;
    }
    
    public String getHash() {
        return this.hash;
    }
    
    public String getAuthor() {
        return this.author;
    }
    
    public String getDate() {
        return this.date;
    }
    
    public String getMerge() {
        return this.merge;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public String toString() {
        String n = System.getProperty("line.separator");
        return "Hash: "+this.hash+n+"Author: "+this.author+n+"Date: "+this.date
                +n+"Merge: "+this.merge+n+"Message: "+this.message;
    }
}
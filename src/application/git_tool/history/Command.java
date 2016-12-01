package application.git_tool.history;

import java.util.Date;
import java.text.SimpleDateFormat;

/**
* Used to store information about a command in the command history.
*/
public class Command {
    
    private Date date;
    private String text;
    
    /**
    * Instantiates a new command with the current date
    *
    * @param text The command text. This is displayed in the history.
    */
    public Command(String text) {
        this.date = new Date();
        this.text = text;
    }
    
    /**
    * Instantiates a new command.
    *
    * @param text The command text.
    * @param date The date the command was executed.
    */
    public Command(String text, Date date) {
        this.date = date;
        this.text = text;
    }
    
    /**
    * Prints the date with a given format.
    *
    * @param format See SimpleDateFormat for more details.
    *
    * @return A String representing the date of the command.
    */
    public String printDate(String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(this.date);
    }
    
    /**
    * Returns the date of the command.
    */
    public Date getDate() {
        return this.date;
    }
    
    /**
    * Returns the text of the command.
    */
    public String getText() {
        return this.text;
    }
}
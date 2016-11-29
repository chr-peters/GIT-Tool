package application.git_tool.commandmenu.helpers;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JTextField;

public class MyTextField extends JTextField implements FocusListener {

  private String defaultText;

  //Erzeugung eines JTextFields
  public MyTextField(){
    super();
    defaultText = "";
    super.addFocusListener(this);
  }

  @Override
  //Wird das Textfeld fokussiert und der defaultText steht drin, so wird dieser entfernt.
  public void focusGained(FocusEvent e){
    if(getText().isEmpty()) super.setText("");
  }

  @Override
  //Verliert das Textfeld den Fokus und es ist leer, wird der defaultText hineingeschrieben.
  public void focusLost(FocusEvent e) {
    if(this.getText().isEmpty()) super.setText(defaultText);
  }

  @Override
  //Wiedergabe eines Leerstrings, falls der defaultText im Textfeld steht
  public String getText() {
    return super.getText().equals(defaultText) ? "" : super.getText();
  }

  //Wird der Text des Texfeldes von außerhalb geändert, so wird auch der defaultText neu gesetzt.
  public void setText(String text){
    super.setText(text);
    defaultText = text;
  }
}

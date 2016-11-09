import net.miginfocom.layout.*;
import net.miginfocom.swing.*;

import javax.swing.*;

import java.io.File;

public class GITTool {
    public static void main (String args []){
        JFrame frame = new JFrame("GIT Tool");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        MigLayout layout = new MigLayout("debug");
        JPanel rootContainer = new JPanel(layout);
        
        FileBrowser fileBrowser = new FileBrowser(new File("/"));
        
        rootContainer.add(new JLabel("Git Commands"), "width 25%, height 75%");
        rootContainer.add(fileBrowser, "width 50%, spany 2, growx, growy, height 100%");
        rootContainer.add(new JLabel("Info"), "width 25%, spany 3, growx, growy, wrap");
        rootContainer.add(new JLabel("History"), "spany 2, growx, growy, height 25%,wrap");
        rootContainer.add(new JLabel("Terminal"), "growx, growy, skip, skip, skip");
        
        frame.getContentPane().add(rootContainer);
        
        frame.setSize(1280, 720);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
//!important!
package application.git_tool.filebrowser;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import net.miginfocom.layout.*;
import net.miginfocom.swing.*;

import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.tree.*;

public class FileBrowser extends JPanel {
    private File curPath;

    private class TreeNode extends DefaultMutableTreeNode {
        private File f;
        private boolean loaded;

        private TreeNode(File path) {
            this.f = path;
            this.loaded = false;
            this.setUserObject(this.f.getName());
        }

        private void lazyLoad() {
            if(!this.loaded) {
                try {
                    String[] content = this.f.list();
                    Arrays.sort(content, String.CASE_INSENSITIVE_ORDER);
                    ArrayList<File> files = new ArrayList<File>();
                    int i=0;
                    for(String s: content) {
                        File file = new File(this.f.getAbsoluteFile()+"/"+s);
                        if(file.isDirectory()) {
                            this.insert(new TreeNode(file), i++);
                        } else {
                            files.add(file);
                        }
                    }
                    for(File file: files) {
                        this.insert(new TreeNode(file), i++);
                    }
                } catch(NullPointerException e) {
                }
                loaded = true;
            }
        }

        @Override
        public boolean isLeaf() {
            return this.f.isFile();
        }

        @Override
        public int getChildCount() {
            this.lazyLoad();
            return super.getChildCount();
        }
    }
    
    private class FileRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            JLabel l = (JLabel) c;
            File f = (File) value;
            l.setText(f.getName());
            l.setIcon(FileSystemView.getFileSystemView().getSystemIcon(f));
            return l;
        }
    }

    public FileBrowser(File path) {
        this.setLayout(new MigLayout());
        if(!path.exists()) {
            this.curPath = new File("/");
        } else if(!path.isDirectory()) {
            this.curPath = path.getParentFile();
        } else {
            this.curPath = path;
        }
        
        JTree tree = new JTree(new TreeNode(new File("/")));
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().add(tree);
        this.openStartPath(tree);
        this.add(scrollPane, "width 40%, height 100%");
        
        JList<File> fileList = new JList<File>(this.getContent());
        JScrollPane scrollPane2 = new JScrollPane();
        scrollPane2.getViewport().add(fileList);
        fileList.setCellRenderer(new FileRenderer());
        fileList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        fileList.setVisibleRowCount(0);
        this.add(scrollPane2, "width 60%, height 100%");
    }
    
    private File[] getContent() {
        try{
            String[] contentString = this.curPath.list();
            Arrays.sort(contentString, String.CASE_INSENSITIVE_ORDER);
            ArrayList<File> files = new ArrayList<File>();
            File[] content = new File[contentString.length];
            int i=0;
            for(String s: contentString) {
                File file = new File(this.curPath.getAbsoluteFile()+"/"+s);
                if(file.isDirectory()) {
                    content[i++] = file;
                } else {
                    files.add(file);
                }
            }
            for(File file: files) {
                content[i++] = file;
            }
            return content;
        } catch(NullPointerException e) {
            return new File[] {};
        }
    }

    private void openStartPath(JTree tree) {
        if(this.curPath.getAbsolutePath().equals("/")) {
            return;
        }
        String[] pathParts = this.curPath.getAbsolutePath().substring(1).split(File.separator);
        TreeNode startNode = (TreeNode) tree.getModel().getRoot();
        for(String s: pathParts) {
            for(int i=0; i<startNode.getChildCount(); i++) {
                if(((TreeNode) startNode.getChildAt(i)).getUserObject().equals(s)) {
                    startNode = (TreeNode) startNode.getChildAt(i);
                    break;
                }
            }
        }
        TreePath startPath = new TreePath(startNode.getPath());
        tree.expandPath(startPath);
        tree.scrollPathToVisible(startPath);
    }
}

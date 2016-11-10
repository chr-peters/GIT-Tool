//!important!
package application.git_tool.filebrowser;

import java.awt.BorderLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.*;
import javax.swing.tree.*;

public class FileBrowser extends JPanel {
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
                    if(this.f.isDirectory()) {
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
                    }
                } catch(NullPointerException e) {
                }
                loaded = true;
            }
        }

        public boolean isLeaf() {
            return this.f.isFile();
        }

        public int getChildCount() {
            this.lazyLoad();
            return super.getChildCount();
        }
    }

    public FileBrowser(File path) {
        this.setLayout(new BorderLayout());
        JTree tree = new JTree(new TreeNode(new File("/")));
        this.openStartPath(tree, path);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().add(tree);
        this.add(BorderLayout.WEST, scrollPane);
    }

    private void openStartPath(JTree tree, File path) {
        if(!path.exists() || path.getAbsolutePath().equals("/")) {
            return;
        }
        if(!path.isDirectory()) {
            path = path.getParentFile();
        }
        String[] pathParts = path.getAbsolutePath().substring(1).split(File.separator);
        TreeNode startNode = (TreeNode) tree.getModel().getRoot();
        for(String s: pathParts) {
            for(int i=0; i<startNode.getChildCount(); i++) {
                if(((TreeNode) startNode.getChildAt(i)).getUserObject().equals(s)) {
                    startNode = (TreeNode) startNode.getChildAt(i);
                    break;
                }
            }
        }
        tree.expandPath(new TreePath(startNode.getPath()));
    }
}
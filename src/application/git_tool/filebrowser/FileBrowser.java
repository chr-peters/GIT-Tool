//!important!
package application.git_tool.filebrowser;

import application.git_tool.GITTool;

import java.awt.Component;
import java.io.File;
import java.util.*;

import net.miginfocom.layout.*;
import net.miginfocom.swing.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import javax.swing.tree.*;

public class FileBrowser extends JPanel {
    private GITTool gitTool;
    private JTree tree;
    private JList<File> list;
    
    private class MyTreeModel extends DefaultTreeModel {
        private MyTreeModel(MyTreeNode node) {
            super(node);
        }
        
        @Override
        public boolean isLeaf(Object node) {
            return ((MyTreeNode) node).path.isFile();
        }
    }
    
    private class MyTreeNode extends DefaultMutableTreeNode {
        private File path;
        private boolean loaded;
        
        private MyTreeNode(File path) {
            this.path = path.getAbsoluteFile();
            this.loaded = false;
            this.setUserObject(this.path.getName());
            if(path.getAbsolutePath().equals("/")) {
                this.load();
            }
        }
        
        private void load() {
            if(!this.loaded) {
                File[] content = FileBrowser.this.getContent(this.path);
                for(int i=0; i<content.length; i++) {
                    this.insert(new MyTreeNode(content[i]), i);
                }
                this.loaded = true;
            }
        }
    }
    
    private class MyTreeWillExpandListener implements TreeWillExpandListener {
        @Override
        public void treeWillExpand(TreeExpansionEvent e) {
            ((MyTreeNode) e.getPath().getLastPathComponent()).load();
            FileBrowser.this.gitTool.getProcessBuilder().directory(((MyTreeNode) e.getPath().getLastPathComponent()).path);
        }
        
        @Override
        public void treeWillCollapse(TreeExpansionEvent e) {
            MyTreeNode node = (MyTreeNode) e.getPath().getLastPathComponent();
            if(node.getParent()!=null) {
                FileBrowser.this.gitTool.getProcessBuilder().directory(((MyTreeNode) node.getParent()).path);
            } else {
                FileBrowser.this.gitTool.getProcessBuilder().directory(((MyTreeNode) node.getRoot()).path);
            }
        }
    }
    
    private class MyListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            JLabel l = (JLabel) c;
            File f = (File) value;
            l.setText(f.getName());
            l.setIcon(FileSystemView.getFileSystemView().getSystemIcon(f));
            return l;
        }
    }
    
    public FileBrowser(GITTool gitTool) {
        this.gitTool = gitTool;
        String[] startPath = this.gitTool.getProcessBuilder().directory().getAbsolutePath().substring(1).split(File.separator);
        this.setLayout(new MigLayout());
        
        this.tree = new JTree(new MyTreeModel(new MyTreeNode(new File("/"))));
        this.tree.addTreeWillExpandListener(new MyTreeWillExpandListener());
        MyTreeNode startNode = (MyTreeNode) this.tree.getModel().getRoot();
        for(String s: startPath) {
            for(int i=0; i<startNode.getChildCount(); i++) {
                if(((MyTreeNode) startNode.getChildAt(i)).getUserObject().equals(s)) {
                    startNode = (MyTreeNode) startNode.getChildAt(i);
                    this.tree.expandPath(new TreePath(startNode.getPath()));
                    break;
                }
            }
        }
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().add(this.tree);
        this.add(scrollPane, "width 40%, height 100%");
        this.tree.scrollPathToVisible(new TreePath(startNode.getPath()));
        
        this.list = new JList<File>(this.getContent(this.gitTool.getProcessBuilder().directory()));
        this.list.setCellRenderer(new MyListCellRenderer());
        this.list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        this.list.setVisibleRowCount(0);
        JScrollPane scrollPane2 = new JScrollPane();
        scrollPane2.getViewport().add(this.list);
        this.add(scrollPane2, "width 60%, height 100%");
    }
    
    private File[] getContent(File path) {
        try{
            String[] contentString = path.getAbsoluteFile().list();
            Arrays.sort(contentString, String.CASE_INSENSITIVE_ORDER);
            ArrayList<File> files = new ArrayList<File>();
            File[] content = new File[contentString.length];
            int i=0;
            for(String s: contentString) {
                File file = new File(path.getAbsoluteFile()+"/"+s);
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
    
    public void refresh() {
    }
}

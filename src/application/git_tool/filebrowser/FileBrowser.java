//!important!
package application.git_tool.filebrowser;

import application.git_tool.GITTool;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import net.miginfocom.layout.*;
import net.miginfocom.swing.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import javax.swing.text.*;
import javax.swing.tree.*;

public class FileBrowser extends JPanel {
    private GITTool gitTool;
    private Desktop desktop;
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
            MyTreeNode node = (MyTreeNode) e.getPath().getLastPathComponent();
            node.load();
            MyTreeNode parent = (MyTreeNode) node.getParent();
            if(parent!=null) {
                for(int i=0; i<parent.getChildCount(); i++) {
                    TreePath tmp = new TreePath(((MyTreeNode) parent.getChildAt(i)).getPath());
                    if(FileBrowser.this.tree.isExpanded(tmp)) {
                        FileBrowser.this.tree.collapsePath(tmp);
                    }
                }
            }
            FileBrowser.this.gitTool.getProcessBuilder().directory(node.path);
            FileBrowser.this.list.setListData(FileBrowser.this.getContent(FileBrowser.this.gitTool.getProcessBuilder().directory()));
        }
        
        @Override
        public void treeWillCollapse(TreeExpansionEvent e) {
            MyTreeNode node = (MyTreeNode) e.getPath().getLastPathComponent();
            for(int i=0; i<node.getChildCount(); i++) {
                TreePath tmp = new TreePath(((MyTreeNode) node.getChildAt(i)).getPath());
                if(FileBrowser.this.tree.isExpanded(tmp)) {
                    FileBrowser.this.tree.collapsePath(tmp);
                }
            }
            if(node.getParent()!=null) {
                FileBrowser.this.gitTool.getProcessBuilder().directory(((MyTreeNode) node.getParent()).path);
            } else {
                FileBrowser.this.gitTool.getProcessBuilder().directory(((MyTreeNode) node.getRoot()).path);
            }
            FileBrowser.this.list.setListData(FileBrowser.this.getContent(FileBrowser.this.gitTool.getProcessBuilder().directory()));
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
    
    private class MyMouseAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if(e.getButton()==MouseEvent.BUTTON1 && e.getClickCount()==2) {
                File clicked = FileBrowser.this.list.getModel().getElementAt(FileBrowser.this.list.locationToIndex(e.getPoint()));
                if(clicked.isFile()) {
                    try {
                        FileBrowser.this.desktop.open(clicked);
                    } catch(IOException exception) {
                    }
                } else {
                    if(FileBrowser.this.tree.isCollapsed(new TreePath(((MyTreeNode) FileBrowser.this.tree.getModel().getRoot()).getPath()))) {
                        FileBrowser.this.openPath(new File("/"));
                    }
                    FileBrowser.this.openPath(clicked);
                }
            }
        }
    }
    
    public FileBrowser(GITTool gitTool) {
        this.gitTool = gitTool;
        this.desktop = Desktop.getDesktop();
        this.tree = new JTree(new MyTreeModel(new MyTreeNode(new File("/"))));
        this.list = new JList<File>();
        this.setLayout(new MigLayout());
        
        this.tree.addTreeWillExpandListener(new MyTreeWillExpandListener());
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().add(this.tree);
        this.add(scrollPane, "width 40%, height 100%");
        this.openPath(this.gitTool.getProcessBuilder().directory());
        
        this.list.setCellRenderer(new MyListCellRenderer());
        this.list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        this.list.setVisibleRowCount(0);
        this.list.addMouseListener(new MyMouseAdapter());
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
    
    private void openPath(File openPath) {
        String[] openPathParts = openPath.getAbsolutePath().substring(1).split(File.separator);
        int currentRow = 0;
        for(String s: openPathParts) {
            if(s.equals(".")) {
                continue;
            }
            TreePath path = this.tree.getNextMatch(s, currentRow, Position.Bias.Forward);
            currentRow = this.tree.getRowForPath(path)+1;
            this.tree.expandPath(path);
            this.tree.scrollPathToVisible(path);
        }
    }
    
    public void refresh() {
    }
}

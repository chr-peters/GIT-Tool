//!important!
package application.git_tool.filebrowser;

import application.git_tool.GITTool;
import application.git_tool.unixcommandexecutor.UnixCommandExecutor;

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
    private UnixCommandExecutor unixCommandExecutor;
    private Desktop desktop;
    private JTree tree;
    private JList<File> list;
    private JPopupMenu menu;

    //setting private attributes, placing tree and list in scrollpane and open current working directory
    public FileBrowser(GITTool gitTool) {
        this.gitTool = gitTool;
        this.unixCommandExecutor = new UnixCommandExecutor(this.gitTool);
        this.desktop = Desktop.getDesktop();
        this.tree = new JTree(new MyTreeModel(new MyTreeNode(new File("/"))));
        this.list = new JList<File>();
        this.menu = new JPopupMenu();
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
        this.createPopupMenu();
    }

    private class MyTreeModel extends DefaultTreeModel {
        private MyTreeModel(MyTreeNode node) {
            super(node);
        }

        //check if node is a leaf, true only if node is a file
        @Override
        public boolean isLeaf(Object node) {
            return ((MyTreeNode) node).path.isFile();
        }
    }

    private class MyTreeNode extends DefaultMutableTreeNode {
        private File path;

        private MyTreeNode(File path) {
            this.path = path.getAbsoluteFile();
            this.setUserObject(this.path.getName());
            if(path.getAbsolutePath().equals("/")) {
                this.load();
            }
        }

        //method for lazy loading the tree nodes
        private void load() {
            File[] content = FileBrowser.this.getContent(this.path);
            for(int i=0; i<content.length; i++) {
                if(content[i].isFile()) {
                    break;
                }
                this.insert(new MyTreeNode(content[i]), i);
            }
        }
    }

    //class for handling the tree events and updating the current working directory
    private class MyTreeWillExpandListener implements TreeWillExpandListener {
        //collapses all siblings of the newly expanding node
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
            FileBrowser.this.gitTool.getInfoMenu().refresh();
        }

        //collapses all child nodes of the newly collapsed node
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
            FileBrowser.this.gitTool.getInfoMenu().refresh();
        }
    }

    //class for editing each list element, so that list elements display an icon and the file name
    private class MyListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            JLabel l = (JLabel) c;
            File f = (File) value;
            l.setText(f.getName());
            l.setIcon(FileSystemView.getFileSystemView().getSystemIcon(f));
            l.setBorder(BorderFactory.createEmptyBorder());
            return l;
        }
    }

    //class for handling the list click events
    private class MyMouseAdapter extends MouseAdapter {
        //mouse pressed over no element removes any selection
        @Override
        public void mousePressed(MouseEvent e) {
            int index = FileBrowser.this.list.locationToIndex(e.getPoint());
            if(index==-1 || !FileBrowser.this.list.getCellBounds(index, index).contains(e.getPoint())) {
                FileBrowser.this.list.clearSelection();
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            this.mousePressed(e);
            boolean hit = !FileBrowser.this.list.isSelectionEmpty();
            int index = FileBrowser.this.list.locationToIndex(e.getPoint());
            //if clicked on a non selected element with not the button1 removes any selection and selects only this element
            if(hit && e.getButton()!=MouseEvent.BUTTON1 && !FileBrowser.this.list.isSelectedIndex(index)) {
                FileBrowser.this.list.clearSelection();
                FileBrowser.this.list.setSelectedIndex(index);
            }
            //if double clicked with button1 tries to opens directory or file
            if(hit && e.getButton()==MouseEvent.BUTTON1 && e.getClickCount()==2) {
                File clicked = FileBrowser.this.list.getModel().getElementAt(index);
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
            //show popup menu
            if(e.getButton()!=MouseEvent.BUTTON1) {
                if(!hit) {
                    FileBrowser.this.menu.getComponent(0).setEnabled(false);
                    FileBrowser.this.menu.getComponent(1).setEnabled(false);
                    FileBrowser.this.menu.getComponent(2).setEnabled(true);
                    FileBrowser.this.menu.getComponent(3).setEnabled(true);
                    FileBrowser.this.menu.getComponent(4).setEnabled(false);
                    FileBrowser.this.menu.getComponent(5).setEnabled(false);
                    FileBrowser.this.menu.getComponent(6).setEnabled(false);
                    FileBrowser.this.menu.getComponent(7).setEnabled(false);
                    FileBrowser.this.menu.getComponent(8).setEnabled(true);
                    FileBrowser.this.menu.getComponent(9).setEnabled(true);
                } else {
                    FileBrowser.this.menu.getComponent(0).setEnabled(true);
                    FileBrowser.this.menu.getComponent(1).setEnabled(true);
                    FileBrowser.this.menu.getComponent(2).setEnabled(false);
                    FileBrowser.this.menu.getComponent(3).setEnabled(false);
                    FileBrowser.this.menu.getComponent(4).setEnabled(true);
                    FileBrowser.this.menu.getComponent(5).setEnabled(true);
                    FileBrowser.this.menu.getComponent(6).setEnabled(true);
                    for(File selected: FileBrowser.this.list.getSelectedValuesList()) {
                        if(!selected.isDirectory()) {
                            FileBrowser.this.menu.getComponent(6).setEnabled(false);
                            break;
                        }
                    }
                    FileBrowser.this.menu.getComponent(7).setEnabled(true);
                    FileBrowser.this.menu.getComponent(8).setEnabled(true);
                    FileBrowser.this.menu.getComponent(9).setEnabled(false);
                }
                FileBrowser.this.showPopupMenu(FileBrowser.this.menu, e.getX(), e.getY());
            }
        }
    }

    private void showPopupMenu(JPopupMenu menu, int x, int y) {
        menu.show(FileBrowser.this.list, x, y);
        menu.setVisible(false);
        Rectangle r = FileBrowser.this.list.getBounds();
        Rectangle r2 = menu.getBounds();
        if(!r.contains(x+r2.getWidth(), y+r2.getHeight())) {
            if(x+r2.getWidth()<r.getX()+r.getWidth()) {
                menu.show(FileBrowser.this.list, x, (int) (r.getY()+r.getHeight()-r2.getHeight()));
            } else if(y+r2.getHeight()<r.getX()+r.getHeight()) {
                menu.show(FileBrowser.this.list, (int) (r.getX()+r.getWidth()-r2.getWidth()), y);
            } else {
                menu.show(FileBrowser.this.list, (int) (r.getX()+r.getWidth()-r2.getWidth()), (int) (r.getY()+r.getHeight()-r2.getHeight()));
            }
        } else {
            menu.show(FileBrowser.this.list, x, y);
        }
    }

    //creates the popup menu
    private void createPopupMenu() {
        this.menu.add(new AbstractAction("chmod") {
            public void actionPerformed(ActionEvent e) {
                final JTextField textField = new JTextField("neue Rechte", 10);
                textField.addActionListener(new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        String text = textField.getText();
                        ((JPopupMenu) ((JTextField) e.getSource()).getParent()).setVisible(false);
                        java.util.List<String> res = FileBrowser.this.unixCommandExecutor.chmod(text, FileBrowser.this.list.getSelectedValuesList());
                        if(!res.isEmpty() && res.get(0).equals("ERRORERRORERROR")) {
                            res.remove(0);
                            FileBrowser.this.gitTool.errorMessage(res, "Error");
                        } else if(!res.isEmpty()) {
                            FileBrowser.this.gitTool.infoMessage(res, "Info");
                        }
                        FileBrowser.this.refresh();
                    }
                });
                JPopupMenu tmp = new JPopupMenu();
                tmp.add(textField);
                Point p = FileBrowser.this.list.getMousePosition();
                FileBrowser.this.showPopupMenu(tmp, (int) p.getX(), (int) p.getY());
                tmp = null;
            }
        });
        this.menu.add(new AbstractAction("cp") {
            public void actionPerformed(ActionEvent e) {
                final JTextField textField = new JTextField("wohin", 10);
                textField.addActionListener(new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        String text = textField.getText();
                        ((JPopupMenu) ((JTextField) e.getSource()).getParent()).setVisible(false);
                        java.util.List<String> res = FileBrowser.this.unixCommandExecutor.cp(text, FileBrowser.this.list.getSelectedValuesList());
                        if(!res.isEmpty() && res.get(0).equals("ERRORERRORERROR")) {
                            res.remove(0);
                            FileBrowser.this.gitTool.errorMessage(res, "Error");
                        } else if(!res.isEmpty()) {
                            FileBrowser.this.gitTool.infoMessage(res, "Info");
                        }
                        FileBrowser.this.refresh();
                    }
                });
                JPopupMenu tmp = new JPopupMenu();
                tmp.add(textField);
                Point p = FileBrowser.this.list.getMousePosition();
                FileBrowser.this.showPopupMenu(tmp, (int) p.getX(), (int) p.getY());
                tmp = null;
            }
        });
        this.menu.add(new AbstractAction("find") {
            public void actionPerformed(ActionEvent e) {
                final JTextField textField = new JTextField("Suchparameter", 10);
                textField.addActionListener(new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        String text = textField.getText();
                        ((JPopupMenu) ((JTextField) e.getSource()).getParent()).setVisible(false);
                        java.util.List<String> res = FileBrowser.this.unixCommandExecutor.find(text);
                        if(!res.isEmpty() && res.get(0).equals("ERRORERRORERROR")) {
                            res.remove(0);
                            FileBrowser.this.gitTool.errorMessage(res, "Error");
                        } else if(!res.isEmpty()) {
                            FileBrowser.this.gitTool.infoMessage(res, "Info");
                        }
                        FileBrowser.this.refresh();
                    }
                });
                JPopupMenu tmp = new JPopupMenu();
                tmp.add(textField);
                Point p = FileBrowser.this.list.getMousePosition();
                FileBrowser.this.showPopupMenu(tmp, (int) p.getX(), (int) p.getY());
                tmp = null;
            }
        });
        this.menu.add(new AbstractAction("mkdir") {
            public void actionPerformed(ActionEvent e) {
                final JTextField textField = new JTextField("Name", 10);
                textField.addActionListener(new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        String text = textField.getText();
                        ((JPopupMenu) ((JTextField) e.getSource()).getParent()).setVisible(false);
                        java.util.List<String> res = FileBrowser.this.unixCommandExecutor.mkdir(text);
                        if(!res.isEmpty() && res.get(0).equals("ERRORERRORERROR")) {
                            res.remove(0);
                            FileBrowser.this.gitTool.errorMessage(res, "Error");
                        } else if(!res.isEmpty()) {
                            FileBrowser.this.gitTool.infoMessage(res, "Info");
                        }
                        FileBrowser.this.refresh();
                    }
                });
                JPopupMenu tmp = new JPopupMenu();
                tmp.add(textField);
                Point p = FileBrowser.this.list.getMousePosition();
                FileBrowser.this.showPopupMenu(tmp, (int) p.getX(), (int) p.getY());
                tmp = null;
            }
        });
        this.menu.add(new AbstractAction("mv") {
            public void actionPerformed(ActionEvent e) {
                final JTextField textField = new JTextField("wohin", 10);
                textField.addActionListener(new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        String text = textField.getText();
                        ((JPopupMenu) ((JTextField) e.getSource()).getParent()).setVisible(false);
                        java.util.List<String> res = FileBrowser.this.unixCommandExecutor.mv(text, FileBrowser.this.list.getSelectedValuesList());
                        if(!res.isEmpty() && res.get(0).equals("ERRORERRORERROR")) {
                            res.remove(0);
                            FileBrowser.this.gitTool.errorMessage(res, "Error");
                        } else if(!res.isEmpty()) {
                            FileBrowser.this.gitTool.infoMessage(res, "Info");
                        }
                        FileBrowser.this.refresh();
                    }
                });
                JPopupMenu tmp = new JPopupMenu();
                tmp.add(textField);
                Point p = FileBrowser.this.list.getMousePosition();
                FileBrowser.this.showPopupMenu(tmp, (int) p.getX(), (int) p.getY());
                tmp = null;
            }
        });
        this.menu.add(new AbstractAction("rm") {
            public void actionPerformed(ActionEvent e) {
                java.util.List<String> res = FileBrowser.this.unixCommandExecutor.rm(FileBrowser.this.list.getSelectedValuesList());
                if(!res.isEmpty() && res.get(0).equals("ERRORERRORERROR")) {
                    res.remove(0);
                    FileBrowser.this.gitTool.errorMessage(res, "Error");
                } else if(!res.isEmpty()) {
                    FileBrowser.this.gitTool.infoMessage(res, "Info");
                }
                FileBrowser.this.refresh();
            }
        });
        this.menu.add(new AbstractAction("rmdir") {
            public void actionPerformed(ActionEvent e) {
                java.util.List<String> res = FileBrowser.this.unixCommandExecutor.rmdir(FileBrowser.this.list.getSelectedValuesList());
                if(!res.isEmpty() && res.get(0).equals("ERRORERRORERROR")) {
                    res.remove(0);
                    FileBrowser.this.gitTool.errorMessage(res, "Error");
                } else if(!res.isEmpty()) {
                    FileBrowser.this.gitTool.infoMessage(res, "Info");
                }
                FileBrowser.this.refresh();
            }
        });
        this.menu.add(new AbstractAction("tar") {
            public void actionPerformed(ActionEvent e) {
                final JTextField textField = new JTextField("Name", 10);
                textField.addActionListener(new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        String text = textField.getText();
                        ((JPopupMenu) ((JTextField) e.getSource()).getParent()).setVisible(false);
                        java.util.List<String> res = FileBrowser.this.unixCommandExecutor.tar(text, FileBrowser.this.list.getSelectedValuesList());
                        if(!res.isEmpty() && res.get(0).equals("ERRORERRORERROR")) {
                            res.remove(0);
                            FileBrowser.this.gitTool.errorMessage(res, "Error");
                        } else if(!res.isEmpty()) {
                            FileBrowser.this.gitTool.infoMessage(res, "Info");
                        }
                        FileBrowser.this.refresh();
                    }
                });
                JPopupMenu tmp = new JPopupMenu();
                tmp.add(textField);
                Point p = FileBrowser.this.list.getMousePosition();
                FileBrowser.this.showPopupMenu(tmp, (int) p.getX(), (int) p.getY());
                tmp = null;
            }
        });
        this.menu.add(new AbstractAction("touch") {
            public void actionPerformed(ActionEvent e) {
                if(!FileBrowser.this.list.getSelectedValuesList().isEmpty()) {
                    java.util.List<String> res = FileBrowser.this.unixCommandExecutor.touch("", FileBrowser.this.list.getSelectedValuesList());
                    if(!res.isEmpty() && res.get(0).equals("ERRORERRORERROR")) {
                        res.remove(0);
                        FileBrowser.this.gitTool.errorMessage(res, "Error");
                    } else if(!res.isEmpty()) {
                        FileBrowser.this.gitTool.infoMessage(res, "Info");
                    }
                    FileBrowser.this.refresh();
                } else {
                    final JTextField textField = new JTextField("Name", 10);
                    textField.addActionListener(new AbstractAction() {
                        public void actionPerformed(ActionEvent e) {
                            String text = textField.getText();
                            ((JPopupMenu) ((JTextField) e.getSource()).getParent()).setVisible(false);
                            java.util.List<String> res = FileBrowser.this.unixCommandExecutor.touch(text, FileBrowser.this.list.getSelectedValuesList());
                            if(!res.isEmpty() && res.get(0).equals("ERRORERRORERROR")) {
                                res.remove(0);
                                FileBrowser.this.gitTool.errorMessage(res, "Error");
                            } else if(!res.isEmpty()) {
                                FileBrowser.this.gitTool.infoMessage(res, "Info");
                            }
                            FileBrowser.this.refresh();
                        }
                    });
                    JPopupMenu tmp = new JPopupMenu();
                    tmp.add(textField);
                    Point p = FileBrowser.this.list.getMousePosition();
                    FileBrowser.this.showPopupMenu(tmp, (int) p.getX(), (int) p.getY());
                    tmp = null;
                }
            }
        });
        this.menu.add(new AbstractAction("wget") {
            public void actionPerformed(ActionEvent e) {
                final JTextField textField = new JTextField("Adresse", 10);
                textField.addActionListener(new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        String text = textField.getText();
                        ((JPopupMenu) ((JTextField) e.getSource()).getParent()).setVisible(false);
                        java.util.List<String> res = FileBrowser.this.unixCommandExecutor.wget(text);
                        if(!res.isEmpty() && res.get(0).equals("ERRORERRORERROR")) {
                            res.remove(0);
                            FileBrowser.this.gitTool.errorMessage(res, "Error");
                        } else if(!res.isEmpty()) {
                            FileBrowser.this.gitTool.infoMessage(res, "Info");
                        }
                        FileBrowser.this.refresh();
                    }
                });
                JPopupMenu tmp = new JPopupMenu();
                tmp.add(textField);
                Point p = FileBrowser.this.list.getMousePosition();
                FileBrowser.this.showPopupMenu(tmp, (int) p.getX(), (int) p.getY());
                tmp = null;
            }
        });
    }

    //returns the content of the directory denoted by path or an empty array if path is not a directory
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

    //expands the tree path to the openPath file
    private void openPath(File openPath) {
        String[] openPathParts = openPath.getAbsolutePath().substring(1).split(File.separator);
        int currentRow = 0;
        for(int i=0; i<openPathParts.length; i++) {
            if(openPathParts[i].equals(".") || openPathParts[i].equals("..")) {
                continue;
            }
            if(i+1<openPathParts.length && openPathParts[i+1].equals("..")) {
                continue;
            }
            TreePath path;
            do {
                path = this.tree.getNextMatch(openPathParts[i], currentRow, Position.Bias.Forward);
                if(path==null) {
                    return;
                }
                currentRow = this.tree.getRowForPath(path)+1;
            } while(!((MyTreeNode) path.getLastPathComponent()).getUserObject().equals(openPathParts[i]));
            this.tree.expandPath(path);
            this.tree.scrollPathToVisible(path);
        }
    }

    public void refresh() {
        File curPath = this.gitTool.getProcessBuilder().directory();
        this.tree.collapsePath(new TreePath((MyTreeNode) this.tree.getModel().getRoot()));
        ((MyTreeNode) this.tree.getModel().getRoot()).removeAllChildren();
        this.tree.expandPath(new TreePath((MyTreeNode) this.tree.getModel().getRoot()));
        ((MyTreeModel) this.tree.getModel()).reload((MyTreeNode) this.tree.getModel().getRoot());
        this.openPath(curPath);
    }
}

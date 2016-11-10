package application.git_tool.filebrowser;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

public class FileBrowser extends JPanel {

	private class TreeNode extends DefaultMutableTreeNode {
		private File f;
		private boolean loaded;

		public TreeNode(File path) {
			this.f = path;
			this.loaded = false;
			this.setUserObject(f.getName());
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
							if(file.isHidden()) {
								continue;
							}
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
		JTree tree = new JTree(addNodes(path));
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().add(tree);
		this.add(BorderLayout.CENTER, scrollPane);
	}

	TreeNode addNodes(File path) {
		TreeNode node = new TreeNode(path);
		return node;
	}
}
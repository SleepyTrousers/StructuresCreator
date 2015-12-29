package crazypants.structures.creator.block.tree;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Enumeration;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolTip;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import crazypants.structures.api.gen.IResource;
import crazypants.structures.creator.block.AbstractResourceDialog;
import crazypants.structures.creator.block.tree.editors.RemoveEditor;
import net.minecraft.client.Minecraft;

public class EditorTreeControl {

  private final AbstractResourceDialog dialog;

  private JPanel editorPan;
  private JTree tree;
  private DefaultTreeModel treeModel;
  private EditorTreeNode rootNode;
  private IResource resource;

  private RemoveEditor removeEditor = new RemoveEditor();
  private JPanel emptyEditor;
  private JPanel rootPan;
  private final DirtMonitor dirtyMonitor = new DirtMonitor();

  public EditorTreeControl(AbstractResourceDialog dialog) {
    this.dialog = dialog;
    initComponents();
    addComponents();
    addListeners();
  }

  public void buildTree(IResource resourceObj) {
    resource = resourceObj;
    if(treeModel != null) {
      treeModel.removeTreeModelListener(dirtyMonitor);
    }

    treeModel = new DefaultTreeModel(new DefaultMutableTreeNode());
    if(resourceObj != null) {
      rootNode = new EditorTreeNode(resourceObj, null, resourceObj, treeModel);
      EditorTreeNode uidNode = new EditorTreeNode(resourceObj, new FieldAccessor(resourceObj.getClass(), String.class, "uid"), dialog.getResourceUid(),
          treeModel);
      rootNode.insert(uidNode, 0);
      treeModel.setRoot(rootNode);
      treeModel.addTreeModelListener(dirtyMonitor);
      dirtyMonitor.setDirty(false);
    }

    tree.setModel(treeModel);
    dialog.revalidate();
    dialog.repaint();
  }

  public boolean isDirty() {
    return dirtyMonitor.isDirty();
  }

  public void setDirty(boolean dirty) {
    dirtyMonitor.setDirty(dirty);
  }

  public Component getRoot() {
    return rootPan;
  }

  private void selectionChanged(DefaultMutableTreeNode node) {
    editorPan.removeAll();
    Component editor = getEditorForSelection(node);
    if(editor != null) {
      editorPan.add(editor, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(2, 6, 2, 6), 0, 0));
    }
    dialog.revalidate();
    dialog.repaint();
  }

  private Component getEditorForSelection(DefaultMutableTreeNode node) {
    Object userObj = node == null ? null : node.getUserObject();
    Component res = null;
    if(!(userObj instanceof NodeData)) {
      return res;
    }

    NodeData nd = (NodeData) userObj;
    IAttributeAccessor aa = nd.getAttributeAccessor();
    IAttributeEditor ed;
    
    if(aa != null) {
      ed = AttributeEditors.INSTANCE.getEditor(aa);
    } else {
      ed = AttributeEditors.INSTANCE.getEditor(nd.getType());
    }
    
    if(ed != null) {
      res = ed.getComponent(dialog.getTile(), resource, nd);
    }
    if(aa instanceof ListElementAccessor) {
      JPanel p = new JPanel(new BorderLayout());
      if(res != null) {
        p.add(res, BorderLayout.CENTER);
      }
      Component remEd = removeEditor.getComponent(nd, (ListElementAccessor) aa);
      p.add(remEd, BorderLayout.EAST);
      res = p;
    }
    return res;

  }

  private void initComponents() {
    emptyEditor = new JPanel();
    emptyEditor.setPreferredSize(new Dimension(20, 50));

    editorPan = new JPanel(new GridBagLayout()) {

      private static final long serialVersionUID = 1L;

      @Override
      public Dimension getPreferredSize() {
        Dimension res = super.getPreferredSize();
        if(res.getHeight() < emptyEditor.getPreferredSize().getHeight()) {
          res.setSize(res.getWidth(), emptyEditor.getPreferredSize().getHeight());
        }
        return res;
      }

    };

    tree = new JTree(new DefaultMutableTreeNode()) {
     
      private static final long serialVersionUID = 1L;

      @Override
      public JToolTip createToolTip() {
        MultiLineToolTip tip = new MultiLineToolTip();
        tip.setColumns(25);
        tip.setComponent(this);
        return tip;
      }
    };
    tree.setRootVisible(false);
    tree.setEditable(false);
    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree.setShowsRootHandles(true);
    tree.setCellRenderer(new NodeRenderer());

  }

  private void addComponents() {
    rootPan = new JPanel(new BorderLayout());
    rootPan.add(editorPan, BorderLayout.SOUTH);
    JScrollPane sp = new JScrollPane(tree);
    sp.setPreferredSize(new Dimension(400, Math.min(Minecraft.getMinecraft().displayHeight - 20, 500)));
    rootPan.add(sp, BorderLayout.CENTER);

  }

  private void addListeners() {
    tree.addTreeSelectionListener(new TreeSelectionListener() {

      @Override
      public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        selectionChanged(node);
      }

    });
    
    ToolTipManager.sharedInstance().registerComponent(tree);

  }

  private class DirtMonitor implements TreeModelListener {

    private boolean dirty = false;

    public void setDirty(boolean dirty) {
      if(dirty == this.dirty) {
        return;
      }
      this.dirty = dirty;
      dialog.onDirtyChanged(dirty);
    }

    public boolean isDirty() {
      return dirty;
    }

    @Override
    public void treeNodesChanged(TreeModelEvent e) {
      setDirty(true);
    }

    @Override
    public void treeNodesInserted(TreeModelEvent e) {
      setDirty(true);      
    }

    @Override
    public void treeNodesRemoved(TreeModelEvent e) {
      setDirty(true);
    }

    @Override
    public void treeStructureChanged(TreeModelEvent e) {
      setDirty(true);

      if(e.getTreePath().getLastPathComponent() instanceof EditorTreeNode) {
        EditorTreeNode node = (EditorTreeNode) e.getTreePath().getLastPathComponent();
        Enumeration<?> kids = node.children();
        if(kids != null && kids.hasMoreElements()) {
          Object lastEl = null;
          while(kids.hasMoreElements()) {
            lastEl = kids.nextElement();
          }          
          if(lastEl != null) {
            TreePath selPath = e.getTreePath().pathByAddingChild(lastEl);
            tree.expandPath(selPath);
            tree.setSelectionPath(selPath);
          }
        }
      }      
    }

  }

}

package crazypants.structures.creator.block.template.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.Display;

import crazypants.structures.Log;
import crazypants.structures.api.gen.IStructureTemplate;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.api.util.Rotation;
import crazypants.structures.creator.PacketHandler;
import crazypants.structures.creator.block.AbstractDialog;
import crazypants.structures.creator.block.template.TileTemplateEditor;
import crazypants.structures.creator.block.template.packet.PacketBuildStructure;
import crazypants.structures.creator.block.template.packet.PacketClearStructure;
import crazypants.structures.creator.block.template.packet.PacketTemplateEditorGui;
import crazypants.structures.creator.block.tree.AttributeEditors;
import crazypants.structures.creator.block.tree.FieldAccessor;
import crazypants.structures.creator.block.tree.IAttributeEditor;
import crazypants.structures.creator.block.tree.ListElementAccessor;
import crazypants.structures.creator.block.tree.NodeData;
import crazypants.structures.creator.block.tree.NodeRenderer;
import crazypants.structures.creator.block.tree.StructuresTreeNode;
import crazypants.structures.creator.block.tree.editors.RemoveEditor;
import crazypants.structures.creator.item.ExportManager;
import crazypants.structures.gen.StructureGenRegister;
import crazypants.structures.gen.io.resource.StructureResourceManager;
import crazypants.structures.gen.structure.StructureTemplate;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class DialogTemplateEditor extends AbstractDialog {

  private static final long serialVersionUID = 1L;

  private static Map<Point3i, DialogTemplateEditor> openDialogs = new HashMap<Point3i, DialogTemplateEditor>();

  public static void openDialog(TileTemplateEditor tile) {
    Point3i key = new Point3i(tile.xCoord, tile.yCoord, tile.zCoord);
    DialogTemplateEditor res = openDialogs.get(key);
    if(res == null) {
      res = new DialogTemplateEditor(tile);
      openDialogs.put(key, res);
    }
    res.open();
  }

  private final TileTemplateEditor tile;
  private final Point3i position;

  private JButton newB;
  private JButton openB;
  private JButton saveB;
  private JButton saveAsB;

  private JButton genB;
  private JButton clearB;
  private JComboBox<Rotation> rotCB;

  private JPanel editorPan;

  private JTree tree;
  private StructuresTreeNode rootNode;
  private IStructureTemplate curTemplate;

  private DefaultTreeModel treeModel;

  private RemoveEditor removeEditor = new RemoveEditor();
  private JPanel emptyEditor;

  private final DirtMonitor dirtyMonitor = new DirtMonitor();

  public DialogTemplateEditor(TileTemplateEditor tile) {
    this.tile = tile;
    position = new Point3i(tile.xCoord, tile.yCoord, tile.zCoord);
    setModal(false);
    setAlwaysOnTop(true);
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    setTitle("Template Editor");

    initComponents();
    addComponents();
    addListeners();

    if(tile.getName() != null && tile.getName().trim().length() > 0 && tile.getExportDir() != null) {
      try {
        curTemplate = loadFromFile(new File(tile.getExportDir(), tile.getName() + StructureResourceManager.TEMPLATE_EXT));
      } catch (Exception e) {
        tile.setName("NewTemplate");
        e.printStackTrace();
      }

    } else {
      tile.setName("NewTemplate");
    }
    buildTree();
  }

  public void open() {
    pack();
    setLocation(Display.getX(), Display.getY());
    setVisible(true);
    requestFocus();
  }

  private void buildTree() {

    String name = tile.getName();
    if(curTemplate == null) {
      curTemplate = new StructureTemplate(name);
    }
    if(treeModel != null) {
      treeModel.removeTreeModelListener(dirtyMonitor);
    }

    treeModel = new DefaultTreeModel(new DefaultMutableTreeNode());
    rootNode = new StructuresTreeNode(curTemplate, null, curTemplate, treeModel);
    StructuresTreeNode uidNode = new StructuresTreeNode(curTemplate, new FieldAccessor(StructureTemplate.class, String.class, "uid"), curTemplate.getUid(),
        treeModel);
    rootNode.insert(uidNode, 0);
    treeModel.setRoot(rootNode);
    treeModel.addTreeModelListener(dirtyMonitor);
    dirtyMonitor.setDirty(false);

    tree.setModel(treeModel);

    revalidate();
    repaint();
  }

  private void sendUpdatePacket() {
    PacketTemplateEditorGui packet = new PacketTemplateEditorGui(tile);
    PacketHandler.INSTANCE.sendToServer(packet);
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

    newB = new JButton("New");
    openB = new JButton("Open");
    saveAsB = new JButton("Save As");
    saveB = new JButton("Save");
    saveB.setEnabled(false);

    KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
      @Override
      public boolean dispatchKeyEvent(KeyEvent e) {
        boolean keyHandled = false;
        if(e.getID() == KeyEvent.KEY_PRESSED) {
          if(e.getKeyCode() == KeyEvent.VK_S && e.isControlDown()) {
            keyHandled = true;
            save();
          }
        }
        return keyHandled;
      }
    });

    genB = new JButton("Generate");
    clearB = new JButton("Clear");
    rotCB = new JComboBox<Rotation>(Rotation.values());
    rotCB.setSelectedIndex(0);

    tree = new JTree(new DefaultMutableTreeNode());
    tree.setRootVisible(false);
    tree.setEditable(false);
    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree.setShowsRootHandles(true);
    tree.setCellRenderer(new NodeRenderer());
  }

  private void addComponents() {
    JPanel filePan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
    filePan.add(newB);
    filePan.add(openB);
    filePan.add(saveB);
    filePan.add(saveAsB);

    JPanel generatePan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
    generatePan.setBorder(new TitledBorder("Generate"));
    generatePan.add(clearB);
    generatePan.add(new JLabel("Rot:"));
    generatePan.add(rotCB);
    generatePan.add(genB);

    JPanel treePan = new JPanel(new BorderLayout());
    treePan.add(editorPan, BorderLayout.SOUTH);
    JScrollPane sp = new JScrollPane(tree);
    sp.setPreferredSize(new Dimension(360, Math.min(Minecraft.getMinecraft().displayHeight - 20, 500)));
    treePan.add(sp, BorderLayout.CENTER);

    Container cp = getContentPane();
    cp.setLayout(new BorderLayout());
    cp.add(filePan, BorderLayout.NORTH);
    cp.add(treePan, BorderLayout.CENTER);
    cp.add(generatePan, BorderLayout.SOUTH);
  }

  private void addListeners() {

    openB.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if(!dirtyMonitor.isDirty() || checkClear()) {
          openTemplate();
        }
      }
    });

    saveAsB.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        saveAs();
      }
    });

    saveB.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        save();
      }

    });

    newB.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        if(!dirtyMonitor.isDirty() || checkClear()) {          
          tile.setName("NewTemplate");
          sendUpdatePacket();
          curTemplate = null;
          buildTree();
        }
      }

    });

    tree.addTreeSelectionListener(new TreeSelectionListener() {

      @Override
      public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        selectionChanged(node);
      }

    });

    clearB.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        clearBounds();
      }
    });

    genB.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        clearBounds();
        if(rotCB.getSelectedIndex() >= 0) {
          generate(rotCB.getItemAt(rotCB.getSelectedIndex()));
        }
      }

    });

  }

  private void selectionChanged(DefaultMutableTreeNode node) {
    editorPan.removeAll();
    Component editor = getEditorForSelection(node);
    if(editor != null) {
      editorPan.add(editor, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(2, 6, 2, 6), 0, 0));
    }
    revalidate();
    repaint();
  }

  private Component getEditorForSelection(DefaultMutableTreeNode node) {
    Object userObj = node == null ? null : node.getUserObject();
    Component res = null;
    if(!(userObj instanceof NodeData)) {
      return res;
    }

    NodeData nd = (NodeData) userObj;
    IAttributeEditor ed = AttributeEditors.INSTANCE.getEditor(nd.getType());
    if(ed != null) {
      res = ed.getComponent(nd);
    }
    if(nd.getAttributeAccessor() instanceof ListElementAccessor) {
      JPanel p = new JPanel(new BorderLayout());
      if(res != null) {
        p.add(res, BorderLayout.CENTER);
      }
      p.add(removeEditor.getComponent(nd, (ListElementAccessor) nd.getAttributeAccessor()), BorderLayout.SOUTH);
      res = p;
    }
    return res;

  }

  private void openTemplate() {
    StructureResourceManager resMan = StructureGenRegister.instance.getResourceManager();
    List<File> files = resMan.getFilesWithExt(StructureResourceManager.TEMPLATE_EXT);

    JPopupMenu menu = new JPopupMenu();
    for (File file : files) {

      final String uid = file.getName().substring(0, file.getName().length() - StructureResourceManager.TEMPLATE_EXT.length());
      final IStructureTemplate template = loadFromFile(file);
      if(template != null) {
        JMenuItem mi = new JMenuItem(file.getName());
        mi.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            openTemplate(uid, template);
          }
        });
        menu.add(mi);
      } else {
        System.out.println("DialogTemplateEditor.openRegisteredTemplate: Could not load template from file: " + file.getAbsolutePath());
      }

    }

    JMenuItem mi = new JMenuItem("...");
    mi.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        importFromFile();
      }
    });
    menu.add(mi);

    menu.show(openB, 0, 0);
  }

  private void importFromFile() {
    File startDir = new File(tile.getExportDir() == null ? ExportManager.EXPORT_DIR.getName() : tile.getExportDir());
    JFileChooser fc = new JFileChooser(startDir);
    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fc.setDialogTitle("Select Template File");
    int res = fc.showOpenDialog(this);
    if(res != JFileChooser.APPROVE_OPTION) {
      return;
    }

    IStructureTemplate sc = loadFromFile(fc.getSelectedFile());
    if(sc != null) {
      StructureGenRegister.instance.registerTemplate(sc);
      String name = sc.getUid();
      openTemplate(name, sc);
    } else {
      JOptionPane.showMessageDialog(this, "Could not load template.", "Bottoms", JOptionPane.ERROR_MESSAGE);
    }

  }

  private IStructureTemplate loadFromFile(File file) {
    String name = file.getName();
    if(name.endsWith(StructureResourceManager.TEMPLATE_EXT)) {
      name = name.substring(0, name.length() - StructureResourceManager.TEMPLATE_EXT.length());
    }

    InputStream stream = null;
    try {
      stream = new FileInputStream(file);
      StructureGenRegister.instance.getResourceManager().addResourceDirectory(file.getParentFile());
      IStructureTemplate res = StructureGenRegister.instance.getResourceManager().loadTemplate(name, stream);
      if(res != null) {
        tile.setExportDir(file.getParentFile().getAbsolutePath());
        sendUpdatePacket();
      }
      return res;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      IOUtils.closeQuietly(stream);
    }
    return null;
  }

  private void save() {

    if(curTemplate == null) {
      dirtyMonitor.setDirty(false);
      return;
    }
    String uid = curTemplate.getUid().trim();
    if(tile.getExportDir() == null) {
      tile.setExportDir(ExportManager.EXPORT_DIR.getName());
      sendUpdatePacket();
    }
    File dir = new File(tile.getExportDir());
    File file = new File(dir, uid + StructureResourceManager.TEMPLATE_EXT);

    if(ExportManager.writeToFile(file, curTemplate, Minecraft.getMinecraft().thePlayer)) {
      dirtyMonitor.setDirty(false);
      StructureGenRegister.instance.registerTemplate(curTemplate);
      Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("Saved template to: " + file.getAbsolutePath()));
    }
    Log.info("DialogTemplateEditor.save: Saved template to " + file.getAbsolutePath());

  }

  private void saveAs() {

    if(!isTemplateValid()) {
      return;
    }
    String uid = curTemplate.getUid().trim();
    File startDir = new File(tile.getExportDir() == null ? ExportManager.EXPORT_DIR.getName() : tile.getExportDir());
    JFileChooser fc = new JFileChooser(startDir);
    fc.setSelectedFile(new File(uid + StructureResourceManager.TEMPLATE_EXT));
    fc.setDialogTitle("Select Directory");
    int res = fc.showSaveDialog(this);

    if(res != JFileChooser.APPROVE_OPTION) {
      return;
    }
    File dir;
    File file = fc.getSelectedFile();
    if(file.isDirectory()) {
      dir = file;
      file = new File(dir, uid + StructureResourceManager.TEMPLATE_EXT);
    } else {
      dir = file.getParentFile();
      if(!file.exists() && !file.getName().endsWith(StructureResourceManager.TEMPLATE_EXT)) {
        file = new File(dir, file.getName() + StructureResourceManager.TEMPLATE_EXT);
      }
    }
    if(!dir.exists()) {
      dir.mkdirs();
    }
    if(!dir.exists()) {
      return;
    }

    tile.setExportDir(dir.getPath());
    sendUpdatePacket();

    String fileName = file.getName();
    if(!fileName.endsWith(StructureResourceManager.TEMPLATE_EXT)) {
      fileName = fileName + StructureResourceManager.TEMPLATE_EXT;
      file = new File(file.getParentFile(), fileName);
    }

    if(file.exists()) {
      res = JOptionPane.showConfirmDialog(this, "Replace existing file?");
      if(res != JFileChooser.APPROVE_OPTION) {
        return;
      }
    }

    if(ExportManager.writeToFile(file, curTemplate, Minecraft.getMinecraft().thePlayer)) {
      String newUid = file.getName().substring(0, file.getName().length() - StructureResourceManager.TEMPLATE_EXT.length());
      if(!newUid.equals(uid)) {
        ((StructureTemplate) curTemplate).setUid(newUid);
        tile.setName(newUid);
        sendUpdatePacket();
        buildTree();
        dirtyMonitor.setDirty(true);
      }
      dirtyMonitor.setDirty(false);
      StructureGenRegister.instance.registerTemplate(curTemplate);
    }

  }

  protected boolean isTemplateValid() {
    if(curTemplate == null) {
      return false;
    }
    if(!curTemplate.isValid()) {
      JOptionPane.showMessageDialog(this, "Current template is not valid", "Boo hoo", JOptionPane.ERROR_MESSAGE, null);
      return false;
    }

    String uid = curTemplate.getUid();
    if(uid == null || uid.trim().length() == 0) {
      JOptionPane.showMessageDialog(this, "No name specified", "Boo hoo", JOptionPane.ERROR_MESSAGE, null);
      return false;
    }
    return true;
  }

  private void openTemplate(String name, IStructureTemplate template) {
    if(name == null || template == null) {
      return;
    }

    clearBounds();
    tile.setName(name);
    sendUpdatePacket();
    curTemplate = template;
    onDirtyChanged(false);
    buildTree();

  }

  private void generate(Rotation rot) {
    sendUpdatePacket();
    PacketBuildStructure packet = new PacketBuildStructure(tile, rot);
    PacketHandler.INSTANCE.sendToServer(packet);
  }

  private void clearBounds() {
    if(tile != null) {
      PacketClearStructure packet = new PacketClearStructure(tile);
      PacketHandler.INSTANCE.sendToServer(packet);
    }
  }

  @Override
  protected void onClose() {
    openDialogs.remove(position);
    super.onClose();
  }

  private void onDirtyChanged(boolean dirty) {
    String title = curTemplate == null ? "" : curTemplate.getUid();
    title = title + StructureResourceManager.GENERATOR_EXT;
    if(dirty) {
      title += "*";
    }
    setTitle(title);
    saveB.setEnabled(dirty);
  }

  private class DirtMonitor implements TreeModelListener {

    private boolean dirty = false;

    public void setDirty(boolean dirty) {
      if(dirty == this.dirty) {
        return;
      }
      this.dirty = dirty;
      onDirtyChanged(dirty);
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
    }

  }

}

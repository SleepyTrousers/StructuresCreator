package crazypants.structures.creator.block.template.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.Display;

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

  private JButton openB;
  private JButton importB;
  private JButton exportB;
  private JButton newB;

  private JButton genB;
  private JButton clearB;
  private JComboBox<Rotation> rotCB;

  private JPanel editorPan;

  private JTree tree;
  private DefaultMutableTreeNode rootNode;
  private IStructureTemplate curTemplate;

  private DefaultTreeModel treeModel;

  private RemoveEditor removeEditor = new RemoveEditor();

  private JPanel emptyEditor;

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

    if(tile.getName() != null && tile.getName().trim().length() > 0) {
      curTemplate = StructureGenRegister.instance.getStructureTemplate(tile.getName(), true);
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
    treeModel = new DefaultTreeModel(new DefaultMutableTreeNode());
    rootNode = new StructuresTreeNode(curTemplate, null, curTemplate, treeModel);
    StructuresTreeNode uidNode = new StructuresTreeNode(curTemplate, new FieldAccessor(StructureTemplate.class, String.class, "uid"), curTemplate.getUid(),
        treeModel);
    rootNode.insert(uidNode, 0);
    treeModel.setRoot(rootNode);

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
    
    editorPan = new JPanel(new GridBagLayout())  {

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
        
    //editorPan.setBorder(new BevelBorder(BevelBorder.LOWERED));

    newB = new JButton("New");
    openB = new JButton("Open");
    importB = new JButton("Import");
    exportB = new JButton("Export");

    genB = new JButton("Generate");
    clearB = new JButton("Clear");
    rotCB = new JComboBox<Rotation>(Rotation.values());
    rotCB.setSelectedIndex(0);

    rootNode = new DefaultMutableTreeNode();
    tree = new JTree(rootNode);
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
    filePan.add(importB);
    filePan.add(exportB);

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
        if(checkClear()) {
          openRegisteredTemplate();
        }
      }
    });

    importB.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if(checkClear()) {
          importFromFile();
        }
      }
    });

    exportB.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        exportToFile();
      }
    });

    newB.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        if(checkClear()) {
          clearBounds();
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
        if(checkClear()) {
          clearBounds();
        }
      }
    });

    genB.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        if(checkClear()) {
          clearBounds();
          if(rotCB.getSelectedIndex() >= 0) {
            generate(rotCB.getItemAt(rotCB.getSelectedIndex()));
          }
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
        
    //Component res = new JLabel("  ");        
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

  private void openRegisteredTemplate() {
    Map<String, IStructureTemplate> comps = StructureGenRegister.instance.getStructureTemplateMap();
    JPopupMenu menu = new JPopupMenu();
    for (Entry<String, IStructureTemplate> comp : comps.entrySet()) {
      final Entry<String, IStructureTemplate> c = comp;
      JMenuItem mi = new JMenuItem(comp.getKey());
      mi.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          openTemplate(c.getKey(), c.getValue());
        }
      });
      menu.add(mi);
    }
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
    } else {
      JOptionPane.showMessageDialog(this, "Could not load template.", "Bottoms", JDialog.ERROR);
    }
    String name = sc.getUid();

    openTemplate(name, sc);
  }

  private IStructureTemplate loadFromFile(File file) {
    String name = file.getName();
    if(name.endsWith(StructureResourceManager.TEMPLATE_EXT)) {
      name = name.substring(0, name.length() - StructureResourceManager.TEMPLATE_EXT.length());
    }

    InputStream stream = null;
    try {
      stream = new FileInputStream(file);
      return StructureGenRegister.instance.getResourceManager().loadTemplate(name, stream);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      IOUtils.closeQuietly(stream);
    }
    return null;
  }

  private void exportToFile() {

    if(curTemplate == null) {
      return;
    }
    if(!curTemplate.isValid()) {
      JOptionPane.showMessageDialog(this, "Current template is not valid", "Boo hoo", JOptionPane.ERROR_MESSAGE, null);
      return;
    }

    String name = curTemplate.getUid();
    if(name == null || name.trim().length() == 0) {
      JOptionPane.showMessageDialog(this, "No name specified", "Boo hoo", JOptionPane.ERROR_MESSAGE, null);
      return;
    }

    File startDir = new File(tile.getExportDir() == null ? ExportManager.EXPORT_DIR.getName() : tile.getExportDir());
    JFileChooser fc = new JFileChooser(startDir);
    fc.setSelectedFile(new File(name + StructureResourceManager.TEMPLATE_EXT));
    fc.setDialogTitle("Select Directory");
    int res = fc.showSaveDialog(this);

    if(res != JFileChooser.APPROVE_OPTION) {
      return;
    }
    File dir;
    File file = fc.getSelectedFile();
    if(file.isDirectory()) {
      dir = file;
      file = new File(dir, name + StructureResourceManager.TEMPLATE_EXT);
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

    if(file.exists()) {
      res = JOptionPane.showConfirmDialog(this, "Replace existing file?");
      if(res != JFileChooser.APPROVE_OPTION) {
        return;
      }
    }

    ExportManager.writeToFile(file, curTemplate, Minecraft.getMinecraft().thePlayer);
    StructureGenRegister.instance.registerTemplate(curTemplate);
  }

  private void openTemplate(String name, IStructureTemplate template) {
    if(name == null || template == null) {
      return;
    }

    clearBounds();
    tile.setName(name);
    sendUpdatePacket();
    curTemplate = template;
    //setTitle(curTemplate == null ? "" : curTemplate.getUid());
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

}

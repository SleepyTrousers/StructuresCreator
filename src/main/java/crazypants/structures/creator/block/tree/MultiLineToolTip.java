package crazypants.structures.creator.block.tree;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JToolTip;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolTipUI;

public class MultiLineToolTip extends JToolTip {

  private static final long serialVersionUID = 1L;

  private int columns = 0;

  private int fixedwidth = 0;
  
  public MultiLineToolTip() {
    updateUI();
  }

  @Override
  public void updateUI() {
    setUI(MultiLineToolTipUI.createUI(this));
  }

  public void setColumns(int columns) {
    this.columns = columns;
    this.fixedwidth = 0;
  }

  public int getColumns() {
    return columns;
  }

  public void setFixedWidth(int width) {
    this.fixedwidth = width;
    this.columns = 0;
  }

  public int getFixedWidth() {
    return fixedwidth;
  }
  
}

class MultiLineToolTipUI extends BasicToolTipUI {
  
  private static MultiLineToolTipUI sharedInstance = new MultiLineToolTipUI();

  private CellRendererPane rendererPane;

  private JTextArea textArea;

  public static ComponentUI createUI(JComponent c) {
    return sharedInstance;
  }

  public MultiLineToolTipUI() {    
  }

  @Override
  public void installUI(JComponent c) {
    super.installUI(c);
    rendererPane = new CellRendererPane();
    c.add(rendererPane);
  }

  @Override
  public void uninstallUI(JComponent c) {
    super.uninstallUI(c);
    c.remove(rendererPane);
    rendererPane = null;
  }

  @Override
  public void paint(Graphics g, JComponent c) {
    Dimension size = c.getSize();
    textArea.setBackground(c.getBackground());
    rendererPane.paintComponent(g, textArea, c, 1, 1, size.width - 1,
        size.height - 1, true);
  }

  @Override
  public Dimension getPreferredSize(JComponent c) {
    String tipText = ((JToolTip) c).getTipText();
    if(tipText == null) {
      return new Dimension(0, 0);
    }
    textArea = new JTextArea(tipText);
    textArea.setBorder(new EmptyBorder(2, 4, 2, 4));
    rendererPane.removeAll();
    rendererPane.add(textArea);
    textArea.setWrapStyleWord(true);
    int width = ((MultiLineToolTip) c).getFixedWidth();
    int columns = ((MultiLineToolTip) c).getColumns();

    if(columns > 0) {
      textArea.setColumns(columns);
      textArea.setSize(0, 0);
      textArea.setLineWrap(true);
      textArea.setSize(textArea.getPreferredSize());
    } else if(width > 0) {
      textArea.setLineWrap(true);
      Dimension d = textArea.getPreferredSize();
      d.width = width;
      d.height++;
      textArea.setSize(d);
    } else
      textArea.setLineWrap(false);

    Dimension dim = textArea.getPreferredSize();

    dim.height += 1;
    dim.width += 1;
    return dim;
  }

  @Override
  public Dimension getMinimumSize(JComponent c) {
    return getPreferredSize(c);
  }

  @Override
  public Dimension getMaximumSize(JComponent c) {
    return getPreferredSize(c);
  }
}

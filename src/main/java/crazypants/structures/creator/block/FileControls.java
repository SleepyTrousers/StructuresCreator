package crazypants.structures.creator.block;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import crazypants.structures.creator.block.tree.Icons;

public class FileControls {

  private final JButton newB;
  private final JButton openB;
  private final JButton saveB;
  private final JButton saveAsB;

  private final JPanel filePan;

  public FileControls(final AbstractResourceDialog dialog) {

    newB = new JButton(Icons.NEW);
    newB.setToolTipText("New");
    openB = new JButton(Icons.OPEN);
    openB.setToolTipText("Open");
    saveAsB = new JButton(Icons.SAVE_AS);
    saveAsB.setToolTipText("Save As");
    saveB = new JButton(Icons.SAVE);
    saveB.setToolTipText("Save");    

    filePan = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
    filePan.add(newB);
    filePan.add(openB);
    filePan.add(saveB);
    filePan.add(saveAsB);

    openB.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        dialog.openResource();
      }

    });

    saveAsB.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        dialog.saveAs();
      }
    });

    saveB.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        dialog.save();
      }

    });

    newB.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        dialog.createNewResource();
      }

    });
  }

  public JButton getNewB() {
    return newB;
  }

  public JButton getOpenB() {
    return openB;
  }

  public JButton getSaveB() {
    return saveB;
  }

  public JButton getSaveAsB() {
    return saveAsB;
  }

  public JPanel getPanel() {
    return filePan;
  }

}

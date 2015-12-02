package crazypants.structures.creator.block;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.sun.glass.events.KeyEvent;

import net.minecraft.client.Minecraft;

public abstract class AbstractDialog extends JDialog {

  private static final long serialVersionUID = 1L;

  protected AbstractDialog() {
    setModal(false);
    setAlwaysOnTop(true);
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    addWindowListener(new WindowAdapter() {

      @Override
      public void windowClosed(WindowEvent e) {
        onClose();
      }
    });

    ActionListener al = new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        onClose();
        setVisible(false);
      }
    };
    KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
    JRootPane rootPane = getRootPane();
    rootPane.registerKeyboardAction(al, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
  }

  protected void onClose() {
    Mouse.setCursorPosition(Display.getX() - Display.getWidth() / 2, Display.getY() - Display.getHeight() / 2);
    if(Minecraft.getMinecraft().thePlayer != null) {
      Minecraft.getMinecraft().thePlayer.closeScreen();
    }
  }
  
  protected boolean checkClear() {
    return JFileChooser.APPROVE_OPTION == JOptionPane.showConfirmDialog(this, "Clear existing data?");
  }

}

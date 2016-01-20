package crazypants.structures.creator.endercore.client.gui.button;

import crazypants.structures.creator.endercore.api.client.gui.IGuiScreen;
import crazypants.structures.creator.endercore.client.render.EnderWidget;

public class CheckBox extends ToggleButton {

    public CheckBox(IGuiScreen gui, int id, int x, int y) {
        super(gui, id, x, y, EnderWidget.BUTTON, EnderWidget.BUTTON_CHECKED);
    }

}

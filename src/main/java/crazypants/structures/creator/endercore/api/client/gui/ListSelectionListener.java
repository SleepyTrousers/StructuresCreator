package crazypants.structures.creator.endercore.api.client.gui;

import crazypants.structures.creator.endercore.client.gui.widget.GuiScrollableList;

public interface ListSelectionListener<T> {

    void selectionChanged(GuiScrollableList<T> list, int selectedIndex);

}

package forestry.sorting.gui;

import java.util.Collection;

import com.mojang.blaze3d.matrix.MatrixStack;

import forestry.core.gui.GuiForestry;

public interface ISelectableProvider<S> {
    Collection<S> getEntries();

    void onSelect(S selectable);

    void draw(GuiForestry gui, S selectable, MatrixStack transform, int y, int x);

    String getName(S selectable);
}

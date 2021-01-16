package forestry.sorting.gui;

import java.util.Collection;

import net.minecraft.util.text.ITextComponent;

import com.mojang.blaze3d.matrix.MatrixStack;

import forestry.core.gui.GuiForestry;

public interface ISelectableProvider<S> {
	Collection<S> getEntries();

	void onSelect(S selectable);

	void draw(GuiForestry gui, S selectable, MatrixStack transform, int y, int x);

	ITextComponent getName(S selectable);
}

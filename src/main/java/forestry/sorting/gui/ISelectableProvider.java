package forestry.sorting.gui;

import java.util.Collection;

import net.minecraft.network.chat.Component;

import com.mojang.blaze3d.vertex.PoseStack;

import forestry.core.gui.GuiForestry;

public interface ISelectableProvider<S> {
	Collection<S> getEntries();

	void onSelect(S selectable);

	void draw(GuiForestry gui, S selectable, PoseStack transform, int y, int x);

	Component getName(S selectable);
}

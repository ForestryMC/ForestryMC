/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.gui.widgets;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.gui.GuiForestry;

@OnlyIn(Dist.CLIENT)
public class WidgetManager {

	public final GuiForestry gui;
	public final Minecraft minecraft;
	protected final List<Widget> widgets = new ArrayList<>();

	public WidgetManager(GuiForestry gui) {
		this.gui = gui;
		this.minecraft = Minecraft.getInstance();
	}

	public void add(Widget slot) {
		this.widgets.add(slot);
	}

	public void remove(Widget slot) {
		this.widgets.remove(slot);
	}

	public void clear() {
		this.widgets.clear();
	}

	public List<Widget> getWidgets() {
		return widgets;
	}

	@Nullable
	public Widget getAtPosition(double mX, double mY) {
		for (Widget slot : widgets) {
			if (slot.isMouseOver(mX, mY)) {
				return slot;
			}
		}

		return null;
	}

	public void drawWidgets() {
		for (Widget slot : widgets) {
			slot.draw(0, 0);
		}
	}

	public void updateWidgets(int mouseX, int mouseY) {
		for (Widget slot : widgets) {
			slot.update(mouseX, mouseY);
		}
	}

	public void handleMouseClicked(double mouseX, double mouseY, int mouseButton) {
		Widget slot = getAtPosition(mouseX - gui.getGuiLeft(), mouseY - gui.getGuiTop());
		if (slot != null) {
			slot.handleMouseClick(mouseX, mouseY, mouseButton);
		}
	}

	public boolean handleMouseRelease(double mouseX, double mouseY, int eventType) {
		boolean hasToStop = false;
		for (Widget slot : widgets) {
			hasToStop |= slot.handleMouseRelease(mouseX - gui.getGuiLeft(), mouseY - gui.getGuiTop(), eventType);
		}
		return hasToStop;
	}

	public void handleMouseMove(int mouseX, int mouseY, int mouseButton, long time) {
		for (Widget slot : widgets) {
			slot.handleMouseMove(mouseX, mouseY, mouseButton, time);
		}
	}
}

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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;

import forestry.core.gui.GuiForestry;
import forestry.core.proxy.Proxies;

public class WidgetManager {

	public final GuiForestry gui;
	public final Minecraft minecraft;
	protected final List<Widget> widgets = new ArrayList<>();

	public WidgetManager(GuiForestry gui) {
		this.gui = gui;
		this.minecraft = Proxies.common.getClientInstance();
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

	public Widget getAtPosition(int mX, int mY) {
		for (Widget slot : widgets) {
			if (slot.isMouseOver(mX, mY)) {
				return slot;
			}
		}

		return null;
	}

	public void drawWidgets() {
		gui.setZLevel(100.0F);
		GuiForestry.getItemRenderer().zLevel = 100.0F;
		for (Widget slot : widgets) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			slot.draw(0, 0);
		}
		gui.setZLevel(0.0F);
		GuiForestry.getItemRenderer().zLevel = 0.0F;

	}

	public void handleMouseClicked(int mouseX, int mouseY, int mouseButton) {
		Widget slot = getAtPosition(mouseX - gui.getGuiLeft(), mouseY - gui.getGuiTop());
		if (slot != null) {
			slot.handleMouseClick(mouseX, mouseY, mouseButton);
		}
	}

	public void handleMouseRelease(int mouseX, int mouseY, int eventType) {
		for (Widget slot : widgets) {
			slot.handleMouseRelease(mouseX, mouseY, eventType);
		}
	}

	public void handleMouseMove(int mouseX, int mouseY, int mouseButton, long time) {
		for (Widget slot : widgets) {
			slot.handleMouseMove(mouseX, mouseY, mouseButton, time);
		}
	}
}

/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.gui;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;

import forestry.core.gadgets.TileForestry;
import forestry.core.gui.widgets.Widget;
import forestry.core.proxy.Proxies;

public class WidgetManager {

	public GuiForestry<? extends TileForestry> gui;
	public Minecraft minecraft;
	protected ArrayList<Widget> widgets = new ArrayList<Widget>();

	public WidgetManager(GuiForestry<? extends TileForestry> gui) {
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

	protected Widget getAtPosition(int mX, int mY) {
		for (Widget slot : widgets) {
			if (slot.isMouseOver(mX, mY))
				return slot;
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
		if (slot != null)
			slot.handleMouseClick(mouseX, mouseY, mouseButton);
	}

	public void handleMouseRelease(int mouseX, int mouseY, int eventType) {
		for (Widget slot : widgets) {
			slot.handleMouseRelease(mouseX, mouseY, eventType);
		}
	}

	public void handleMouseMove(int mouseX, int mouseY,  int mouseButton, long time) {
		for (Widget slot : widgets) {
			slot.handleMouseMove(mouseX, mouseY, mouseButton, time);
		}
	}
}

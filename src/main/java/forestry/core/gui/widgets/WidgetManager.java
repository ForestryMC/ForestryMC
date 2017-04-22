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

import forestry.core.gui.GuiForestry;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class WidgetManager {

	public final GuiForestry gui;
	public final Minecraft minecraft;
	protected final List<Widget> widgets = new ArrayList<>();

	public WidgetManager(GuiForestry gui) {
		this.gui = gui;
		this.minecraft = Minecraft.getMinecraft();
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
	public Widget getAtPosition(int mX, int mY) {
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

	public void handleMouseClicked(int mouseX, int mouseY, int mouseButton) {
		Widget slot = getAtPosition(mouseX - gui.getGuiLeft(), mouseY - gui.getGuiTop());
		if (slot != null) {
			slot.handleMouseClick(mouseX, mouseY, mouseButton);
		}
	}

	public boolean handleMouseRelease(int mouseX, int mouseY, int eventType) {
		boolean hasToStop = false;
		for (Widget slot : widgets) {
			hasToStop|=slot.handleMouseRelease(mouseX, mouseY, eventType);
		}
		return hasToStop;
	}

	public void handleMouseMove(int mouseX, int mouseY, int mouseButton, long time) {
		for (Widget slot : widgets) {
			slot.handleMouseMove(mouseX, mouseY, mouseButton, time);
		}
	}
}

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

import forestry.core.gui.tooltips.IToolTipProvider;
import forestry.core.gui.tooltips.ToolTip;

/**
 * Basic non-ItemStack slot
 */
public abstract class Widget implements IToolTipProvider {
	protected final WidgetManager manager;
	protected final int xPos;
	protected final int yPos;
	protected int width = 16;
	protected int height = 16;

	public Widget(WidgetManager manager, int xPos, int yPos) {
		this.manager = manager;
		this.xPos = xPos;
		this.yPos = yPos;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public abstract void draw(int startX, int startY);

	@Nullable
	@Override
	public ToolTip getToolTip(int mouseX, int mouseY) {
		return null;
	}

	// Not fully implemented
	@Override
	public boolean isToolTipVisible() {
		return true;
	}

	@Override
	public boolean isMouseOver(int mouseX, int mouseY) {
		return mouseX >= xPos && mouseX <= xPos + this.width && mouseY >= yPos && mouseY <= yPos + this.height;
	}

	public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
	}

	public void handleMouseRelease(int mouseX, int mouseY, int eventType) {
	}

	public void handleMouseMove(int mouseX, int mouseY, int mouseButton, long time) {
	}
}

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
package forestry.core.gui;

public class GuiId {
	private final int id;
	private final GuiType guiType;
	private final Class<? extends IGuiHandlerForestry> guiHandlerClass;

	GuiId(int id, GuiType guiType, Class<? extends IGuiHandlerForestry> guiHandlerClass) {
		this.id = id;
		this.guiType = guiType;
		this.guiHandlerClass = guiHandlerClass;
	}

	public GuiType getGuiType() {
		return guiType;
	}

	public Class<? extends IGuiHandlerForestry> getGuiHandlerClass() {
		return guiHandlerClass;
	}

	public int getId() {
		return id;
	}
}

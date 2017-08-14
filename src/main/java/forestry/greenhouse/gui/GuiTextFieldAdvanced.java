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
package forestry.greenhouse.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class GuiTextFieldAdvanced extends GuiTextField {

	public ITextFieldListener listener;

	public GuiTextFieldAdvanced(int componentId, FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height) {
		super(componentId, fontrendererObj, x, y, par5Width, par6Height);
	}

	public void setListener(ITextFieldListener listener) {
		this.listener = listener;
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		boolean wasFocused = isFocused();
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (wasFocused && !isFocused()) {
			if (listener != null) {
				setText(listener.parseValue(getText()));
			}
		}
	}

	public interface ITextFieldListener {
		String parseValue(String value);
	}
}

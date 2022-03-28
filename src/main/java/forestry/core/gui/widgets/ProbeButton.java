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

import net.minecraft.network.chat.TranslatableComponent;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import forestry.api.core.tooltips.ToolTip;
import forestry.core.gui.GuiEscritoire;
import forestry.core.network.packets.PacketGuiSelectRequest;
import forestry.core.utils.NetworkUtil;
import forestry.core.utils.SoundUtil;

public class ProbeButton extends Widget {

	private final GuiEscritoire guiEscritoire;
	private boolean pressed;

	public ProbeButton(GuiEscritoire guiEscritoire, WidgetManager manager, int xPos, int yPos) {
		super(manager, xPos, yPos);
		this.guiEscritoire = guiEscritoire;
		width = 22;
		height = 25;
	}

	@Override
	public void draw(PoseStack transform, int startY, int startX) {
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0F);
		RenderSystem.setShaderTexture(0, manager.gui.textureFile);
		manager.gui.blit(transform, startX + xPos, startY + yPos, 228, pressed ? 47 : 22, width, height);
	}

	@Override
	public ToolTip getToolTip(int mouseX, int mouseY) {
		ToolTip tooltip = new ToolTip();
		tooltip.add(new TranslatableComponent("for.gui.escritoire.probe"));
		return tooltip;
	}

	@Override
	public void handleMouseClick(double mouseX, double mouseY, int mouseButton) {
		pressed = true;
		NetworkUtil.sendToServer(new PacketGuiSelectRequest(-1, 0));
		SoundUtil.playButtonClick();
	}

	@Override
	public boolean handleMouseRelease(double mouseX, double mouseY, int eventType) {
		if (pressed) {
			pressed = false;
		}
		return false;
	}

	@Override
	public void handleMouseMove(int mouseX, int mouseY, int mouseButton, long time) {
		if (manager.getAtPosition(mouseX - guiEscritoire.getGuiLeft(), mouseY - guiEscritoire.getGuiTop()) != this) {
			pressed = false;
		}
	}
}

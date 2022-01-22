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
package forestry.factory.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;

import forestry.core.config.Constants;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.gui.widgets.TankWidget;
import forestry.core.render.ColourProperties;
import forestry.core.utils.Translator;
import forestry.factory.tiles.TileBottler;

public class GuiBottler extends GuiForestryTitled<ContainerBottler> {
	private final TileBottler tile;

	public GuiBottler(ContainerBottler container, Inventory inventory, Component title) {
		super(Constants.TEXTURE_PATH_GUI + "/bottler.png", container, inventory, title);
		this.tile = container.getTile();
		widgetManager.add(new TankWidget(this.widgetManager, 80, 14, 0));
	}

	@Override
	protected void renderBg(PoseStack transform, float partialTicks, int mouseY, int mouseX) {
		bindTexture(textureFile);

		int x = (width - imageWidth) / 2;
		int y = (height - imageHeight) / 2;
		blit(transform, x, y, 0, 0, imageWidth, imageHeight);

		//RenderHelper.enableGUIStandardItemLighting(); TODO Gui Light
		// RenderSystem.disableLighting();
		// RenderSystem.enableRescaleNormal();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		transform.pushPose();
		{
			transform.translate(leftPos, topPos, 0.0F);
			drawWidgets(transform);
		}
		transform.popPose();

		String name = Translator.translateToLocal(tile.getUnlocalizedTitle());
		textLayout.line = 5;
		textLayout.drawCenteredLine(transform, name, 0, ColourProperties.INSTANCE.get("gui.title"));
		bindTexture(textureFile);

		bindTexture(textureFile);

		TileBottler bottler = tile;
		int progressArrow = bottler.getProgressScaled(22);
		if (progressArrow > 0) {
			if (bottler.isFillRecipe) {
				blit(transform, leftPos + 108, topPos + 35, 177, 74, progressArrow, 16);
			} else {
				blit(transform, leftPos + 46, topPos + 35, 177, 74, progressArrow, 16);
			}
		}
	}

	@Override
	protected void addLedgers() {
		addErrorLedger(tile);
		addHintLedger("bottler");
		addPowerLedger(tile.getEnergyManager());
	}
}

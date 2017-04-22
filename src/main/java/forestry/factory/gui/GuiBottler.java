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

import forestry.core.config.Constants;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.gui.widgets.TankWidget;
import forestry.core.render.ColourProperties;
import forestry.core.utils.Translator;
import forestry.factory.tiles.TileBottler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiBottler extends GuiForestryTitled<ContainerBottler> {
	private final TileBottler tile;

	public GuiBottler(InventoryPlayer inventory, TileBottler tile) {
		super(Constants.TEXTURE_PATH_GUI + "/bottler.png", new ContainerBottler(inventory, tile), tile);
		this.tile = tile;
		widgetManager.add(new TankWidget(this.widgetManager, 80, 14, 0));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		bindTexture(textureFile);

		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.enableRescaleNormal();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		{
			GlStateManager.translate(guiLeft, guiTop, 0.0F);
			drawWidgets();
		}
		GlStateManager.popMatrix();

		String name = Translator.translateToLocal(tile.getUnlocalizedTitle());
		textLayout.line = 5;
		textLayout.drawCenteredLine(name, 0, ColourProperties.INSTANCE.get("gui.title"));
		bindTexture(textureFile);

		bindTexture(textureFile);

		TileBottler bottler = tile;
		int progressArrow = bottler.getProgressScaled(22);
		if (progressArrow > 0) {
			if (bottler.isFillRecipe) {
				drawTexturedModalRect(guiLeft + 108, guiTop + 35, 177, 74, progressArrow, 16);
			} else {
				drawTexturedModalRect(guiLeft + 46, guiTop + 35, 177, 74, progressArrow, 16);
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

/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.energy.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.IIcon;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import org.lwjgl.opengl.GL11;

import forestry.core.config.Defaults;
import forestry.core.gui.WidgetManager;
import forestry.core.gui.widgets.TankWidget;
import forestry.core.gui.widgets.Widget;
import forestry.core.proxy.Proxies;
import forestry.core.render.SpriteSheet;
import forestry.core.utils.StringUtil;
import forestry.energy.gadgets.EngineBronze;

public class GuiEngineBronze extends GuiEngine {

	protected class BiogasSlot extends Widget {

		EngineBronze engine;

		public BiogasSlot(WidgetManager manager, int xPos, int yPos, EngineBronze engine) {
			super(manager, xPos, yPos);
			this.engine = engine;
			this.height = 16;
		}

		@Override
		public void draw(int startX, int startY) {

			if (engine == null || engine.totalTime <= 0)
				return;

			Fluid fluid = FluidRegistry.getFluid(engine.currentFluidId);
			if (fluid == null)
				return;
			IIcon liquidIcon = fluid.getIcon();
			if (liquidIcon == null)
				return;

			int squaled = (engine.burnTime * height) / engine.totalTime;
			if (squaled > height)
				squaled = height;

			Proxies.common.bindTexture(SpriteSheet.BLOCKS);
			int start = 0;

			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0F);
			while (true) {
				int x = 0;

				if (squaled > 16) {
					x = 16;
					squaled -= 16;
				} else {
					x = squaled;
					squaled = 0;
				}

				// drawTexturedModalRect(startX + xPos, startY + yPos + height - x - start, imgColumn * 16, imgLine * 16, 16, 16 - (16 - x));
				manager.gui.drawTexturedModelRectFromIcon(startX + xPos, startY + yPos + height - x - start, liquidIcon, 16, 16 - (16 - x));
				start = start + 16;

				if (x == 0 || squaled == 0)
					break;
			}

			Proxies.common.bindTexture(textureFile);
		}

		@Override
		public String getLegacyTooltip(EntityPlayer player) {
			Fluid fluid = FluidRegistry.getFluid(engine.currentFluidId);
			if (fluid == null)
				return StringUtil.localize("gui.empty");

			String tooltip = fluid.getLocalizedName();
			return tooltip;
		}
	}

	public GuiEngineBronze(InventoryPlayer inventory, EngineBronze tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/bioengine.png", new ContainerEngineBronze(inventory, tile), tile);
		widgetManager.add(new TankWidget(this.widgetManager, 89, 19, 0));
		widgetManager.add(new TankWidget(this.widgetManager, 107, 19, 1));

		widgetManager.add(new BiogasSlot(this.widgetManager, 30, 47, tile));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		String name = StringUtil.localize("tile.for.engine.2");
		this.fontRendererObj.drawString(name, getCenteredOffset(name), 6, fontColor.get("gui.title"));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);
		EngineBronze engine = (EngineBronze) tile;

		int temp = engine.getOperatingTemperatureScaled(16);
		if (temp > 16)
			temp = 16;
		if (temp > 0)
			drawTexturedModalRect(guiLeft + 53, guiTop + 47 + 16 - temp, 176, 60 + 16 - temp, 4, temp);

	}
}

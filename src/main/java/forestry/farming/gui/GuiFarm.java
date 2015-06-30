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
package forestry.farming.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmLogic;
import forestry.core.config.Defaults;
import forestry.core.gui.ClimateLedger;
import forestry.core.gui.GuiForestry;
import forestry.core.gui.OwnerLedger;
import forestry.core.gui.WidgetManager;
import forestry.core.gui.tooltips.ToolTip;
import forestry.core.gui.widgets.SocketWidget;
import forestry.core.gui.widgets.TankWidget;
import forestry.core.gui.widgets.Widget;
import forestry.core.interfaces.IClimatised;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;
import forestry.farming.multiblock.IFarmController;
import forestry.farming.multiblock.TileFarm;

public class GuiFarm extends GuiForestry<ContainerFarm, TileFarm> {

	public GuiFarm(EntityPlayer player, TileFarm tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/mfarm.png", new ContainerFarm(player.inventory, tile), tile);

		widgetManager.add(new TankWidget(widgetManager, 15, 19, 0).setOverlayOrigin(216, 18));

		widgetManager.add(new SocketWidget(widgetManager, 69, 40, tile, 0));

		widgetManager.add(new FarmLogicSlot(widgetManager, 69, 22, FarmDirection.NORTH));
		widgetManager.add(new FarmLogicSlot(widgetManager, 69, 58, FarmDirection.SOUTH));
		widgetManager.add(new FarmLogicSlot(widgetManager, 51, 40, FarmDirection.EAST));
		widgetManager.add(new FarmLogicSlot(widgetManager, 87, 40, FarmDirection.WEST));

		this.xSize = 216;
		this.ySize = 220;
	}

	@Override
	protected void initLedgers() {
		super.initLedgers();
		IFarmController farmController = inventory.getFarmController();
		ledgerManager.add(new ClimateLedger(ledgerManager, farmController));
		ledgerManager.add(new FarmLedger(ledgerManager, farmController.getFarmLedgerDelegate()));
		ledgerManager.add(new OwnerLedger(ledgerManager, farmController));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		String title = StringUtil.localize("gui.farm.title");
		this.fontRendererObj.drawString(title, getCenteredOffset(title), 6, fontColor.get("gui.title"));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		// Fuel remaining
		int fertilizerRemain = inventory.getFarmController().getStoredFertilizerScaled(16);
		if (fertilizerRemain > 0) {
			drawTexturedModalRect(guiLeft + 81, guiTop + 94 + 17 - fertilizerRemain, xSize, 17 - fertilizerRemain, 4, fertilizerRemain);
		}
	}

	private class FarmLogicSlot extends Widget {

		private final FarmDirection farmDirection;

		public FarmLogicSlot(WidgetManager manager, int xPos, int yPos, FarmDirection farmDirection) {
			super(manager, xPos, yPos);
			this.farmDirection = farmDirection;
		}

		private IFarmLogic getLogic() {
			return inventory.getFarmController().getFarmLogic(farmDirection);
		}

		private IIcon getIconIndex() {
			if (getLogic() == null) {
				return null;
			}
			return getLogic().getIcon();
		}

		@Override
		public void draw(int startX, int startY) {
			if (getLogic() == null) {
				return;
			}

			if (getIconIndex() != null) {
				GL11.glDisable(GL11.GL_LIGHTING);
				Proxies.common.bindTexture(getLogic().getSpriteSheet());
				manager.gui.drawTexturedModelRectFromIcon(startX + xPos, startY + yPos, getIconIndex(), 16, 16);
				GL11.glEnable(GL11.GL_LIGHTING);
			}

		}

		@Override
		public ToolTip getToolTip() {
			return toolTip;
		}

		protected final ToolTip toolTip = new ToolTip(250) {
			@Override
			public void refresh() {
				toolTip.clear();
				if (getLogic() == null) {
					return;
				}
				toolTip.add(getLogic().getName());
				toolTip.add("Fertilizer: " + getLogic().getFertilizerConsumption());
				toolTip.add("Water: " + getLogic().getWaterConsumption(inventory.getFarmController().getFarmLedgerDelegate().getHydrationModifier()));
			}
		};
	}

}

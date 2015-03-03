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
import net.minecraft.init.Items;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;

import forestry.api.farming.IFarmLogic;
import forestry.core.config.Defaults;
import forestry.core.gui.GuiForestry;
import forestry.core.gui.Ledger;
import forestry.core.gui.WidgetManager;
import forestry.core.gui.tooltips.ToolTip;
import forestry.core.gui.widgets.SocketWidget;
import forestry.core.gui.widgets.TankWidget;
import forestry.core.gui.widgets.Widget;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;
import forestry.farming.gadgets.TileFarmPlain;

public class GuiFarm extends GuiForestry<ContainerFarm, TileFarmPlain> {

	protected class FarmLedger extends Ledger {

		public FarmLedger() {
			super(ledgerManager);
			maxHeight = 118;
			overlayColor = fontColor.get("ledger.farm.background");
		}

		@Override
		public void draw(int x, int y) {

			// Draw background
			drawBackground(x, y);

			// Draw icon
			drawIcon(Items.water_bucket.getIconFromDamage(0), x + 3, y + 4);

			if (!isFullyOpened()) {
				return;
			}

			fontRendererObj.drawStringWithShadow(StringUtil.localize("gui.hydration"), x + 22, y + 8, fontColor.get("ledger.power.header"));
			fontRendererObj.drawStringWithShadow(StringUtil.localize("gui.hydr.heat") + ":", x + 22, y + 20, fontColor.get("ledger.power.subheader"));
			fontRendererObj.drawString(StringUtil.floatAsPercent(inventory.getHydrationTempModifier()), x + 22, y + 32, fontColor.get("ledger.power.text"));
			fontRendererObj.drawStringWithShadow(StringUtil.localize("gui.hydr.humid") + ":", x + 22, y + 44, fontColor.get("ledger.power.subheader"));
			fontRendererObj.drawString(StringUtil.floatAsPercent(inventory.getHydrationHumidModifier()), x + 22, y + 56, fontColor.get("ledger.power.text"));
			fontRendererObj.drawStringWithShadow(StringUtil.localize("gui.hydr.rainfall") + ":", x + 22, y + 68, fontColor.get("ledger.power.subheader"));
			fontRendererObj.drawString(StringUtil.floatAsPercent(inventory.getHydrationRainfallModifier()) + " (" + inventory.getDrought() + " d)", x + 22, y + 80,
					fontColor.get("ledger.power.text"));
			fontRendererObj.drawStringWithShadow(StringUtil.localize("gui.hydr.overall") + ":", x + 22, y + 92, fontColor.get("ledger.power.subheader"));
			fontRendererObj.drawString(StringUtil.floatAsPercent(inventory.getHydrationModifier()), x + 22, y + 104, fontColor.get("ledger.power.text"));

		}

		@Override
		public String getTooltip() {
			return StringUtil.floatAsPercent(inventory.getHydrationModifier()) + " " + StringUtil.localize("gui.hydration");
		}
	}

	private class FarmLogicSlot extends Widget {

		private final int slot;

		public FarmLogicSlot(WidgetManager manager, int xPos, int yPos, int slot) {
			super(manager, xPos, yPos);
			this.slot = slot;
		}

		private IFarmLogic getLogic() {
			return inventory.getFarmLogics()[slot];
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
				toolTip.add("Water: " + getLogic().getWaterConsumption(inventory.getHydrationModifier()));
			}
		};
	}

	public GuiFarm(EntityPlayer player, TileFarmPlain tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/mfarm.png", new ContainerFarm(player.inventory, tile), tile);

		widgetManager.add(new TankWidget(widgetManager, 15, 19, 0).setOverlayOrigin(216, 18));

		widgetManager.add(new SocketWidget(widgetManager, 69, 40, tile, 0));

		widgetManager.add(new FarmLogicSlot(widgetManager, 69, 22, 0));
		widgetManager.add(new FarmLogicSlot(widgetManager, 69, 58, 1));
		widgetManager.add(new FarmLogicSlot(widgetManager, 51, 40, 2));
		widgetManager.add(new FarmLogicSlot(widgetManager, 87, 40, 3));

		this.xSize = 216;
		this.ySize = 220;
	}

	@Override
	protected void initLedgers() {
		super.initLedgers();
		ledgerManager.insert(new FarmLedger());
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
		int fertilizerRemain = inventory.getStoredFertilizerScaled(16);
		if (fertilizerRemain > 0) {
			drawTexturedModalRect(guiLeft + 81, guiTop + 94 + 17 - fertilizerRemain, xSize, 17 - fertilizerRemain, 4, fertilizerRemain);
		}

	}
}

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
package forestry.apiculture.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.apiculture.genetics.BeeGenome;
import forestry.apiculture.inventory.ItemInventoryImprinter;
import forestry.core.config.Constants;
import forestry.core.gui.GuiForestry;
import forestry.core.gui.GuiUtil;
import forestry.core.network.packets.PacketGuiSelectRequest;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;
import forestry.plugins.PluginApiculture;

public class GuiImprinter extends GuiForestry<ContainerImprinter, ItemInventoryImprinter> {

	private int startX;
	private int startY;

	private final Map<String, ItemStack> iconStacks = new HashMap<>();

	public GuiImprinter(InventoryPlayer inventoryplayer, ItemInventoryImprinter inventory) {
		super(Constants.TEXTURE_PATH_GUI + "/imprinter.png", new ContainerImprinter(inventoryplayer, inventory), inventory);

		xSize = 176;
		ySize = 185;

		List<ItemStack> beeList = new ArrayList<>();
		PluginApiculture.items.beeDroneGE.addCreativeItems(beeList, false);
		for (ItemStack beeStack : beeList) {
			IAlleleBeeSpecies species = BeeGenome.getSpecies(beeStack);
			if (species != null) {
				iconStacks.put(species.getUID(), beeStack);
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		int offset = (138 - fontRendererObj.getStringWidth(StringUtil.localize("gui.imprinter.name"))) / 2;
		fontRendererObj.drawString(StringUtil.localize("gui.imprinter.name"), startX + 8 + offset, startY + 16, fontColor.get("gui.screen"));

		IAlleleBeeSpecies primary = inventory.getPrimary();
		drawBeeSpeciesIcon(primary, startX + 12, startY + 32);
		fontRendererObj.drawString(primary.getName(), startX + 32, startY + 36, fontColor.get("gui.screen"));

		IAlleleBeeSpecies secondary = inventory.getSecondary();
		drawBeeSpeciesIcon(secondary, startX + 12, startY + 52);
		fontRendererObj.drawString(secondary.getName(), startX + 32, startY + 56, fontColor.get("gui.screen"));

		String youCheater = StringUtil.localize("gui.imprinter.cheater");
		offset = (138 - fontRendererObj.getStringWidth(youCheater)) / 2;
		fontRendererObj.drawString(youCheater, startX + 8 + offset, startY + 76, fontColor.get("gui.screen"));

	}

	private void drawBeeSpeciesIcon(IAlleleBeeSpecies bee, int x, int y) {
		GuiUtil.drawItemStack(this, iconStacks.get(bee.getUID()), x, y);
	}

	private static int getHabitatSlotAtPosition(int i, int j) {
		int[] xPos = new int[]{12, 12};
		int[] yPos = new int[]{32, 52};

		for (int l = 0; l < xPos.length; l++) {
			if (i >= xPos[l] && i <= xPos[l] + 16 && j >= yPos[l] && j <= yPos[l] + 16) {
				return l;
			}
		}

		return -1;
	}

	@Override
	protected void mouseClicked(int i, int j, int k) {
		super.mouseClicked(i, j, k);

		int cornerX = (width - xSize) / 2;
		int cornerY = (height - ySize) / 2;

		int slot = getHabitatSlotAtPosition(i - cornerX, j - cornerY);
		if (slot < 0) {
			return;
		}

		if (k == 0) {
			advanceSelection(slot);
		} else {
			regressSelection(slot);
		}
	}

	@Override
	public void initGui() {
		super.initGui();

		startX = (this.width - this.xSize) / 2;
		startY = (this.height - this.ySize) / 2;
	}

	private static void advanceSelection(int index) {
		sendSelectionChange(index, 0);
	}

	private static void regressSelection(int index) {
		sendSelectionChange(index, 1);
	}

	private static void sendSelectionChange(int index, int advance) {
		Proxies.net.sendToServer(new PacketGuiSelectRequest(index, advance));
	}
}

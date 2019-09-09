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

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;

import genetics.api.GeneticHelper;
import genetics.api.organism.IOrganism;

import forestry.api.apiculture.genetics.BeeChromosomes;
import forestry.api.apiculture.genetics.IAlleleBeeSpecies;
import forestry.apiculture.ModuleApiculture;
import forestry.apiculture.inventory.ItemInventoryImprinter;
import forestry.core.config.Constants;
import forestry.core.gui.GuiForestry;
import forestry.core.gui.GuiUtil;
import forestry.core.network.packets.PacketGuiSelectRequest;
import forestry.core.render.ColourProperties;
import forestry.core.utils.NetworkUtil;
import forestry.core.utils.Translator;

public class GuiImprinter extends GuiForestry<ContainerImprinter> {
	private final ItemInventoryImprinter itemInventory;
	private int startX;
	private int startY;

	private final Map<String, ItemStack> iconStacks = new HashMap<>();

	public GuiImprinter(ContainerImprinter container, PlayerInventory inventoryplayer, ITextComponent title) {
		super(Constants.TEXTURE_PATH_GUI + "/imprinter.png", container, inventoryplayer, title);

		this.itemInventory = container.getItemInventory();
		this.xSize = 176;
		this.ySize = 185;

		NonNullList<ItemStack> beeList = NonNullList.create();
		ModuleApiculture.getItems().beeDroneGE.addCreativeItems(beeList, false);
		for (ItemStack beeStack : beeList) {
			IOrganism<?> organism = GeneticHelper.getOrganism(beeStack);
			if (organism.isEmpty()) {
				continue;
			}
			IAlleleBeeSpecies species = organism.getAllele(BeeChromosomes.SPECIES, true);
			iconStacks.put(species.getRegistryName().toString(), beeStack);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		int offset = (138 - getFontRenderer().getStringWidth(Translator.translateToLocal("for.gui.imprinter"))) / 2;
		getFontRenderer().drawString(Translator.translateToLocal("for.gui.imprinter"), startX + 8 + offset, startY + 16, ColourProperties.INSTANCE.get("gui.screen"));

		IAlleleBeeSpecies primary = itemInventory.getPrimary();
		drawBeeSpeciesIcon(primary, startX + 12, startY + 32);
		getFontRenderer().drawString(primary.getDisplayName().getFormattedText(), startX + 32, startY + 36, ColourProperties.INSTANCE.get("gui.screen"));

		IAlleleBeeSpecies secondary = itemInventory.getSecondary();
		drawBeeSpeciesIcon(secondary, startX + 12, startY + 52);
		getFontRenderer().drawString(secondary.getDisplayName().getFormattedText(), startX + 32, startY + 56, ColourProperties.INSTANCE.get("gui.screen"));

		String youCheater = Translator.translateToLocal("for.gui.imprinter.cheater");
		offset = (138 - getFontRenderer().getStringWidth(youCheater)) / 2;
		getFontRenderer().drawString(youCheater, startX + 8 + offset, startY + 76, ColourProperties.INSTANCE.get("gui.screen"));

	}

	private void drawBeeSpeciesIcon(IAlleleBeeSpecies bee, int x, int y) {
		GuiUtil.drawItemStack(this, iconStacks.get(bee.getRegistryName().toString()), x, y);
	}

	private static int getHabitatSlotAtPosition(double i, double j) {
		int[] xPos = new int[]{12, 12};
		int[] yPos = new int[]{32, 52};

		for (int l = 0; l < xPos.length; l++) {
			if (i >= xPos[l] && i <= xPos[l] + 16 && j >= yPos[l] && j <= yPos[l] + 16) {
				return l;
			}
		}

		return -1;
	}

	//TODO check return
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int k) {
		super.mouseClicked(mouseX, mouseY, k);

		int cornerX = (width - xSize) / 2;
		int cornerY = (height - ySize) / 2;

		int slot = getHabitatSlotAtPosition(mouseX - cornerX, mouseY - cornerY);
		if (slot < 0) {
			return true;
		}

		if (k == 0) {
			advanceSelection(slot);
			return true;
		} else {
			regressSelection(slot);
			return true;
		}
	}

	@Override
	public void init() {
		super.init();

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
		NetworkUtil.sendToServer(new PacketGuiSelectRequest(index, advance));
	}

	@Override
	protected void addLedgers() {

	}
}

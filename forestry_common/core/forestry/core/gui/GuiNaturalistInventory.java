/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.gui;

import java.util.HashMap;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import forestry.api.apiculture.IApiaristTracker;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IMutation;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.config.Defaults;
import forestry.core.gadgets.TileForestry;
import forestry.core.genetics.EnumMutateChance;
import forestry.core.gui.buttons.GuiBetterButton;
import forestry.core.gui.buttons.StandardButtonTextureSets;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketPayload;
import forestry.core.network.PacketUpdate;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;

public class GuiNaturalistInventory extends GuiForestry<TileForestry> {
	private final ISpeciesRoot speciesRoot;
	private final IBreedingTracker breedingTracker;
	private final HashMap<String, ItemStack> iconStacks = new HashMap<String, ItemStack>();
	private final int pageCurrent, pageMax;

	public GuiNaturalistInventory(ISpeciesRoot speciesRoot, EntityPlayer player, ContainerForestry container, IPagedInventory inventory, int page, int maxPages) {
		super(Defaults.TEXTURE_PATH_GUI + "/apiaristinventory.png", container, inventory);

		this.speciesRoot = speciesRoot;

		this.pageCurrent = page;
		this.pageMax = maxPages;

		xSize = 196;
		ySize = 202;

		for (IIndividual individual : speciesRoot.getIndividualTemplates()) {
			iconStacks.put(individual.getIdent(), speciesRoot.getMemberStack(individual, 0));
		}

		breedingTracker = speciesRoot.getBreedingTracker(player.worldObj, player.getGameProfile().getId());
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		super.drawGuiContainerBackgroundLayer(f, i, j);
		String header = StringUtil.localize("gui.page") + " " + (pageCurrent + 1) + "/" + pageMax;
		fontRendererObj.drawString(header, guiLeft + 95 + getCenteredOffset(header, 98), guiTop + 10, fontColor.get("gui.title"));

		IIndividual individual = getIndividualAtPosition(i, j);
		if (individual == null)
			displayBreedingStatistics(10);

		if (individual != null) {
			RenderHelper.enableGUIStandardItemLighting();
			startPage();

			displaySpeciesInformation(true, individual.getGenome().getPrimary(), iconStacks.get(individual.getIdent()), 10);
			if (!individual.isPureBred(0))
				displaySpeciesInformation(individual.isAnalyzed(), individual.getGenome().getSecondary(), iconStacks.get(individual.getGenome().getSecondary().getUID()),
						10);

			endPage();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		super.initGui();

		buttonList.add(new GuiBetterButton(1, guiLeft + 99, guiTop + 7, StandardButtonTextureSets.LEFT_BUTTON_SMALL));
		buttonList.add(new GuiBetterButton(2, guiLeft + 180, guiTop + 7, StandardButtonTextureSets.RIGHT_BUTTON_SMALL));
	}

	private void flipPage(int page) {
		PacketPayload payload = new PacketPayload(1, 0, 0);
		payload.intPayload[0] = page;
		PacketUpdate packet = new PacketUpdate(PacketIds.GUI_SELECTION_CHANGE, payload);
		Proxies.net.sendToServer(packet);
	}

	@Override
	protected void actionPerformed(GuiButton guibutton) {
		super.actionPerformed(guibutton);

		if (guibutton.id == 1 && pageCurrent > 0)
			flipPage(pageCurrent - 1);
		else if (guibutton.id == 2 && pageCurrent < pageMax - 1)
			flipPage(pageCurrent + 1);
	}

	private IIndividual getIndividualAtPosition(int x, int y) {
		Slot slot = getSlotAtPosition(x, y);
		if (slot == null)
			return null;

		if (!slot.getHasStack())
			return null;

		if (!slot.getStack().hasTagCompound())
			return null;

		if (!speciesRoot.isMember(slot.getStack()))
			return null;

		return speciesRoot.getMember(slot.getStack());
	}

	private void displayBreedingStatistics(int x) {

		startPage();

		drawLine(StringUtil.localize("gui.speciescount") + ": " + breedingTracker.getSpeciesBred() + "/" + speciesRoot.getSpeciesCount(), x);
		newLine();
		newLine();

		if (breedingTracker instanceof IApiaristTracker) {
			IApiaristTracker tracker = (IApiaristTracker) breedingTracker;
			drawLine(StringUtil.localize("gui.queens") + ": " + tracker.getQueenCount(), x);
			newLine();

			drawLine(StringUtil.localize("gui.princesses") + ": " + tracker.getPrincessCount(), x);
			newLine();

			drawLine(StringUtil.localize("gui.drones") + ": " + tracker.getDroneCount(), x);
			newLine();
		}

		endPage();
	}

	private void displaySpeciesInformation(boolean analyzed, IAlleleSpecies species, ItemStack iconStack, int x) {

		if (!analyzed) {
			drawLine(StringUtil.localize("gui.unknown"), x);
			return;
		}

		drawLine(species.getName(), x);
		RenderHelper.enableGUIStandardItemLighting();
		drawItemStack(iconStack, adjustToFactor(guiLeft + x + 69), adjustToFactor(guiTop + getLineY() - 2));
		RenderHelper.disableStandardItemLighting();

		newLine();

		// Viable Combinations
		int columnWidth = 16;
		int column = 10;

		for (IMutation combination : speciesRoot.getCombinations(species)) {
			if (combination.isSecret())
				continue;

			if (breedingTracker.isDiscovered(combination))
				drawMutationIcon(combination, species, column);
			else
				drawUnknownIcon(combination, column);

			column += columnWidth;
			if (column > 75) {
				column = 10;
				newLine(18);
			}
		}

		newLine();
		newLine();
	}

	private void drawMutationIcon(IMutation combination, IAlleleSpecies species, int x) {

		RenderHelper.enableGUIStandardItemLighting();
		drawItemStack(iconStacks.get(combination.getPartner(species).getUID()), adjustToFactor(guiLeft + x),
				adjustToFactor(guiTop + getLineY()));
		RenderHelper.disableStandardItemLighting();

		int line = 48;
		int column = 0;
		EnumMutateChance chance = EnumMutateChance.rateChance(combination.getBaseChance());
		if (chance == EnumMutateChance.HIGHEST) {
			line += 16;
			column = 228;
		} else if (chance == EnumMutateChance.HIGHER) {
			line += 16;
			column = 212;
		} else if (chance == EnumMutateChance.HIGH) {
			line += 16;
			column = 196;
		} else if (chance == EnumMutateChance.NORMAL) {
			line += 0;
			column = 228;
		} else if (chance == EnumMutateChance.LOW) {
			line += 0;
			column = 212;
		} else {
			line += 0;
			column = 196;
		}

		Proxies.common.bindTexture(textureFile);
		drawTexturedModalRect(adjustToFactor(guiLeft + x), adjustToFactor(guiTop + getLineY()), column, line, 16, 16);

	}

	private void drawUnknownIcon(IMutation mutation, int x) {

		float chance = mutation.getBaseChance();

		int line = 0;
		int column = 0;
		if (chance >= 20) {
			line = 16;
			column = 228;
		} else if (chance >= 15) {
			line = 16;
			column = 212;
		} else if (chance >= 12) {
			line = 16;
			column = 196;
		} else if (chance >= 10) {
			line = 0;
			column = 228;
		} else if (chance >= 5) {
			line = 0;
			column = 212;
		} else {
			line = 0;
			column = 196;
		}

		Proxies.common.bindTexture(textureFile);
		drawTexturedModalRect(adjustToFactor(guiLeft + x), adjustToFactor(guiTop + getLineY()), column, line, 16, 16);
	}

	@Override
	protected boolean checkHotbarKeys(int key) {
		return false;
	}
}

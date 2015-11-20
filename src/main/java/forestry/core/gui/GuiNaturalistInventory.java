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
package forestry.core.gui;

import java.util.HashMap;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import forestry.api.apiculture.IApiaristTracker;
import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IMutation;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.config.Constants;
import forestry.core.genetics.mutations.EnumMutateChance;
import forestry.core.gui.buttons.GuiBetterButton;
import forestry.core.gui.buttons.StandardButtonTextureSets;
import forestry.core.network.packets.PacketGuiSelectRequest;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;

public class GuiNaturalistInventory extends GuiForestry<Container, IPagedInventory> {
	private final ISpeciesRoot speciesRoot;
	private final IBreedingTracker breedingTracker;
	private final HashMap<String, ItemStack> iconStacks = new HashMap<>();
	private final int pageCurrent, pageMax;

	public GuiNaturalistInventory(ISpeciesRoot speciesRoot, EntityPlayer player, Container container, IPagedInventory inventory, int page, int maxPages) {
		super(Constants.TEXTURE_PATH_GUI + "/apiaristinventory.png", container, inventory);

		this.speciesRoot = speciesRoot;

		this.pageCurrent = page;
		this.pageMax = maxPages;

		xSize = 196;
		ySize = 202;

		for (IIndividual individual : speciesRoot.getIndividualTemplates()) {
			iconStacks.put(individual.getIdent(), speciesRoot.getMemberStack(individual, 0));
		}

		breedingTracker = speciesRoot.getBreedingTracker(player.worldObj, player.getGameProfile());
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		super.drawGuiContainerBackgroundLayer(f, i, j);
		String header = StringUtil.localize("gui.page") + " " + (pageCurrent + 1) + "/" + pageMax;
		fontRendererObj.drawString(header, guiLeft + 95 + textLayout.getCenteredOffset(header, 98), guiTop + 10, fontColor.get("gui.title"));

		IIndividual individual = getIndividualAtPosition(i, j);
		if (individual == null) {
			displayBreedingStatistics(10);
		}

		if (individual != null) {
			RenderHelper.enableGUIStandardItemLighting();
			textLayout.startPage();

			displaySpeciesInformation(true, individual.getGenome().getPrimary(), iconStacks.get(individual.getIdent()), 10);
			if (!individual.isPureBred(EnumTreeChromosome.SPECIES)) {
				displaySpeciesInformation(individual.isAnalyzed(), individual.getGenome().getSecondary(), iconStacks.get(individual.getGenome().getSecondary().getUID()), 10);
			}

			textLayout.endPage();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		super.initGui();

		buttonList.add(new GuiBetterButton(1, guiLeft + 99, guiTop + 7, StandardButtonTextureSets.LEFT_BUTTON_SMALL));
		buttonList.add(new GuiBetterButton(2, guiLeft + 180, guiTop + 7, StandardButtonTextureSets.RIGHT_BUTTON_SMALL));
	}

	private static void flipPage(int page) {
		Proxies.net.sendToServer(new PacketGuiSelectRequest(page, 0));
	}

	@Override
	protected void actionPerformed(GuiButton guibutton) {
		super.actionPerformed(guibutton);

		if (guibutton.id == 1 && pageCurrent > 0) {
			flipPage(pageCurrent - 1);
		} else if (guibutton.id == 2 && pageCurrent < pageMax - 1) {
			flipPage(pageCurrent + 1);
		}
	}

	private IIndividual getIndividualAtPosition(int x, int y) {
		Slot slot = getSlotAtPosition(x, y);
		if (slot == null) {
			return null;
		}

		if (!slot.getHasStack()) {
			return null;
		}

		if (!slot.getStack().hasTagCompound()) {
			return null;
		}

		if (!speciesRoot.isMember(slot.getStack())) {
			return null;
		}

		return speciesRoot.getMember(slot.getStack());
	}

	private void displayBreedingStatistics(int x) {

		textLayout.startPage();

		textLayout.drawLine(StringUtil.localize("gui.speciescount") + ": " + breedingTracker.getSpeciesBred() + "/" + speciesRoot.getSpeciesCount(), x);
		textLayout.newLine();
		textLayout.newLine();

		if (breedingTracker instanceof IApiaristTracker) {
			IApiaristTracker tracker = (IApiaristTracker) breedingTracker;
			textLayout.drawLine(StringUtil.localize("gui.queens") + ": " + tracker.getQueenCount(), x);
			textLayout.newLine();

			textLayout.drawLine(StringUtil.localize("gui.princesses") + ": " + tracker.getPrincessCount(), x);
			textLayout.newLine();

			textLayout.drawLine(StringUtil.localize("gui.drones") + ": " + tracker.getDroneCount(), x);
			textLayout.newLine();
		}

		textLayout.endPage();
	}

	private void displaySpeciesInformation(boolean analyzed, IAlleleSpecies species, ItemStack iconStack, int x) {

		if (!analyzed) {
			textLayout.drawLine(StringUtil.localize("gui.unknown"), x);
			return;
		}

		textLayout.drawLine(species.getName(), x);
		GuiUtil.drawItemStack(this, iconStack, guiLeft + x + 69, guiTop + textLayout.getLineY() - 2);

		textLayout.newLine();

		// Viable Combinations
		int columnWidth = 16;
		int column = 10;

		for (IMutation combination : speciesRoot.getCombinations(species)) {
			if (combination.isSecret()) {
				continue;
			}

			if (breedingTracker.isDiscovered(combination)) {
				drawMutationIcon(combination, species, column);
			} else {
				drawUnknownIcon(combination, column);
			}

			column += columnWidth;
			if (column > 75) {
				column = 10;
				textLayout.newLine(18);
			}
		}

		textLayout.newLine();
		textLayout.newLine();
	}

	private void drawMutationIcon(IMutation combination, IAlleleSpecies species, int x) {
		GuiUtil.drawItemStack(this, iconStacks.get(combination.getPartner(species).getUID()), guiLeft + x, guiTop + textLayout.getLineY());

		int line = 48;
		int column;
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

		Proxies.render.bindTexture(textureFile);
		drawTexturedModalRect(guiLeft + x, guiTop + textLayout.getLineY(), column, line, 16, 16);

	}

	private void drawUnknownIcon(IMutation mutation, int x) {

		float chance = mutation.getBaseChance();

		int line;
		int column;
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

		Proxies.render.bindTexture(textureFile);
		drawTexturedModalRect(guiLeft + x, guiTop + textLayout.getLineY(), column, line, 16, 16);
	}
}

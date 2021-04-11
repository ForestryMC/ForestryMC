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
package forestry.core.inventory;

import java.util.Optional;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

import forestry.api.genetics.ForestryComponentKeys;
import forestry.api.genetics.IResearchHandler;
import forestry.api.genetics.alleles.IAlleleForestrySpecies;
import forestry.core.tiles.EscritoireGame;
import forestry.core.tiles.TileEscritoire;
import forestry.core.utils.GeneticsUtil;
import forestry.core.utils.SlotUtil;

import genetics.api.individual.IIndividual;
import genetics.utils.RootUtils;

public class InventoryEscritoire extends InventoryAdapterTile<TileEscritoire> {
	public static final short SLOT_ANALYZE = 0;
	public static final short SLOT_RESULTS_1 = 1;
	public static final short SLOTS_RESULTS_COUNT = 6;
	public static final short SLOT_INPUT_1 = 7;
	public static final short SLOTS_INPUT_COUNT = 5;

	public InventoryEscritoire(TileEscritoire escritoire) {
		super(escritoire, 12, "Items");
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		if (slotIndex >= SLOT_INPUT_1 && slotIndex < SLOT_INPUT_1 + tile.getGame().getSampleSize(SLOTS_INPUT_COUNT)) {
			ItemStack specimen = getItem(SLOT_ANALYZE);
			if (specimen.isEmpty()) {
				return false;
			}
			Optional<IIndividual> optional = RootUtils.getIndividual(specimen);
			return optional.filter(individual -> {
				IResearchHandler handler = ((IResearchHandler) individual.getRoot().getComponent(ForestryComponentKeys.RESEARCH));
				return handler.getResearchSuitability(individual.getGenome().getPrimary(IAlleleForestrySpecies.class), itemStack) > 0;
			}).isPresent();
		}

		return slotIndex == SLOT_ANALYZE &&
			(RootUtils.isIndividual(itemStack) || GeneticsUtil.getGeneticEquivalent(itemStack).isPresent());

	}

	@Override
	public boolean isLocked(int slotIndex) {
		if (slotIndex == SLOT_ANALYZE) {
			return false;
		}

		if (getItem(SLOT_ANALYZE).isEmpty()) {
			return true;
		}

		if (SlotUtil.isSlotInRange(slotIndex, SLOT_INPUT_1, SLOTS_INPUT_COUNT)) {
			return slotIndex >= SLOT_INPUT_1 + tile.getGame().getSampleSize(SLOTS_INPUT_COUNT);
		}

		return false;
	}

	@Override
	public boolean canTakeItemThroughFace(int slotIndex, ItemStack itemstack, Direction side) {
		return SlotUtil.isSlotInRange(slotIndex, SLOT_RESULTS_1, SLOTS_RESULTS_COUNT);
	}

	@Override
	public void setItem(int slotIndex, ItemStack itemstack) {
		super.setItem(slotIndex, itemstack);
		if (slotIndex == SLOT_ANALYZE) {
			if (!RootUtils.isIndividual(getItem(SLOT_ANALYZE)) && !getItem(SLOT_ANALYZE).isEmpty()) {
				ItemStack ersatz = GeneticsUtil.convertToGeneticEquivalent(getItem(SLOT_ANALYZE));
				if (RootUtils.isIndividual(ersatz)) {
					super.setItem(SLOT_ANALYZE, ersatz);
				}
			}
			World world = tile.getLevel();
			if (world != null && !world.isClientSide) {
				EscritoireGame game = tile.getGame();
				game.initialize(getItem(SLOT_ANALYZE));
			}
		}
	}
}

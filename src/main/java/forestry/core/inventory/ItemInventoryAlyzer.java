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

import com.google.common.collect.ImmutableSet;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import forestry.api.core.ForestryAPI;
import forestry.api.core.IErrorSource;
import forestry.api.core.IErrorState;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAlyzerPlugin;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.ISpeciesRoot;
import forestry.apiculture.PluginApiculture;
import forestry.apiculture.items.ItemRegistryApiculture;
import forestry.core.errors.EnumErrorCode;
import forestry.core.gui.IHintSource;
import forestry.core.utils.GeneticsUtil;
import forestry.plugins.ForestryPluginUids;

public class ItemInventoryAlyzer extends ItemInventory implements IErrorSource, IHintSource {
	public static final int SLOT_ENERGY = 0;
	public static final int SLOT_SPECIMEN = 1;
	public static final int SLOT_ANALYZE_1 = 2;
	public static final int SLOT_ANALYZE_2 = 3;
	public static final int SLOT_ANALYZE_3 = 4;
	public static final int SLOT_ANALYZE_4 = 5;
	public static final int SLOT_ANALYZE_5 = 6;

	public ItemInventoryAlyzer(@Nonnull EntityPlayer player, ItemStack itemstack) {
		super(player, 7, itemstack);
	}

	public static boolean isAlyzingFuel(ItemStack itemstack) {
		if (itemstack == null || itemstack.stackSize <= 0) {
			return false;
		}

		ItemRegistryApiculture beeItems = PluginApiculture.items;
		if (beeItems == null) {
			return false;
		}

		Item item = itemstack.getItem();
		return beeItems.honeyDrop == item || beeItems.honeydew == item;
	}
	
	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		if (slotIndex == SLOT_ENERGY) {
			return isAlyzingFuel(itemStack);
		}

		// only allow one slot to be used at a time
		if (hasSpecimen() && getStackInSlot(slotIndex) == null) {
			return false;
		}

		itemStack = GeneticsUtil.convertToGeneticEquivalent(itemStack);
		ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(itemStack);
		if (speciesRoot == null) {
			return false;
		}

		if (slotIndex == SLOT_SPECIMEN) {
			return true;
		}

		IIndividual individual = speciesRoot.getMember(itemStack);
		return individual.isAnalyzed();
	}
	
	@Override
	public void setInventorySlotContents(int slotIndex, ItemStack itemStack) {
		super.setInventorySlotContents(slotIndex, itemStack);
		if (slotIndex == SLOT_SPECIMEN) {
			analyzeSpecimen(itemStack);
		}
	}

	private void analyzeSpecimen(ItemStack specimen) {
		if (specimen == null) {
			return;
		}

		ItemStack convertedSpecimen = GeneticsUtil.convertToGeneticEquivalent(specimen);
		if (!ItemStack.areItemStacksEqual(specimen, convertedSpecimen)) {
			setInventorySlotContents(SLOT_SPECIMEN, convertedSpecimen);
			specimen = convertedSpecimen;
		}

		ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(specimen);

		// No individual, abort
		if (speciesRoot == null) {
			return;
		}

		IIndividual individual = speciesRoot.getMember(specimen);

		// Analyze if necessary
		if (!individual.isAnalyzed()) {
			final boolean requiresEnergy = ForestryAPI.enabledPlugins.contains(ForestryPluginUids.APICULTURE);
			if (requiresEnergy) {
				// Requires energy
				if (!isAlyzingFuel(getStackInSlot(SLOT_ENERGY))) {
					return;
				}
			}
			
			if (individual.analyze()) {
				IBreedingTracker breedingTracker = speciesRoot.getBreedingTracker(player.worldObj, player.getGameProfile());
				breedingTracker.registerSpecies(individual.getGenome().getPrimary());
				breedingTracker.registerSpecies(individual.getGenome().getSecondary());

				NBTTagCompound nbttagcompound = new NBTTagCompound();
				individual.writeToNBT(nbttagcompound);
				specimen.setTagCompound(nbttagcompound);

				if (requiresEnergy) {
					// Decrease energy
					decrStackSize(SLOT_ENERGY, 1);
				}
			}
		}

		setInventorySlotContents(SLOT_ANALYZE_1, specimen);
		setInventorySlotContents(SLOT_SPECIMEN, null);
	}

	@Override
	public final ImmutableSet<IErrorState> getErrorStates() {
		ImmutableSet.Builder<IErrorState> errorStates = ImmutableSet.builder();

		if (!hasSpecimen()) {
			errorStates.add(EnumErrorCode.NO_SPECIMEN);
		} else {
			ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(getSpecimen());
			if (speciesRoot != null && !isAlyzingFuel(getStackInSlot(SLOT_ENERGY))) {
				errorStates.add(EnumErrorCode.NO_HONEY);
			}
		}

		return errorStates.build();
	}
	
	public ItemStack getSpecimen() {
		for (int i = SLOT_SPECIMEN; i <= SLOT_ANALYZE_5; i++) {
			ItemStack itemStack = getStackInSlot(i);
			if (itemStack != null) {
				return itemStack;
			}
		}
		return null;
	}

	public boolean hasSpecimen() {
		return getSpecimen() != null;
	}
	
	/* IHintSource */
	@Override
	public List<String> getHints() {
		ItemStack specimen = getSpecimen();
		if (specimen != null) {
			ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(specimen);
			if (speciesRoot != null) {
				IAlyzerPlugin alyzerPlugin = speciesRoot.getAlyzerPlugin();
				if (alyzerPlugin != null) {
					return alyzerPlugin.getHints();
				}
			}
		}
		return Collections.emptyList();
	}
}

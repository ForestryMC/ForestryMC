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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import forestry.api.core.IErrorSource;
import forestry.api.core.IErrorState;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.ISpeciesRoot;
import forestry.apiculture.items.ItemRegistryApiculture;
import forestry.core.errors.EnumErrorCode;
import forestry.plugins.PluginApiculture;
import forestry.plugins.PluginManager;

public abstract class ItemInventoryAlyzer extends ItemInventory implements IErrorSource {
	public static final int SLOT_SPECIMEN = 0;
	public static final int SLOT_ANALYZE_1 = 1;
	public static final int SLOT_ANALYZE_2 = 2;
	public static final int SLOT_ANALYZE_3 = 3;
	public static final int SLOT_ANALYZE_4 = 4;
	public static final int SLOT_ANALYZE_5 = 6;
	public static final int SLOT_ENERGY = 5;

	private final ISpeciesRoot speciesRoot;

	public ItemInventoryAlyzer(ISpeciesRoot speciesRoot, EntityPlayer player, ItemStack itemstack) {
		super(player, 7, itemstack);
		this.speciesRoot = speciesRoot;
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		if (slotIndex == SLOT_ENERGY) {
			return isEnergy(itemStack);
		}

		if (!speciesRoot.isMember(itemStack)) {
			return false;
		}

		// only allow one slot to be used at a time
		if (hasSpecimen() && getStackInSlot(slotIndex) == null) {
			return false;
		}

		if (slotIndex == SLOT_SPECIMEN) {
			return true;
		}

		IIndividual individual = speciesRoot.getMember(itemStack);
		return individual.isAnalyzed();
	}

	@Override
	public void onSlotClick(EntityPlayer player) {
		// Source slot to analyze empty
		ItemStack specimen = getStackInSlot(SLOT_SPECIMEN);
		if (specimen == null) {
			return;
		}

		IIndividual individual = speciesRoot.getMember(specimen);
		// No individual, abort
		if (individual == null) {
			return;
		}

		// Analyze if necessary
		if (!individual.isAnalyzed()) {

			if (PluginManager.Module.APICULTURE.isEnabled()) {
				// Requires energy
				if (!isEnergy(getStackInSlot(SLOT_ENERGY))) {
					return;
				}
			}

			individual.analyze();
			if (player != null) {
				IBreedingTracker breedingTracker = speciesRoot.getBreedingTracker(player.worldObj, player.getGameProfile());
				breedingTracker.registerSpecies(individual.getGenome().getPrimary());
				breedingTracker.registerSpecies(individual.getGenome().getSecondary());
			}

			NBTTagCompound nbttagcompound = new NBTTagCompound();
			individual.writeToNBT(nbttagcompound);
			specimen.setTagCompound(nbttagcompound);

			// Decrease energy
			decrStackSize(SLOT_ENERGY, 1);
		}

		setInventorySlotContents(SLOT_ANALYZE_1, specimen);
		setInventorySlotContents(SLOT_SPECIMEN, null);
	}

	@Override
	public final ImmutableSet<IErrorState> getErrorStates() {
		ImmutableSet.Builder<IErrorState> errorStates = ImmutableSet.builder();

		if (!hasSpecimen()) {
			errorStates.add(EnumErrorCode.NO_SPECIMEN);
		}

		if (!isEnergy(getStackInSlot(SLOT_ENERGY))) {
			errorStates.add(EnumErrorCode.NO_HONEY);
		}

		return errorStates.build();
	}

	private static boolean isEnergy(ItemStack itemstack) {
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

	private boolean hasSpecimen() {
		for (int i = SLOT_SPECIMEN; i <= SLOT_ANALYZE_5; i++) {
			if (i == SLOT_ENERGY) {
				continue;
			}

			ItemStack itemStack = getStackInSlot(i);
			if (itemStack != null) {
				return true;
			}
		}
		return false;
	}
}

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
package forestry.apiculture.inventory;

import com.google.common.collect.ImmutableSet;

import java.util.Optional;
import java.util.Set;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.core.IErrorSource;
import forestry.api.core.IErrorState;
import forestry.apiculture.features.ApicultureItems;
import forestry.apiculture.items.HabitatLocatorLogic;
import forestry.apiculture.items.ItemHabitatLocator;
import forestry.core.errors.EnumErrorCode;
import forestry.core.inventory.ItemInventory;
import net.minecraft.world.level.biome.Biome;

public class ItemInventoryHabitatLocator extends ItemInventory implements IErrorSource {

	private static final short SLOT_ENERGY = 2;
	private static final short SLOT_SPECIMEN = 0;
	private static final short SLOT_ANALYZED = 1;

	private final HabitatLocatorLogic locatorLogic;

	public ItemInventoryHabitatLocator(Player player, ItemStack itemstack) {
		super(player, 3, itemstack);
		ItemHabitatLocator habitatLocator = (ItemHabitatLocator) itemstack.getItem();
		this.locatorLogic = habitatLocator.getLocatorLogic();
	}

	private static boolean isEnergy(ItemStack itemstack) {
		return ApicultureItems.HONEY_DROPS.itemEqual(itemstack) || ApicultureItems.HONEYDEW.itemEqual(itemstack);
	}

	@Override
	public void onSlotClick(int slotIndex, Player player) {
		if (!getItem(SLOT_ANALYZED).isEmpty()) {
			if (locatorLogic.isBiomeFound()) {
				return;
			}
		} else if (!getItem(SLOT_SPECIMEN).isEmpty()) {
			// Requires energy
			if (!isEnergy(getItem(SLOT_ENERGY))) {
				return;
			}

			// Decrease energy
			removeItem(SLOT_ENERGY, 1);

			setItem(SLOT_ANALYZED, getItem(SLOT_SPECIMEN));
			setItem(SLOT_SPECIMEN, ItemStack.EMPTY);
		}

		ItemStack analyzed = getItem(SLOT_ANALYZED);
		Optional<IBee> optionalBee = BeeManager.beeRoot.create(analyzed);
		if (optionalBee.isPresent()) {
			IBee bee = optionalBee.get();
			locatorLogic.startBiomeSearch(bee, player);
		}
	}

	public Set<Biome> getBiomesToSearch() {
		return locatorLogic.getTargetBiomes();
	}

	/* IErrorSource */
	@Override
	public ImmutableSet<IErrorState> getErrorStates() {
		if (!getItem(SLOT_ANALYZED).isEmpty()) {
			return ImmutableSet.of();
		}

		ImmutableSet.Builder<IErrorState> errorStates = ImmutableSet.builder();

		ItemStack specimen = getItem(SLOT_SPECIMEN);
		if (!BeeManager.beeRoot.isMember(specimen)) {
			errorStates.add(EnumErrorCode.NO_SPECIMEN);
		}

		if (!isEnergy(getItem(SLOT_ENERGY))) {
			errorStates.add(EnumErrorCode.NO_HONEY);
		}

		return errorStates.build();
	}

	/* IFilterSlotDelegate */
	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		if (slotIndex == SLOT_ENERGY) {
			return isEnergy(itemStack);
		} else if (slotIndex == SLOT_SPECIMEN) {
			return BeeManager.beeRoot.isMember(itemStack);
		}
		return false;
	}

}

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

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBee;
import forestry.api.core.IErrorSource;
import forestry.api.core.IErrorState;
import forestry.apiculture.PluginApiculture;
import forestry.apiculture.items.HabitatLocatorLogic;
import forestry.apiculture.items.ItemHabitatLocator;
import forestry.core.errors.EnumErrorCode;
import forestry.core.inventory.ItemInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.Biome;

public class ItemInventoryHabitatLocator extends ItemInventory implements IErrorSource {

	private static final short SLOT_ENERGY = 2;
	private static final short SLOT_SPECIMEN = 0;
	private static final short SLOT_ANALYZED = 1;

	private final HabitatLocatorLogic locatorLogic;

	public ItemInventoryHabitatLocator(EntityPlayer player, ItemStack itemstack) {
		super(player, 3, itemstack);
		ItemHabitatLocator habitatLocator = (ItemHabitatLocator) itemstack.getItem();
		this.locatorLogic = habitatLocator.getLocatorLogic();
	}

	private static boolean isEnergy(ItemStack itemstack) {
		if (itemstack.isEmpty()) {
			return false;
		}

		Item item = itemstack.getItem();
		return PluginApiculture.getItems().honeyDrop == item || PluginApiculture.getItems().honeydew == item;
	}

	@Override
	public void onSlotClick(int slotIndex, EntityPlayer player) {
		if (!getStackInSlot(SLOT_ANALYZED).isEmpty()) {
			if (locatorLogic.isBiomeFound()) {
				return;
			}
		} else if (!getStackInSlot(SLOT_SPECIMEN).isEmpty()) {
			// Requires energy
			if (!isEnergy(getStackInSlot(SLOT_ENERGY))) {
				return;
			}

			// Decrease energy
			decrStackSize(SLOT_ENERGY, 1);

			setInventorySlotContents(SLOT_ANALYZED, getStackInSlot(SLOT_SPECIMEN));
			setInventorySlotContents(SLOT_SPECIMEN, ItemStack.EMPTY);
		}

		ItemStack analyzed = getStackInSlot(SLOT_ANALYZED);
		IBee bee = BeeManager.beeRoot.getMember(analyzed);
		if (bee != null) {
			locatorLogic.startBiomeSearch(bee, player);
		}
	}

	public Set<Biome> getBiomesToSearch() {
		return locatorLogic.getTargetBiomes();
	}

	/* IErrorSource */
	@Override
	public ImmutableSet<IErrorState> getErrorStates() {
		if (!getStackInSlot(SLOT_ANALYZED).isEmpty()) {
			return ImmutableSet.of();
		}

		ImmutableSet.Builder<IErrorState> errorStates = ImmutableSet.builder();

		ItemStack specimen = getStackInSlot(SLOT_SPECIMEN);
		if (!BeeManager.beeRoot.isMember(specimen)) {
			errorStates.add(EnumErrorCode.NO_SPECIMEN);
		}

		if (!isEnergy(getStackInSlot(SLOT_ENERGY))) {
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

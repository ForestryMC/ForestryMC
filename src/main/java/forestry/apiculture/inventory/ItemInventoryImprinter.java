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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBee;
import forestry.apiculture.genetics.Bee;
import forestry.core.inventory.ItemInventory;

public class ItemInventoryImprinter extends ItemInventory {
	private static final short specimenSlot = 0;
	private static final short imprintedSlot = 1;

	private int primaryIndex = 0;
	private int secondaryIndex = 0;

	public ItemInventoryImprinter(EntityPlayer player, ItemStack itemStack) {
		super(player, 2, itemStack);
	}

	public void advancePrimary() {
		if (primaryIndex < BeeManager.beeRoot.getIndividualTemplates().size() - 1) {
			primaryIndex++;
		} else {
			primaryIndex = 0;
		}
	}

	public void advanceSecondary() {
		if (secondaryIndex < BeeManager.beeRoot.getIndividualTemplates().size() - 1) {
			secondaryIndex++;
		} else {
			secondaryIndex = 0;
		}
	}

	public void regressPrimary() {
		if (primaryIndex > 0) {
			primaryIndex--;
		} else {
			primaryIndex = BeeManager.beeRoot.getIndividualTemplates().size() - 1;
		}
	}

	public void regressSecondary() {
		if (secondaryIndex > 0) {
			secondaryIndex--;
		} else {
			secondaryIndex = BeeManager.beeRoot.getIndividualTemplates().size() - 1;
		}
	}

	public IAlleleBeeSpecies getPrimary() {
		return BeeManager.beeRoot.getIndividualTemplates().get(primaryIndex).getGenome().getPrimary();
	}

	public IAlleleBeeSpecies getSecondary() {
		return BeeManager.beeRoot.getIndividualTemplates().get(secondaryIndex).getGenome().getPrimary();
	}

	public IBee getSelectedBee() {
		return new Bee(BeeManager.beeRoot.templateAsGenome(
				BeeManager.beeRoot.getGenomeTemplates().get(BeeManager.beeRoot.getIndividualTemplates().get(primaryIndex).getIdent()),
				BeeManager.beeRoot.getGenomeTemplates().get(BeeManager.beeRoot.getIndividualTemplates().get(secondaryIndex).getIdent())));
	}

	public int getPrimaryIndex() {
		return primaryIndex;
	}

	public int getSecondaryIndex() {
		return secondaryIndex;
	}

	public void setPrimaryIndex(int index) {
		primaryIndex = index;
	}

	public void setSecondaryIndex(int index) {
		secondaryIndex = index;
	}

	@Override
	public void onSlotClick(EntityPlayer player) {
		ItemStack specimen = getStackInSlot(specimenSlot);
		if (specimen == null) {
			return;
		}

		// Only imprint bees
		if (!BeeManager.beeRoot.isMember(specimen)) {
			return;
		}

		// Needs space
		if (getStackInSlot(imprintedSlot) != null) {
			return;
		}

		IBee imprint = getSelectedBee();
		if (imprint == null) {
			return;
		}

		NBTTagCompound nbttagcompound = new NBTTagCompound();
		imprint.writeToNBT(nbttagcompound);
		specimen.setTagCompound(nbttagcompound);

		setInventorySlotContents(imprintedSlot, specimen);
		setInventorySlotContents(specimenSlot, null);
	}

	@Override
	public String getInventoryName() {
		return "Imprinter";
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		return BeeManager.beeRoot.isMember(itemStack);
	}

}

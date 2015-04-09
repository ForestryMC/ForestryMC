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
package forestry.apiculture.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBee;
import forestry.api.core.ForestryAPI;
import forestry.api.core.Tabs;
import forestry.apiculture.genetics.Bee;
import forestry.core.inventory.ItemInventory;
import forestry.core.items.ItemForestry;
import forestry.core.network.GuiId;
import forestry.core.proxy.Proxies;
import forestry.plugins.PluginApiculture;

public class ItemImprinter extends ItemForestry {

	public static class ImprinterInventory extends ItemInventory {

		private final short specimenSlot = 0;
		private final short imprintedSlot = 1;

		private int primaryIndex = 0;
		private int secondaryIndex = 0;

		private final EntityPlayer player;

		public ImprinterInventory(EntityPlayer player, ItemStack itemStack) {
			super(ItemImprinter.class, 2, itemStack);
			this.player = player;
		}

		public void advancePrimary() {
			if (primaryIndex < PluginApiculture.beeInterface.getIndividualTemplates().size() - 1) {
				primaryIndex++;
			} else {
				primaryIndex = 0;
			}
		}

		public void advanceSecondary() {
			if (secondaryIndex < PluginApiculture.beeInterface.getIndividualTemplates().size() - 1) {
				secondaryIndex++;
			} else {
				secondaryIndex = 0;
			}
		}

		public void regressPrimary() {
			if (primaryIndex > 0) {
				primaryIndex--;
			} else {
				primaryIndex = PluginApiculture.beeInterface.getIndividualTemplates().size() - 1;
			}
		}

		public void regressSecondary() {
			if (secondaryIndex > 0) {
				secondaryIndex--;
			} else {
				secondaryIndex = PluginApiculture.beeInterface.getIndividualTemplates().size() - 1;
			}
		}

		public IAlleleBeeSpecies getPrimary() {
			return PluginApiculture.beeInterface.getIndividualTemplates().get(primaryIndex).getGenome().getPrimary();
		}

		public IAlleleBeeSpecies getSecondary() {
			return PluginApiculture.beeInterface.getIndividualTemplates().get(secondaryIndex).getGenome().getPrimary();
		}

		public IBee getSelectedBee() {
			return new Bee(PluginApiculture.beeInterface.templateAsGenome(
					PluginApiculture.beeInterface.getGenomeTemplates().get(PluginApiculture.beeInterface.getIndividualTemplates().get(primaryIndex).getIdent()),
					PluginApiculture.beeInterface.getGenomeTemplates().get(PluginApiculture.beeInterface.getIndividualTemplates().get(secondaryIndex).getIdent())));
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

		private void tryImprint() {

			if (inventoryStacks[specimenSlot] == null) {
				return;
			}

			// Only imprint bees
			if (!PluginApiculture.beeInterface.isMember(inventoryStacks[specimenSlot])) {
				return;
			}

			// Needs space
			if (inventoryStacks[imprintedSlot] != null) {
				return;
			}

			IBee imprint = getSelectedBee();
			if (imprint == null) {
				return;
			}

			NBTTagCompound nbttagcompound = new NBTTagCompound();
			imprint.writeToNBT(nbttagcompound);
			inventoryStacks[specimenSlot].setTagCompound(nbttagcompound);

			inventoryStacks[imprintedSlot] = inventoryStacks[specimenSlot];
			inventoryStacks[specimenSlot] = null;
		}

		@Override
		public void markDirty() {
			// if (!Proxies.common.isSimulating(player.worldObj)) {
			// 	return;
			// }
			tryImprint();
		}

		@Override
		public String getInventoryName() {
			return "Imprinter";
		}

		@Override
		public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
			return PluginApiculture.beeInterface.isMember(itemStack);
		}

	}

	public ItemImprinter() {
		super();
		setCreativeTab(Tabs.tabApiculture);
		setMaxStackSize(1);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (Proxies.common.isSimulating(world)) {
			entityplayer.openGui(ForestryAPI.instance, GuiId.ImprinterGUI.ordinal(), world, (int) entityplayer.posX, (int) entityplayer.posY,
					(int) entityplayer.posZ);
		}

		return itemstack;
	}

}

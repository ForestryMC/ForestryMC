/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.arboriculture.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import forestry.api.arboriculture.ITree;
import forestry.api.core.ForestryAPI;
import forestry.api.core.Tabs;
import forestry.core.EnumErrorCode;
import forestry.core.config.Config;
import forestry.core.config.ForestryItem;
import forestry.core.interfaces.IErrorSource;
import forestry.core.interfaces.IHintSource;
import forestry.core.items.ItemInventoried;
import forestry.core.network.GuiId;
import forestry.core.proxy.Proxies;
import forestry.core.utils.ItemInventory;
import forestry.core.utils.Utils;
import forestry.plugins.PluginArboriculture;

public class ItemTreealyzer extends ItemInventoried {

	public static class TreealyzerInventory extends ItemInventory implements IErrorSource, IHintSource {

		public final static int SLOT_SPECIMEN = 0;
		public final static int SLOT_ANALYZE_1 = 1;
		public final static int SLOT_ANALYZE_2 = 2;
		public final static int SLOT_ANALYZE_3 = 3;
		public final static int SLOT_ANALYZE_4 = 4;
		public final static int SLOT_ANALYZE_5 = 6;
		public final static int SLOT_ENERGY = 5;
		EntityPlayer player;

		public TreealyzerInventory(EntityPlayer player) {
			super(ItemTreealyzer.class, 7);
			this.player = player;
		}

		public TreealyzerInventory(EntityPlayer player, ItemStack itemStack) {
			super(ItemTreealyzer.class, 7, itemStack);
			this.player = player;
		}

		@Override
		public void writeToNBT(NBTTagCompound nbttagcompound) {

			NBTTagList nbttaglist = new NBTTagList();
			for (int i = SLOT_ENERGY; i < SLOT_ENERGY + 1; i++) {
				if (inventoryStacks[i] != null) {
					NBTTagCompound nbttagcompound1 = new NBTTagCompound();
					nbttagcompound1.setByte("Slot", (byte) i);
					inventoryStacks[i].writeToNBT(nbttagcompound1);
					nbttaglist.appendTag(nbttagcompound1);
				}
			}
			nbttagcompound.setTag("Items", nbttaglist);

		}

		private boolean isEnergy(ItemStack itemstack) {
			if (itemstack == null || itemstack.stackSize <= 0)
				return false;

			return ForestryItem.honeyDrop.isItemEqual(itemstack) || ForestryItem.honeydew.isItemEqual(itemstack);
		}

		private void tryAnalyze() {

			// Analyzed slot occupied, abort
			if (inventoryStacks[SLOT_ANALYZE_1] != null || inventoryStacks[SLOT_ANALYZE_2] != null || inventoryStacks[SLOT_ANALYZE_3] != null
					|| inventoryStacks[SLOT_ANALYZE_4] != null || inventoryStacks[SLOT_ANALYZE_5] != null)
				return;

			// Source slot to analyze empty
			if (getStackInSlot(SLOT_SPECIMEN) == null)
				return;

			if (!PluginArboriculture.treeInterface.isMember(getStackInSlot(SLOT_SPECIMEN))) {
				ItemStack ersatz = Utils.convertSaplingToGeneticEquivalent(getStackInSlot(SLOT_SPECIMEN));
				if (ersatz != null)
					setInventorySlotContents(SLOT_SPECIMEN, ersatz);
			}
			ITree tree = PluginArboriculture.treeInterface.getMember(getStackInSlot(SLOT_SPECIMEN));
			// No tree, abort
			if (tree == null)
				return;

			// Analyze if necessary
			if (!tree.isAnalyzed()) {

				// Requires energy
				if (!isEnergy(getStackInSlot(SLOT_ENERGY)))
					return;

				tree.analyze();
				if (player != null) {
					PluginArboriculture.treeInterface.getBreedingTracker(player.worldObj, player.getGameProfile().getId()).registerSpecies(tree.getGenome().getPrimary());
					PluginArboriculture.treeInterface.getBreedingTracker(player.worldObj, player.getGameProfile().getId()).registerSpecies(tree.getGenome().getSecondary());
				}
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				tree.writeToNBT(nbttagcompound);
				getStackInSlot(SLOT_SPECIMEN).setTagCompound(nbttagcompound);

				// Decrease energy
				decrStackSize(SLOT_ENERGY, 1);
			}

			setInventorySlotContents(SLOT_ANALYZE_1, getStackInSlot(SLOT_SPECIMEN));
			setInventorySlotContents(SLOT_SPECIMEN, null);
		}

		@Override
		public void markDirty() {
			// if (!Proxies.common.isSimulating(player.worldObj))
			// return;
			tryAnalyze();
		}

		// / IHINTSOURCE
		@Override
		public boolean hasHints() {
			return Config.hints.get("treealyzer") != null && Config.hints.get("treealyzer").length > 0;
		}

		@Override
		public String[] getHints() {
			return Config.hints.get("treealyzer");
		}

		// / IERRORSOURCE
		@Override
		public boolean throwsErrors() {
			return true;
		}

		@Override
		public EnumErrorCode getErrorState() {
			if (PluginArboriculture.treeInterface.isMember(inventoryStacks[SLOT_SPECIMEN]) && !isEnergy(getStackInSlot(SLOT_ENERGY)))
				return EnumErrorCode.NOHONEY;

			return EnumErrorCode.OK;
		}
	}

	public ItemTreealyzer() {
		super();
		setMaxStackSize(1);
		setCreativeTab(Tabs.tabArboriculture);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (Proxies.common.isSimulating(world))
			entityplayer.openGui(ForestryAPI.instance, GuiId.TreealyzerGUI.ordinal(), world, (int) entityplayer.posX, (int) entityplayer.posY,
					(int) entityplayer.posZ);

		return itemstack;
	}
}

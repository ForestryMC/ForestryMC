/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.lepidopterology.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import forestry.api.core.ForestryAPI;
import forestry.api.core.Tabs;
import forestry.api.lepidopterology.IButterfly;
import forestry.core.EnumErrorCode;
import forestry.core.config.Config;
import forestry.core.config.ForestryItem;
import forestry.core.interfaces.IErrorSource;
import forestry.core.interfaces.IHintSource;
import forestry.core.items.ItemInventoried;
import forestry.core.network.GuiId;
import forestry.core.proxy.Proxies;
import forestry.core.utils.ItemInventory;
import forestry.plugins.PluginLepidopterology;

public class ItemFlutterlyzer extends ItemInventoried {

	public static class FlutterlyzerInventory extends ItemInventory implements IErrorSource, IHintSource {

		public final static int SLOT_SPECIMEN = 0;
		public final static int SLOT_ANALYZE_1 = 1;
		public final static int SLOT_ANALYZE_2 = 2;
		public final static int SLOT_ANALYZE_3 = 3;
		public final static int SLOT_ANALYZE_4 = 4;
		public final static int SLOT_ANALYZE_5 = 6;
		public final static int SLOT_ENERGY = 5;
		EntityPlayer player;

		public FlutterlyzerInventory(EntityPlayer player) {
			super(ItemFlutterlyzer.class, 7);
			this.player = player;
		}

		public FlutterlyzerInventory(EntityPlayer player, ItemStack itemStack) {
			super(ItemFlutterlyzer.class, 7, itemStack);
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

			IButterfly butterfly = PluginLepidopterology.butterflyInterface.getMember(getStackInSlot(SLOT_SPECIMEN));
			// No tree, abort
			if (butterfly == null) {
				return;
			}

			// Analyze if necessary
			if (!butterfly.isAnalyzed()) {

				// Requires energy
				if (!isEnergy(getStackInSlot(SLOT_ENERGY)))
					return;

				butterfly.analyze();
				if (player != null) {
					PluginLepidopterology.butterflyInterface.getBreedingTracker(player.worldObj, player.getGameProfile()).registerSpecies(butterfly.getGenome().getPrimary());
					PluginLepidopterology.butterflyInterface.getBreedingTracker(player.worldObj, player.getGameProfile()).registerSpecies(butterfly.getGenome().getSecondary());
				}
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				butterfly.writeToNBT(nbttagcompound);
				getStackInSlot(SLOT_SPECIMEN).setTagCompound(nbttagcompound);

				// Decrease energy
				decrStackSize(SLOT_ENERGY, 1);
			}

			setInventorySlotContents(SLOT_ANALYZE_1, getStackInSlot(SLOT_SPECIMEN));
			setInventorySlotContents(SLOT_SPECIMEN, null);
		}

		@Override
		public void markDirty() {
			tryAnalyze();
		}

		/* IHINTSOURCE */
		@Override
		public boolean hasHints() {
			return Config.hints.get("flutterlyzer") != null && Config.hints.get("flutterlyzer").length > 0;
		}

		@Override
		public String[] getHints() {
			return Config.hints.get("flutterlyzer");
		}

		/* IERRORSOURCE */
		@Override
		public boolean throwsErrors() {
			return true;
		}

		@Override
		public EnumErrorCode getErrorState() {
			if (PluginLepidopterology.butterflyInterface.isMember(inventoryStacks[SLOT_SPECIMEN]) && !isEnergy(getStackInSlot(SLOT_ENERGY)))
				return EnumErrorCode.NOHONEY;

			return EnumErrorCode.OK;
		}
	}

	public ItemFlutterlyzer() {
		super();
		setMaxStackSize(1);
		setCreativeTab(Tabs.tabLepidopterology);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (Proxies.common.isSimulating(world))
			entityplayer.openGui(ForestryAPI.instance, GuiId.FlutterlyzerGUI.ordinal(), world, (int) entityplayer.posX, (int) entityplayer.posY,
					(int) entityplayer.posZ);

		return itemstack;
	}
}

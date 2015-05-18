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
package forestry.arboriculture.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import forestry.api.arboriculture.ITree;
import forestry.api.core.ForestryAPI;
import forestry.api.core.Tabs;
import forestry.core.config.Config;
import forestry.core.interfaces.IHintSource;
import forestry.core.inventory.AlyzerInventory;
import forestry.core.items.ItemInventoried;
import forestry.core.network.GuiId;
import forestry.core.proxy.Proxies;
import forestry.core.utils.GeneticsUtil;
import forestry.plugins.PluginArboriculture;

public class ItemTreealyzer extends ItemInventoried {

	public static class TreealyzerInventory extends AlyzerInventory implements IHintSource {

		public TreealyzerInventory(EntityPlayer player, ItemStack itemStack) {
			super(player, 7, itemStack);
		}

		@Override
		protected boolean isSpecimen(ItemStack itemStack) {
			return GeneticsUtil.getGeneticEquivalent(itemStack) instanceof ITree;
		}

		private void tryAnalyze() {
			// Source slot to analyze empty
			if (getStackInSlot(SLOT_SPECIMEN) == null) {
				return;
			}

			if (!PluginArboriculture.treeInterface.isMember(getStackInSlot(SLOT_SPECIMEN))) {
				ItemStack ersatz = GeneticsUtil.convertSaplingToGeneticEquivalent(getStackInSlot(SLOT_SPECIMEN));
				if (ersatz != null) {
					setInventorySlotContents(SLOT_SPECIMEN, ersatz);
				}
			}
			ITree tree = PluginArboriculture.treeInterface.getMember(getStackInSlot(SLOT_SPECIMEN));
			// No tree, abort
			if (tree == null) {
				return;
			}

			// Analyze if necessary
			if (!tree.isAnalyzed()) {

				// Requires energy
				if (!isEnergy(getStackInSlot(SLOT_ENERGY))) {
					return;
				}

				tree.analyze();
				if (player != null) {
					PluginArboriculture.treeInterface.getBreedingTracker(player.worldObj, player.getGameProfile()).registerSpecies(tree.getGenome().getPrimary());
					PluginArboriculture.treeInterface.getBreedingTracker(player.worldObj, player.getGameProfile()).registerSpecies(tree.getGenome().getSecondary());
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
	}

	public ItemTreealyzer() {
		super();
		setMaxStackSize(1);
		setCreativeTab(Tabs.tabArboriculture);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (Proxies.common.isSimulating(world)) {
			entityplayer.openGui(ForestryAPI.instance, GuiId.TreealyzerGUI.ordinal(), world, (int) entityplayer.posX, (int) entityplayer.posY,
					(int) entityplayer.posZ);
		}

		return itemstack;
	}
}

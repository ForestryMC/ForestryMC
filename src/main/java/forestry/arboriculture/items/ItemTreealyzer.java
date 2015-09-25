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
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.ForestryAPI;
import forestry.api.core.Tabs;
import forestry.core.config.Config;
import forestry.core.gui.IHintSource;
import forestry.core.items.ItemAlyzer;
import forestry.core.network.GuiId;
import forestry.core.utils.GeneticsUtil;

public class ItemTreealyzer extends ItemAlyzer {

	public ItemTreealyzer() {
		super();
		setMaxStackSize(1);
		setCreativeTab(Tabs.tabArboriculture);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (!world.isRemote) {
			entityplayer.openGui(ForestryAPI.instance, GuiId.TreealyzerGUI.ordinal(), world, (int) entityplayer.posX, (int) entityplayer.posY,
					(int) entityplayer.posZ);
		}

		return itemstack;
	}

	public static class TreealyzerInventory extends AlyzerInventory implements IHintSource {

		public TreealyzerInventory(EntityPlayer player, ItemStack itemStack) {
			super(player, 7, itemStack);
		}

		@Override
		protected boolean isSpecimen(ItemStack itemStack) {
			return GeneticsUtil.getGeneticEquivalent(itemStack) instanceof ITree;
		}

		@Override
		public void onSlotClick(EntityPlayer player) {
			// Source slot to analyze empty
			if (getStackInSlot(SLOT_SPECIMEN) == null) {
				return;
			}

			if (!TreeManager.treeRoot.isMember(getStackInSlot(SLOT_SPECIMEN))) {
				ItemStack ersatz = GeneticsUtil.convertSaplingToGeneticEquivalent(getStackInSlot(SLOT_SPECIMEN));
				if (ersatz != null) {
					setInventorySlotContents(SLOT_SPECIMEN, ersatz);
				}
			}
			ITree tree = TreeManager.treeRoot.getMember(getStackInSlot(SLOT_SPECIMEN));
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
					TreeManager.treeRoot.getBreedingTracker(player.worldObj, player.getGameProfile()).registerSpecies(tree.getGenome().getPrimary());
					TreeManager.treeRoot.getBreedingTracker(player.worldObj, player.getGameProfile()).registerSpecies(tree.getGenome().getSecondary());
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

}

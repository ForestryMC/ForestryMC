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
import net.minecraft.world.World;

import forestry.api.arboriculture.TreeManager;
import forestry.api.core.ForestryAPI;
import forestry.api.core.Tabs;
import forestry.api.genetics.ISpeciesRoot;
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
		protected ISpeciesRoot getSpeciesRoot() {
			return TreeManager.treeRoot;
		}

		@Override
		public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
			if (!TreeManager.treeRoot.isMember(itemStack)) {
				ItemStack ersatz = GeneticsUtil.convertSaplingToGeneticEquivalent(itemStack);
				if (ersatz != null) {
					return super.canSlotAccept(slotIndex, ersatz);
				}
			}
			return super.canSlotAccept(slotIndex, itemStack);
		}

		@Override
		public void onSlotClick(EntityPlayer player) {
			ItemStack specimen = getStackInSlot(SLOT_SPECIMEN);
			if (!TreeManager.treeRoot.isMember(specimen)) {
				ItemStack ersatz = GeneticsUtil.convertSaplingToGeneticEquivalent(specimen);
				if (ersatz != null) {
					setInventorySlotContents(SLOT_SPECIMEN, ersatz);
				}
			}

			super.onSlotClick(player);
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

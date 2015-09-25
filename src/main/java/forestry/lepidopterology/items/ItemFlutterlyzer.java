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
package forestry.lepidopterology.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import forestry.api.core.ForestryAPI;
import forestry.api.core.Tabs;
import forestry.api.lepidopterology.IButterfly;
import forestry.core.config.Config;
import forestry.core.gui.IHintSource;
import forestry.core.items.ItemAlyzer;
import forestry.core.network.GuiId;
import forestry.plugins.PluginLepidopterology;

public class ItemFlutterlyzer extends ItemAlyzer {

	public static class FlutterlyzerInventory extends AlyzerInventory implements IHintSource {

		public FlutterlyzerInventory(EntityPlayer player, ItemStack itemStack) {
			super(player, 7, itemStack);
		}

		@Override
		protected boolean isSpecimen(ItemStack itemStack) {
			return PluginLepidopterology.butterflyInterface.isMember(itemStack);
		}

		@Override
		public void onSlotClick(EntityPlayer player) {
			// Source slot to analyze empty
			if (getStackInSlot(SLOT_SPECIMEN) == null) {
				return;
			}

			IButterfly butterfly = PluginLepidopterology.butterflyInterface.getMember(getStackInSlot(SLOT_SPECIMEN));
			// No tree, abort
			if (butterfly == null) {
				return;
			}

			// Analyze if necessary
			if (!butterfly.isAnalyzed()) {

				// Requires energy
				if (!isEnergy(getStackInSlot(SLOT_ENERGY))) {
					return;
				}

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

		/* IHINTSOURCE */
		@Override
		public boolean hasHints() {
			return Config.hints.get("flutterlyzer") != null && Config.hints.get("flutterlyzer").length > 0;
		}

		@Override
		public String[] getHints() {
			return Config.hints.get("flutterlyzer");
		}
	}

	public ItemFlutterlyzer() {
		super();
		setMaxStackSize(1);
		setCreativeTab(Tabs.tabLepidopterology);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (!world.isRemote) {
			entityplayer.openGui(ForestryAPI.instance, GuiId.FlutterlyzerGUI.ordinal(), world, (int) entityplayer.posX, (int) entityplayer.posY,
					(int) entityplayer.posZ);
		}

		return itemstack;
	}
}

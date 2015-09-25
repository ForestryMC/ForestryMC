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

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBee;
import forestry.api.core.ForestryAPI;
import forestry.api.core.Tabs;
import forestry.core.config.Config;
import forestry.core.gui.IHintSource;
import forestry.core.items.ItemAlyzer;
import forestry.core.network.GuiId;

public class ItemBeealyzer extends ItemAlyzer {

	public ItemBeealyzer() {
		super();
		setMaxStackSize(1);
		setCreativeTab(Tabs.tabApiculture);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (!world.isRemote) {
			entityplayer.openGui(ForestryAPI.instance, GuiId.BeealyzerGUI.ordinal(), world, (int) entityplayer.posX, (int) entityplayer.posY, (int) entityplayer.posZ);
		}

		return itemstack;
	}

	public static class BeealyzerInventory extends AlyzerInventory implements IHintSource {

		public BeealyzerInventory(EntityPlayer player, ItemStack itemStack) {
			super(player, 7, itemStack);
		}

		@Override
		protected boolean isSpecimen(ItemStack itemStack) {
			return BeeManager.beeRoot.isMember(itemStack);
		}

		@Override
		public void onSlotClick(EntityPlayer player) {
			// Source slot to analyze empty
			if (getStackInSlot(SLOT_SPECIMEN) == null) {
				return;
			}

			IBee bee = BeeManager.beeRoot.getMember(getStackInSlot(SLOT_SPECIMEN));
			// No bee, abort
			if (bee == null) {
				return;
			}

			// Analyze if necessary
			if (!bee.isAnalyzed()) {

				// Requires energy
				if (!isEnergy(getStackInSlot(SLOT_ENERGY))) {
					return;
				}

				bee.analyze();
				if (player != null) {
					BeeManager.beeRoot.getBreedingTracker(player.worldObj, player.getGameProfile()).registerSpecies(bee.getGenome().getPrimary());
					BeeManager.beeRoot.getBreedingTracker(player.worldObj, player.getGameProfile()).registerSpecies(bee.getGenome().getSecondary());
				}

				NBTTagCompound nbttagcompound = new NBTTagCompound();
				bee.writeToNBT(nbttagcompound);
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
			return Config.hints.get("beealyzer") != null && Config.hints.get("beealyzer").length > 0;
		}

		@Override
		public String[] getHints() {
			return Config.hints.get("beealyzer");
		}
	}

}

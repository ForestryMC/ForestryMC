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
import net.minecraft.world.World;

import forestry.api.core.ForestryAPI;
import forestry.api.core.Tabs;
import forestry.api.genetics.ISpeciesRoot;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.core.config.Config;
import forestry.core.gui.IHintSource;
import forestry.core.items.ItemAlyzer;
import forestry.core.network.GuiId;

public class ItemFlutterlyzer extends ItemAlyzer {

	public static class FlutterlyzerInventory extends AlyzerInventory implements IHintSource {

		public FlutterlyzerInventory(EntityPlayer player, ItemStack itemStack) {
			super(player, 7, itemStack);
		}

		@Override
		protected ISpeciesRoot getSpeciesRoot() {
			return ButterflyManager.butterflyRoot;
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

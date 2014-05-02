/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.storage.items;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import forestry.api.core.ForestryAPI;
import forestry.plugins.PluginApiculture;
import forestry.plugins.PluginLepidopterology;
import forestry.storage.BackpackDefinition;

public class ItemNaturalistBackpack extends ItemBackpack {

	public static class BackpackDefinitionApiarist extends BackpackDefinition {

		public BackpackDefinitionApiarist(String name, int primaryColor) {
			super(name, primaryColor);
		}

		@Override
		public boolean isValidItem(EntityPlayer player, ItemStack stack) {
			return PluginApiculture.beeInterface.isMember(stack);
		}
	}

	public static class BackpackDefinitionLepidopterist extends BackpackDefinition {

		public BackpackDefinitionLepidopterist(String name, int primaryColor) {
			super(name, primaryColor);
		}

		@Override
		public boolean isValidItem(EntityPlayer player, ItemStack stack) {
			return PluginLepidopterology.butterflyInterface.isMember(stack);
		}
	}
	private final int guiId;

	public ItemNaturalistBackpack(int guiId, BackpackDefinition definition) {
		super(definition, 0);
		this.guiId = guiId;
	}

	@Override
	public void openGui(EntityPlayer player, ItemStack itemstack) {
		player.openGui(ForestryAPI.instance, guiId, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
	}

	@Override
	public ArrayList<ItemStack> getValidItems(EntityPlayer player) {
		return null;
	}
}

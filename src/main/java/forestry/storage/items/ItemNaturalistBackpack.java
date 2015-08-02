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
package forestry.storage.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import forestry.api.apiculture.BeeManager;
import forestry.api.core.ForestryAPI;
import forestry.api.storage.EnumBackpackType;
import forestry.api.storage.IBackpackDefinition;
import forestry.plugins.PluginLepidopterology;
import forestry.storage.BackpackDefinition;

public class ItemNaturalistBackpack extends ItemBackpack {

	public static class BackpackDefinitionApiarist extends BackpackDefinition {

		public BackpackDefinitionApiarist(int primaryColor) {
			super("apiarist", primaryColor);
		}

		@Override
		public boolean isValidItem(ItemStack itemStack) {
			return BeeManager.beeRoot.isMember(itemStack);
		}
	}

	public static class BackpackDefinitionLepidopterist extends BackpackDefinition {

		public BackpackDefinitionLepidopterist(int primaryColor) {
			super("lepidopterist", primaryColor);
		}

		@Override
		public boolean isValidItem(ItemStack itemStack) {
			return PluginLepidopterology.butterflyInterface.isMember(itemStack);
		}
	}

	private final int guiId;

	public ItemNaturalistBackpack(int guiId, IBackpackDefinition definition) {
		super(definition, EnumBackpackType.APIARIST);
		this.guiId = guiId;
	}

	@Override
	public void openGui(EntityPlayer player) {
		player.openGui(ForestryAPI.instance, guiId, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
	}
}

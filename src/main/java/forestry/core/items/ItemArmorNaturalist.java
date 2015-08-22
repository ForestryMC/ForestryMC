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
package forestry.core.items;

import forestry.api.core.IArmorNaturalist;
import forestry.core.CreativeTabForestry;
import forestry.core.config.Defaults;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class ItemArmorNaturalist extends ItemArmor implements IArmorNaturalist {

	public ItemArmorNaturalist(int slot) {
		super(ArmorMaterial.LEATHER, 0, slot);
		this.setMaxDamage(100);
		setCreativeTab(CreativeTabForestry.tabForestry);
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		return Defaults.ID + ":" + Defaults.TEXTURE_NATURALIST_ARMOR_PRIMARY;
	}

	@Override
	public int getColorFromItemStack(ItemStack itemstack, int renderPass) {
		return 0xffffff;
	}

	@Override
	public boolean hasColor(ItemStack itemstack) {
		return false;
	}

	@Override
	public boolean canSeePollination(EntityPlayer player, ItemStack armor, boolean doSee) {
		return armorType == 0;
	}

}

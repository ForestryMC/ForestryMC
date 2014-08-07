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
package forestry.core.items;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import net.minecraftforge.common.ForgeHooks;

import forestry.api.core.IToolScoop;
import forestry.api.core.Tabs;

public class ItemScoop extends ItemForestry implements IToolScoop {

	private final float efficiencyOnProperMaterial;

	public ItemScoop() {
		super();
		this.maxStackSize = 1;
		efficiencyOnProperMaterial = 4.0F;
		setMaxDamage(10);
		setCreativeTab(Tabs.tabApiculture);
		setMaxStackSize(1);
	}

	@Override
	public float func_150893_a(ItemStack itemstack, Block block) {
		return 1.0F;
	}

	@Override
	public float getDigSpeed(ItemStack itemstack, Block block, int md) {
		if (ForgeHooks.isToolEffective(itemstack, block, md))
			return efficiencyOnProperMaterial;

		return func_150893_a(itemstack, block);
	}

	@Override
	public boolean onBlockDestroyed(ItemStack itemstack, World world, Block block, int j, int k, int l, EntityLivingBase entityliving) {
		itemstack.damageItem(1, entityliving);
		return true;
	}

	/*@Override
	public float getDamageVsEntity(Entity entity, ItemStack itemstack) {
		return 1;
	}*/

	@Override
	public boolean isFull3D() {
		return true;
	}

}

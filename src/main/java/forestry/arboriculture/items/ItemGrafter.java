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

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import net.minecraftforge.common.ForgeHooks;

import forestry.api.arboriculture.IToolGrafter;
import forestry.api.core.Tabs;
import forestry.core.items.ItemForestry;

public class ItemGrafter extends ItemForestry implements IToolGrafter {
	private final float efficiencyOnProperMaterial;

	public ItemGrafter(int maxDamage) {
		setMaxStackSize(1);
		efficiencyOnProperMaterial = 4.0F;
		setMaxDamage(maxDamage);
		setCreativeTab(Tabs.tabArboriculture);
	}

	@Override
	public float func_150893_a(ItemStack itemstack, Block block) {
		return 1.0F;
	}

	@Override
	public float getDigSpeed(ItemStack itemstack, Block block, int md) {
		if (ForgeHooks.isToolEffective(itemstack, block, md)) {
			return efficiencyOnProperMaterial;
		}

		return func_150893_a(itemstack, block);
	}

	@Override
	public boolean onBlockDestroyed(ItemStack itemstack, World world, Block block, int j, int k, int l, EntityLivingBase entityliving) {
		return true;
	}

	@Override
	public boolean isFull3D() {
		return true;
	}

	@Override
	public float getSaplingModifier(ItemStack stack, World world, EntityPlayer player, int x, int y, int z) {
		return 100f;
	}
}

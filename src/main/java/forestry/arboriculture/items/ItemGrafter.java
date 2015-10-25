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
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import forestry.api.arboriculture.IToolGrafter;
import forestry.api.core.Tabs;
import forestry.core.items.ItemForestry;

public class ItemGrafter extends ItemForestry implements IToolGrafter {

	private final float efficiencyOnProperMaterial;

	public ItemGrafter(int maxDamage) {
		super();
		setMaxStackSize(1);
		efficiencyOnProperMaterial = 4.0F;
		setMaxDamage(maxDamage);
		setCreativeTab(Tabs.tabArboriculture);
	}

	@Override
	public float getStrVsBlock(ItemStack stack, Block block) {
		return 1.0F;
	}

	@Override
	public float getDigSpeed(ItemStack itemstack, IBlockState state) {
		for (String type : getToolClasses(itemstack)) {
			if (state.getBlock().isToolEffective(type, state))
				return efficiencyOnProperMaterial;
		}

		return getStrVsBlock(itemstack, state.getBlock());
	}

	@Override
	public boolean onBlockDestroyed(ItemStack itemstack, World world, Block block, BlockPos pos,
			EntityLivingBase entityliving) {
		return true;
	}

	/*
	 * @Override public float getDamageVsEntity(Entity entity, ItemStack
	 * itemstack) { return 1; }
	 */

	@Override
	public boolean isFull3D() {
		return true;
	}

	@Override
	public float getSaplingModifier(ItemStack stack, World world, EntityPlayer player, BlockPos pos) {
		return 100f;
	}

}

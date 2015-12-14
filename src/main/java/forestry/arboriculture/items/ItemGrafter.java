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
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import forestry.api.arboriculture.IToolGrafter;
import forestry.api.core.Tabs;
import forestry.core.items.ItemForestryTool;

public class ItemGrafter extends ItemForestryTool implements IToolGrafter {
	public ItemGrafter(int maxDamage) {
		super(null);
		setMaxDamage(maxDamage);
		setMaxStackSize(1);
		setCreativeTab(Tabs.tabArboriculture);
		setHarvestLevel("grafter", 3);
		setEfficiencyOnProperMaterial(4.0f);
	}

	@Override
	public boolean canHarvestBlock(Block block, ItemStack itemStack) {
		if (block instanceof BlockLeaves || block.getMaterial() == Material.leaves) {
			return true;
		}
		return super.canHarvestBlock(block, itemStack);
	}

	@Override
	public boolean onBlockDestroyed(ItemStack itemstack, World world, Block block, int j, int k, int l, EntityLivingBase entityliving) {
		// damage is done by the harvested leaves
		return true;
	}

	@Override
	public float getSaplingModifier(ItemStack stack, World world, EntityPlayer player, int x, int y, int z) {
		return 100f;
	}
}

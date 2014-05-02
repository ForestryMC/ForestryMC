/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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
		super();
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
		if (ForgeHooks.isToolEffective(itemstack, block, md))
			return efficiencyOnProperMaterial;

		return func_150893_a(itemstack, block);
	}

	@Override
	public boolean onBlockDestroyed(ItemStack itemstack, World world, Block block, int j, int k, int l, EntityLivingBase entityliving) {
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

	@Override
	public float getSaplingModifier(ItemStack stack, World world, EntityPlayer player, int x, int y, int z) {
		return 100f;
	}

}

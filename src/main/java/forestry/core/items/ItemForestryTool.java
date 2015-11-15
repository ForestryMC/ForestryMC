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

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import forestry.core.utils.ItemStackUtil;

public class ItemForestryTool extends ItemForestry {
	private final ItemStack remnants;
	private float efficiencyOnProperMaterial;

	public ItemForestryTool(ItemStack remnants) {
		this.maxStackSize = 1;
		efficiencyOnProperMaterial = 6F;
		setMaxDamage(200);
		this.remnants = remnants;
		if (remnants != null) {
			MinecraftForge.EVENT_BUS.register(this);
		}
	}

	public void setEfficiencyOnProperMaterial(float efficiencyOnProperMaterial) {
		this.efficiencyOnProperMaterial = efficiencyOnProperMaterial;
	}

	@Override
	public float getDigSpeed(ItemStack itemstack, Block block, int metadata) {
		if (ForgeHooks.isToolEffective(itemstack, block, metadata)) {
			return efficiencyOnProperMaterial;
		}

		return super.getDigSpeed(itemstack, block, metadata);
	}

	@Override
	public boolean canHarvestBlock(Block block, ItemStack stack) {
		return ForgeHooks.canToolHarvestBlock(block, 0, stack);
	}

	@SubscribeEvent
	public void onDestroyCurrentItem(PlayerDestroyItemEvent event) {
		if (event.original == null || event.original.getItem() != this) {
			return;
		}

		EntityPlayer player = event.entityPlayer;
		World world = player.worldObj;

		if (!world.isRemote && remnants != null) {
			ItemStackUtil.dropItemStackAsEntity(remnants.copy(), world, player.posX, player.posY, player.posZ);
		}
	}

	@Override
	public boolean onBlockDestroyed(ItemStack itemstack, World world, Block block, int x, int y, int z, EntityLivingBase entityliving) {
		if (block.getBlockHardness(world, x, y, z) != 0) {
			itemstack.damageItem(1, entityliving);
		}
		return true;
	}

	@Override
	public boolean isFull3D() {
		return true;
	}
}

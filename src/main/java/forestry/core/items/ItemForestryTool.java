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

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public abstract class ItemForestryTool extends ItemForestry {

	private final ItemStack remnants;
	protected float efficiencyOnProperMaterial;
	private final List<Block> blocksEffectiveAgainst;

	protected ItemForestryTool(Block[] blocksEffectiveAgainst, ItemStack remnants) {
		this.blocksEffectiveAgainst = Arrays.asList(blocksEffectiveAgainst);
		this.maxStackSize = 1;
		efficiencyOnProperMaterial = 6F;
		setMaxDamage(200);
		this.remnants = remnants;
	}

	@Override
	public float func_150893_a(ItemStack itemstack, Block block) {
		if (blocksEffectiveAgainst.contains(block)) {
			return efficiencyOnProperMaterial;
		}
		return 1.0F;
	}

	@Override
	public float getDigSpeed(ItemStack itemstack, Block block, int md) {
		if (ForgeHooks.isToolEffective(itemstack, block, md)) {
			return efficiencyOnProperMaterial;
		}
		return func_150893_a(itemstack, block);
	}

	@SubscribeEvent
	public void onDestroyCurrentItem(PlayerDestroyItemEvent event) {
		if (event.original == null || event.original.getItem() != this) {
			return;
		}

		if (!event.entityPlayer.worldObj.isRemote && remnants != null) {
			EntityItem entity = new EntityItem(event.entityPlayer.worldObj, event.entityPlayer.posX, event.entityPlayer.posY, event.entityPlayer.posZ,
					remnants.copy());
			event.entityPlayer.worldObj.spawnEntityInWorld(entity);
		}
	}

	@Override
	public boolean onBlockDestroyed(ItemStack itemstack, World world, Block block, int j, int k, int l, EntityLivingBase entityliving) {
		itemstack.damageItem(1, entityliving);
		return true;
	}

	@Override
	public boolean isFull3D() {
		return true;
	}

}

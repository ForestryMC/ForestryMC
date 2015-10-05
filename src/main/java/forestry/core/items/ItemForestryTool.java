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
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import forestry.core.proxy.Proxies;

public abstract class ItemForestryTool extends ItemForestry {

	private final ItemStack remnants;
	protected float efficiencyOnProperMaterial;
	private final List<Block> blocksEffectiveAgainst;

	protected ItemForestryTool(Block[] blocksEffectiveAgainst, ItemStack remnants) {
		super();
		this.blocksEffectiveAgainst = Arrays.asList(blocksEffectiveAgainst);
		this.maxStackSize = 1;
		efficiencyOnProperMaterial = 6F;
		setMaxDamage(200);
		this.remnants = remnants;
	}
	
	@Override
	public float getStrVsBlock(ItemStack stack, Block block) {
		if (blocksEffectiveAgainst.contains(block)) {
			return efficiencyOnProperMaterial;
		}
		return 1.0F;
	}
	
	@Override
	public float getDigSpeed(ItemStack itemstack, IBlockState state) {
        for (String type : getToolClasses(itemstack))
        {
            if (state.getBlock().isToolEffective(type, state))
                return efficiencyOnProperMaterial;
        }
		return getStrVsBlock(itemstack, state.getBlock());
	}

	@SubscribeEvent
	public void onDestroyCurrentItem(PlayerDestroyItemEvent event) {
		if (event.original == null || event.original.getItem() != this) {
			return;
		}

		if (Proxies.common.isSimulating(event.entityPlayer.worldObj) && remnants != null) {
			EntityItem entity = new EntityItem(event.entityPlayer.worldObj, event.entityPlayer.posX, event.entityPlayer.posY, event.entityPlayer.posZ,
					remnants.copy());
			event.entityPlayer.worldObj.spawnEntityInWorld(entity);
		}
	}
	
	@Override
	public boolean onBlockDestroyed(ItemStack itemstack, World worldIn, Block blockIn, BlockPos pos, EntityLivingBase entityliving) {
		itemstack.damageItem(1, entityliving);
		return true;
	}

	@Override
	public boolean isFull3D() {
		return true;
	}

}

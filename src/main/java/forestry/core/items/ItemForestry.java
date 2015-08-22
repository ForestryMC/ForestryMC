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

import forestry.api.core.IModelObject;
import forestry.core.CreativeTabForestry;
import forestry.core.proxy.Proxies;
import forestry.core.render.TextureManager;
import forestry.core.utils.StringUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemForestry extends Item implements IModelObject {

	private boolean isBonemeal = false;

	public ItemForestry() {
		setCreativeTab(CreativeTabForestry.tabForestry);
	}

	public ItemForestry setBonemeal(boolean isBonemeal) {
		this.isBonemeal = isBonemeal;
		return this;
	}
	
	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (isBonemeal) {
			if (ItemDye.applyBonemeal(itemstack, world, pos, player)) {
				if (Proxies.common.isSimulating(world)) {
					world.playAuxSFX(2005, pos, 0);
				}

				return true;
			}
		}
		return super.onItemUse(itemstack, player, world, pos, side, hitX, hitY, hitZ);
	}

	@Override
	public ModelType getModelType() {
		return ModelType.DEFAULT;
	}
}

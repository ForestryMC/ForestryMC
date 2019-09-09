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

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.ItemGroupForestry;
import forestry.core.blocks.IBlockRotatable;
import forestry.core.tiles.TileForestry;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.ItemTooltipUtil;

public class ItemBlockForestry<B extends Block> extends BlockItem {

	public ItemBlockForestry(B block, Item.Properties builder) {
		super(block, builder);
	}

	public ItemBlockForestry(B block) {
		this(block, new Item.Properties().group(ItemGroupForestry.tabForestry));
	}

	@Override
	public B getBlock() {
		//noinspection unchecked
		return (B) super.getBlock();
	}

	@Override
	public String getTranslationKey(ItemStack itemstack) {
		return getBlock().getTranslationKey();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag advanced) {
		super.addInformation(stack, world, tooltip, advanced);
		ItemTooltipUtil.addInformation(stack, world, tooltip, advanced);
	}

	//TODO is this the right method
	//and is it needed any more
	@Override
	public ActionResultType tryPlace(BlockItemUseContext context) {
		ActionResultType placed = super.tryPlace(context);

		ItemStack stack = context.getItem();
		BlockPos pos = context.getPos();
		World world = context.getWorld();
		PlayerEntity player = context.getPlayer();
		Direction side = context.getFace();

		if (placed == ActionResultType.SUCCESS) {
			if (getBlock().hasTileEntity(getBlock().getDefaultState())) {    //TODO how to getComb the state??
				if (stack.getItem() instanceof ItemBlockNBT && stack.getTag() != null) {
					TileForestry tile = TileUtil.getTile(world, pos, TileForestry.class);
					if (tile != null) {
						tile.read(stack.getTag());
						tile.setPos(pos);
					}
				}
			}

			if (getBlock() instanceof IBlockRotatable) {
				((IBlockRotatable) getBlock()).rotateAfterPlacement(player, world, pos, side);
			}
		}

		return placed;
	}
}

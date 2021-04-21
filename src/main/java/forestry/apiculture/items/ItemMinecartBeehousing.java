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
package forestry.apiculture.items;

import java.util.Locale;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.MinecartItem;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.core.IItemSubtype;
import forestry.api.core.ItemGroups;
import forestry.apiculture.entities.MinecartEntityApiary;
import forestry.apiculture.entities.MinecartEntityBeeHousingBase;
import forestry.apiculture.entities.MinecartEntityBeehouse;

public class ItemMinecartBeehousing extends MinecartItem {

	//TODO merge with BlockTypeApiculture?
	public enum Type implements IItemSubtype {
		BEE_HOUSE,
		APIARY;

		@Override
		public String getSerializedName() {
			return toString().toLowerCase(Locale.ENGLISH);
		}
	}

	private final Type type;

	public ItemMinecartBeehousing(Type type) {
		super(null, (new Item.Properties()).durability(0).tab(ItemGroups.tabApiculture));
		this.type = type;

		DispenserBlock.DISPENSER_REGISTRY.put(this, IDispenseItemBehavior.NOOP);
	}

	@Override
	public ActionResultType useOn(ItemUseContext context) {
		World world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		PlayerEntity player = context.getPlayer();
		BlockState blockstate = world.getBlockState(pos);

		if (!AbstractRailBlock.isRail(world.getBlockState(pos))) {
			return ActionResultType.PASS;
		}

		ItemStack stack = player.getItemInHand(context.getHand());

		if (!context.getLevel().isClientSide) {
			RailShape railshape = blockstate.getBlock() instanceof AbstractRailBlock ? ((AbstractRailBlock) blockstate.getBlock()).getRailDirection(blockstate, world, pos, null) : RailShape.NORTH_SOUTH;
			double offset = 0.0D;
			if (railshape.isAscending()) {
				offset = 0.5D;
			}

			MinecartEntityBeeHousingBase minecart;


			if (type == Type.BEE_HOUSE) {
				minecart = new MinecartEntityBeehouse(world, pos.getX() + 0.5D, pos.getY() + 0.0625D + offset, pos.getZ() + 0.5D);
			} else {
				minecart = new MinecartEntityApiary(world, pos.getX() + 0.5D, pos.getY() + 0.0625D + offset, pos.getZ() + 0.5D);
			}

			minecart.getOwnerHandler().setOwner(player.getGameProfile());

			if (stack.hasCustomHoverName()) {
				minecart.setCustomName(stack.getHoverName());
			}

			world.addFreshEntity(minecart);
		}

		stack.shrink(1);
		return ActionResultType.sidedSuccess(world.isClientSide);
	}

	@Override
	public String getDescriptionId(ItemStack stack) {
		return super.getDescriptionId(stack);
	}
}

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

import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

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

		DispenserBlock.DISPENSER_REGISTRY.put(this, DispenseItemBehavior.NOOP);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		Player player = context.getPlayer();
		BlockState blockstate = world.getBlockState(pos);

		if (!BaseRailBlock.isRail(world.getBlockState(pos))) {
			return InteractionResult.PASS;
		}

		ItemStack stack = player.getItemInHand(context.getHand());

		if (!context.getLevel().isClientSide) {
			RailShape railshape = blockstate.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock) blockstate.getBlock()).getRailDirection(blockstate, world, pos, null) : RailShape.NORTH_SOUTH;
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
		return InteractionResult.sidedSuccess(world.isClientSide);
	}

	@Override
	public String getDescriptionId(ItemStack stack) {
		return super.getDescriptionId(stack);
	}
}

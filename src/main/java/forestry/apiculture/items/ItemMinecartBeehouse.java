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

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.MinecartItem;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.apiculture.entities.MinecartEntityApiary;
import forestry.apiculture.entities.MinecartEntityBeeHousingBase;
import forestry.apiculture.entities.MinecartEntityBeehouse;

public class ItemMinecartBeehouse extends MinecartItem implements IItemModelRegister {
	private final String[] definition = new String[]{"cart.beehouse", "cart.apiary"};

	private static final IDispenseItemBehavior dispenserMinecartBehavior = (source, stack) -> stack;

	public ItemMinecartBeehouse() {
		super(MinecartEntity.Type.CHEST, (new Item.Properties()).maxDamage(0));
		//		setHasSubtypes(true);

		DispenserBlock.DISPENSE_BEHAVIOR_REGISTRY.put(this, dispenserMinecartBehavior);
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		World world = context.getWorld();
		BlockPos pos = context.getPos();
		PlayerEntity player = context.getPlayer();


		if (!AbstractRailBlock.isRail(world.getBlockState(pos))) {
			return ActionResultType.PASS;
		}

		ItemStack stack = player.getHeldItem(context.getHand());

		if (!context.getWorld().isRemote) {
			MinecartEntityBeeHousingBase minecart;

			//TODO flatten
			if (true) {//stack.getItem() == 0) {
				minecart = new MinecartEntityBeehouse(world, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);
			} else {
				minecart = new MinecartEntityApiary(world, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);
			}

			minecart.getOwnerHandler().setOwner(player.getGameProfile());

			if (stack.hasDisplayName()) {
				minecart.setCustomName(stack.getDisplayName());
			}

			world.addEntity(minecart);
		}

		stack.shrink(1);
		return ActionResultType.SUCCESS;
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		//		if (stack.getItemDamage() >= definition.length || stack.getItemDamage() < 0) {
			return "item.forestry.unknown";
		//		} else {
		//			return "item.forestry." + definition[stack.getItemDamage()];
		//		}
	}

	/* MODELS */
	@OnlyIn(Dist.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		for (int i = 0; i < definition.length; i++) {
			manager.registerItemModel(item, i, definition[i]);
		}
	}

	@Override
	public void fillItemGroup(ItemGroup tab, NonNullList<ItemStack> subItems) {
		if (this.isInGroup(tab)) {
			for (int i = 0; i < definition.length; i++) {
				subItems.add(new ItemStack(this, 1)); //TODO
			}
		}
	}

	//TODO
	public ItemStack getBeeHouseMinecart() {
		return new ItemStack(this, 1);
	}

	//TODO
	public ItemStack getApiaryMinecart() {
		return new ItemStack(this, 1);
	}
}

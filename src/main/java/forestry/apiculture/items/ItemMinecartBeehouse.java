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

import java.util.List;

import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRailBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.apiculture.entities.EntityMinecartApiary;
import forestry.apiculture.entities.EntityMinecartBeeHousingBase;
import forestry.apiculture.entities.EntityMinecartBeehouse;

public class ItemMinecartBeehouse extends ItemMinecart implements IItemModelRegister {
	private final String[] definition = new String[]{"cart.beehouse", "cart.apiary"};
	
	private static final IBehaviorDispenseItem dispenserMinecartBehavior = new IBehaviorDispenseItem() {
		@Override
		public ItemStack dispense(IBlockSource source, ItemStack stack) {
			return stack;
		}
	};

	public ItemMinecartBeehouse() {
		super(null);
		setMaxDamage(0);
		setHasSubtypes(true);
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, dispenserMinecartBehavior);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!BlockRailBase.isRailBlock(worldIn.getBlockState(pos))) {
			return EnumActionResult.PASS;
		}

		if (!worldIn.isRemote) {
			EntityMinecartBeeHousingBase entityMinecart;
			if (stack.getItemDamage() == 0) {
				entityMinecart = new EntityMinecartBeehouse(worldIn, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);
			} else {
				entityMinecart = new EntityMinecartApiary(worldIn, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);
			}

			entityMinecart.getOwnerHandler().setOwner(playerIn.getGameProfile());

			if (stack.hasDisplayName()) {
				entityMinecart.setCustomNameTag(stack.getDisplayName());
			}

			worldIn.spawnEntityInWorld(entityMinecart);
		}

		--stack.stackSize;
		return EnumActionResult.SUCCESS;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		if (stack.getItemDamage() >= definition.length || stack.getItemDamage() < 0) {
			return "item.forestry.unknown";
		} else {
			return "item.for." + definition[stack.getItemDamage()];
		}
	}

	/* MODELS */
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		for (int i = 0; i < definition.length; i++) {
			manager.registerItemModel(item, i, definition[i]);
		}
	}

	@Override
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List<ItemStack> itemList) {
		for (int i = 0; i < definition.length; i++) {
			itemList.add(new ItemStack(this, 1, i));
		}
	}

	public ItemStack getBeeHouseMinecart() {
		return new ItemStack(this, 1, 0);
	}

	public ItemStack getApiaryMinecart() {
		return new ItemStack(this, 1, 1);
	}
}

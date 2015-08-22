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
package forestry.core.gadgets;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.CreativeTabForestry;
import forestry.core.config.ForestryItem;
import forestry.core.render.TextureManager;

public class BlockResource extends Block {
	
	public static final PropertyEnum RESOURCES = PropertyEnum.create("resource", Resources.class);
	
	public BlockResource() {
		super(Material.rock);
		setHardness(3F);
		setResistance(5F);
		setCreativeTab(CreativeTabForestry.tabForestry);
	}
	
	public enum Resources implements IStringSerializable
	{
		APATITE,
		COPPER,
		TIN;

		@Override
		public String getName() {
			return name().toLowerCase();
		}
		
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return ((Resources)state.getValue(RESOURCES)).ordinal();
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(RESOURCES,  Resources.values()[meta]);
	}
	
	@Override
	public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune) {
		super.dropBlockAsItemWithChance(world, pos, state, chance, fortune);
		if (getMetaFromState(state) == 0) {
			this.dropXpOnBlockBreak(world, pos, MathHelper.getRandomIntegerInRange(world.rand, 1, 4));
		}
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();

		if (getMetaFromState(state) == 0) {
			int fortmod = RANDOM.nextInt(fortune + 2) - 1;
			if (fortmod < 0) {
				fortmod = 0;
			}

			int amount = (2 + RANDOM.nextInt(5)) * (fortmod + 1);
			if (amount > 0) {
				drops.add(ForestryItem.apatite.getItemStack(amount));
			}
		} else {
			drops.add(new ItemStack(this, 1, getMetaFromState(state)));
		}

		return drops;
	}
	
	@Override
	public int getDamageValue(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		return getMetaFromState(state);
	}

	// / CREATIVE INVENTORY
	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List itemList) {
		itemList.add(new ItemStack(this, 1, 0));
		itemList.add(new ItemStack(this, 1, 1));
		itemList.add(new ItemStack(this, 1, 2));
	}

}

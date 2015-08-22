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
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.core.IModelObject;
import forestry.api.core.IVariantObject;
import forestry.core.CreativeTabForestry;
import forestry.core.render.TextureManager;

public class BlockStainedGlass extends BlockBreakable implements IModelObject, IVariantObject {

	public final static PropertyEnum COLOR = PropertyEnum.create("color", EnumDyeColor.class);
	
	public BlockStainedGlass() {
		super(Material.glass, true);
		setHardness(0.3F);
		setStepSound(soundTypeGlass);
		setCreativeTab(CreativeTabForestry.tabForestry);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List itemList) {
		for (int i = 0; i < 16; i++) {
			itemList.add(new ItemStack(this, 1, i));
		}
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumDyeColor)state.getValue(COLOR)).getMetadata();
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(COLOR, EnumDyeColor.byMetadata(meta));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing side) {
		Block block = world.getBlockState(pos).getBlock();
		return block != this && super.shouldSideBeRendered(world, pos, side);
	}


	@Override
	public int getDamageValue(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		return getMetaFromState(state);
	}

	@Override
	public int quantityDropped(Random rand) {
		return 0;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	protected boolean canSilkHarvest() {
		return true;
	}

	@Override
	public String[] getVariants() {
		ArrayList<String> list = new ArrayList<String>();
		for(EnumDyeColor dye : EnumDyeColor.values())
		{
			list.add(dye.getUnlocalizedName());
		}
		return list.toArray(new String[list.size()]);
	}

	@Override
	public ModelType getModelType() {
		return ModelType.META;
	}

}

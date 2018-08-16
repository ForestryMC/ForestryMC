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
package forestry.greenhouse.blocks;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import net.minecraftforge.client.model.ModelLoader;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.core.blocks.IBlockWithMeta;
import forestry.core.utils.Translator;

public class BlockClimatiser extends Block implements IBlockWithMeta, IItemModelRegister {
	public static final PropertyEnum<BlockClimatiserType> TYPE = PropertyEnum.create("type", BlockClimatiserType.class);

	public BlockClimatiser() {
		super(Material.GLASS);

		setHardness(1f);
		setHarvestLevel("pickaxe", 0);
		this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, BlockClimatiserType.HUMIDIFIER));
	}

	@Override
	public String getNameFromMeta(int meta) {
		BlockClimatiserType type = BlockClimatiserType.VALUES[meta];
		return type.getName();
	}

	@SuppressWarnings("deprecation")
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(TYPE, BlockClimatiserType.VALUES[meta]);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(TYPE).ordinal();
	}

	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		for (int i = 0; i < BlockClimatiserType.VALUES.length; i++) {
			list.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, TYPE);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		tooltip.add(TextFormatting.RED.toString() + Translator.translateToLocal("tile.for.greenhouse.deprecated1"));
		tooltip.add(TextFormatting.RED.toString() + Translator.translateToLocal("tile.for.greenhouse.deprecated2"));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		for (int i = 0; i < BlockClimatiserType.VALUES.length; i++) {
			ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation("forestry:greenhouse/" + BlockClimatiserType.VALUES[i].getName(), "inventory"));
		}
	}
}

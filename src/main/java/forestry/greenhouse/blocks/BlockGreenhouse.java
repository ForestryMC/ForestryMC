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
import net.minecraft.util.BlockRenderLayer;
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

public class BlockGreenhouse extends Block implements IItemModelRegister, IBlockWithMeta {
	public static final PropertyEnum<BlockGreenhouseType> TYPE = PropertyEnum.create("type", BlockGreenhouseType.class);

	public BlockGreenhouse() {
		super(Material.ROCK);

		setHardness(1.0f);
		setHarvestLevel("pickaxe", 0);
		this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, BlockGreenhouseType.PLAIN));
	}

	@Override
	public String getNameFromMeta(int meta) {
		BlockGreenhouseType type = BlockGreenhouseType.VALUES[meta];
		return type.getName();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		for (int i = 0; i < BlockGreenhouseType.VALUES.length; i++) {
			if (i == 1 || i == 2) {
				continue;
			}
			ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation("forestry:greenhouse/" + BlockGreenhouseType.VALUES[i].getName(), "inventory"));
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(TYPE, BlockGreenhouseType.VALUES[meta]);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(TYPE).ordinal();
	}

	@Override
	public int damageDropped(IBlockState state) {
		int meta = getMetaFromState(state);
		if (meta == 1 || meta == 2) {
			meta = 0;
		}
		return meta;
	}

	/* MODELS */
	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.SOLID;
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
		for (int i = 0; i < BlockGreenhouseType.VALUES.length; i++) {
			if (i == 1 || i == 2) {
				continue;
			}
			items.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, TYPE);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
		tooltip.add(TextFormatting.RED.toString() + Translator.translateToLocal("tile.for.greenhouse.deprecated1"));
		tooltip.add(TextFormatting.RED.toString() + Translator.translateToLocal("tile.for.greenhouse.deprecated2"));
	}
}

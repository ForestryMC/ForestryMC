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
package forestry.arboriculture.blocks;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumWoodType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.api.core.IStateMapperRegister;
import forestry.api.core.Tabs;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.WoodHelper;
import forestry.arboriculture.WoodHelper.WoodMeshDefinition;
import forestry.arboriculture.blocks.property.PropertyWoodType;
import forestry.core.proxy.Proxies;

public abstract class BlockArbSlab extends BlockSlab implements IWoodTyped, IItemModelRegister, IStateMapperRegister {
	private static final int VARIANTS_PER_BLOCK = 8;
	private static final int VARIANTS_META_MASK = VARIANTS_PER_BLOCK - 1;

	public static List<BlockArbSlab> create(boolean fireproof, final boolean doubleSlab) {
		List<BlockArbSlab> blocks = new ArrayList<>();
		final int blockCount = PropertyWoodType.getBlockCount(VARIANTS_PER_BLOCK);
		for (int blockNumber = 0; blockNumber < blockCount; blockNumber++) {
			final PropertyWoodType variant = PropertyWoodType.create("variant", blockNumber, VARIANTS_PER_BLOCK);
			BlockArbSlab block = new BlockArbSlab(fireproof, blockNumber) {
				@Nonnull
				@Override
				public PropertyWoodType getVariant() {
					return variant;
				}

				@Override
				public boolean isDouble() {
					return doubleSlab;
				}
			};
			blocks.add(block);
		}
		return blocks;
	}

	private final boolean fireproof;
	private final int blockNumber;

	private BlockArbSlab(boolean fireproof, int blockNumber) {
		super(MaterialArbWood.ARB_WOOD);
		this.fireproof = fireproof;
		this.blockNumber = blockNumber;

		IBlockState iblockstate = this.blockState.getBaseState();

		if (!isDouble()) {
			iblockstate = iblockstate.withProperty(HALF, EnumBlockHalf.BOTTOM);
		}

		PropertyWoodType variant = getVariant();
		setDefaultState(iblockstate.withProperty(variant, variant.getFirstType()));

		setCreativeTab(Tabs.tabArboriculture);
		setLightOpacity(0);
		setHardness(2.0F);
		setResistance(5.0F);
		setSoundType(SoundType.WOOD);
		setHarvestLevel("axe", 0);
	}

	@Override
	public boolean isFireproof() {
		return fireproof;
	}

	@Nonnull
	public abstract PropertyWoodType getVariant();

	public int getBlockNumber() {
		return blockNumber;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return this.isDouble() ? new BlockStateContainer(this, getVariant()) : new BlockStateContainer(this, HALF, getVariant());
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumWoodType woodType = getWoodType(meta);
		IBlockState iblockstate = this.getDefaultState().withProperty(getVariant(), woodType);

		if (!this.isDouble()) {
			iblockstate = iblockstate.withProperty(HALF, (meta & 8) == 0 ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP);
		}

		return iblockstate;
	}

	@Nonnull
	@Override
	public EnumWoodType getWoodType(int meta) {
		int variantMeta = (meta & VARIANTS_META_MASK) + blockNumber * VARIANTS_PER_BLOCK;
		return EnumWoodType.byMetadata(variantMeta);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		EnumWoodType woodType = state.getValue(getVariant());
		int meta = woodType.getMetadata() - blockNumber * VARIANTS_PER_BLOCK;

		if (!this.isDouble() && state.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP) {
			meta |= 8;
		}

		return meta;
	}

	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(getVariant()).getMetadata() - blockNumber * VARIANTS_PER_BLOCK;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		EnumWoodType woodType = state.getValue(getVariant());
		ItemStack slab = TreeManager.woodAccess.getSlab(woodType, isFireproof());
		return slab.getItem();
	}

	@SuppressWarnings("deprecation") // this is the way the vanilla slabs work
	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		EnumWoodType woodType = state.getValue(getVariant());
		ItemStack slab = TreeManager.woodAccess.getSlab(woodType, isFireproof());
		return new ItemStack(slab.getItem(), 1, getMetaFromState(state));
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerVariant(item, WoodHelper.getResourceLocations(this));
		manager.registerItemModel(item, new WoodMeshDefinition(this));
	}

	@Override
	public String getUnlocalizedName(int meta) {
		EnumWoodType woodType = getWoodType(meta);
		return WoodHelper.getDisplayName(this, woodType);
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List<ItemStack> list) {
		if (!isDouble()) {
			for (EnumWoodType woodType : getVariant().getAllowedValues()) {
				list.add(TreeManager.woodAccess.getSlab(woodType, fireproof));
			}
		}
	}

	@Override
	public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
		int meta = getMetaFromState(blockState);
		EnumWoodType woodType = getWoodType(meta);
		return woodType.getHardness();
	}

	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return fireproof ? 0 : 20;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return fireproof ? 0 : 5;
	}

	@Nonnull
	@Override
	public String getBlockKind() {
		return "slab";
	}

	@Override
	public IProperty getVariantProperty() {
		return getVariant();
	}

	@Override
	public Comparable<?> getTypeForItem(ItemStack stack) {
		return getWoodType(stack.getMetadata());
	}

	@Nonnull
	@Override
	public Collection<EnumWoodType> getWoodTypes() {
		return getVariant().getAllowedValues();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerStateMapper() {
		String blockPath = isDouble() ? "slab/double" : "slab";
		Proxies.render.registerStateMapper(this, new WoodTypeStateMapper(this, blockPath, getVariant()));
	}
}

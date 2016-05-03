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

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
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
import forestry.arboriculture.PluginArboriculture;
import forestry.arboriculture.WoodHelper;
import forestry.arboriculture.WoodHelper.WoodMeshDefinition;
import forestry.arboriculture.blocks.property.PropertyWoodType;
import forestry.core.proxy.Proxies;

public abstract class BlockArbFence extends BlockFence implements IWoodTyped, IItemModelRegister, IStateMapperRegister {
	private static final int VARIANTS_PER_BLOCK = 16;
	private static final int VARIANTS_META_MASK = VARIANTS_PER_BLOCK - 1;

	public static List<BlockArbFence> create(boolean fireproof) {
		List<BlockArbFence> blocks = new ArrayList<>();
		final int blockCount = PropertyWoodType.getBlockCount(VARIANTS_PER_BLOCK);
		for (int blockNumber = 0; blockNumber < blockCount; blockNumber++) {
			final PropertyWoodType variant = PropertyWoodType.create("variant", blockNumber, VARIANTS_PER_BLOCK);
			BlockArbFence block = new BlockArbFence(fireproof, blockNumber) {
				@Nonnull
				@Override
				protected PropertyWoodType getVariant() {
					return variant;
				}
			};
			blocks.add(block);
		}
		return blocks;
	}

	private final boolean fireproof;
	private final int blockNumber;

	public BlockArbFence(boolean fireproof, int blockNumber) {
		super(Material.WOOD, BlockPlanks.EnumType.OAK.getMapColor());
		this.fireproof = fireproof;
		this.blockNumber = blockNumber;

		PropertyWoodType variant = getVariant();
		this.setDefaultState(this.blockState.getBaseState()
				.withProperty(NORTH, false)
				.withProperty(EAST, false)
				.withProperty(SOUTH, false)
				.withProperty(WEST, false)
				.withProperty(variant, variant.getFirstType())
		);

		setHardness(2.0F);
		setResistance(5.0F);
		setHarvestLevel("axe", 0);
		setSoundType(SoundType.WOOD);
		setCreativeTab(Tabs.tabArboriculture);
	}

	@Nonnull
	protected abstract PropertyWoodType getVariant();

	@Override
	public boolean isFireproof() {
		return fireproof;
	}

	public int getBlockNumber() {
		return blockNumber;
	}

	@Override
	public boolean canConnectTo(IBlockAccess worldIn, BlockPos pos) {
		IBlockState blockState = worldIn.getBlockState(pos);
		Block block = blockState.getBlock();
		if (PluginArboriculture.validFences.contains(block) || block instanceof BlockFence || block instanceof BlockFenceGate) {
			return true;
		}

		if (block != Blocks.BARRIER) {
			Material blockMaterial = block.getMaterial(blockState);
			if (blockMaterial.isOpaque() && blockState.isFullCube() && blockMaterial != Material.GOURD) {
				return true;
			}
		}
		return false;
	}

	/* Models */
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerVariant(item, WoodHelper.getResourceLocations(this));
		manager.registerItemModel(item, new WoodMeshDefinition(this));
	}

	@Override
	public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
		int meta = getMetaFromState(blockState);
		EnumWoodType woodType = getWoodType(meta);
		return woodType.getHardness();
	}

	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
		if (fireproof) {
			return 0;
		}
		return 20;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
		if (fireproof) {
			return 0;
		}
		return 5;
	}

	@Nonnull
	@Override
	public String getBlockKind() {
		return "fences";
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return damageDropped(state);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumWoodType woodType = getWoodType(meta);
		return getDefaultState().withProperty(getVariant(), woodType);
	}

	@Nonnull
	@Override
	public EnumWoodType getWoodType(int meta) {
		int variantMeta = (meta & VARIANTS_META_MASK) + blockNumber * VARIANTS_PER_BLOCK;
		return EnumWoodType.byMetadata(variantMeta);
	}

	@Nonnull
	@Override
	public Collection<EnumWoodType> getWoodTypes() {
		return getVariant().getAllowedValues();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, NORTH, EAST, WEST, SOUTH, getVariant());
	}

	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(getVariant()).getMetadata() - blockNumber * VARIANTS_PER_BLOCK;
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		EnumWoodType woodType = getWoodType(meta);
		return getDefaultState().withProperty(getVariant(), woodType);
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
		for (EnumWoodType woodType : getVariant().getAllowedValues()) {
			list.add(TreeManager.woodItemAccess.getFence(woodType, fireproof));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerStateMapper() {
		Proxies.render.registerStateMapper(this, new WoodTypeStateMapper(this, getVariant()));
	}
}

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

import net.minecraft.block.BlockLog;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
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
import forestry.arboriculture.blocks.property.PropertyWoodType;
import forestry.core.proxy.Proxies;

public abstract class BlockArbLog extends BlockLog implements IWoodTyped, IStateMapperRegister, IItemModelRegister {
	private static final int VARIANTS_PER_BLOCK = 4;
	private static final int VARIANTS_META_MASK = VARIANTS_PER_BLOCK - 1;

	public static List<BlockArbLog> create(boolean fireproof) {
		List<BlockArbLog> logs = new ArrayList<>();
		final int blockCount = PropertyWoodType.getBlockCount(VARIANTS_PER_BLOCK);
		for (int blockNumber = 0; blockNumber < blockCount; blockNumber++) {
			final PropertyWoodType variant = PropertyWoodType.create("variant", blockNumber, VARIANTS_PER_BLOCK);
			BlockArbLog log = new BlockArbLog(fireproof, blockNumber) {
				@Nonnull
				@Override
				protected PropertyWoodType getVariant() {
					return variant;
				}
			};
			logs.add(log);
		}
		return logs;
	}

	private final boolean fireproof;
	private final int blockNumber;

	private BlockArbLog(boolean fireproof, int blockNumber) {
		this.fireproof = fireproof;
		this.blockNumber = blockNumber;

		PropertyWoodType variant = getVariant();
		setDefaultState(this.blockState.getBaseState().withProperty(variant, variant.getFirstType()).withProperty(LOG_AXIS, EnumAxis.NONE));

		setHarvestLevel("axe", 0);
		setCreativeTab(Tabs.tabArboriculture);
	}

	@Nonnull
	@Override
	public String getBlockKind() {
		return "log";
	}

	@Override
	public boolean isFireproof() {
		return fireproof;
	}

	@Nonnull
	protected abstract PropertyWoodType getVariant();

	public int getBlockNumber() {
		return blockNumber;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumWoodType woodType = getWoodType(meta);
		EnumAxis axis = getAxis(meta);
		return getDefaultState().withProperty(getVariant(), woodType).withProperty(LOG_AXIS, axis);
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

	private static EnumAxis getAxis(int meta) {
		switch (meta & 12) {
			case 0:
				return EnumAxis.Y;
			case 4:
				return EnumAxis.X;
			case 8:
				return EnumAxis.Z;
			default:
				return EnumAxis.NONE;
		}
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public int getMetaFromState(IBlockState state) {
		int i = damageDropped(state);

		switch (state.getValue(LOG_AXIS)) {
			case X:
				i |= 4;
				break;
			case Z:
				i |= 8;
				break;
			case NONE:
				i |= 12;
		}

		return i;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, getVariant(), LOG_AXIS);
	}

	@Override
	protected ItemStack createStackedBlock(IBlockState state) {
		int meta = damageDropped(state);
		Item item = Item.getItemFromBlock(this);
		return new ItemStack(item, 1, meta);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(getVariant()).getMetadata() - blockNumber * VARIANTS_PER_BLOCK;
	}
	
	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		EnumAxis axis = EnumAxis.fromFacingAxis(facing.getAxis());
		EnumWoodType woodType = getWoodType(meta);
		return getDefaultState().withProperty(getVariant(), woodType).withProperty(LOG_AXIS, axis);
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
		for (EnumWoodType woodType : getVariant().getAllowedValues()) {
			list.add(TreeManager.woodItemAccess.getLog(woodType, fireproof));
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerVariant(item, WoodHelper.getResourceLocations(this));
		manager.registerItemModel(item, new WoodHelper.WoodMeshDefinition(this));
	}

	@Override
	public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
		int meta = getMetaFromState(blockState);
		EnumWoodType woodType = getWoodType(meta);
		return woodType.getHardness();
	}

	/* PROPERTIES */
	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
		if (fireproof) {
			return 0;
		} else if (face == EnumFacing.DOWN) {
			return 20;
		} else if (face != EnumFacing.UP) {
			return 10;
		} else {
			return 5;
		}
	}

	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
		if (fireproof) {
			return 0;
		}
		return super.getFlammability(world, pos, face);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerStateMapper() {
		Proxies.render.registerStateMapper(this, new WoodTypeStateMapper(this, getVariant()));
	}

}

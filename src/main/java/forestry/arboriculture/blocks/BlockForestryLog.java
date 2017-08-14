package forestry.arboriculture.blocks;

import java.util.Collection;

import net.minecraft.block.BlockLog;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.api.core.IStateMapperRegister;
import forestry.api.core.Tabs;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.WoodHelper;
import forestry.arboriculture.proxy.ProxyArboricultureClient;

public abstract class BlockForestryLog<T extends Enum<T> & IWoodType> extends BlockLog implements IWoodTyped, IStateMapperRegister, IItemModelRegister {
	protected static final int VARIANTS_PER_BLOCK = 4;

	private final boolean fireproof;
	private final int blockNumber;

	protected BlockForestryLog(boolean fireproof, int blockNumber) {
		this.fireproof = fireproof;
		this.blockNumber = blockNumber;

		PropertyWoodType<T> variant = getVariant();
		setDefaultState(this.blockState.getBaseState().withProperty(variant, variant.getFirstType()).withProperty(LOG_AXIS, EnumAxis.Y));

		setHarvestLevel("axe", 0);
		setCreativeTab(Tabs.tabArboriculture);
	}


	@Override
	public final WoodBlockKind getBlockKind() {
		return WoodBlockKind.LOG;
	}

	@Override
	public final boolean isFireproof() {
		return fireproof;
	}

	public final int getBlockNumber() {
		return blockNumber;
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

	@Override
	public final IBlockState getStateFromMeta(int meta) {
		T woodType = getWoodType(meta);
		EnumAxis axis = getAxis(meta);
		return getDefaultState().withProperty(getVariant(), woodType).withProperty(LOG_AXIS, axis);
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public final int getMetaFromState(IBlockState state) {
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

	public abstract PropertyWoodType<T> getVariant();

	@Override
	public abstract T getWoodType(int meta);

	@Override
	public final Collection<T> getWoodTypes() {
		return getVariant().getAllowedValues();
	}

	@Override
	protected final BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, getVariant(), LOG_AXIS);
	}

	@Override
	public final int damageDropped(IBlockState state) {
		return state.getValue(getVariant()).getMetadata() - blockNumber * VARIANTS_PER_BLOCK;
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		EnumAxis axis = EnumAxis.fromFacingAxis(facing.getAxis());
		T woodType = getWoodType(meta);
		return getDefaultState().withProperty(getVariant(), woodType).withProperty(LOG_AXIS, axis);
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		for (T woodType : getVariant().getAllowedValues()) {
			list.add(TreeManager.woodAccess.getStack(woodType, getBlockKind(), fireproof));
		}
	}

	@Override
	public final float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
		int meta = getMetaFromState(blockState);
		T woodType = getWoodType(meta);
		return woodType.getHardness();
	}

	@Override
	protected ItemStack getSilkTouchDrop(IBlockState state) {
		ItemStack silkTouchDrop = super.getSilkTouchDrop(state);
		int damage = this.damageDropped(state);
		silkTouchDrop.setItemDamage(damage);
		return silkTouchDrop;
	}

	/* PROPERTIES */
	@Override
	public final int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
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
	public final int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
		if (fireproof) {
			return 0;
		}
		return 5;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public final void registerModel(Item item, IModelManager manager) {
		ModelBakery.registerItemVariants(item, WoodHelper.getDefaultResourceLocations(this));
		ProxyArboricultureClient.registerWoodMeshDefinition(item, new WoodHelper.WoodMeshDefinition(this));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final void registerStateMapper() {
		ProxyArboricultureClient.registerWoodStateMapper(this, new WoodTypeStateMapper(this, getVariant()));
	}
}

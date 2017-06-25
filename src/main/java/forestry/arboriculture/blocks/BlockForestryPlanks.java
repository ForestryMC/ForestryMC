package forestry.arboriculture.blocks;

import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
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

public abstract class BlockForestryPlanks<T extends Enum<T> & IWoodType> extends Block implements IWoodTyped, IStateMapperRegister, IItemModelRegister {
	protected static final int VARIANTS_PER_BLOCK = 16;

	private final boolean fireproof;
	private final int blockNumber;

	protected BlockForestryPlanks(boolean fireproof, int blockNumber) {
		super(Material.WOOD);
		this.fireproof = fireproof;
		this.blockNumber = blockNumber;

		PropertyWoodType<T> variant = getVariant();
		setDefaultState(this.blockState.getBaseState().withProperty(variant, variant.getFirstType()));

		setResistance(5.0F);
		setHarvestLevel("axe", 0);
		setSoundType(SoundType.WOOD);
		setCreativeTab(Tabs.tabArboriculture);
	}

	@Override
	public boolean isFireproof() {
		return fireproof;
	}

	public abstract PropertyWoodType<T> getVariant();

	@Override
	public abstract T getWoodType(int meta);

	public int getBlockNumber() {
		return blockNumber;
	}

	@Override
	public WoodBlockKind getBlockKind() {
		return WoodBlockKind.PLANKS;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		T woodType = getWoodType(meta);
		return getDefaultState().withProperty(getVariant(), woodType);
	}

	@Override
	public Collection<T> getWoodTypes() {
		return getVariant().getAllowedValues();
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return damageDropped(state);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, getVariant());
	}

	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(getVariant()).getMetadata() - blockNumber * VARIANTS_PER_BLOCK;
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		T woodType = getWoodType(meta);
		return getDefaultState().withProperty(getVariant(), woodType);
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		for (T woodType : getVariant().getAllowedValues()) {
			list.add(TreeManager.woodAccess.getStack(woodType, getBlockKind(), fireproof));
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		ModelBakery.registerItemVariants(item, WoodHelper.getDefaultResourceLocations(this));
		ProxyArboricultureClient.registerWoodMeshDefinition(item, new WoodHelper.WoodMeshDefinition(this));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final void registerStateMapper() {
		ProxyArboricultureClient.registerWoodStateMapper(this, new WoodTypeStateMapper(this, getVariant()));
	}

	@Override
	public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
		int meta = getMetaFromState(blockState);
		T woodType = getWoodType(meta);
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
}

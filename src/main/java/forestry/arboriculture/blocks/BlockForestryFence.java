package forestry.arboriculture.blocks;

import java.util.Collection;

import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.api.core.IStateMapperRegister;
import forestry.api.core.Tabs;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.PluginArboriculture;
import forestry.arboriculture.WoodHelper;
import forestry.core.proxy.Proxies;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
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

public abstract class BlockForestryFence<T extends Enum<T> & IWoodType> extends BlockFence implements IWoodTyped, IItemModelRegister, IStateMapperRegister {
	protected static final int VARIANTS_PER_BLOCK = 16;

	private final boolean fireproof;
	private final int blockNumber;

	protected BlockForestryFence(boolean fireproof, int blockNumber) {
		super(Material.WOOD, BlockPlanks.EnumType.OAK.getMapColor());
		this.fireproof = fireproof;
		this.blockNumber = blockNumber;

		PropertyWoodType<T> variant = getVariant();
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

	public abstract PropertyWoodType<T> getVariant();

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
		if (PluginArboriculture.validFences.contains(block)) {
			return true;
		}

		if (block != Blocks.BARRIER) {
			Material blockMaterial = blockState.getMaterial();
			if (block instanceof BlockFence || block instanceof BlockFenceGate) {
				return blockMaterial == this.blockMaterial;
			}
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
		ModelBakery.registerItemVariants(item, WoodHelper.getDefaultResourceLocations(this));
		manager.registerItemModel(item, new WoodHelper.WoodMeshDefinition(this));
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

	@Override
	public WoodBlockKind getBlockKind() {
		return WoodBlockKind.FENCE;
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return damageDropped(state);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		T woodType = getWoodType(meta);
		return getDefaultState().withProperty(getVariant(), woodType);
	}

	@Override
	public abstract T getWoodType(int meta);

	@Override
	public Collection<T> getWoodTypes() {
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
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		T woodType = getWoodType(meta);
		return getDefaultState().withProperty(getVariant(), woodType);
	}

	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list) {
		for (T woodType : getVariant().getAllowedValues()) {
			list.add(TreeManager.woodAccess.getStack(woodType, getBlockKind(), fireproof));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerStateMapper() {
		Proxies.render.registerStateMapper(this, new WoodTypeStateMapper(this, getVariant()));
	}
}

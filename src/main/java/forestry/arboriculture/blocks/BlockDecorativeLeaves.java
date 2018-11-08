package forestry.arboriculture.blocks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.IShearable;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.ILeafSpriteProvider;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.api.core.Tabs;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.core.blocks.IColoredBlock;
import forestry.core.proxy.Proxies;

public abstract class BlockDecorativeLeaves extends Block implements IItemModelRegister, IColoredBlock, IShearable {
	private static final int VARIANTS_PER_BLOCK = 16;

	public static List<BlockDecorativeLeaves> create() {
		List<BlockDecorativeLeaves> blocks = new ArrayList<>();
		final int blockCount = PropertyTreeType.getBlockCount(VARIANTS_PER_BLOCK);
		for (int blockNumber = 0; blockNumber < blockCount; blockNumber++) {
			final PropertyTreeType variant = PropertyTreeType.create("variant", blockNumber, VARIANTS_PER_BLOCK);
			BlockDecorativeLeaves block = new BlockDecorativeLeaves(blockNumber) {
				@Override
				public PropertyTreeType getVariant() {
					return variant;
				}
			};
			blocks.add(block);
		}
		return blocks;
	}

	private final int blockNumber;

	public BlockDecorativeLeaves(int blockNumber) {
		super(Material.LEAVES);
		this.blockNumber = blockNumber;
		this.setCreativeTab(Tabs.tabArboriculture);
		this.setHardness(0.2F);
		this.setLightOpacity(1);
		this.setSoundType(SoundType.PLANT);
	}

	public int getBlockNumber() {
		return blockNumber;
	}

	public abstract PropertyTreeType getVariant();

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, getVariant());
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		for (IBlockState state : getBlockState().getValidStates()) {
			int meta = getMetaFromState(state);
			ItemStack itemStack = new ItemStack(this, 1, meta);
			list.add(itemStack);
		}
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		TreeDefinition treeDefinition = blockState.getValue(getVariant());
		if (TreeDefinition.Willow.equals(treeDefinition)) {
			return null;
		}
		return super.getCollisionBoundingBox(blockState, worldIn, pos);
	}

	@Override
	public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		entityIn.motionX *= 0.4D;
		entityIn.motionZ *= 0.4D;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		if (!Proxies.render.fancyGraphicsEnabled()) {
			TreeDefinition treeDefinition = state.getValue(getVariant());
			return !TreeDefinition.Willow.equals(treeDefinition);
		}
		return false;
	}

	@Override
	public boolean causesSuffocation(IBlockState state) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return (Proxies.render.fancyGraphicsEnabled() || blockAccess.getBlockState(pos.offset(side)).getBlock() != this) && super.shouldSideBeRendered(blockState, blockAccess, pos, side);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED; // fruit overlays require CUTOUT_MIPPED, even in Fast graphics
	}

	/* MODELS */
	@Override
	@SideOnly(Side.CLIENT)
	public void registerModel(Item item, IModelManager manager) {
		for (IBlockState state : blockState.getValidStates()) {
			int meta = getMetaFromState(state);
			ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation("forestry:leaves.decorative." + blockNumber, "inventory"));
		}
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		TreeDefinition type = getTreeType(meta);
		return getDefaultState().withProperty(getVariant(), type);
	}

	public TreeDefinition getTreeType(int meta) {
		int variantCount = getVariant().getAllowedValues().size();
		int variantMeta = (meta % variantCount) + blockNumber * VARIANTS_PER_BLOCK;
		return TreeDefinition.byMetadata(variantMeta);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return damageDropped(state);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(getVariant()).getMetadata() - blockNumber * VARIANTS_PER_BLOCK;
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {

	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		TreeDefinition type = getTreeType(meta);
		return getDefaultState().withProperty(getVariant(), type);
	}

	/* PROPERTIES */
	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return 60;
	}

	@Override
	public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return true;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
		if (face == EnumFacing.DOWN) {
			return 20;
		} else if (face != EnumFacing.UP) {
			return 10;
		} else {
			return 5;
		}
	}

	@Override
	public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos) {
		return true;
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		IBlockState state = world.getBlockState(pos);
		return Collections.singletonList(new ItemStack(this, 1, damageDropped(state)));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex) {
		TreeDefinition treeDefinition = state.getValue(getVariant());
		ITreeGenome genome = treeDefinition.getGenome();

		if (tintIndex == BlockAbstractLeaves.FRUIT_COLOR_INDEX) {
			IFruitProvider fruitProvider = genome.getFruitProvider();
			return fruitProvider.getDecorativeColor();
		}
		ILeafSpriteProvider spriteProvider = genome.getPrimary().getLeafSpriteProvider();
		return spriteProvider.getColor(false);
	}
}

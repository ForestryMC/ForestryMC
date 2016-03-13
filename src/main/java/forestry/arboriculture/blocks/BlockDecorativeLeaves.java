package forestry.arboriculture.blocks;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.api.core.Tabs;
import forestry.arboriculture.blocks.property.PropertyTreeType;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.core.proxy.Proxies;

public abstract class BlockDecorativeLeaves extends Block implements IShearable, IItemModelRegister {
	private static final int VARIANTS_PER_BLOCK = 16;
	private static final int VARIANTS_META_MASK = VARIANTS_PER_BLOCK - 1;

	public static List<BlockDecorativeLeaves> create() {
		List<BlockDecorativeLeaves> blocks = new ArrayList<>();
		final int blockCount = PropertyTreeType.getBlockCount(VARIANTS_PER_BLOCK);
		for (int blockNumber = 0; blockNumber < blockCount; blockNumber++) {
			final PropertyTreeType variant = PropertyTreeType.create("variant", blockNumber, VARIANTS_PER_BLOCK);
			BlockDecorativeLeaves block = new BlockDecorativeLeaves(blockNumber) {
				@Nonnull
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
		super(Material.leaves);
		this.blockNumber = blockNumber;
		this.setCreativeTab(Tabs.tabArboriculture);
		setStepSound(soundTypeGrass);
	}

	public int getBlockNumber() {
		return blockNumber;
	}

	@Nonnull
	public abstract PropertyTreeType getVariant();

	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, getVariant());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
		for (IBlockState state : getBlockState().getValidStates()) {
			int meta = getMetaFromState(state);
			ItemStack itemStack = new ItemStack(item, 1, meta);
			list.add(itemStack);
		}
	}

	@Override
	public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos) {
		return true;
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		IBlockState state = world.getBlockState(pos);
		int meta = getMetaFromState(state);
		return Collections.singletonList(new ItemStack(this, 1, meta));
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
		TreeDefinition treeDefinition = state.getValue(getVariant());
		if (TreeDefinition.Willow.equals(treeDefinition)) {
			return null;
		}
		return super.getCollisionBoundingBox(world, pos, state);
	}

	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, Entity entity) {
		entity.motionX *= 0.4D;
		entity.motionZ *= 0.4D;
	}

	/* RENDERING */
	@Override
	public boolean isOpaqueCube() {
		return !Proxies.render.fancyGraphicsEnabled();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int colorMultiplier(IBlockAccess world, BlockPos pos, int renderPass) {
		IBlockState state = world.getBlockState(pos);
		TreeDefinition treeDefinition = state.getValue(getVariant());
		if (treeDefinition == null) {
			return super.colorMultiplier(world, pos, renderPass);
		}

		ITreeGenome genome = treeDefinition.getGenome();

		if (renderPass == 0) {
			return genome.getPrimary().getLeafSpriteProvider().getColor(false);
		} else {
			IFruitProvider fruitProvider = genome.getFruitProvider();
			return fruitProvider.getDecorativeColor();
		}
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumWorldBlockLayer getBlockLayer() {
		return Proxies.render.fancyGraphicsEnabled() ? EnumWorldBlockLayer.CUTOUT_MIPPED : EnumWorldBlockLayer.SOLID;
	}

	/* MODELS */
	@Override
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

	@Nonnull
	public TreeDefinition getTreeType(int meta) {
		int variantMeta = (meta & VARIANTS_META_MASK) + blockNumber * VARIANTS_PER_BLOCK;
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
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
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
}

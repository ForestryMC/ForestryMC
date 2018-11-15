package forestry.arboriculture.blocks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.client.model.ModelLoader;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.ILeafSpriteProvider;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.IModelManager;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.core.proxy.Proxies;

/**
 * Genetic leaves with no tile entity, used for worldgen trees.
 * Similar to decorative leaves, but these will drop saplings and can be used for pollination.
 */
public abstract class BlockDefaultLeaves extends BlockAbstractLeaves {
	private static final int VARIANTS_PER_BLOCK = 4;

	public static List<BlockDefaultLeaves> create() {
		List<BlockDefaultLeaves> blocks = new ArrayList<>();
		final int blockCount = PropertyTreeType.getBlockCount(VARIANTS_PER_BLOCK);
		for (int blockNumber = 0; blockNumber < blockCount; blockNumber++) {
			final PropertyTreeType variant = PropertyTreeType.create("variant", blockNumber, VARIANTS_PER_BLOCK);
			BlockDefaultLeaves block = new BlockDefaultLeaves(blockNumber) {
				@Override
				public PropertyTreeType getVariant() {
					return variant;
				}
			};
			blocks.add(block);
		}
		return blocks;
	}

	protected final int blockNumber;

	public BlockDefaultLeaves(int blockNumber) {
		this.blockNumber = blockNumber;
		PropertyTreeType variant = getVariant();
		setDefaultState(this.blockState.getBaseState()
			.withProperty(variant, variant.getFirstType())
			.withProperty(CHECK_DECAY, false)
			.withProperty(DECAYABLE, true));
	}

	public int getBlockNumber() {
		return blockNumber;
	}

	protected abstract PropertyTreeType getVariant();

	@Nullable
	public TreeDefinition getTreeDefinition(IBlockState blockState) {
		if (blockState.getBlock() == this) {
			return blockState.getValue(getVariant());
		} else {
			return null;
		}
	}

	@Override
	public int damageDropped(IBlockState state) {
		TreeDefinition treeDefinition = getTreeDefinition(state);
		if (treeDefinition == null) {
			return 0;
		}
		return treeDefinition.getMetadata() - blockNumber * VARIANTS_PER_BLOCK;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState()
			.withProperty(getVariant(), getTreeType(meta))
			.withProperty(DECAYABLE, (meta & DECAYABLE_FLAG) == 0)
			.withProperty(CHECK_DECAY, (meta & CHECK_DECAY_FLAG) > 0);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int i = damageDropped(state);

		if (!state.getValue(DECAYABLE)) {
			i |= DECAYABLE_FLAG;
		}

		if (state.getValue(CHECK_DECAY)) {
			i |= CHECK_DECAY_FLAG;
		}

		return i;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, getVariant(), CHECK_DECAY, DECAYABLE);
	}

	public TreeDefinition getTreeType(int meta) {
		int variantCount = getVariant().getAllowedValues().size();
		int variantMeta = (meta % variantCount) + blockNumber * VARIANTS_PER_BLOCK;
		return TreeDefinition.byMetadata(variantMeta);
	}

	@Override
	protected void getLeafDrop(NonNullList<ItemStack> drops, World world, @Nullable GameProfile playerProfile, BlockPos pos, float saplingModifier, int fortune) {
		ITree tree = getTree(world, pos);
		if (tree == null) {
			return;
		}

		// Add saplings
		List<ITree> saplings = tree.getSaplings(world, playerProfile, pos, saplingModifier);
		for (ITree sapling : saplings) {
			if (sapling != null) {
				drops.add(TreeManager.treeRoot.getMemberStack(sapling, EnumGermlingType.SAPLING));
			}
		}
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		TreeDefinition type = getTreeType(meta);
		return getDefaultState().withProperty(getVariant(), type);
	}

	@Override
	protected ITree getTree(IBlockAccess world, BlockPos pos) {
		IBlockState blockState = world.getBlockState(pos);
		TreeDefinition treeDefinition = getTreeDefinition(blockState);
		if (treeDefinition != null) {
			return treeDefinition.getIndividual();
		} else {
			return null;
		}
	}

	/* RENDERING */
	@Override
	public final boolean isOpaqueCube(IBlockState state) {
		if (!Proxies.render.fancyGraphicsEnabled()) {
			TreeDefinition treeDefinition = state.getValue(getVariant());
			return !TreeDefinition.Willow.equals(treeDefinition);
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModel(Item item, IModelManager manager) {
		for (IBlockState state : blockState.getValidStates()) {
			int meta = getMetaFromState(state);
			ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation("forestry:leaves.default." + blockNumber, "inventory"));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex) {
		TreeDefinition treeDefinition = getTreeDefinition(state);
		if (treeDefinition == null) {
			treeDefinition = TreeDefinition.Oak;
		}
		ITreeGenome genome = treeDefinition.getGenome();

		ILeafSpriteProvider spriteProvider = genome.getPrimary().getLeafSpriteProvider();
		return spriteProvider.getColor(false);
	}
}

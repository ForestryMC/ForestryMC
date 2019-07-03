package forestry.arboriculture.blocks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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
import net.minecraftforge.items.ItemHandlerHelper;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.ILeafSpriteProvider;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.IModelManager;
import forestry.arboriculture.ModuleArboriculture;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.proxy.Proxies;
import forestry.core.utils.NetworkUtil;

/**
 * Genetic leaves with no tile entity, used for worldgen trees.
 * Similar to decorative leaves, but these will drop saplings and can be used for pollination.
 */
public abstract class BlockDefaultLeavesFruit extends BlockAbstractLeaves {
	private static final int VARIANTS_PER_BLOCK = 4;

	public static List<BlockDefaultLeavesFruit> create() {
		List<BlockDefaultLeavesFruit> blocks = new ArrayList<>();
		final int blockCount = PropertyTreeTypeFruit.getBlockCount(VARIANTS_PER_BLOCK);
		for (int blockNumber = 0; blockNumber < blockCount; blockNumber++) {
			final PropertyTreeTypeFruit variant = PropertyTreeTypeFruit.create("variant", blockNumber, VARIANTS_PER_BLOCK);
			BlockDefaultLeavesFruit block = new BlockDefaultLeavesFruit(blockNumber) {
				@Override
				public PropertyTreeTypeFruit getVariant() {
					return variant;
				}
			};
			blocks.add(block);
		}
		return blocks;
	}

	protected final int blockNumber;

	public BlockDefaultLeavesFruit(int blockNumber) {
		this.blockNumber = blockNumber;
		PropertyTreeTypeFruit variant = getVariant();
		setDefaultState(this.blockState.getBaseState()
			.withProperty(variant, variant.getFirstType())
			.withProperty(CHECK_DECAY, false)
			.withProperty(DECAYABLE, true));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, getVariant(), CHECK_DECAY, DECAYABLE);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack mainHand = player.getHeldItem(EnumHand.MAIN_HAND);
		ItemStack offHand = player.getHeldItem(EnumHand.OFF_HAND);
		if (mainHand.isEmpty() && offHand.isEmpty()) {
			PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.VisualFXType.BLOCK_BREAK, PacketFXSignal.SoundFXType.BLOCK_BREAK, pos, state);
			NetworkUtil.sendNetworkPacket(packet, pos, world);
			ITree tree = getTree(world, pos);
			if (tree == null) {
				return false;
			}
			IFruitProvider fruitProvider = tree.getGenome().getFruitProvider();
			NonNullList<ItemStack> products = tree.produceStacks(world, pos, fruitProvider.getRipeningPeriod());
			world.setBlockState(pos, ModuleArboriculture.getBlocks().getDefaultLeaves(tree.getIdent()), 2);
			for (ItemStack fruit : products) {
				ItemHandlerHelper.giveItemToPlayer(player, fruit);
			}
			return true;
		}

		return false;
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

		// Add fruitsk
		ITreeGenome genome = tree.getGenome();
		IFruitProvider fruitProvider = genome.getFruitProvider();
		if (fruitProvider.isFruitLeaf(genome, world, pos)) {
			NonNullList<ItemStack> produceStacks = tree.produceStacks(world, pos, Integer.MAX_VALUE);
			drops.addAll(produceStacks);
		}
	}

	public int getBlockNumber() {
		return blockNumber;
	}

	protected abstract PropertyTreeTypeFruit getVariant();

	@Nullable
	public PropertyTreeTypeFruit.LeafVariant getLeafVariant(IBlockState blockState) {
		if (blockState.getBlock() == this) {
			return blockState.getValue(getVariant());
		} else {
			return null;
		}
	}

	@Override
	public int damageDropped(IBlockState state) {
		PropertyTreeTypeFruit.LeafVariant treeDefinition = getLeafVariant(state);
		if (treeDefinition == null) {
			return 0;
		}
		return treeDefinition.metadata - blockNumber * VARIANTS_PER_BLOCK;
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

	public PropertyTreeTypeFruit.LeafVariant getTreeType(int meta) {
		int variantCount = getVariant().getAllowedValues().size();
		int variantMeta = (meta % variantCount) + blockNumber * VARIANTS_PER_BLOCK;
		return PropertyTreeTypeFruit.getVariant(variantMeta);
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		PropertyTreeTypeFruit.LeafVariant type = getTreeType(meta);
		return getDefaultState().withProperty(getVariant(), type);
	}

	@Override
	protected ITree getTree(IBlockAccess world, BlockPos pos) {
		IBlockState blockState = world.getBlockState(pos);
		PropertyTreeTypeFruit.LeafVariant treeDefinition = getLeafVariant(blockState);
		if (treeDefinition != null) {
			return treeDefinition.definition.getIndividual();
		} else {
			return null;
		}
	}

	/* RENDERING */
	@Override
	public final boolean isOpaqueCube(IBlockState state) {
		if (!Proxies.render.fancyGraphicsEnabled()) {
			PropertyTreeTypeFruit.LeafVariant treeDefinition = state.getValue(getVariant());
			return !TreeDefinition.Willow.equals(treeDefinition.definition);
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModel(Item item, IModelManager manager) {
		for (IBlockState state : blockState.getValidStates()) {
			int meta = getMetaFromState(state);
			ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation("forestry:leaves.default.fruit." + blockNumber, "inventory"));
		}
	}

	/* RENDERING */

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex) {
		PropertyTreeTypeFruit.LeafVariant variant = getLeafVariant(state);
		TreeDefinition treeDefinition;
		if (variant != null) {
			treeDefinition = variant.definition;
		} else {
			treeDefinition = TreeDefinition.Oak;
		}
		ITreeGenome genome = treeDefinition.getGenome();
		if (tintIndex == BlockAbstractLeaves.FRUIT_COLOR_INDEX) {
			IFruitProvider fruitProvider = genome.getFruitProvider();
			return fruitProvider.getDecorativeColor();
		}

		ILeafSpriteProvider spriteProvider = genome.getPrimary().getLeafSpriteProvider();
		return spriteProvider.getColor(false);
	}
}

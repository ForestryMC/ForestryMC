package forestry.arboriculture.blocks;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemHandlerHelper;

import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.ILeafSpriteProvider;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.genetics.EnumGermlingType;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.arboriculture.features.ArboricultureBlocks;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.core.config.Constants;
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.utils.NetworkUtil;

import genetics.api.individual.IGenome;

/**
 * Genetic leaves with no tile entity, used for worldgen trees.
 * Similar to decorative leaves, but these will drop saplings and can be used for pollination.
 */
public class BlockDefaultLeavesFruit extends BlockAbstractLeaves {
	private final TreeDefinition definition;

	public BlockDefaultLeavesFruit(TreeDefinition definition) {
		super(Block.Properties.of(Material.LEAVES)
				.strength(0.2f)
				.sound(SoundType.GRASS)
				.randomTicks()
				.noOcclusion());
		this.definition = definition;
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult traceResult) {
		ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
		ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);
		if (mainHand.isEmpty() && offHand.isEmpty()) {
			PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.VisualFXType.BLOCK_BREAK, PacketFXSignal.SoundFXType.BLOCK_BREAK, pos, state);
			NetworkUtil.sendNetworkPacket(packet, pos, world);
			ITree tree = getTree(world, pos);
			if (tree == null) {
				return InteractionResult.FAIL;
			}
			IFruitProvider fruitProvider = tree.getGenome().getActiveAllele(TreeChromosomes.FRUITS).getProvider();
			NonNullList<ItemStack> products = tree.produceStacks(world, pos, fruitProvider.getRipeningPeriod());
			world.setBlock(pos, ArboricultureBlocks.LEAVES_DEFAULT.get(definition).defaultState()
					.setValue(LeavesBlock.PERSISTENT, state.getValue(LeavesBlock.PERSISTENT))
					.setValue(LeavesBlock.DISTANCE, state.getValue(LeavesBlock.DISTANCE)), Constants.FLAG_BLOCK_SYNC);
			for (ItemStack fruit : products) {
				ItemHandlerHelper.giveItemToPlayer(player, fruit);
			}
			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	@Override
	protected void getLeafDrop(NonNullList<ItemStack> drops, Level world, @Nullable GameProfile playerProfile, BlockPos pos, float saplingModifier, int fortune, LootContext.Builder builder) {
		ITree tree = getTree(world, pos);
		if (tree == null) {
			return;
		}

		// Add saplings
		List<ITree> saplings = tree.getSaplings(world, playerProfile, pos, saplingModifier);
		for (ITree sapling : saplings) {
			if (sapling != null) {
				drops.add(TreeManager.treeRoot.getTypes().createStack(sapling, EnumGermlingType.SAPLING));
			}
		}

		// Add fruits
		IGenome genome = tree.getGenome();
		IFruitProvider fruitProvider = genome.getActiveAllele(TreeChromosomes.FRUITS).getProvider();
		if (fruitProvider.isFruitLeaf(genome, world, pos)) {
			NonNullList<ItemStack> produceStacks = tree.produceStacks(world, pos, Integer.MAX_VALUE);
			drops.addAll(produceStacks);
		}
	}

	public TreeDefinition getDefinition() {
		return definition;
	}

	@Override
	protected ITree getTree(BlockGetter world, BlockPos pos) {
		return definition.createIndividual();
	}

	/* RENDERING */
	//	@Override
	//	public final boolean isOpaqueCube(BlockState state) {
	//		if (!Proxies.render.fancyGraphicsEnabled()) {
	//			PropertyTreeTypeFruit.LeafVariant treeDefinition = state.getValue(getVariant());
	//			return !TreeDefinition.Willow.equals(treeDefinition.definition);
	//		}
	//		return false;
	//	}
	//
	//	@Override
	//	@OnlyIn(Dist.CLIENT)
	//	public void registerModel(Item item, IModelManager manager) {
	//		for (BlockState state : blockState.getValidStates()) {
	//			int meta = getMetaFromState(state);
	//			ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation("forestry:leaves.default." + blockNumber, "inventory"));
	//		}
	//	}

	/* RENDERING */

	@Override
	@OnlyIn(Dist.CLIENT)
	public int colorMultiplier(BlockState state, @Nullable BlockGetter worldIn, @Nullable BlockPos pos, int tintIndex) {
		IGenome genome = definition.getGenome();
		if (tintIndex == BlockAbstractLeaves.FRUIT_COLOR_INDEX) {
			IFruitProvider fruitProvider = genome.getActiveAllele(TreeChromosomes.FRUITS).getProvider();
			return fruitProvider.getDecorativeColor();
		}

		ILeafSpriteProvider spriteProvider = genome.getActiveAllele(TreeChromosomes.SPECIES).getLeafSpriteProvider();
		return spriteProvider.getColor(false);
	}
}

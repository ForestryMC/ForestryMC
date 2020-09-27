package forestry.arboriculture.blocks;

import com.mojang.authlib.GameProfile;
import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.ILeafSpriteProvider;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.genetics.EnumGermlingType;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.arboriculture.features.ArboricultureBlocks;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.utils.NetworkUtil;
import genetics.api.individual.IGenome;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Genetic leaves with no tile entity, used for worldgen trees.
 * Similar to decorative leaves, but these will drop saplings and can be used for pollination.
 */
public class BlockDefaultLeavesFruit extends BlockAbstractLeaves {
    private final TreeDefinition definition;

    public BlockDefaultLeavesFruit(TreeDefinition definition) {
        super(Block.Properties.create(Material.LEAVES)
                              .hardnessAndResistance(0.2f)
                              .sound(SoundType.PLANT)
                              .tickRandomly()
                              .notSolid());
        this.definition = definition;
    }

    @Override
    public ActionResultType onBlockActivated(
            BlockState state,
            World world,
            BlockPos pos,
            PlayerEntity player,
            Hand hand,
            BlockRayTraceResult traceResult
    ) {
        ItemStack mainHand = player.getHeldItem(Hand.MAIN_HAND);
        ItemStack offHand = player.getHeldItem(Hand.OFF_HAND);
        if (mainHand.isEmpty() && offHand.isEmpty()) {
            PacketFXSignal packet = new PacketFXSignal(
                    PacketFXSignal.VisualFXType.BLOCK_BREAK,
                    PacketFXSignal.SoundFXType.BLOCK_BREAK,
                    pos,
                    state
            );
            NetworkUtil.sendNetworkPacket(packet, pos, world);
            ITree tree = getTree(world, pos);
            if (tree == null) {
                return ActionResultType.FAIL;
            }
            IFruitProvider fruitProvider = tree.getGenome().getActiveAllele(TreeChromosomes.FRUITS).getProvider();
            NonNullList<ItemStack> products = tree.produceStacks(world, pos, fruitProvider.getRipeningPeriod());
            world.setBlockState(pos, ArboricultureBlocks.LEAVES_DEFAULT.get(definition).defaultState(), 2);
            for (ItemStack fruit : products) {
                ItemHandlerHelper.giveItemToPlayer(player, fruit);
            }
            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }

    @Override
    protected void getLeafDrop(
            NonNullList<ItemStack> drops,
            World world,
            @Nullable GameProfile playerProfile,
            BlockPos pos,
            float saplingModifier,
            int fortune
    ) {
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

        // Add fruitsk
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
    protected ITree getTree(IBlockReader world, BlockPos pos) {
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
    public int colorMultiplier(
            BlockState state,
            @Nullable IBlockReader worldIn,
            @Nullable BlockPos pos,
            int tintIndex
    ) {
        IGenome genome = definition.getGenome();
        if (tintIndex == BlockAbstractLeaves.FRUIT_COLOR_INDEX) {
            IFruitProvider fruitProvider = genome.getActiveAllele(TreeChromosomes.FRUITS).getProvider();
            return fruitProvider.getDecorativeColor();
        }

        ILeafSpriteProvider spriteProvider = genome.getActiveAllele(TreeChromosomes.SPECIES).getLeafSpriteProvider();
        return spriteProvider.getColor(false);
    }
}

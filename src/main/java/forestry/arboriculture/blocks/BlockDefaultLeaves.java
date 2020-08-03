package forestry.arboriculture.blocks;

import com.mojang.authlib.GameProfile;
import forestry.api.arboriculture.ILeafSpriteProvider;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.genetics.EnumGermlingType;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.arboriculture.genetics.TreeDefinition;
import genetics.api.individual.IGenome;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Genetic leaves with no tile entity, used for worldgen trees.
 * Similar to decorative leaves, but these will drop saplings and can be used for pollination.
 */
public class BlockDefaultLeaves extends BlockAbstractLeaves {
    private final TreeDefinition definition;

    public BlockDefaultLeaves(TreeDefinition definition) {
        super(Block.Properties.create(Material.LEAVES)
                .hardnessAndResistance(0.2f)
                .sound(SoundType.PLANT)
                .tickRandomly()
                .notSolid());
        this.definition = definition;
    }

    //TODO needed?
    @Nullable
    public TreeDefinition getTreeDefinition(BlockState blockState) {
        if (blockState.getBlock() == this) {
            return this.definition;
        } else {
            return null;
        }
    }

    public TreeDefinition getTreeDefinition() {
        return definition;
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
                drops.add(TreeManager.treeRoot.getTypes().createStack(sapling, EnumGermlingType.SAPLING));
            }
        }
    }

    @Override
    protected ITree getTree(IBlockReader world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        TreeDefinition treeDefinition = getTreeDefinition(blockState);
        if (treeDefinition != null) {
            return treeDefinition.createIndividual();
        } else {
            return null;
        }
    }

    /* RENDERING */
    //TODO hitbox/rendering
    //	@Override
    //	public final boolean isOpaqueCube(BlockState state) {
    //		if (!Proxies.render.fancyGraphicsEnabled()) {
    //			return !TreeDefinition.Willow.equals(definition);
    //		}
    //		return false;
    //	}

    @Override
    @OnlyIn(Dist.CLIENT)
    public int colorMultiplier(BlockState state, @Nullable IBlockReader worldIn, @Nullable BlockPos pos, int tintIndex) {
        TreeDefinition treeDefinition = getTreeDefinition(state);
        if (treeDefinition == null) {
            treeDefinition = TreeDefinition.Oak;
        }
        IGenome genome = treeDefinition.getGenome();

        ILeafSpriteProvider spriteProvider = genome.getActiveAllele(TreeChromosomes.SPECIES).getLeafSpriteProvider();
        return spriteProvider.getColor(false);
    }
}

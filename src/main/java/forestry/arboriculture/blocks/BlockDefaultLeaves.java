package forestry.arboriculture.blocks;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.arboriculture.ILeafSpriteProvider;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.genetics.EnumGermlingType;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.arboriculture.genetics.TreeDefinition;

import genetics.api.individual.IGenome;

/**
 * Genetic leaves with no tile entity, used for worldgen trees.
 * Similar to decorative leaves, but these will drop saplings and can be used for pollination.
 */
public class BlockDefaultLeaves extends BlockAbstractLeaves {
	private final TreeDefinition definition;

	public BlockDefaultLeaves(TreeDefinition definition) {
		super(Block.Properties.of(Material.LEAVES)
				.strength(0.2f)
				.sound(SoundType.GRASS)
				.randomTicks()
				.noOcclusion());
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
	}

	@Override
	protected ITree getTree(BlockGetter world, BlockPos pos) {
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
	public int colorMultiplier(BlockState state, @Nullable BlockGetter worldIn, @Nullable BlockPos pos, int tintIndex) {
		TreeDefinition treeDefinition = getTreeDefinition(state);
		if (treeDefinition == null) {
			treeDefinition = TreeDefinition.Oak;
		}
		IGenome genome = treeDefinition.getGenome();

		ILeafSpriteProvider spriteProvider = genome.getActiveAllele(TreeChromosomes.SPECIES).getLeafSpriteProvider();
		return spriteProvider.getColor(false);
	}
}

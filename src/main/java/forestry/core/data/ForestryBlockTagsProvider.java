package forestry.core.data;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.Tags;

import forestry.arboriculture.ModuleArboriculture;
import forestry.arboriculture.ModuleCharcoal;
import forestry.arboriculture.blocks.BlockRegistryArboriculture;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

//TODO: Split up ?
public final class ForestryBlockTagsProvider extends BlockTagsProvider {
	@Nullable
	private Set<ResourceLocation> filter = null;

	public ForestryBlockTagsProvider(DataGenerator generator) {
		super(generator);
	}

	@Override
	protected void registerTags() {
		//super.registerTags();
		filter = this.tagToBuilder.keySet().stream().map(Tag::getId).collect(Collectors.toSet());
		if (ModuleHelper.isEnabled(ForestryModuleUids.CHARCOAL)) {
			getBuilder(ForestryTags.Blocks.CHARCOAL).add(ModuleCharcoal.getBlocks().charcoal);
		}
		if (ModuleHelper.isEnabled(ForestryModuleUids.ARBORICULTURE)) {
			BlockRegistryArboriculture registry = ModuleArboriculture.getBlocks();
			getBuilder(BlockTags.PLANKS).add(registry.planks.values().toArray(new Block[0]));
			getBuilder(BlockTags.LOGS).add(registry.logs.values().toArray(new Block[0]));
			getBuilder(BlockTags.STAIRS).add(registry.stairs.values().toArray(new Block[0]));
			getBuilder(BlockTags.WOODEN_STAIRS).add(registry.stairs.values().toArray(new Block[0]));
			getBuilder(BlockTags.FENCES).add(registry.fences.values().toArray(new Block[0]));
			getBuilder(BlockTags.WOODEN_FENCES).add(registry.fences.values().toArray(new Block[0]));
			getBuilder(Tags.Blocks.FENCE_GATES).add(registry.fenceGates.values().toArray(new Block[0]));
			getBuilder(Tags.Blocks.FENCE_GATES_WOODEN).add(registry.fenceGates.values().toArray(new Block[0]));
			getBuilder(BlockTags.SLABS).add(registry.slabs.values().toArray(new Block[0]));
			getBuilder(BlockTags.WOODEN_SLABS).add(registry.slabs.values().toArray(new Block[0]));
			getBuilder(BlockTags.DOORS).add(registry.doors.values().toArray(new Block[0]));
			getBuilder(BlockTags.WOODEN_DOORS).add(registry.doors.values().toArray(new Block[0]));

			getBuilder(BlockTags.PLANKS).add(registry.planksFireproof.values().toArray(new Block[0]));
			getBuilder(BlockTags.LOGS).add(registry.logsFireproof.values().toArray(new Block[0]));
			getBuilder(BlockTags.STAIRS).add(registry.stairsFireproof.values().toArray(new Block[0]));
			getBuilder(BlockTags.WOODEN_STAIRS).add(registry.stairsFireproof.values().toArray(new Block[0]));
			getBuilder(BlockTags.FENCES).add(registry.fencesFireproof.values().toArray(new Block[0]));
			getBuilder(BlockTags.WOODEN_FENCES).add(registry.fencesFireproof.values().toArray(new Block[0]));
			getBuilder(Tags.Blocks.FENCE_GATES).add(registry.fenceGatesFireproof.values().toArray(new Block[0]));
			getBuilder(Tags.Blocks.FENCE_GATES_WOODEN).add(registry.fenceGatesFireproof.values().toArray(new Block[0]));
			getBuilder(BlockTags.SLABS).add(registry.slabsFireproof.values().toArray(new Block[0]));
			getBuilder(BlockTags.WOODEN_SLABS).add(registry.slabsFireproof.values().toArray(new Block[0]));

			getBuilder(BlockTags.PLANKS).add(registry.planksVanillaFireproof.values().toArray(new Block[0]));
			getBuilder(BlockTags.LOGS).add(registry.logsVanillaFireproof.values().toArray(new Block[0]));
			getBuilder(BlockTags.STAIRS).add(registry.stairsVanillaFireproof.values().toArray(new Block[0]));
			getBuilder(BlockTags.WOODEN_STAIRS).add(registry.stairsVanillaFireproof.values().toArray(new Block[0]));
			getBuilder(BlockTags.FENCES).add(registry.fencesVanillaFireproof.values().toArray(new Block[0]));
			getBuilder(BlockTags.WOODEN_FENCES).add(registry.fencesVanillaFireproof.values().toArray(new Block[0]));
			getBuilder(Tags.Blocks.FENCE_GATES).add(registry.fenceGatesVanillaFireproof.values().toArray(new Block[0]));
			getBuilder(Tags.Blocks.FENCE_GATES_WOODEN).add(registry.fenceGatesVanillaFireproof.values().toArray(new Block[0]));
			getBuilder(BlockTags.SLABS).add(registry.slabsVanillaFireproof.values().toArray(new Block[0]));
			getBuilder(BlockTags.WOODEN_SLABS).add(registry.slabsVanillaFireproof.values().toArray(new Block[0]));

			getBuilder(BlockTags.SAPLINGS).add(registry.saplingGE);
			getBuilder(BlockTags.LEAVES).add(registry.leaves).add(registry.leavesDefaultFruit.values().toArray(new Block[0])).add(registry.leavesDefault.values().toArray(new Block[0])).add(registry.leavesDecorative.values().toArray(new Block[0]));


			//getBuilder(Tags.Blocks.CHESTS).add(registry.treeChest);
			//getBuilder(Tags.Blocks.CHESTS_WOODEN).add(registry.treeChest);
		}
	}

	/*@Override
	@Nullable
	protected Path makePath(ResourceLocation id) {
		return filter != null && filter.contains(id) ? null : super.makePath(id); //We don't want to save vanilla tags.
	}*/

	@Override
	public String getName() {
		return "Forestry Block Tags";
	}
}

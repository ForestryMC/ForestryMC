package forestry.arboriculture.models;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import com.mojang.datafixers.util.Pair;

import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import forestry.api.arboriculture.genetics.IAlleleTreeSpecies;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.arboriculture.tiles.TileSapling;

import genetics.api.GeneticHelper;
import genetics.api.organism.IOrganism;
import genetics.utils.AlleleUtils;

public class ModelSapling implements IModelGeometry<ModelSapling> {

	private final Map<IAlleleTreeSpecies, Pair<ResourceLocation, ResourceLocation>> modelsBySpecies;

	public ModelSapling() {
		this.modelsBySpecies = AlleleUtils.filteredStream(TreeChromosomes.SPECIES)
				.collect(Collectors.toMap(allele -> allele, allele -> Pair.of(allele.getBlockModel(), allele.getItemModel())));
	}

	@Override
	public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
		ImmutableMap.Builder<IAlleleTreeSpecies, BakedModel> itemModels = new ImmutableMap.Builder<>();
		ImmutableMap.Builder<IAlleleTreeSpecies, BakedModel> blockModels = new ImmutableMap.Builder<>();
		for (Map.Entry<IAlleleTreeSpecies, Pair<ResourceLocation, ResourceLocation>> entry : modelsBySpecies.entrySet()) {
			BakedModel blockModel = bakery.bake(entry.getValue().getFirst(), BlockModelRotation.X0_Y0, spriteGetter);
			if (blockModel != null) {
				blockModels.put(entry.getKey(), blockModel);
			}
			BakedModel itemModel = bakery.bake(entry.getValue().getSecond(), BlockModelRotation.X0_Y0, spriteGetter);
			if (itemModel != null) {
				itemModels.put(entry.getKey(), itemModel);
			}
		}
		return new Baked(itemModels.build(), blockModels.build());
	}

	public Collection<ResourceLocation> getDependencies() {
		return modelsBySpecies.values().stream()
				.flatMap(pair -> Stream.of(pair.getFirst(), pair.getSecond())).collect(Collectors.toSet());
	}

	@Override
	public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
		return getDependencies().stream()
				.flatMap(location -> modelGetter.apply(location).getMaterials(modelGetter, missingTextureErrors).stream())
				.collect(Collectors.toSet());
	}

	public static class Baked implements BakedModel {
		private final Map<IAlleleTreeSpecies, BakedModel> itemModels;
		private final Map<IAlleleTreeSpecies, BakedModel> blockModels;
		private final BakedModel defaultBlock;
		private final BakedModel defaultItem;
		@Nullable
		private ItemOverrides overrideList;

		public Baked(Map<IAlleleTreeSpecies, BakedModel> itemModels, Map<IAlleleTreeSpecies, BakedModel> blockModels) {
			this.itemModels = itemModels;
			this.blockModels = blockModels;
			this.defaultBlock = blockModels.get(TreeDefinition.Oak.getSpecies());
			this.defaultItem = itemModels.get(TreeDefinition.Oak.getSpecies());
		}

		@Override
		public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData) {
			IAlleleTreeSpecies species = extraData.get(TileSapling.TREE_SPECIES);
			if (species == null) {
				species = TreeDefinition.Oak.getSpecies();
			}
			return blockModels.get(species).getQuads(state, side, rand);
		}

		@Override
		public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
			return getQuads(state, side, rand, ModelData.EMPTY);
		}

		@Override
		public boolean useAmbientOcclusion() {
			return defaultBlock.useAmbientOcclusion();
		}

		@Override
		public boolean isGui3d() {
			return defaultItem.isGui3d();
		}

		@Override
		public boolean usesBlockLight() {
			return false;
		}

		@Override
		public boolean isCustomRenderer() {
			return false;
		}

		@Override
		public TextureAtlasSprite getParticleIcon() {
			return defaultBlock.getParticleIcon();
		}

		@Override
		public ItemOverrides getOverrides() {
			if (overrideList == null) {
				overrideList = new OverrideList();
			}
			return overrideList;
		}

		public class OverrideList extends ItemOverrides {

			@Nullable
			@Override
			public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int p_173469_) {
				IOrganism<ITree> organism = GeneticHelper.getOrganism(stack);
				if (organism.isEmpty()) {
					return model;
				}
				IAlleleTreeSpecies species = organism.getAllele(TreeChromosomes.SPECIES, true);
				return itemModels.getOrDefault(species, model);
			}
		}
	}
}

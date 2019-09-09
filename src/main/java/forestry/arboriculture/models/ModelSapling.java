package forestry.arboriculture.models;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import com.mojang.datafixers.util.Pair;

import net.minecraftforge.client.model.data.IModelData;

import genetics.api.GeneticHelper;
import genetics.api.GeneticsAPI;
import genetics.api.organism.IOrganism;

import forestry.api.arboriculture.genetics.IAlleleTreeSpecies;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.arboriculture.blocks.BlockSapling;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.arboriculture.tiles.TileSapling;

public class ModelSapling implements IUnbakedModel {

	private final Map<IAlleleTreeSpecies, Pair<ResourceLocation, ResourceLocation>> modelsBySpecies;

	public ModelSapling() {
		this.modelsBySpecies = GeneticsAPI.apiInstance.getAlleleRegistry().getRegisteredAlleles(TreeChromosomes.SPECIES).stream()
			.filter(allele -> allele instanceof IAlleleTreeSpecies)
			.map(allele -> (IAlleleTreeSpecies) allele)
			.collect(Collectors.toMap(allele -> allele, allele -> Pair.of(allele.getBlockModel(), allele.getItemModel())));
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return modelsBySpecies.values().stream()
			.flatMap(pair -> Stream.of(pair.getFirst(), pair.getSecond())).collect(Collectors.toSet());
	}

	@Override
	public Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors) {
		return getDependencies().stream()
			.flatMap(location -> modelGetter.apply(location).getTextures(modelGetter, missingTextureErrors).stream())
			.collect(Collectors.toSet());
	}

	@Override
	public IBakedModel bake(ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, ISprite sprite, VertexFormat format) {
		ImmutableMap.Builder<IAlleleTreeSpecies, IBakedModel> itemModels = new ImmutableMap.Builder<>();
		ImmutableMap.Builder<IAlleleTreeSpecies, IBakedModel> blockModels = new ImmutableMap.Builder<>();
		for (Map.Entry<IAlleleTreeSpecies, Pair<ResourceLocation, ResourceLocation>> entry : modelsBySpecies.entrySet()) {
			IBakedModel blockModel = bakery.getBakedModel(entry.getValue().getFirst(), ModelRotation.X0_Y0, spriteGetter, format);
			if (blockModel != null) {
				blockModels.put(entry.getKey(), blockModel);
			}
			IBakedModel itemModel = bakery.getBakedModel(entry.getValue().getSecond(), ModelRotation.X0_Y0, spriteGetter, format);
			if (itemModel != null) {
				itemModels.put(entry.getKey(), itemModel);
			}
		}
		return new Baked(itemModels.build(), blockModels.build());
	}

	public static class Baked implements IBakedModel {
		private final Map<IAlleleTreeSpecies, IBakedModel> itemModels;
		private final Map<IAlleleTreeSpecies, IBakedModel> blockModels;
		private final IBakedModel defaultBlock;
		private final IBakedModel defaultItem;
		@Nullable
		private ItemOverrideList overrideList;

		public Baked(Map<IAlleleTreeSpecies, IBakedModel> itemModels, Map<IAlleleTreeSpecies, IBakedModel> blockModels) {
			this.itemModels = itemModels;
			this.blockModels = blockModels;
			this.defaultBlock = blockModels.get(TreeDefinition.Oak.getSpecies());
			this.defaultItem = itemModels.get(TreeDefinition.Oak.getSpecies());
		}

		@Override
		public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand, IModelData extraData) {
			IAlleleTreeSpecies species = extraData.getData(TileSapling.TREE_SPECIES);
			if (species == null) {
				return getQuads(state, side, rand);
			}
			return blockModels.get(species).getQuads(state, side, rand);
		}

		@Override
		public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
			if (state == null) {
				return Collections.emptyList();
			}
			return blockModels.get(state.get(BlockSapling.TREE)).getQuads(state, side, rand);
		}

		@Override
		public boolean isAmbientOcclusion() {
			return defaultBlock.isAmbientOcclusion();
		}

		@Override
		public boolean isGui3d() {
			return defaultItem.isGui3d();
		}

		@Override
		public boolean isBuiltInRenderer() {
			return false;
		}

		@Override
		public TextureAtlasSprite getParticleTexture() {
			return defaultBlock.getParticleTexture();
		}

		@Override
		public ItemOverrideList getOverrides() {
			if (overrideList == null) {
				overrideList = new OverrideList();
			}
			return overrideList;
		}

		public class OverrideList extends ItemOverrideList {
			@Nullable
			@Override
			public IBakedModel getModelWithOverrides(IBakedModel model, ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
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

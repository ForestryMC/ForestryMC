package forestry.lepidopterology.render;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import com.mojang.datafixers.util.Pair;

import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import genetics.api.GeneticHelper;
import genetics.api.organism.IOrganism;

import genetics.utils.AlleleUtils;

import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.api.lepidopterology.genetics.IAlleleButterflyCocoon;
import forestry.api.lepidopterology.genetics.IButterfly;
import forestry.core.models.AbstractItemModel;
import forestry.lepidopterology.items.ItemButterflyGE;

public class CocoonItemModel extends AbstractItemModel {
	private final ImmutableMap<String, ImmutableList<BakedModel>> bakedModel;

	public CocoonItemModel(ImmutableMap<String, ImmutableList<BakedModel>> bakedModel) {
		this.bakedModel = bakedModel;
	}

	@Override
	protected BakedModel getOverride(BakedModel model, ItemStack stack) {
		int age = Mth.clamp(stack.getOrCreateTag().getInt(ItemButterflyGE.NBT_AGE), 0, ItemButterflyGE.MAX_AGE);
		IOrganism<IButterfly> organism = GeneticHelper.getOrganism(stack);
		IAlleleButterflyCocoon alleleCocoon = organism.getAllele(ButterflyChromosomes.COCOON, true);
		return bakedModel.getOrDefault(alleleCocoon.getCocoonName(), ImmutableList.of()).get(age);
	}

	private static class Geometry implements IModelGeometry<CocoonItemModel.Geometry> {

		@Override
		public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
			ImmutableMap.Builder<String, ImmutableList<BakedModel>> bakedModels = new ImmutableMap.Builder<>();
			AlleleUtils.forEach(ButterflyChromosomes.COCOON, (allele) -> {
				ImmutableList.Builder<BakedModel> models = new ImmutableList.Builder<>();
				for (int age = 0; age < ItemButterflyGE.MAX_AGE; age++) {
					models.add(bakery.bake(allele.getCocoonItemModel(age), modelTransform, spriteGetter));
				}
				bakedModels.put(allele.getCocoonName(), models.build());
			});
			return new CocoonItemModel(bakedModels.build());
		}

		@Override
		public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
			return ImmutableList.of();
		}
	}

	public static class Loader implements IModelLoader<Geometry> {
		@Override
		public void onResourceManagerReload(ResourceManager resourceManager) {
		}

		@Override
		public CocoonItemModel.Geometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
			return new CocoonItemModel.Geometry();
		}
	}
}

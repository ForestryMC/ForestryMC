package forestry.lepidopterology.render;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.api.lepidopterology.genetics.IAlleleButterflyCocoon;
import forestry.api.lepidopterology.genetics.IButterfly;
import forestry.core.models.AbstractItemModel;
import forestry.lepidopterology.items.ItemButterflyGE;
import genetics.api.GeneticHelper;
import genetics.api.organism.IOrganism;
import genetics.utils.AlleleUtils;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

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

	private static class Geometry implements IUnbakedGeometry<Geometry> {

		@Override
		public BakedModel bake(IGeometryBakingContext context, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
			ImmutableMap.Builder<String, ImmutableList<BakedModel>> bakedModels = new ImmutableMap.Builder<>();
			AlleleUtils.forEach(ButterflyChromosomes.COCOON, (allele) -> {
				ImmutableList.Builder<BakedModel> models = new ImmutableList.Builder<>();
				for (int age = 0; age < ItemButterflyGE.MAX_AGE; age++) {
					models.add(bakery.bake(allele.getCocoonItemModel(age), modelState, spriteGetter));
				}
				bakedModels.put(allele.getCocoonName(), models.build());
			});
			return new CocoonItemModel(bakedModels.build());
		}

		@Override
		public Collection<Material> getMaterials(IGeometryBakingContext context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
			return ImmutableList.of();
		}
	}

	public static class Loader implements IGeometryLoader<CocoonItemModel.Geometry> {

		@Override
		public CocoonItemModel.Geometry read(JsonObject modelContents, JsonDeserializationContext context) throws JsonParseException {
			return new CocoonItemModel.Geometry();
		}
	}
}

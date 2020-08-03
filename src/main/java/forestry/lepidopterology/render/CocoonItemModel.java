package forestry.lepidopterology.render;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.api.lepidopterology.genetics.IAlleleButterflyCocoon;
import forestry.api.lepidopterology.genetics.IButterfly;
import forestry.core.models.AbstractItemModel;
import forestry.lepidopterology.items.ItemButterflyGE;
import genetics.api.GeneticHelper;
import genetics.api.organism.IOrganism;
import genetics.utils.AlleleUtils;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

public class CocoonItemModel extends AbstractItemModel {
    private final ImmutableMap<String, ImmutableList<IBakedModel>> bakedModel;

    public CocoonItemModel(ImmutableMap<String, ImmutableList<IBakedModel>> bakedModel) {
        this.bakedModel = bakedModel;
    }

    @Override
    protected IBakedModel getOverride(IBakedModel model, ItemStack stack) {
        int age = MathHelper.clamp(stack.getOrCreateTag().getInt(ItemButterflyGE.NBT_AGE), 0, ItemButterflyGE.MAX_AGE);
        IOrganism<IButterfly> organism = GeneticHelper.getOrganism(stack);
        IAlleleButterflyCocoon alleleCocoon = organism.getAllele(ButterflyChromosomes.COCOON, true);
        return bakedModel.getOrDefault(alleleCocoon.getCocoonName(), ImmutableList.of()).get(age);
    }

    private static class Geometry implements IModelGeometry<CocoonItemModel.Geometry> {

        @Override
        public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
            ImmutableMap.Builder<String, ImmutableList<IBakedModel>> bakedModels = new ImmutableMap.Builder<>();
            AlleleUtils.forEach(ButterflyChromosomes.COCOON, (allele) -> {
                ImmutableList.Builder<IBakedModel> models = new ImmutableList.Builder<>();
                for (int age = 0; age < ItemButterflyGE.MAX_AGE; age++) {
                    models.add(bakery.getBakedModel(allele.getCocoonItemModel(age), modelTransform, spriteGetter));
                }
                bakedModels.put(allele.getCocoonName(), models.build());
            });
            return new CocoonItemModel(bakedModels.build());
        }

        @Override
        public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
            return ImmutableList.of();
        }
    }

    public static class Loader implements IModelLoader<Geometry> {
        @Override
        public void onResourceManagerReload(IResourceManager resourceManager) {
        }

        @Override
        public CocoonItemModel.Geometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
            return new CocoonItemModel.Geometry();
        }
    }
}
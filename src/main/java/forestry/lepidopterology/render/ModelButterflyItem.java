/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.lepidopterology.render;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector3f;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.SimpleModelTransform;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import genetics.api.GeneticHelper;
import genetics.api.alleles.IAlleleValue;
import genetics.api.organism.IOrganism;

import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.api.lepidopterology.genetics.IAlleleButterflySpecies;
import forestry.api.lepidopterology.genetics.IButterfly;
import forestry.core.config.Constants;
import forestry.core.models.AbstractBakedModel;
import forestry.core.models.TRSRBakedModel;
import forestry.core.utils.ResourceUtil;

@OnlyIn(Dist.CLIENT)
public class ModelButterflyItem extends AbstractBakedModel {
    @Nullable
    private static IUnbakedModel modelButterfly;

    private final ImmutableMap<String, IBakedModel> subModels;
    private final Cache<Pair<String, Float>, IBakedModel> cache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build();

    public ModelButterflyItem(ImmutableMap<String, IBakedModel> subModels) {
        this.subModels = subModels;
    }

    @Override
    protected ItemOverrideList createOverrides() {
        return new ButterflyItemOverrideList();
    }


    private class ButterflyItemOverrideList extends ItemOverrideList {
        public ButterflyItemOverrideList() {
            super();
        }

        @Override
        public IBakedModel func_239290_a_(IBakedModel model, ItemStack stack, @Nullable ClientWorld worldIn, @Nullable LivingEntity entityIn) {
            IOrganism<IButterfly> organism = GeneticHelper.getOrganism(stack);
            IAlleleButterflySpecies species = organism.getAllele(ButterflyChromosomes.SPECIES, true);
            IAlleleValue<Float> size = organism.getAllele(ButterflyChromosomes.SIZE, true);
            Preconditions.checkNotNull(species);
            Preconditions.checkNotNull(size);
            IBakedModel bakedModel = cache.getIfPresent(Pair.of(species.getRegistryName().getPath(), size));
            if (bakedModel != null) {
                return bakedModel;
            }
            float scale = 1F / 16F;
            float sizeValue = size.getValue();
            String identifier = species.getRegistryName().getPath();
            IModelTransform transform = new SimpleModelTransform(getTransformations(1));
            bakedModel = new PerspectiveMapWrapper(new TRSRBakedModel(subModels.get(identifier), -0.03125F, 0.25F - sizeValue * 0.37F, -0.03125F + sizeValue * scale, sizeValue * 1.4F), transform);
            cache.put(Pair.of(identifier, sizeValue), bakedModel);
            return bakedModel;
        }

        private ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> getTransformations(float size) {
            float scale = 1F / 16F;
            ImmutableMap.Builder<ItemCameraTransforms.TransformType, TransformationMatrix> builder = ImmutableMap.builder();
            builder.put(ItemCameraTransforms.TransformType.FIXED,
                    new TransformationMatrix(new Vector3f(scale * 0.5F, scale - (size / 0.75F) * scale, scale * 1.25F), null, null, null));
            builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND,
                    new TransformationMatrix(new Vector3f(0, scale - (size / 1F) * scale, 0), null, null, null));
            builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND,
                    new TransformationMatrix(new Vector3f(0, scale - (size / 1F) * scale, 0), null, null, null));
            return builder.build();
        }
    }

    public static class Geometry implements IModelGeometry<Geometry> {

        public final ImmutableMap<String, String> subModels;

        public Geometry(ImmutableMap<String, String> subModels) {
            this.subModels = subModels;
        }

        @Override
        public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
            IUnbakedModel modelButterfly = bakery.getUnbakedModel(new ResourceLocation(Constants.MOD_ID, "item/butterfly"));
            if (!(modelButterfly instanceof BlockModel)) {
                return null;
            }
            ImmutableMap.Builder<String, IBakedModel> subModelBuilder = new ImmutableMap.Builder<>();
            BlockModel modelBlock = (BlockModel) modelButterfly;
            for (Map.Entry<String, String> subModel : this.subModels.entrySet()) {
                String identifier = subModel.getKey();
                String texture = subModel.getValue();

                BlockModel model = new BlockModel(modelBlock.getParentLocation(), modelBlock.getElements(), ImmutableMap.of("butterfly", Either.left(new RenderMaterial(PlayerContainer.LOCATION_BLOCKS_TEXTURE, new ResourceLocation(texture)))), modelBlock.ambientOcclusion, modelBlock.func_230176_c_(), modelBlock.getAllTransforms(), modelBlock.getOverrides());
                ResourceLocation location = new ResourceLocation(Constants.MOD_ID, "item/butterfly");
                IModelTransform transform = ResourceUtil.loadTransform(new ResourceLocation(Constants.MOD_ID, "item/butterfly"));
                subModelBuilder.put(identifier, model.bakeModel(bakery, model, spriteGetter, transform, location, true));
            }
            return new ModelButterflyItem(subModelBuilder.build());
        }

        @Override
        public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
            return subModels.values().stream().map((location) -> new RenderMaterial(PlayerContainer.LOCATION_BLOCKS_TEXTURE, new ResourceLocation(location))).collect(Collectors.toSet());
        }
    }
}

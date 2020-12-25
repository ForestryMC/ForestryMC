/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.lepidopterology.render;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.api.lepidopterology.genetics.IAlleleButterflySpecies;
import forestry.api.lepidopterology.genetics.IButterfly;
import forestry.core.config.Constants;
import forestry.core.models.AbstractBakedModel;
import forestry.core.models.TRSRBakedModel;
import forestry.core.utils.ResourceUtil;
import genetics.api.GeneticHelper;
import genetics.api.alleles.IAlleleValue;
import genetics.api.organism.IOrganism;
import genetics.utils.AlleleUtils;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.SimpleModelTransform;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class ButterflyItemModel extends AbstractBakedModel {
    private final ImmutableMap<String, IBakedModel> subModels;
    private final Cache<Pair<String, Float>, IBakedModel> cache = CacheBuilder.newBuilder()
                                                                              .expireAfterAccess(1, TimeUnit.MINUTES)
                                                                              .build();

    public ButterflyItemModel(ImmutableMap<String, IBakedModel> subModels) {
        this.subModels = subModels;
    }

    @Override
    protected ItemOverrideList createOverrides() {
        return new OverrideList();
    }


    private class OverrideList extends ItemOverrideList {
        public OverrideList() {
            super();
        }

        @Override
        public IBakedModel getOverrideModel(
                IBakedModel model,
                ItemStack stack,
                @Nullable ClientWorld worldIn,
                @Nullable LivingEntity entityIn
        ) {
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
            IModelTransform transform = new SimpleModelTransform(getTransformations(sizeValue));//-0.03125F, 0.25F - sizeValue * 0.37F, -0.03125F + sizeValue * scale, sizeValue * 1.4F
            bakedModel = new PerspectiveMapWrapper(
                    new TRSRBakedModel(subModels.get(identifier), 0, 0, 0, 1),
                    transform
            );
            cache.put(Pair.of(identifier, sizeValue), bakedModel);
            return bakedModel;
        }

        private ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> getTransformations(float size) {
            float scale = 1F / 16F;
            float sSize = size * 1.15F;
            Vector3f scaledSize = new Vector3f(sSize, sSize, sSize);
            ImmutableMap.Builder<ItemCameraTransforms.TransformType, TransformationMatrix> builder = ImmutableMap.builder();
            builder.put(
                    ItemCameraTransforms.TransformType.FIXED,
                    new TransformationMatrix(
                            new Vector3f(scale * 0.5F, scale - (size / 0.75F) * scale, scale * 1.25F),
                            null,
                            scaledSize,
                            null
                    )
            );
            builder.put(
                    ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND,
                    new TransformationMatrix(new Vector3f(0, -scale * 4.75F, 0), null, scaledSize, null)
            );
            builder.put(
                    ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND,
                    new TransformationMatrix(new Vector3f(0, -scale * 4.75F, 0), null, scaledSize, null)
            );
            builder.put(
                    ItemCameraTransforms.TransformType.GUI,
                    new TransformationMatrix(
                            new Vector3f(0, -scale, 0),
                            new Quaternion(new Vector3f(1, 0, 0), 90F, true),
                            scaledSize,
                            null
                    )
            );
            builder.put(
                    ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND,
                    new TransformationMatrix(new Vector3f(0, 0, 0), null, scaledSize, null)
            );
            return builder.build();
        }
    }

    private static class Geometry implements IModelGeometry<Geometry> {

        public final ImmutableMap<String, String> subModels;

        public Geometry(ImmutableMap<String, String> subModels) {
            this.subModels = subModels;
        }

        @Override
        public IBakedModel bake(
                IModelConfiguration owner,
                ModelBakery bakery,
                Function<RenderMaterial, TextureAtlasSprite> spriteGetter,
                IModelTransform modelTransform,
                ItemOverrideList overrides,
                ResourceLocation modelLocation
        ) {
            IUnbakedModel modelButterfly = bakery.getUnbakedModel(new ResourceLocation(
                    Constants.MOD_ID,
                    "item/butterfly"
            ));
            if (!(modelButterfly instanceof BlockModel)) {
                return null;
            }
            ImmutableMap.Builder<String, IBakedModel> subModelBuilder = new ImmutableMap.Builder<>();
            BlockModel modelBlock = (BlockModel) modelButterfly;
            for (Map.Entry<String, String> subModel : this.subModels.entrySet()) {
                String identifier = subModel.getKey();
                String texture = subModel.getValue();

                BlockModel model = new BlockModel(
                        modelBlock.getParentLocation(),
                        modelBlock.getElements(),
                        ImmutableMap.of(
                                "butterfly",
                                Either.left(new RenderMaterial(
                                        PlayerContainer.LOCATION_BLOCKS_TEXTURE,
                                        new ResourceLocation(texture)
                                ))
                        ),
                        modelBlock.ambientOcclusion,
                        modelBlock.getGuiLight(),
                        modelBlock.getAllTransforms(),
                        modelBlock.getOverrides()
                );
                ResourceLocation location = new ResourceLocation(Constants.MOD_ID, "item/butterfly");
                IModelTransform transform = ResourceUtil.loadTransform(new ResourceLocation(
                        Constants.MOD_ID,
                        "item/butterfly"
                ));
                subModelBuilder.put(
                        identifier,
                        model.bakeModel(bakery, model, spriteGetter, transform, location, true)
                );
            }
            return new ButterflyItemModel(subModelBuilder.build());
        }

        @Override
        public Collection<RenderMaterial> getTextures(
                IModelConfiguration owner,
                Function<ResourceLocation, IUnbakedModel> modelGetter,
                Set<Pair<String, String>> missingTextureErrors
        ) {
            return subModels.values()
                            .stream()
                            .map((location) -> new RenderMaterial(
                                    PlayerContainer.LOCATION_BLOCKS_TEXTURE,
                                    new ResourceLocation(location)
                            ))
                            .collect(Collectors.toSet());
        }
    }

    public static class Loader implements IModelLoader<Geometry> {

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager) {
        }

        @Override
        public ButterflyItemModel.Geometry read(
                JsonDeserializationContext deserializationContext,
                JsonObject modelContents
        ) {
            ImmutableMap.Builder<String, String> subModels = new ImmutableMap.Builder<>();
            AlleleUtils.forEach(ButterflyChromosomes.SPECIES, (butterfly) -> {
                ResourceLocation registryName = butterfly.getRegistryName();
                subModels.put(registryName.getPath(), butterfly.getItemTexture().toString());
            });
            return new ButterflyItemModel.Geometry(subModels.build());
        }
    }
}

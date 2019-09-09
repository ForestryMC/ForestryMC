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
import javax.vecmath.Vector3f;
import java.util.concurrent.TimeUnit;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.ModelStateComposition;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import genetics.api.GeneticHelper;
import genetics.api.alleles.IAlleleValue;
import genetics.api.organism.IOrganism;

import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.api.lepidopterology.genetics.IAlleleButterflySpecies;
import forestry.api.lepidopterology.genetics.IButterfly;
import forestry.core.config.Constants;
import forestry.core.models.BlankModel;
import forestry.core.models.DefaultTextureGetter;
import forestry.core.models.TRSRBakedModel;
import forestry.core.utils.ModelUtil;

@OnlyIn(Dist.CLIENT)
public class ModelButterflyItem extends BlankModel {
	@Nullable
	private static IModel modelButterfly;

	private static final Cache<IAlleleButterflySpecies, IBakedModel> cache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build();

	public static void onModelBake(ModelBakeEvent event) {
		ModelLoader loader = event.getModelLoader();    //TODO this needs to be passed to be used by cache I think. I don't like rendering/baking code.
		modelButterfly = null;
		cache.invalidateAll();
	}

	@Override
	protected ItemOverrideList createOverrides() {
		return new ButterflyItemOverrideList();
	}

	private IBakedModel bakeModel(IAlleleButterflySpecies species, float size) {
		ImmutableMap<String, String> textures = ImmutableMap.of("butterfly", species.getItemTexture());

		if (modelButterfly == null) {
			try {
				modelButterfly = ModelLoaderRegistry.getModel(new ResourceLocation(Constants.MOD_ID, "item/butterfly_ge"));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		IModel model = modelButterfly.retexture(textures);
		float scale = 1F / 16F;
		IModelState state = ModelUtil.loadModelState(new ResourceLocation(Constants.MOD_ID, "models/item/butterfly_ge"));
		ModelStateComposition compState = new ModelStateComposition(state, new SimpleModelState(getTransformations(size)));
		IBakedModel bakedModel = model.bake(null, DefaultTextureGetter.INSTANCE, compState, DefaultVertexFormats.ITEM);    //TODO models
		return new PerspectiveMapWrapper(new TRSRBakedModel(bakedModel, -0.03125F, 0.25F - size * 0.37F, -0.03125F + size * scale, size * 1.4F), state);
	}

	private static ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> getTransformations(float size) {
		float scale = 1F / 16F;
		ImmutableMap.Builder<ItemCameraTransforms.TransformType, TRSRTransformation> builder = ImmutableMap.builder();
		builder.put(ItemCameraTransforms.TransformType.FIXED,
			new TRSRTransformation(new Vector3f(scale * 0.5F, scale - (size / 0.75F) * scale, scale * 1.25F), null, null, null));
		builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND,
			new TRSRTransformation(new Vector3f(0, scale - (size / 1F) * scale, 0), null, null, null));
		builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND,
			new TRSRTransformation(new Vector3f(0, scale - (size / 1F) * scale, 0), null, null, null));
		return builder.build();
	}

	private class ButterflyItemOverrideList extends ItemOverrideList {
		public ButterflyItemOverrideList() {
			super();
		}

		@Override
		public IBakedModel getModelWithOverrides(IBakedModel model, ItemStack stack, @Nullable World worldIn, @Nullable LivingEntity entityIn) {
			IOrganism<IButterfly> organism = GeneticHelper.getOrganism(stack);
			IAlleleButterflySpecies species = organism.getAllele(ButterflyChromosomes.SPECIES, true);
			IAlleleValue<Float> size = organism.getAllele(ButterflyChromosomes.SIZE, true);
			Preconditions.checkNotNull(species);
			Preconditions.checkNotNull(size);
			IBakedModel bakedModel = cache.getIfPresent(species);
			if (bakedModel == null) {
				bakedModel = bakeModel(species, size.getValue());
				cache.put(species, bakedModel);
			}
			return bakedModel;
		}
	}
}

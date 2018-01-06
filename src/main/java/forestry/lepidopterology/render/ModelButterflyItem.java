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
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.ModelStateComposition;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.genetics.IAlleleFloat;
import forestry.api.lepidopterology.EnumButterflyChromosome;
import forestry.api.lepidopterology.IAlleleButterflySpecies;
import forestry.core.config.Constants;
import forestry.core.genetics.Genome;
import forestry.core.models.BlankModel;
import forestry.core.models.DefaultTextureGetter;
import forestry.core.models.TRSRBakedModel;
import forestry.core.utils.ModelUtil;

@SideOnly(Side.CLIENT)
public class ModelButterflyItem extends BlankModel {
	@Nullable
	private static IModel modelButterfly;

	private static final Cache<IAlleleButterflySpecies, IBakedModel> cache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build();

	public static void onModelBake(ModelBakeEvent event) {
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
		IBakedModel bakedModel = model.bake(ModelRotation.X0_Y0, DefaultVertexFormats.ITEM, DefaultTextureGetter.INSTANCE);
		float scale = 1F / 16F;
		IModelState state = ModelUtil.loadModelState(new ResourceLocation(Constants.MOD_ID, "models/item/butterfly_ge"));
		state = new ModelStateComposition(state, new SimpleModelState(getTransformations(size)));
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
			super(Collections.emptyList());
		}

		@Override
		public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
			IAlleleButterflySpecies species = Genome.getAllele(stack, EnumButterflyChromosome.SPECIES, true, IAlleleButterflySpecies.class);
			IAlleleFloat size = Genome.getAllele(stack, EnumButterflyChromosome.SIZE, true, IAlleleFloat.class);
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

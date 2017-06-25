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
package forestry.arboriculture.models;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.block.model.VariantList;
import net.minecraft.client.renderer.block.model.WeightedBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.MultiModelState;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SimpleModel implements IModel {

	private static final ModelResourceLocation MISSING = new ModelResourceLocation("builtin/missing", "missing");

	private final List<Variant> variants;
	private final List<ResourceLocation> locations = new ArrayList<>();
	private final Set<ResourceLocation> textures = Sets.newHashSet();
	private final List<IModel> models = new ArrayList<>();
	private final IModelState defaultState;

	public SimpleModel(List<ResourceLocation> locations, List<IModel> models, List<Variant> variants,
					   IModelState defaultState) {
		this.locations.addAll(locations);
		this.models.addAll(models);
		this.variants = variants;
		this.defaultState = defaultState;
		for (IModel model : models) {
			textures.addAll(model.getTextures());
		}
	}

	public SimpleModel(ResourceLocation parent, VariantList variants) throws Exception {
		this.variants = variants.getVariantList();
		ImmutableList.Builder<Pair<IModel, IModelState>> builder = ImmutableList.builder();
		for (Variant variant : this.variants) {
			ResourceLocation loc = variant.getModelLocation();
			locations.add(loc);

			IModel model;
			if (loc.equals(MISSING)) {
				model = ModelLoaderRegistry.getMissingModel();
			} else {
				model = ModelLoaderRegistry.getModel(loc);
			}

			model = variant.process(model);
			for (ResourceLocation location : model.getDependencies()) {
				ModelLoaderRegistry.getModelOrMissing(location);
			}
			textures.addAll(model.getTextures());

			models.add(model);
			builder.add(Pair.of(model, variant.getState()));
		}

		if (models.size() == 0) {
			IModel missing = ModelLoaderRegistry.getMissingModel();
			models.add(missing);
			builder.add(Pair.of(missing, TRSRTransformation.identity()));
		}

		defaultState = new MultiModelState(builder.build());
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return ImmutableList.copyOf(locations);
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		return ImmutableSet.copyOf(textures);
	}
	
	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		if (variants.size() == 1) {
			IModel model = models.get(0);
			return model.bake(MultiModelState.getPartState(state, model, 0), format, bakedTextureGetter);
		} else {
			WeightedBakedModel.Builder builder = new WeightedBakedModel.Builder();
			for (int i = 0; i < variants.size(); i++) {
				IModel model = models.get(i);
				builder.add(model.bake(MultiModelState.getPartState(state, model, i), format, bakedTextureGetter),
						variants.get(i).getWeight());
			}
			return builder.build();
		}
	}

	@Override
	public IModelState getDefaultState() {
		return defaultState;
	}

	@Override
	public IModel retexture(ImmutableMap<String, String> textures) {
		List<IModel> models = new ArrayList<>();
		ImmutableList.Builder<Pair<IModel, IModelState>> builder = ImmutableList.builder();
		for (Variant variant : this.variants) {
			ResourceLocation loc = variant.getModelLocation();
			locations.add(loc);

			IModel model;
			if (loc.equals(MISSING)) {
				model = ModelLoaderRegistry.getMissingModel();
			} else {
				try {
					model = ModelLoaderRegistry.getModel(loc);
				} catch (Exception e) {
					throw Throwables.propagate(e);
				}
			}

			model = variant.process(model);
			for (ResourceLocation location : model.getDependencies()) {
				ModelLoaderRegistry.getModelOrMissing(location);
			}
			model = model.retexture(textures);

			models.add(model);
			builder.add(Pair.of(model, variant.getState()));
		}

		if (models.isEmpty()) {
			IModel missing = ModelLoaderRegistry.getMissingModel();
			models.add(missing);
			builder.add(Pair.of(missing, TRSRTransformation.identity()));
		}
		return new SimpleModel(locations, models, variants, new MultiModelState(builder.build()));
	}

}

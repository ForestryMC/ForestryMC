///*******************************************************************************
// * Copyright (c) 2011-2014 SirSengir.
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the GNU Lesser Public License v3
// * which accompanies this distribution, and is available at
// * http://www.gnu.org/licenses/lgpl-3.0.txt
// *
// * Various Contributors including, but not limited to:
// * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
// ******************************************************************************/
//package forestry.arboriculture.models;
//
//import com.google.common.base.Throwables;
//import com.google.common.collect.ImmutableList;
//import com.google.common.collect.ImmutableMap;
//import com.google.common.collect.ImmutableSet;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//import java.util.function.Function;
//
//import org.apache.commons.lang3.tuple.Pair;
//
//import net.minecraft.client.renderer.model.IBakedModel;
//import net.minecraft.client.renderer.model.ModelResourceLocation;
//import net.minecraft.client.renderer.model.Variant;
//import net.minecraft.client.renderer.model.VariantList;
//import net.minecraft.client.renderer.model.WeightedBakedModel;
//import net.minecraft.client.renderer.texture.TextureAtlasSprite;
//import net.minecraft.client.renderer.vertex.VertexFormat;
//import net.minecraft.util.ResourceLocation;
//
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.api.distmarker.OnlyIn;
//import net.minecraftforge.client.model.IModel;
//import net.minecraftforge.client.model.ModelLoaderRegistry;
//import net.minecraftforge.client.model.MultiModelState;
//import net.minecraftforge.common.model.IModelState;
//import net.minecraftforge.common.model.TRSRTransformation;
//@OnlyIn(Dist.CLIENT)
//public class SimpleModel implements IModel {
//
//	private static final ModelResourceLocation MISSING = new ModelResourceLocation("builtin/missing", "missing");
//
//	private final List<Variant> variants;
//	private final ImmutableList<ResourceLocation> locations;
//	private final ImmutableSet<ResourceLocation> textures;
//	private final ImmutableList<IModel> models;
//	private final IModelState defaultState;
//
//	public SimpleModel(List<ResourceLocation> locations, List<IModel> models, List<Variant> variants,
//		IModelState defaultState) {
//		this.variants = variants;
//		this.locations = ImmutableList.copyOf(locations);
//		this.models = ImmutableList.copyOf(models);
//		this.defaultState = defaultState;
//
//		ImmutableSet.Builder<ResourceLocation> texturesBuilder = ImmutableSet.builder();
//		for (IModel model : models) {
//			texturesBuilder.addAll(model.getTextures());
//		}
//		textures = texturesBuilder.build();
//	}
//
//	public SimpleModel(ResourceLocation parent, VariantList variants) throws Exception {
//		this.variants = variants.getVariantList();
//		ImmutableList.Builder<Pair<IModel, IModelState>> builder = ImmutableList.builder();
//		ImmutableList.Builder<IModel> modelsBuilder = ImmutableList.builder();
//		ImmutableList.Builder<ResourceLocation> locationsBuilder = ImmutableList.builder();
//		ImmutableSet.Builder<ResourceLocation> texturesBuilder = ImmutableSet.builder();
//
//		for (Variant variant : this.variants) {
//			ResourceLocation loc = variant.getModelLocation();
//			locationsBuilder.add(loc);
//
//			IModel model;
//			if (loc.equals(MISSING)) {
//				model = ModelLoaderRegistry.getMissingModel();
//			} else {
//				model = ModelLoaderRegistry.getModel(loc);
//			}
//
//			model = variant.process(model);
//			for (ResourceLocation location : model.getDependencies()) {
//				ModelLoaderRegistry.getModelOrMissing(location);
//			}
//			texturesBuilder.addAll(model.getTextures());
//
//			modelsBuilder.add(model);
//			builder.add(Pair.of(model, variant.getState()));
//		}
//
//		if (this.variants.size() == 0) {
//			IModel missing = ModelLoaderRegistry.getMissingModel();
//			modelsBuilder.add(missing);
//			builder.add(Pair.of(missing, TRSRTransformation.identity()));
//		}
//
//		defaultState = new MultiModelState(builder.build());
//
//		locations = locationsBuilder.build();
//		models = modelsBuilder.build();
//		textures = texturesBuilder.build();
//	}
//
//	@Override
//	public Collection<ResourceLocation> getDependencies() {
//		return ImmutableList.copyOf(locations);
//	}
//
//	@Override
//	public Collection<ResourceLocation> getTextures() {
//		return ImmutableSet.copyOf(textures);
//	}
//
//	@Override
//	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
//		if (variants.size() == 1) {
//			IModel model = models.get(0);
//			return model.bake(MultiModelState.getPartState(state, model, 0), format, bakedTextureGetter);
//		} else {
//			WeightedBakedModel.Builder builder = new WeightedBakedModel.Builder();
//			for (int i = 0; i < variants.size(); i++) {
//				IModel model = models.get(i);
//				builder.add(model.bake(MultiModelState.getPartState(state, model, i), format, bakedTextureGetter),
//					variants.get(i).getWeight());
//			}
//			return builder.build();
//		}
//	}
//
//	@Override
//	public IModelState getDefaultState() {
//		return defaultState;
//	}
//
//	@Override
//	public IModel retexture(ImmutableMap<String, String> textures) {
//		List<ResourceLocation> locations = new ArrayList<>();
//		List<IModel> models = new ArrayList<>();
//		ImmutableList.Builder<Pair<IModel, IModelState>> builder = ImmutableList.builder();
//		for (Variant variant : this.variants) {
//			ResourceLocation loc = variant.getModelLocation();
//			locations.add(loc);
//
//			IModel model;
//			if (loc.equals(MISSING)) {
//				model = ModelLoaderRegistry.getMissingModel();
//			} else {
//				try {
//					model = ModelLoaderRegistry.getModel(loc);
//				} catch (Exception e) {
//					Throwables.throwIfUnchecked(e);
//					throw new RuntimeException(e);
//				}
//			}
//
//			model = variant.process(model);
//			for (ResourceLocation location : model.getDependencies()) {
//				ModelLoaderRegistry.getModelOrMissing(location);
//			}
//			model = model.retexture(textures);
//
//			models.add(model);
//			builder.add(Pair.of(model, variant.getState()));
//		}
//
//		if (models.isEmpty()) {
//			IModel missing = ModelLoaderRegistry.getMissingModel();
//			models.add(missing);
//			builder.add(Pair.of(missing, TRSRTransformation.identity()));
//		}
//		return new SimpleModel(locations, models, variants, new MultiModelState(builder.build()));
//	}
//
//}

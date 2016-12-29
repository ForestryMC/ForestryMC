package forestry.arboriculture.models;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import forestry.core.config.Constants;
import forestry.core.utils.Log;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelBlockDefinition;
import net.minecraft.client.renderer.block.model.ModelBlockDefinition.MissingVariantException;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.block.model.VariantList;
import net.minecraft.client.renderer.block.model.WeightedBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.ModelProcessingHelper;
import net.minecraftforge.client.model.MultiModel;
import net.minecraftforge.client.model.MultiModelState;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;

public enum WoodModelLoader implements ICustomModelLoader {
	INSTANCE;

	public static final Map<ResourceLocation, Exception> loadingExceptions = Maps.newHashMap();
	private final Map<ResourceLocation, ModelBlockDefinition> blockDefinitions = Maps.newHashMap();
	public static final List<String> validFiles = new ArrayList<>();
	public boolean isRegistered = false;

	static {
		validFiles.add("door");
		validFiles.add("double_slab");
		validFiles.add("fence");
		validFiles.add("fence_gate");
		validFiles.add("log");
		validFiles.add("planks");
		validFiles.add("slab");
		validFiles.add("stairs");
	}

	// NOOP, handled in loader
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
	}

	@Override
	public boolean accepts(ResourceLocation modelLocation) {
		if (!(modelLocation instanceof ModelResourceLocation) || !modelLocation.getResourceDomain().equals(Constants.MOD_ID) || !modelLocation.getResourcePath().contains("arboriculture")) {
			return false;
		}
		String path = modelLocation.getResourcePath();
		for (String validFile : validFiles) {
			if (path.endsWith(validFile)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws Exception {
		ModelResourceLocation variant = (ModelResourceLocation) modelLocation;
		ModelBlockDefinition definition = getModelBlockDefinition(variant);
		try {
			VariantList variants = definition.getVariant(variant.getVariant());
			return new WeightedRandomModel(variant, variants);
		} catch (MissingVariantException e) {
			if (definition.hasMultipartData()) {
				Log.error("The forestry model loader can't load the model with the location: %s, because it dons't support multipart models.", modelLocation.toString());
			}
			throw e;
		}
	}

	/* MODEL LOADER PRIVATE METHODS */
	private ModelBlockDefinition getModelBlockDefinition(ResourceLocation location) {
		try {
			ResourceLocation resourcelocation = this.getBlockstateLocation(location);
			return this.blockDefinitions.computeIfAbsent(resourcelocation, k -> this.loadMultipartMBD(location, resourcelocation));
		} catch (Exception exception) {
			loadingExceptions.put(location, new Exception("Could not load model definition for variant " + location, exception));
		}
		return new ModelBlockDefinition(new ArrayList<>());
	}

	private ResourceLocation getBlockstateLocation(ResourceLocation location) {
		return new ResourceLocation(location.getResourceDomain(), "blockstates/" + location.getResourcePath() + ".json");
	}

	private ModelBlockDefinition loadMultipartMBD(ResourceLocation location, ResourceLocation fileIn) {
		List<ModelBlockDefinition> list = Lists.newArrayList();

		try {
			for (IResource iresource : Minecraft.getMinecraft().getResourceManager().getAllResources(fileIn)) {
				list.add(this.loadModelBlockDefinition(location, iresource));
			}
		} catch (IOException ioexception) {
			throw new RuntimeException("Encountered an exception when loading model definition of model " + fileIn, ioexception);
		}

		return new ModelBlockDefinition(list);
	}

	private ModelBlockDefinition loadModelBlockDefinition(ResourceLocation location, IResource iresource) {
		InputStream inputstream = null;
		ModelBlockDefinition definition;

		try {
			inputstream = iresource.getInputStream();
			definition = ModelBlockDefinition.parseFromReader(new InputStreamReader(inputstream, Charsets.UTF_8));
		} catch (Exception exception) {
			throw new RuntimeException("Encountered an exception when loading model definition of \'" + location + "\' from: \'" + iresource.getResourceLocation() + "\' in resourcepack: \'" + iresource.getResourcePackName() + "\'", exception);
		} finally {
			IOUtils.closeQuietly(inputstream);
		}

		return definition;
	}

	private class WeightedRandomModel implements IRetexturableModel {
		private final List<Variant> variants;
		private final List<ResourceLocation> locations = new ArrayList<>();
		private final Set<ResourceLocation> textures = Sets.newHashSet();
		private final List<IModel> models = new ArrayList<>();
		private final IModelState defaultState;

		public WeightedRandomModel(List<ResourceLocation> locations, List<IModel> models, List<Variant> variants, IModelState defaultState) {
			this.locations.addAll(locations);
			this.models.addAll(models);
			this.variants = variants;
			this.defaultState = defaultState;
			for (IModel model : models) {
				textures.addAll(model.getTextures());
			}
		}

		public WeightedRandomModel(ResourceLocation parent, VariantList variants) throws Exception {
			this.variants = variants.getVariantList();
			ImmutableList.Builder<Pair<IModel, IModelState>> builder = ImmutableList.builder();
			for (Variant v : this.variants) {
				ResourceLocation loc = v.getModelLocation();
				locations.add(loc);

                /*
				 * Vanilla eats this, which makes it only show variants that have models.
                 * But that doesn't help debugging, so throw the exception
                 */
				IModel model;
				if (loc.equals(new ModelResourceLocation("builtin/missing", "missing"))) {
					// explicit missing location, happens if blockstate has "model"=null
					model = ModelLoaderRegistry.getMissingModel();
				} else {
					model = ModelLoaderRegistry.getModel(loc);
				}

				// FIXME: is this the place? messes up dependency and texture resolution
				model = v.process(model);
				for (ResourceLocation location : model.getDependencies()) {
					ModelLoaderRegistry.getModelOrMissing(location);
				}
				//FMLLog.getLogger().error("Exception resolving indirect dependencies for model" + loc, e);
				textures.addAll(model.getTextures()); // Kick this, just in case.

				models.add(model);
				builder.add(Pair.of(model, v.getState()));
			}

			if (models.size() == 0) { //If all variants are missing, add one with the missing model and default rotation.
				// FIXME: log this?
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
			}
			WeightedBakedModel.Builder builder = new WeightedBakedModel.Builder();
			for (int i = 0; i < variants.size(); i++) {
				IModel model = models.get(i);
				builder.add(model.bake(MultiModelState.getPartState(state, model, i), format, bakedTextureGetter), variants.get(i).getWeight());
			}
			return builder.build();
		}

		@Override
		public IModelState getDefaultState() {
			return defaultState;
		}

		@Override
		public IModel retexture(ImmutableMap<String, String> textures) {
			List<IModel> models = new ArrayList<>();
			ImmutableList.Builder<Pair<IModel, IModelState>> builder = ImmutableList.builder();
			for (Variant v : this.variants) {
				ResourceLocation loc = v.getModelLocation();
				locations.add(loc);

				IModel model;
				if (loc.equals(new ModelResourceLocation("builtin/missing", "missing"))) {
					model = ModelLoaderRegistry.getMissingModel();
				} else {
					try {
						model = ModelLoaderRegistry.getModel(loc);
					} catch (Exception e) {
						throw Throwables.propagate(e);
					}
				}

				model = v.process(model);
				for (ResourceLocation location : model.getDependencies()) {
					ModelLoaderRegistry.getModelOrMissing(location);
				}

				if (model instanceof MultiModel) {
					IModel base = ObfuscationReflectionHelper.getPrivateValue(MultiModel.class, (MultiModel) model, 1);
					Map<String, Pair<IModel, IModelState>> parts = ObfuscationReflectionHelper.getPrivateValue(MultiModel.class, (MultiModel) model, 3);

					ImmutableMap.Builder<String, Pair<IModel, IModelState>> partBuilder = new ImmutableMap.Builder<>();
					IModel retexturedModel = ModelProcessingHelper.retexture(base, textures);
					for (Entry<String, Pair<IModel, IModelState>> part : parts.entrySet()) {
						partBuilder.put(part.getKey(), Pair.of(ModelProcessingHelper.retexture(part.getValue().getLeft(), textures), part.getValue().getRight()));
					}

					ObfuscationReflectionHelper.setPrivateValue(MultiModel.class, (MultiModel) model, retexturedModel, 1);
					ObfuscationReflectionHelper.setPrivateValue(MultiModel.class, (MultiModel) model, partBuilder.build(), 3);
				}
				model = ModelProcessingHelper.retexture(model, textures);

				models.add(model);
				builder.add(Pair.of(model, v.getState()));
			}

			if (models.size() == 0) {
				IModel missing = ModelLoaderRegistry.getMissingModel();
				models.add(missing);
				builder.add(Pair.of(missing, TRSRTransformation.identity()));
			}
			return new WeightedRandomModel(locations, models, variants, new MultiModelState(builder.build()));
		}
	}
}

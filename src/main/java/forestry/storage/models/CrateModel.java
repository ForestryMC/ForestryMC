package forestry.storage.models;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import com.mojang.datafixers.util.Pair;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.registries.ForgeRegistries;

import forestry.core.config.Constants;
import forestry.core.models.ClientManager;
import forestry.core.utils.ResourceUtil;
import forestry.storage.features.CreateItems;
import forestry.storage.items.ItemCrated;

@OnlyIn(Dist.CLIENT)
public class CrateModel implements IModelGeometry<CrateModel> {

	private static final String CUSTOM_CRATES = "forestry:item/crates/";

	private static List<BakedQuad> bakedQuads = new LinkedList<>();

	public static void clearCachedQuads() {
		bakedQuads.clear();
	}

	private final ItemCrated crated;
	private final ItemStack contained;

	public CrateModel(ItemCrated crated) {
		this.crated = crated;
		this.contained = crated.getContained();
	}

	@Nullable
	private IBakedModel getCustomContentModel(ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform transform) {
		ResourceLocation registryName = crated.getRegistryName();
		if (registryName == null) {
			return null;
		}
		String containedName = registryName.getPath().replace("crated.", "");
		ResourceLocation location = new ResourceLocation(CUSTOM_CRATES + containedName);
		IUnbakedModel model;
		if (!ResourceUtil.resourceExists(new ResourceLocation(location.getNamespace(), "models/" + location.getPath() + ".json"))) {
			return null;
		}
		try {
			model = ModelLoader.instance().getUnbakedModel(location);
		} catch (Exception e) {
			return null;
		}
		return model.bakeModel(bakery, spriteGetter, transform, location);
	}

	@Override
	public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation modelLocation) {
		if (bakedQuads.isEmpty()) {
			IBakedModel bakedModel = bakery.getBakedModel(new ModelResourceLocation(Constants.MOD_ID + ":crate-filled", "inventory"), transform, spriteGetter);
			if (bakedModel != null) {
				//Set the crate color index to 100
				for (BakedQuad quad : bakedModel.getQuads(null, null, new Random(0L), EmptyModelData.INSTANCE)) {
					bakedQuads.add(new BakedQuad(quad.getVertexData(), 100, quad.getFace(), quad.func_187508_a(), quad.func_239287_f_()));
				}
			}
		}
		IBakedModel model;
		List<BakedQuad> quads = new LinkedList<>(bakedQuads);
		IBakedModel contentModel = getCustomContentModel(bakery, spriteGetter, transform);
		if (contentModel == null) {
			model = new CrateBakedModel(quads, contained);
		} else {
			quads.addAll(contentModel.getQuads(null, null, new Random(0), EmptyModelData.INSTANCE));
			model = new CrateBakedModel(quads);
		}
		return new PerspectiveMapWrapper(model, ClientManager.getInstance().getDefaultItemState());
	}

	@Override
	public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
		return Collections.emptyList();
	}

	public static class Loader implements IModelLoader {

		public static final ResourceLocation LOCATION = new ResourceLocation(Constants.MOD_ID, "crate-filled");

		@Override
		public void onResourceManagerReload(IResourceManager resourceManager) {
			clearCachedQuads();
		}

		@Override
		public IModelGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
			ResourceLocation registryName = new ResourceLocation(Constants.MOD_ID, JSONUtils.getString(modelContents, "variant"));
			Item item = ForgeRegistries.ITEMS.getValue(registryName);
			if (!(item instanceof ItemCrated)) {
				return ModelLoaderRegistry.getModel(new ModelResourceLocation(new ResourceLocation(Constants.MOD_ID, CreateItems.CRATE.getIdentifier()), "inventory"), deserializationContext, modelContents);
			}
			ItemCrated crated = (ItemCrated) item;
			return new CrateModel(crated);
		}
	}
}

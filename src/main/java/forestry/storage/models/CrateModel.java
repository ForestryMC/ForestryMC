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

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;

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
import forestry.storage.features.CrateItems;
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
	private BakedModel getCustomContentModel(ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform) {
		ResourceLocation registryName = crated.getRegistryName();
		if (registryName == null) {
			return null;
		}
		String containedName = registryName.getPath().replace("crated.", "");
		ResourceLocation location = new ResourceLocation(CUSTOM_CRATES + containedName);
		UnbakedModel model;
		if (!ResourceUtil.resourceExists(new ResourceLocation(location.getNamespace(), "models/" + location.getPath() + ".json"))) {
			return null;
		}
		try {
			model = ModelLoader.instance().getModel(location);
		} catch (Exception e) {
			return null;
		}
		return model.bake(bakery, spriteGetter, transform, location);
	}

	@Override
	public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform, ItemOverrides overrides, ResourceLocation modelLocation) {
		if (bakedQuads.isEmpty()) {
			BakedModel bakedModel = bakery.getBakedModel(new ModelResourceLocation(Constants.MOD_ID + ":crate-filled", "inventory"), transform, spriteGetter);
			if (bakedModel != null) {
				//Set the crate color index to 100
				for (BakedQuad quad : bakedModel.getQuads(null, null, new Random(0L), EmptyModelData.INSTANCE)) {
					bakedQuads.add(new BakedQuad(quad.getVertices(), 100, quad.getDirection(), quad.getSprite(), quad.isShade()));
				}
			}
		}
		BakedModel model;
		List<BakedQuad> quads = new LinkedList<>(bakedQuads);
		BakedModel contentModel = getCustomContentModel(bakery, spriteGetter, transform);
		if (contentModel == null) {
			model = new CrateBakedModel(quads, contained);
		} else {
			quads.addAll(contentModel.getQuads(null, null, new Random(0), EmptyModelData.INSTANCE));
			model = new CrateBakedModel(quads);
		}
		return new PerspectiveMapWrapper(model, ClientManager.getInstance().getDefaultItemState());
	}

	@Override
	public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
		return Collections.emptyList();
	}

	public static class Loader implements IModelLoader {

		public static final ResourceLocation LOCATION = new ResourceLocation(Constants.MOD_ID, "crate-filled");

		@Override
		public void onResourceManagerReload(ResourceManager resourceManager) {
			clearCachedQuads();
		}

		@Override
		public IModelGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
			ResourceLocation registryName = new ResourceLocation(Constants.MOD_ID, GsonHelper.getAsString(modelContents, "variant"));
			Item item = ForgeRegistries.ITEMS.getValue(registryName);
			if (!(item instanceof ItemCrated)) {
				return ModelLoaderRegistry.getModel(new ModelResourceLocation(new ResourceLocation(Constants.MOD_ID, CrateItems.CRATE.getIdentifier()), "inventory"), deserializationContext, modelContents);
			}
			ItemCrated crated = (ItemCrated) item;
			return new CrateModel(crated);
		}
	}
}

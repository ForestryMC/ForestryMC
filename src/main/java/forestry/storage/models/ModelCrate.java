package forestry.storage.models;

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
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.mojang.datafixers.util.Pair;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import forestry.core.config.Constants;
import forestry.core.models.ClientManager;
import forestry.core.utils.ResourceUtil;
import forestry.storage.items.ItemCrated;

//TODO this is pretty broken probably
@OnlyIn(Dist.CLIENT)
public class ModelCrate implements IModelGeometry<ModelCrate> {

	private static final String CUSTOM_CRATES = "forestry:item/crates/";

	private static List<BakedQuad> bakedQuads = new LinkedList<>();

	public static void clearCachedQuads() {
		bakedQuads.clear();
	}

	private final ItemCrated crated;
	private final ItemStack contained;

	public ModelCrate(ItemCrated crated) {
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
				for (BakedQuad quad : bakedModel.getQuads(null, null, new Random(0L))) {
					bakedQuads.add(new BakedQuad(quad.getVertexData(), 100, quad.getFace(), quad.func_187508_a(), quad.func_239287_f_()));
				}
			}
		}
		IBakedModel model;
		List<BakedQuad> quads = new LinkedList<>(bakedQuads);
		IBakedModel contentModel = getCustomContentModel(bakery, spriteGetter, transform);
		if (contentModel == null) {
			model = new ModelCrateBaked(quads, contained);
		} else {
			quads.addAll(contentModel.getQuads(null, null, new Random(0)));
			model = new ModelCrateBaked(quads);
		}
		return new PerspectiveMapWrapper(model, ClientManager.getInstance().getDefaultItemState());
	}

	@Override
	public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
		return Collections.emptyList();
	}
}

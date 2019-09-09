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
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.PerspectiveMapWrapper;

import forestry.core.config.Constants;
import forestry.core.models.DefaultTextureGetter;
import forestry.core.models.ModelManager;
import forestry.core.utils.ModelUtil;
import forestry.storage.items.ItemCrated;

//TODO this is pretty broken probably
@OnlyIn(Dist.CLIENT)
public class ModelCrate implements IUnbakedModel {

	private static final String CUSTOM_CRATES = "forestry:item/crates/";

	private static List<BakedQuad> bakedQuads = new LinkedList<>();

	public static void clearCachedQuads() {
		bakedQuads.clear();
	}

	private final ItemCrated crated;
	private final ItemStack contained;

	ModelCrate(ItemCrated crated) {
		this.crated = crated;
		this.contained = crated.getContained();
	}

	@Nullable
	private IBakedModel getCustomContentModel(ModelBakery bakery, ISprite sprite) {
		ResourceLocation registryName = crated.getRegistryName();
		if (registryName == null) {
			return null;
		}
		String containedName = registryName.getPath().replace("crated.", "");
		ResourceLocation location = new ResourceLocation(CUSTOM_CRATES + containedName);
		IModel model;
		if (!ModelUtil.resourceExists(new ResourceLocation(location.getNamespace(), "models/" + location.getPath() + ".json"))) {
			return null;
		}
		try {
			model = ModelLoaderRegistry.getModel(location);
		} catch (Exception e) {
			return null;
		}
		return model.bake(bakery, DefaultTextureGetter.INSTANCE, sprite, DefaultVertexFormats.ITEM);
	}

	@Nullable
	@Override
	public IBakedModel bake(ModelBakery bakery, Function spriteGetter, ISprite sprite, VertexFormat format) {
		if (bakedQuads.isEmpty()) {
			IModel crateModel = ModelLoaderRegistry.getModelOrMissing(new ResourceLocation(Constants.MOD_ID + ":item/crate-filled"));
			IBakedModel bakedModel = crateModel.bake(bakery, DefaultTextureGetter.INSTANCE, sprite, DefaultVertexFormats.ITEM);
			//Set the crate color index to 100
			for (BakedQuad quad : bakedModel.getQuads(null, null, new Random(0L))) {
				bakedQuads.add(new BakedQuad(quad.getVertexData(), 100, quad.getFace(), quad.getSprite(), quad.shouldApplyDiffuseLighting(), quad.getFormat()));
			}
		}
		IBakedModel model;
		List<BakedQuad> quads = new LinkedList<>(bakedQuads);
		IBakedModel contentModel = getCustomContentModel(bakery, sprite);
		if (contentModel == null) {
			model = new ModelCrateBaked(quads, contained);

		} else {
			quads.addAll(contentModel.getQuads(null, null, new Random(0)));
			model = new ModelCrateBaked(quads);
		}
		return new PerspectiveMapWrapper(model, ModelManager.getInstance().getDefaultItemState());
	}

	//TODO for these
	@Override
	public Collection<ResourceLocation> getDependencies() {
		return Collections.emptyList();
	}

	@Override
	public Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors) {
		return Collections.emptyList();
	}
}

package forestry.storage.models;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.IModelState;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.config.Constants;
import forestry.core.models.DefaultTextureGetter;
import forestry.core.models.ModelManager;
import forestry.core.utils.ModelUtil;
import forestry.storage.items.ItemCrated;

@SideOnly(Side.CLIENT)
public class ModelCrate implements IModel {

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
	private IBakedModel getCustomContentModel() {
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
		return model.bake(ModelManager.getInstance().getDefaultItemState(), DefaultVertexFormats.ITEM, DefaultTextureGetter.INSTANCE);
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		if (bakedQuads.isEmpty()) {
			IModel crateModel = ModelLoaderRegistry.getModelOrMissing(new ResourceLocation(Constants.MOD_ID + ":item/crate-filled"));
			IBakedModel bakedModel = crateModel.bake(ModelManager.getInstance().getDefaultItemState(), DefaultVertexFormats.ITEM, DefaultTextureGetter.INSTANCE);
			//Set the crate color index to 100
			for (BakedQuad quad : bakedModel.getQuads(null, null, 0L)) {
				bakedQuads.add(new BakedQuad(quad.getVertexData(), 100, quad.getFace(), quad.getSprite(), quad.shouldApplyDiffuseLighting(), quad.getFormat()));
			}
		}
		IBakedModel model;
		List<BakedQuad> quads = new LinkedList<>(bakedQuads);
		IBakedModel contentModel = getCustomContentModel();
		if (contentModel == null) {
			model = new ModelCrateBaked(quads, contained);

		} else {
			quads.addAll(contentModel.getQuads(null, null, 0));
			model = new ModelCrateBaked(quads);
		}
		return new PerspectiveMapWrapper(model, ModelManager.getInstance().getDefaultItemState());
	}
}

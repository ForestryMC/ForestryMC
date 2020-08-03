package forestry.storage.models;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import com.mojang.datafixers.util.Pair;

import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import forestry.api.storage.EnumBackpackType;
import forestry.core.config.Constants;
import forestry.core.models.AbstractItemModel;
import forestry.storage.BackpackMode;
import forestry.storage.items.ItemBackpack;

public class BackpackItemModel extends AbstractItemModel {
	private static ImmutableMap<EnumBackpackType, ImmutableMap<BackpackMode, IBakedModel>> cachedBakedModels = ImmutableMap.of();

	public BackpackItemModel() {
	}

	@Override
	protected IBakedModel getOverride(IBakedModel model, ItemStack stack) {
		BackpackMode mode = ItemBackpack.getMode(stack);
		EnumBackpackType type = ItemBackpack.getType(stack);
		return cachedBakedModels.getOrDefault(type, ImmutableMap.of()).getOrDefault(mode, model);
	}

	private static class Geometry implements IModelGeometry<BackpackItemModel.Geometry> {

		@Override
		public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
			if (cachedBakedModels.isEmpty()) {
				ImmutableMap.Builder<EnumBackpackType, ImmutableMap<BackpackMode, IBakedModel>> modelBuilder = new ImmutableMap.Builder<>();
				for (EnumBackpackType backpackType : EnumBackpackType.values()) {
					ImmutableMap.Builder<BackpackMode, IBakedModel> modeModels = new ImmutableMap.Builder<>();
					for (BackpackMode mode : BackpackMode.values()) {
						modeModels.put(mode, bakery.getBakedModel(backpackType.getLocation(mode)
							, modelTransform
							, spriteGetter)
						);
					}
					modelBuilder.put(backpackType, modeModels.build());
					cachedBakedModels = modelBuilder.build();
				}
			}
			return new BackpackItemModel();
		}

		@Override
		public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
			return ImmutableList.of();
		}
	}

	public static class Loader implements IModelLoader<Geometry> {
		public static final ResourceLocation LOCATION = new ResourceLocation(Constants.MOD_ID, "backpacks");

		@Override
		public void onResourceManagerReload(IResourceManager resourceManager) {
			cachedBakedModels = ImmutableMap.of();
		}

		@Override
		public BackpackItemModel.Geometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
			return new BackpackItemModel.Geometry();
		}
	}
}

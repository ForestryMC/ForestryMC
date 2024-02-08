package forestry.storage.models;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import forestry.api.storage.EnumBackpackType;
import forestry.core.config.Constants;
import forestry.core.models.AbstractItemModel;
import forestry.storage.BackpackMode;
import forestry.storage.items.ItemBackpack;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

public class BackpackItemModel extends AbstractItemModel {
	private static ImmutableMap<EnumBackpackType, ImmutableMap<BackpackMode, BakedModel>> cachedBakedModels = ImmutableMap.of();

	public BackpackItemModel() {
	}

	@Override
	protected BakedModel getOverride(BakedModel model, ItemStack stack) {
		BackpackMode mode = ItemBackpack.getMode(stack);
		EnumBackpackType type = ItemBackpack.getType(stack);
		return cachedBakedModels.getOrDefault(type, ImmutableMap.of()).getOrDefault(mode, model);
	}

	private static class Geometry implements IUnbakedGeometry<Geometry> {

		@Override
		public BakedModel bake(IGeometryBakingContext context, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
			if (cachedBakedModels.isEmpty()) {
				ImmutableMap.Builder<EnumBackpackType, ImmutableMap<BackpackMode, BakedModel>> modelBuilder = new ImmutableMap.Builder<>();
				for (EnumBackpackType backpackType : EnumBackpackType.values()) {
					ImmutableMap.Builder<BackpackMode, BakedModel> modeModels = new ImmutableMap.Builder<>();
					for (BackpackMode mode : BackpackMode.values()) {
						modeModels.put(mode, bakery.bake(backpackType.getLocation(mode),
								modelState,
								spriteGetter)
						);
					}
					modelBuilder.put(backpackType, modeModels.build());
				}
				cachedBakedModels = modelBuilder.build();
			}
			return new BackpackItemModel();
		}

		@Override
		public Collection<Material> getMaterials(IGeometryBakingContext context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
			return ImmutableList.of();
		}
	}

	public static class Loader implements IGeometryLoader<Geometry>, ResourceManagerReloadListener {
		public static final ResourceLocation LOCATION = new ResourceLocation(Constants.MOD_ID, "backpacks");
		public static final Loader INSTANCE = new Loader();

		private Loader() {
		}

		@Override
		public void onResourceManagerReload(ResourceManager resourceManager) {
			// TODO: Find a way to clear the cache before the models get reloaded an not after
			//cachedBakedModels = ImmutableMap.of();
		}

		@Override
		public Geometry read(JsonObject modelContents, JsonDeserializationContext context) throws JsonParseException {
			return new BackpackItemModel.Geometry();
		}
	}
}

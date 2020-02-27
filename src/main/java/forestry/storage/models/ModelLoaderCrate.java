package forestry.storage.models;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.registries.ForgeRegistries;

import forestry.core.config.Constants;
import forestry.storage.features.CreateItems;
import forestry.storage.items.ItemCrated;

@OnlyIn(Dist.CLIENT)
public enum ModelLoaderCrate implements IModelLoader {
	INSTANCE;

	public static final ResourceLocation LOCATION = new ResourceLocation(Constants.MOD_ID, "crate-filled");

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
	}

	@Override
	public IModelGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
		ResourceLocation registryName = new ResourceLocation(Constants.MOD_ID, JSONUtils.getString(modelContents, "variant"));
		Item item = ForgeRegistries.ITEMS.getValue(registryName);
		if (!(item instanceof ItemCrated)) {
			return ModelLoaderRegistry.getModel(new ModelResourceLocation(new ResourceLocation(Constants.MOD_ID, CreateItems.CRATE.getIdentifier()), "inventory"), deserializationContext, modelContents);
		}
		ItemCrated crated = (ItemCrated) item;
		return new ModelCrate(crated);
	}
}

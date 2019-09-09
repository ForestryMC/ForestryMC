package forestry.storage.models;

import java.util.function.Predicate;

import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.VanillaResourceType;

import forestry.core.config.Constants;
import forestry.storage.items.ItemCrated;

@OnlyIn(Dist.CLIENT)
public enum ModelLoaderCrate implements ICustomModelLoader {
	INSTANCE;

	@Override
	public boolean accepts(ResourceLocation modelLocation) {
		if (!(modelLocation instanceof ModelResourceLocation)) {
			return false;
		}
		ModelResourceLocation location = (ModelResourceLocation) modelLocation;
		return location.getNamespace().equals(Constants.MOD_ID)
			&& location.getPath().equals("crate-filled")
			&& !location.getVariant().equals("inventory");
	}

	@Override
	public IUnbakedModel loadModel(ResourceLocation modelLocation) {
		ModelResourceLocation location = (ModelResourceLocation) modelLocation;
		ResourceLocation registryName = new ResourceLocation(Constants.MOD_ID, location.getVariant());
		Item item = ForgeRegistries.ITEMS.getValue(registryName);
		if (!(item instanceof ItemCrated)) {
			return ModelLoaderRegistry.getModelOrMissing(new ModelResourceLocation(modelLocation, "inventory"));
		}
		ItemCrated crated = (ItemCrated) item;
		return new ModelCrate(crated);
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		ModelCrate.clearCachedQuads();
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
		if (resourcePredicate.test(VanillaResourceType.TEXTURES)) {
			this.onResourceManagerReload(resourceManager);
		}
	}
}

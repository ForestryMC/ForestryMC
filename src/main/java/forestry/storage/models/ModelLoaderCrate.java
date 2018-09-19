package forestry.storage.models;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;

import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.config.Constants;
import forestry.storage.items.ItemCrated;

@SideOnly(Side.CLIENT)
public enum ModelLoaderCrate implements ICustomModelLoader {
	INSTANCE;

	@Override
	public boolean accepts(ResourceLocation modelLocation) {
		if(!(modelLocation instanceof ModelResourceLocation)){
			return false;
		}
		ModelResourceLocation location = (ModelResourceLocation) modelLocation;
		return location.getResourceDomain().equals(Constants.MOD_ID)
			&& location.getResourcePath().equals("crate-filled")
			&& !location.getVariant().equals("inventory");
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws Exception {
		ModelResourceLocation location = (ModelResourceLocation) modelLocation;
		ResourceLocation registryName = new ResourceLocation(Constants.MOD_ID, location.getVariant());
		Item item = ForgeRegistries.ITEMS.getValue(registryName);
		if(!(item instanceof ItemCrated)){
			return ModelLoaderRegistry.getModelOrMissing(new ModelResourceLocation(modelLocation, "inventory"));
		}
		ItemCrated crated = (ItemCrated) item;
		return new ModelCrate(crated);
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {

	}
}

package forestry.arboriculture.models;

import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.model.ICustomModelLoader;

import forestry.core.config.Constants;

public enum SaplingModelLoader implements ICustomModelLoader {
	INSTANCE;


	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		// NOOP, handled in loader
	}

	@Override
	public boolean accepts(ResourceLocation modelLocation) {
		return modelLocation.getNamespace().equals(Constants.MOD_ID) && modelLocation.getPath().equals("block/sapling_ge");
	}

	@Override
	public IUnbakedModel loadModel(ResourceLocation modelLocation) throws Exception {
		return new ModelSapling();
	}
}

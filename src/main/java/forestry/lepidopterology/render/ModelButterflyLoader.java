package forestry.lepidopterology.render;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.model.IModelLoader;

import genetics.utils.AlleleUtils;

import forestry.api.lepidopterology.genetics.ButterflyChromosomes;

public class ModelButterflyLoader implements IModelLoader<ModelButterflyItem.Geometry> {

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
	}

	@Override
	public ModelButterflyItem.Geometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
		ImmutableMap.Builder<String, String> subModels = new ImmutableMap.Builder<>();
		AlleleUtils.forEach(ButterflyChromosomes.SPECIES, (butterfly) -> {
			ResourceLocation registryName = butterfly.getRegistryName();
			subModels.put(registryName.getPath(), butterfly.getItemTexture().toString());
		});
		return new ModelButterflyItem.Geometry(subModels.build());
	}
}

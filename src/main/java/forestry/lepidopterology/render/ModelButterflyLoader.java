package forestry.lepidopterology.render;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.resources.IResourceManager;

import net.minecraftforge.client.model.IModelLoader;

import genetics.api.GeneticsAPI;
import genetics.api.alleles.IAllele;

import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.api.lepidopterology.genetics.IAlleleButterflySpecies;

public class ModelButterflyLoader implements IModelLoader<ModelButterflyItem.Geometry> {

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
	}

	@Override
	public ModelButterflyItem.Geometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
		ImmutableMap.Builder<String, String> subModels = new ImmutableMap.Builder<>();
		for (IAllele species : GeneticsAPI.apiInstance.getAlleleRegistry().getRegisteredAlleles(ButterflyChromosomes.SPECIES)) {
			subModels.put(species.getRegistryName().getPath(), ((IAlleleButterflySpecies) species).getItemTexture());
		}
		return new ModelButterflyItem.Geometry(subModels.build());
	}
}

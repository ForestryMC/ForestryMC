package forestry.arboriculture.models;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.geometry.IModelGeometry;

public enum SaplingModelLoader implements IModelLoader {
    INSTANCE;


    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        // NOOP, handled in loader
    }

    @Override
    public IModelGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        return new ModelSapling();
    }
}

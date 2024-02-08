package forestry.arboriculture.models;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

public enum SaplingModelLoader implements IGeometryLoader<ModelSapling> {
	INSTANCE;

	@Override
	public ModelSapling read(JsonObject modelContents, JsonDeserializationContext context) throws JsonParseException {
		return new ModelSapling();
	}
}

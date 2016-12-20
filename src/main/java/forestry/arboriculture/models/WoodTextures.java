package forestry.arboriculture.models;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.WoodBlockKind;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class WoodTextures {
	protected static final Map<String, Texture> textures = new HashMap<>();

	public static void deserializeFile(IResourceManager resourceManager) {
		try {
			InputStream stream = resourceManager.getResource(new ResourceLocation("forestry:textures/woodTextures.json")).getInputStream();
			JsonReader reader = null;
			try {
				reader = new JsonReader(new BufferedReader(new InputStreamReader(stream)));
				JsonElement json = Streams.parse(reader);
				if (json.isJsonObject()) {
					for (Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
						JsonElement ele = entry.getValue();
						if (ele.isJsonObject()) {
							textures.put(entry.getKey(), loadTexture(ele.getAsJsonObject()));
						} else if (ele.isJsonArray()) {
							ImmutableMap.Builder<String, SimpleTexture> locations = new ImmutableMap.Builder<>();
							for (JsonElement eleEntry : ele.getAsJsonArray()) {
								if (eleEntry.isJsonObject()) {
									JsonObject obj = eleEntry.getAsJsonObject();
									if (!obj.has("kind") || !obj.get("kind").isJsonPrimitive() || !obj.get("kind").getAsJsonPrimitive().isString()) {
										continue;
									}
									String kind = obj.get("kind").getAsString();
									locations.put(kind, loadTexture(obj));
								}
							}
							textures.put(entry.getKey(), new KindTexture(locations.build()));
						}
					}
				}
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
		} catch (IOException e) {
			if (!(e instanceof FileNotFoundException)) {
				e.printStackTrace();
			}
		}
	}

	private static SimpleTexture loadTexture(JsonObject obj) {
		ImmutableMap.Builder<String, String> locations = new ImmutableMap.Builder<>();
		for (Entry<String, JsonElement> eleEntry : obj.entrySet()) {
			JsonElement e = eleEntry.getValue();
			if (e.isJsonPrimitive() && e.getAsJsonPrimitive().isString()) {
				locations.put(eleEntry.getKey(), e.getAsString());
			}
		}
		return new SimpleTexture(locations.build());
	}

	public static ImmutableMap<String, String> getLocations(IWoodType woodType, WoodBlockKind blockKind) {
		String kindName = blockKind.toString();
		Texture kindTex = textures.get(kindName);
		Texture tex = textures.get(woodType.getName().toLowerCase(Locale.ENGLISH));
		Map<String, String> locations = new HashMap<>();
		if (kindTex != null && kindTex instanceof SimpleTexture) {
			locations.putAll(((SimpleTexture) kindTex).locations);
		}
		if (tex != null) {
			if (tex instanceof SimpleTexture) {
				locations.putAll(((SimpleTexture) tex).locations);
			} else {
				SimpleTexture kind = ((KindTexture) tex).kindLocations.get(kindName);
				locations.putAll(kind.locations);
			}
		}
		ImmutableMap.Builder<String, String> builder = new ImmutableMap.Builder<>();
		for (Entry<String, String> location : locations.entrySet()) {
			String texture = location.getValue();
			switch (texture) {
				case "plank":
					texture = woodType.getPlankTexture();
					break;
				case "bark":
					texture = woodType.getBarkTexture();
					break;
				case "heart":
					texture = woodType.getHeartTexture();
					break;
				case "doorUp":
					texture = woodType.getDoorUpperTexture();
					break;
				case "doorLow":
					texture = woodType.getDoorLowerTexture();
					break;
				default:
					builder.put(location.getKey(), texture.replace("%woodType", woodType.getName().toLowerCase(Locale.ENGLISH)));
					continue;
			}
			builder.put(location.getKey(), texture);
		}
		return builder.build();
	}

	private static class Texture {

	}

	private static class SimpleTexture extends Texture {
		public final ImmutableMap<String, String> locations;

		public SimpleTexture(ImmutableMap<String, String> locations) {
			this.locations = locations;
		}
	}

	private static class KindTexture extends Texture {
		public final ImmutableMap<String, SimpleTexture> kindLocations;

		public KindTexture(ImmutableMap<String, SimpleTexture> kindLocations) {
			this.kindLocations = kindLocations;
		}
	}

}

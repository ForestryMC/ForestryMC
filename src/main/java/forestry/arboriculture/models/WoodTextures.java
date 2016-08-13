package forestry.arboriculture.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;

import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.core.render.FontColour;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class WoodTextures {
	
	public static final Gson GSON = new GsonBuilder().create();
	protected static final Map<String, Texture> textures = new HashMap<>();
	
	public static void deserializeFile(IResourceManager resourceManager){
		try {
			InputStream stream = resourceManager.getResource(new ResourceLocation("forestry:textures/woodTextures.json")).getInputStream();
			if(stream != null){
				JsonReader reader = null;
				try {
					reader = new JsonReader(new BufferedReader(new InputStreamReader(stream)));
					JsonElement json = Streams.parse(reader);
					if(json.isJsonObject()){
						for(Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()){
							JsonElement ele = entry.getValue();
							if(ele.isJsonObject()){
								textures.put(entry.getKey(), loadTexture(ele.getAsJsonObject()));
							}else if(ele.isJsonArray()){
								ImmutableMap.Builder<String, SimpleTexture> locations = new ImmutableMap.Builder<>();
								for(JsonElement eleEntry : ele.getAsJsonArray()){
									if(eleEntry.isJsonObject()){
										JsonObject obj = eleEntry.getAsJsonObject();
										if(!obj.has("kind") || !obj.get("kind").isJsonPrimitive() || !obj.get("kind").getAsJsonPrimitive().isString()){
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
				}finally{
					if(reader != null){
						reader.close();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	private static SimpleTexture loadTexture(JsonObject obj){
		ImmutableMap.Builder<String, String> locations = new ImmutableMap.Builder<>();
		for(Entry<String, JsonElement> eleEntry : obj.entrySet()){
			JsonElement e = eleEntry.getValue();
			if(e.isJsonPrimitive() && e.getAsJsonPrimitive().isString()){
				locations.put(eleEntry.getKey(), e.getAsString());
			}
		}
		return new SimpleTexture(locations.build());
	}
	
	public static ImmutableMap<String, String> getLocations(IWoodType woodType, WoodBlockKind blockKind){
		if(woodType != null && blockKind != null && woodType.getName() != null){
			String kindName = blockKind.toString();
			Texture kindTex = textures.get(blockKind);
			Texture tex = textures.get(woodType.getName().toLowerCase(Locale.ENGLISH));
			Map<String, String> locations = new HashMap<>();
			if(kindTex != null && kindTex instanceof SimpleTexture){
				locations.putAll(((SimpleTexture)kindTex).locations);
			}
			if(tex != null){
				if(tex instanceof SimpleTexture){
					locations.putAll(((SimpleTexture) tex).locations);
				}else{
					SimpleTexture kind = ((KindTexture)tex).kindLocations.get(kindName);
					locations.putAll(kind.locations);
				}
			}
			ImmutableMap.Builder<String, String> builder = new ImmutableMap.Builder<>();
			for(Entry<String, String> location : locations.entrySet()){
				String texture = location.getValue();
				if(texture.equals("plank")){
					texture = woodType.getPlankTexture();
				}else if(texture.equals("bark")){
					texture = woodType.getBarkTexture();
				}else if(texture.equals("heart")){
					texture = woodType.getHeartTexture();
				}else if(texture.equals("doorUp")){
					texture = woodType.getDoorUpperTexture();
				}else if(texture.equals("doorLow")){
					texture = woodType.getDoorLowerTexture();
				}else{
					builder.put(location.getKey(), texture.replace("%woodType", woodType.getName().toLowerCase(Locale.ENGLISH)));
					continue;
				}
				builder.put(location.getKey(), texture);
			}
			return builder.build();
		}
		return ImmutableMap.of();
	}
	
	private static class Texture{
		
	}
	
	private static class SimpleTexture extends Texture{
		public final ImmutableMap<String, String> locations;
		
		public SimpleTexture(ImmutableMap<String, String> locations) {
			this.locations = locations;
		}
	}
	
	private static class KindTexture extends Texture{
		public final ImmutableMap<String, SimpleTexture> kindLocations;
		
		public KindTexture(ImmutableMap<String, SimpleTexture> kindLocations) {
			this.kindLocations = kindLocations;
		}
	}

}

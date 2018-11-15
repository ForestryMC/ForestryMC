/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.arboriculture.models;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.arboriculture.models.WoodTexture.SimpleTexture;
import forestry.arboriculture.models.WoodTexture.TextureMap;
import forestry.core.utils.Log;

@SideOnly(Side.CLIENT)
public class WoodTextureManager {
	protected static final Map<String, WoodTexture> WOOD_TEXTURES = new HashMap<>();
	public static final String KIND_KEY = "kind";
	public static final String LOCATION = "/assets/forestry/textures/wood_textures.json";

	public static void parseFile() {
		try (InputStream stream = WoodTextureManager.class.getResourceAsStream(LOCATION)) {
			if (stream == null) {
				return;
			}
			try (JsonReader reader = new JsonReader(new BufferedReader(new InputStreamReader(stream)))) {
				JsonElement json = Streams.parse(reader);
				if (json.isJsonObject()) {
					JsonObject jsonObject = json.getAsJsonObject();
					for (Entry<String, JsonElement> jsonEntry : jsonObject.entrySet()) {
						JsonElement element = jsonEntry.getValue();
						if (element.isJsonObject()) {
							addSingleTexture(jsonEntry.getKey(), element.getAsJsonObject());
						} else if (element.isJsonArray()) {
							addTextureMap(jsonEntry.getKey(), element.getAsJsonArray());
						}
					}
				}
			}
		} catch (IOException e) {
			if (!(e instanceof FileNotFoundException)) {
				Log.error("Error finding wood textures.", e);
			}
		}
	}

	private static void addSingleTexture(String key, JsonObject object) {
		WOOD_TEXTURES.put(key, createSimpleTexture(object));
	}

	private static void addTextureMap(String key, JsonArray jsonArray) {
		ImmutableMap.Builder<String, SimpleTexture> textures = new ImmutableMap.Builder<>();
		for (JsonElement elementEntry : jsonArray) {
			if (elementEntry.isJsonObject()) {
				JsonObject obj = elementEntry.getAsJsonObject();
				String kind = getKind(obj);
				if (kind != null) {
					textures.put(kind, createSimpleTexture(obj));
				}
			}
		}
		WOOD_TEXTURES.put(key, new TextureMap(textures.build()));
	}

	@Nullable
	private static String getKind(JsonObject obj) {
		if (!obj.has(KIND_KEY)) {
			return null;
		}
		JsonElement element = obj.get(KIND_KEY);
		if (!element.isJsonPrimitive()) {
			return null;
		}
		JsonPrimitive primative = element.getAsJsonPrimitive();
		if (!primative.isString()) {
			return null;
		}
		return primative.getAsString();
	}

	private static SimpleTexture createSimpleTexture(JsonObject obj) {
		ImmutableMap.Builder<String, String> locations = new ImmutableMap.Builder<>();
		for (Entry<String, JsonElement> entry : obj.entrySet()) {
			JsonElement element = entry.getValue();
			if (element.isJsonPrimitive()) {
				JsonPrimitive primitive = element.getAsJsonPrimitive();
				if (primitive.isString()) {
					locations.put(entry.getKey(), primitive.getAsString());
				}
			}
		}
		return new SimpleTexture(locations.build());
	}

	@Nullable
	private static WoodTexture getKindTexture(WoodBlockKind blockKind) {
		String kindName = blockKind.getName();
		return WOOD_TEXTURES.get(kindName);
	}

	@Nullable
	private static WoodTexture getTexture(IWoodType woodType) {
		String woodName = woodType.getName();
		woodName = woodName.toLowerCase(Locale.ENGLISH);
		return WOOD_TEXTURES.get(woodName);
	}

	public static ImmutableMap<String, String> getTextures(IWoodType woodType, WoodBlockKind blockKind) {
		String kindName = blockKind.toString();
		WoodTexture kindTexture = getKindTexture(blockKind);
		WoodTexture tex = getTexture(woodType);
		Map<String, String> locations = new HashMap<>();
		if (kindTexture != null && kindTexture instanceof SimpleTexture) {
			SimpleTexture texture = (SimpleTexture) kindTexture;
			locations.putAll(texture.getLocations(kindName));
		}
		if (tex != null) {
			locations.putAll(tex.getLocations(kindName));
		}
		ImmutableMap.Builder<String, String> texuresBuilder = new ImmutableMap.Builder<>();
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
					texuresBuilder.put(location.getKey(), texture.replace("%woodType", woodType.getName().toLowerCase(Locale.ENGLISH)));
					continue;
			}
			texuresBuilder.put(location.getKey(), texture);
		}
		return texuresBuilder.build();
	}

}

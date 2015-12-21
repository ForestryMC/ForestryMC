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
package forestry.core.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import forestry.api.core.ForestryAPI;
import forestry.api.core.ISpriteProvider;
import forestry.api.core.ITextureManager;

@SideOnly(Side.CLIENT)
public class TextureManager implements ITextureManager {

	private static final TextureManager instance = new TextureManager();

	private static final Map<String, TextureAtlasSprite> defaultSprites = new HashMap<>();
	private static final DefaultIconProvider defaultIconProvider = new DefaultIconProvider();
	private static final List<ISpriteProvider> iconProviders = new ArrayList<>();

	static {
		ForestryAPI.textureManager = instance;
		instance.registerSpriteProvider(defaultIconProvider);
	}

	public static TextureManager getInstance() {
		return instance;
	}

	private TextureManager() {
	}

	public static void initDefaultSprites() {
		String[] defaultIconNames = new String[]{"habitats/desert", "habitats/end", "habitats/forest", "habitats/hills", "habitats/jungle", "habitats/mushroom",
				"habitats/nether", "habitats/ocean", "habitats/plains", "habitats/snow", "habitats/swamp", "habitats/taiga", "misc/access.private",
				"misc/access.viewable", "misc/access.shared", "misc/energy", "misc/hint",
				"analyzer/anything", "analyzer/bee", "analyzer/cave", "analyzer/closed", "analyzer/drone", "analyzer/flyer",
				"analyzer/item", "analyzer/nocturnal", "analyzer/princess", "analyzer/pure_breed", "analyzer/pure_cave",
				"analyzer/pure_flyer", "analyzer/pure_nocturnal", "analyzer/queen",
				"particles/swarm_bee", "errors/errored", "errors/unknown",
				"slots/blocked", "slots/blocked_2", "slots/liquid", "slots/container", "slots/locked",
				"mail/carrier.player", "mail/carrier.trader"};
		for (String str : defaultIconNames) {
			TextureAtlasSprite icon = registerSprite("items/" + str);
			defaultSprites.put(str, icon);
		}
	}

	public static TextureAtlasSprite registerSprite(String identifier) {
		TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
		return map.registerSprite(new ResourceLocation("forestry:" + identifier));
	}
	
	public TextureAtlasSprite getSprite(String modID, String modifier, String identifier) {
		TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
		if (map.getAtlasSprite(new ResourceLocation(modID + ":" + modifier + "/" + identifier).toString()) == map.getMissingSprite())
			return map.registerSprite(new ResourceLocation(modID + ":" + modifier + "/" + identifier));
		return map.getAtlasSprite(new ResourceLocation(modID + ":" + modifier + "/" + identifier).toString());
	}

	public TextureAtlasSprite getSprite(String modID, String identifier) {
		TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
		if (map.getAtlasSprite(new ResourceLocation(modID + ":" + identifier).toString()) == map.getMissingSprite())
			return map.registerSprite(new ResourceLocation(modID + ":" + identifier));
		return map.getAtlasSprite(new ResourceLocation(modID + ":" + identifier).toString());
	}

	public static TextureAtlasSprite registerTexUID(short uid, String identifier) {
		TextureAtlasSprite texture = registerSprite(identifier);
		defaultIconProvider.addTexture(uid, texture);
		return texture;
	}

	@Override
	public TextureAtlasSprite getDefault(String ident) {
		return defaultSprites.get(ident);
	}

	@Override
	public void registerSpriteProvider(ISpriteProvider provider) {
		iconProviders.add(provider);
	}

	@Override
	public TextureAtlasSprite getSprite(short texUID) {
		if (texUID < 0) {
			return null;
		}

		for (ISpriteProvider provider : iconProviders) {
			TextureAtlasSprite texture = provider.getSprite(texUID);
			if (texture != null) {
				return texture;
			}
		}

		return null;
	}

	private static class DefaultIconProvider implements ISpriteProvider {
		private final Map<Short, TextureAtlasSprite> sprites = new HashMap<>();

		public void addTexture(short texUID, TextureAtlasSprite texture) {
			sprites.put(texUID, texture);
		}

		@Override
		public TextureAtlasSprite getSprite(short texUID) {
			return sprites.get(texUID);
		}

		@Override
		public void registerSprites() {

		}
	}
}

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

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.ForestryAPI;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.ISpriteProvider;
import forestry.api.core.ISpriteRegister;
import forestry.api.core.IStateMapperRegister;
import forestry.api.core.ITextureManager;
import forestry.core.proxy.Proxies;

@SideOnly(Side.CLIENT)
public class TextureManager implements ITextureManager {

	private static final TextureManager instance = new TextureManager();

	private static final Map<String, TextureAtlasSprite> defaultIcons = new HashMap<>();
	private static final DefaultSpriteProvider defaultIconProvider = new DefaultSpriteProvider();
	private static final List<ISpriteProvider> iconProviders = new ArrayList<>();

	static {
		ForestryAPI.textureManager = instance;
		instance.registerIconProvider(defaultIconProvider);
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
				"errors/errored", "errors/unknown",
				"slots/blocked", "slots/blocked_2", "slots/liquid", "slots/container", "slots/locked",
				"mail/carrier.player", "mail/carrier.trader"};
		for (String str : defaultIconNames) {
			TextureAtlasSprite icon = registerSprite("gui/" + str);
			defaultIcons.put(str, icon);
		}
	}

	public static TextureAtlasSprite registerSprite(String identifier) {
		TextureMap map = Proxies.common.getClientInstance().getTextureMapBlocks();
		return map.registerSprite(new ResourceLocation("forestry:" + identifier));
	}
	
	public static TextureAtlasSprite registerSprite(TextureAtlasSprite sprite, String identifier) {
		TextureMap map = Proxies.common.getClientInstance().getTextureMapBlocks();
		if(map.setTextureEntry(identifier, sprite)){
			return sprite;
		}else{
			return map.getTextureExtry(identifier);
		}
	}
	
	public static TextureAtlasSprite getSprite(String modID, String identifier) {
		TextureMap map = Proxies.common.getClientInstance().getTextureMapBlocks();
		if (map.getAtlasSprite(new ResourceLocation(modID + ":" + identifier).toString()) == map.getMissingSprite()) {
			return map.registerSprite(new ResourceLocation(modID + ":" + identifier));
		}
		return map.getAtlasSprite(new ResourceLocation(modID + ":" + identifier).toString());
	}
	
	public static TextureAtlasSprite getSprite(String modID, String modifier, String identifier) {
		TextureMap map = Proxies.common.getClientInstance().getTextureMapBlocks();
		if (map.getAtlasSprite(new ResourceLocation(modID + ":" + modifier + "/" + identifier).toString()) == map.getMissingSprite()) {
			return map.registerSprite(new ResourceLocation(modID + ":" + modifier + "/" + identifier));
		}
		return map.getAtlasSprite(new ResourceLocation(modID + ":" + modifier + "/" + identifier).toString());
	}

	public static TextureAtlasSprite registerSpriteUID(short uid, String identifier) {
		TextureAtlasSprite texture = registerSprite(identifier);
		defaultIconProvider.addTexture(uid, texture);
		return texture;
	}

	@Override
	public TextureAtlasSprite getDefault(String ident) {
		return defaultIcons.get(ident);
	}

	@Override
	public void registerIconProvider(ISpriteProvider provider) {
		iconProviders.add(provider);
	}

	@Override
	public TextureAtlasSprite getSprite(short texUID) {
		if (texUID < 0) {
			return null;
		}

		for (ISpriteProvider provider : iconProviders) {
			TextureAtlasSprite icon = provider.getSprite(texUID);
			if (icon != null) {
				return icon;
			}
		}

		return null;
	}
	
	@SideOnly(Side.CLIENT)
	public static void registerSprites() {
		for (Block block : GameData.getBlockRegistry()) {
			if (block instanceof ISpriteRegister) {
				((ISpriteRegister) block).registerSprites(getInstance());
			}
		}
		for (Item item : GameData.getItemRegistry()) {
			if (item instanceof ISpriteRegister) {
				((ISpriteRegister) item).registerSprites(getInstance());
			}
		}
	}

	private static class DefaultSpriteProvider implements ISpriteProvider {
		private final Map<Short, TextureAtlasSprite> textures = new HashMap<>();

		public void addTexture(short texUID, TextureAtlasSprite texture) {
			textures.put(texUID, texture);
		}

		@Override
		public TextureAtlasSprite getSprite(short texUID) {
			return textures.get(texUID);
		}

		@Override
		public void registerSprites() {

		}
	}
}

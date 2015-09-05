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

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.core.ForestryAPI;
import forestry.api.core.ISpriteProvider;
import forestry.api.core.ITextureManager;
import forestry.api.core.sprite.ISprite;
import forestry.api.core.sprite.Sprite;

@SideOnly(Side.CLIENT)
public class TextureManager implements ITextureManager {

	private static TextureManager instance;

	public static TextureManager getInstance() {
		if (instance == null) {
			instance = new TextureManager();
			ForestryAPI.textureManager = instance;
		}

		return instance;
	}

	private final HashMap<String, ISprite> defaultIcons = new HashMap<String, ISprite>();

	private final ISprite[] textures = new ISprite[2048];

	private final ArrayList<ISpriteProvider> iconProvider = new ArrayList<ISpriteProvider>();

	private TextureManager() {
	}

	public void initDefaultIcons() {
		String[] defaultIconNames = new String[]{"habitats/desert", "habitats/end", "habitats/forest", "habitats/hills", "habitats/jungle", "habitats/mushroom",
				"habitats/nether", "habitats/ocean", "habitats/plains", "habitats/snow", "habitats/swamp", "habitats/taiga", "misc/access.private",
				"misc/access.viewable", "misc/access.shared", "misc/energy", "misc/hint",
				"analyzer/anything", "analyzer/bee", "analyzer/cave", "analyzer/closed", "analyzer/drone", "analyzer/flyer",
				"analyzer/item", "analyzer/nocturnal", "analyzer/princess", "analyzer/pure_breed", "analyzer/pure_cave",
				"analyzer/pure_flyer", "analyzer/pure_nocturnal", "analyzer/queen",
				"particles/swarm_bee", "errors/errored",
				"slots/blocked", "slots/blocked_2", "slots/liquid", "slots/container", "slots/locked",
				"mail/carrier.player", "mail/carrier.trader"};
		for (String str : defaultIconNames) {
			defaultIcons.put(str, registerTex("items", str));
		}
	}

	@Override
	public ISprite getDefault(String ident) {
		return defaultIcons.get(ident);
	}

	public ISprite registerTex(String modifier, String identifier) {
		TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
		return new Sprite(map.registerSprite(new ResourceLocation("forestry:" + modifier + "/" + identifier)));
	}
	
	public ISprite registerTex(String modID, String modifier, String identifier) {
		TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
		if(map.getAtlasSprite(new ResourceLocation(modID + ":" + modifier + "/" + identifier).toString()) == map.getMissingSprite())
			return new Sprite(map.registerSprite(new ResourceLocation(modID + ":" + modifier + "/" + identifier)));
		return new Sprite(map.getAtlasSprite(new ResourceLocation(modID + ":" + modifier + "/" + identifier).toString()));
		//return new Sprite(map.registerSprite(new ResourceLocation(modID + ":" + modifier + "/" + identifier)));
	}
	
	public ResourceLocation getRL(String modifier, String identifier) {
		return new ResourceLocation("forestry:" + modifier + "/" + identifier + ".png");
	}


	public ISprite registerTexUID(short uid, String modifier, String identifier) {
		return setTexUID(uid, registerTex(modifier, identifier));
	}

	public ISprite setTexUID(short uid, ISprite texture) {
		textures[uid] = texture;
		return texture;
	}

	@Override
	public void registerIconProvider(ISpriteProvider provider) {
		iconProvider.add(provider);
	}

	@Override
	public ISprite getIcon(short texUID) {
		if (texUID < 0) {
			return null;
		}
		if (texUID < textures.length) {
			return textures[texUID];
		}

		for (ISpriteProvider provider : iconProvider) {
			ISprite retr = provider.getIcon(texUID);
			if (retr != null) {
				return retr;
			}
		}

		return null;
	}
}

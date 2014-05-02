/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.render;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.core.ForestryAPI;
import forestry.api.core.IIconProvider;
import forestry.api.core.ITextureManager;

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

	String[] defaultIconNames = new String[] { "habitats/desert", "habitats/end", "habitats/forest", "habitats/hills", "habitats/jungle", "habitats/mushroom",
			"habitats/nether", "habitats/ocean", "habitats/plains", "habitats/snow", "habitats/swamp", "habitats/taiga", "misc/access.private",
			"misc/access.viewable", "misc/access.shared", "misc/empty", "misc/energy", "misc/hint", "misc/liquid",
			"analyzer/anything", "analyzer/bee", "analyzer/cave", "analyzer/closed", "analyzer/drone", "analyzer/flyer",
			"analyzer/item", "analyzer/nocturnal", "analyzer/princess", "analyzer/pure_breed", "analyzer/pure_cave",
			"analyzer/pure_flyer", "analyzer/pure_nocturnal", "analyzer/queen", "analyzer/natural",
			"particles/ember", "particles/flame", "particles/poison", "particles/snow", "particles/swarm_bee", "errors/errored",
			"slots/blocked", "slots/blocked_2", "slots/liquid", "slots/container", "slots/locked",
			"mail/carrier.player", "mail/carrier.trader" };
	HashMap<String, IIcon> defaultIcons = new HashMap<String, IIcon>();

	public void initDefaultIcons(IIconRegister register) {
		for (String str : defaultIconNames)
			defaultIcons.put(str, TextureManager.getInstance().registerTex(register, str));
	}

	public IIcon getDefault(String ident) {
		if (defaultIcons.containsKey(ident))
			return defaultIcons.get(ident);
		else
			return null;
	}

	//public TextureMap terrainMap;
	//public TextureMap itemMap;

	IIcon[] textures = new IIcon[2048];

	ArrayList<IIconProvider> iconProvider = new ArrayList<IIconProvider>();

	public TextureManager() {
		//this.terrainMap = textureMap;
		//this.itemMap = itemMap;
	}

	public IIcon registerTex(IIconRegister register, String identifier) {
		return register.registerIcon("forestry:" + identifier);
	}

	public IIcon registerTexUID(IIconRegister register, short uid, String identifier) {
		return setTexUID(uid, registerTex(register, identifier));
	}

	public IIcon setTexUID(short uid, IIcon texture) {
		textures[uid] = texture;
		return texture;
	}

	@Override
	public void registerIconProvider(IIconProvider provider) {
		iconProvider.add(provider);
	}

	@Override
	public IIcon getIcon(short texUID) {
		if (texUID < textures.length)
			return textures[texUID];

		for (IIconProvider provider : iconProvider) {
			IIcon retr = provider.getIcon(texUID);
			if (retr != null)
				return retr;
		}

		return null;
	}
}

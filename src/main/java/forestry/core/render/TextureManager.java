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

	private final HashMap<String, IIcon> defaultIcons = new HashMap<>();

	private final IIcon[] textures = new IIcon[2048];

	private final ArrayList<IIconProvider> iconProvider = new ArrayList<>();

	private TextureManager() {
	}

	public void initDefaultIcons(IIconRegister register) {
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
			defaultIcons.put(str, registerTex(register, str));
		}
	}

	public IIcon getDefault(String ident) {
		return defaultIcons.get(ident);
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
		if (texUID < 0) {
			return null;
		}
		if (texUID < textures.length) {
			return textures[texUID];
		}

		for (IIconProvider provider : iconProvider) {
			IIcon retr = provider.getIcon(texUID);
			if (retr != null) {
				return retr;
			}
		}

		return null;
	}
}

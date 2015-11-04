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

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.core.ForestryAPI;
import forestry.api.core.IIconProvider;
import forestry.api.core.ITextureManager;

@SideOnly(Side.CLIENT)
public class TextureManager implements ITextureManager {

	private static final TextureManager instance = new TextureManager();

	private static final Map<String, IIcon> defaultIcons = new HashMap<>();
	private static final DefaultIconProvider defaultIconProvider = new DefaultIconProvider();
	private static final List<IIconProvider> iconProviders = new ArrayList<>();

	static {
		ForestryAPI.textureManager = instance;
		instance.registerIconProvider(defaultIconProvider);
	}

	public static TextureManager getInstance() {
		return instance;
	}

	private TextureManager() {
	}

	public static void initDefaultIcons(IIconRegister register) {
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
			IIcon icon = registerTex(register, str);
			defaultIcons.put(str, icon);
		}
	}

	public static IIcon registerTex(IIconRegister register, String identifier) {
		return register.registerIcon("forestry:" + identifier);
	}

	public static IIcon registerTexUID(IIconRegister register, short uid, String identifier) {
		IIcon texture = registerTex(register, identifier);
		defaultIconProvider.addTexture(uid, texture);
		return texture;
	}

	@Override
	public IIcon getDefault(String ident) {
		return defaultIcons.get(ident);
	}

	@Override
	public void registerIconProvider(IIconProvider provider) {
		iconProviders.add(provider);
	}

	@Override
	public IIcon getIcon(short texUID) {
		if (texUID < 0) {
			return null;
		}

		for (IIconProvider provider : iconProviders) {
			IIcon icon = provider.getIcon(texUID);
			if (icon != null) {
				return icon;
			}
		}

		return null;
	}

	private static class DefaultIconProvider implements IIconProvider {
		private final Map<Short, IIcon> textures = new HashMap<>();

		public void addTexture(short texUID, IIcon texture) {
			textures.put(texUID, texture);
		}

		@Override
		public IIcon getIcon(short texUID) {
			return textures.get(texUID);
		}

		@Override
		public void registerIcons(IIconRegister register) {

		}
	}
}

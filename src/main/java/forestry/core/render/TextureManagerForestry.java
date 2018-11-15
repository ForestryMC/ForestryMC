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
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.ForestryAPI;
import forestry.api.core.ISpriteRegister;
import forestry.api.core.ITextureManager;
import forestry.core.config.Constants;

@SideOnly(Side.CLIENT)
public class TextureManagerForestry implements ITextureManager {
	public static final ResourceLocation LOCATION_FORESTRY_TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/atlas/gui");
	private static final TextureManagerForestry INSTANCE = new TextureManagerForestry();
	private final List<ISpriteRegister> spriteRegisters = new ArrayList<>();
	private final TextureMapForestry textureMap;

	static {
		ForestryAPI.textureManager = INSTANCE;
	}

	public static TextureManagerForestry getInstance() {
		return INSTANCE;
	}

	private TextureManagerForestry() {
		this.textureMap = new TextureMapForestry("textures");
	}

	public TextureMapForestry getTextureMap() {
		return textureMap;
	}

	public static void initDefaultSprites() {
		String[] defaultIconNames = new String[]{"habitats/desert", "habitats/end", "habitats/forest", "habitats/hills", "habitats/jungle",
			"habitats/mushroom", "habitats/nether", "habitats/ocean", "habitats/plains", "habitats/snow", "habitats/swamp", "habitats/taiga",
			"misc/access.shared", "misc/energy", "misc/hint",
			"analyzer/anything", "analyzer/bee", "analyzer/cave", "analyzer/closed", "analyzer/drone", "analyzer/flyer", "analyzer/item",
			"analyzer/nocturnal", "analyzer/princess", "analyzer/pure_breed", "analyzer/pure_cave", "analyzer/pure_flyer",
			"analyzer/pure_nocturnal", "analyzer/queen", "analyzer/tree", "analyzer/sapling", "analyzer/pollen", "analyzer/flutter",
			"analyzer/butterfly", "analyzer/serum", "analyzer/caterpillar", "analyzer/cocoon",
			"errors/errored", "errors/unknown",
			"slots/blocked", "slots/blocked_2", "slots/liquid", "slots/container", "slots/locked", "slots/cocoon", "slots/bee",
			"mail/carrier.player", "mail/carrier.trader"
		};
		for (String identifier : defaultIconNames) {
			ResourceLocation resourceLocation = getForestryGuiLocation(identifier);
			INSTANCE.registerGuiSprite(resourceLocation);
		}
	}

	private static ResourceLocation getForestryGuiLocation(String identifier) {
		return new ResourceLocation(Constants.MOD_ID, "gui/" + identifier);
	}

	public static TextureAtlasSprite registerSprite(ResourceLocation location) {
		TextureMap textureMap = Minecraft.getMinecraft().getTextureMapBlocks();
		return textureMap.registerSprite(location);
	}

	@Override
	public TextureAtlasSprite getDefault(String identifier) {
		ResourceLocation resourceLocation = getForestryGuiLocation(identifier);
		return getTextureMap().getAtlasSprite(resourceLocation.toString());
	}

	@Override
	public ResourceLocation getGuiTextureMap() {
		return LOCATION_FORESTRY_TEXTURE;
	}

	public void bindGuiTextureMap() {
		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
		ResourceLocation guiTextureMap = getGuiTextureMap();
		textureManager.bindTexture(guiTextureMap);
	}

	@Override
	public TextureAtlasSprite registerGuiSprite(ResourceLocation location) {
		return getTextureMap().registerSprite(location);
	}

	public void registerBlock(Block block) {
		if (block instanceof ISpriteRegister) {
			spriteRegisters.add((ISpriteRegister) block);
		}
	}

	public void registerItem(Item item) {
		if (item instanceof ISpriteRegister) {
			spriteRegisters.add((ISpriteRegister) item);
		}
	}

	@SideOnly(Side.CLIENT)
	public void registerSprites() {
		for (ISpriteRegister spriteRegister : spriteRegisters) {
			spriteRegister.registerSprites(getInstance());
		}
	}
}

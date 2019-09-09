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
import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.item.Item;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;

import forestry.api.core.ForestryAPI;
import forestry.api.core.ISpriteRegister;
import forestry.api.core.ITextureManager;
import forestry.core.config.Constants;

@OnlyIn(Dist.CLIENT)
public class TextureManagerForestry extends ReloadListener<AtlasTexture.SheetData> implements ITextureManager, AutoCloseable {
	public static final ResourceLocation LOCATION_FORESTRY_TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/atlas/gui.png");
	private static final TextureManagerForestry INSTANCE = new TextureManagerForestry();
	private final List<ISpriteRegister> spriteRegisters = new ArrayList<>();
	private final AtlasTexture textureMap;

	static {
		ForestryAPI.textureManager = INSTANCE;
	}

	public static TextureManagerForestry getInstance() {
		return INSTANCE;
	}

	private TextureManagerForestry() {
		this.textureMap = new AtlasTexture("textures");
	}

	public AtlasTexture getTextureMap() {
		return textureMap;
	}

	public static void initDefaultSprites(TextureStitchEvent.Pre event) {
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
			event.addSprite(resourceLocation);
		}
	}

	private static ResourceLocation getForestryGuiLocation(String identifier) {
		return new ResourceLocation(Constants.MOD_ID, "gui/" + identifier);
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
		TextureManager textureManager = Minecraft.getInstance().getTextureManager();
		ResourceLocation guiTextureMap = getGuiTextureMap();
		textureManager.bindTexture(guiTextureMap);
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

	@OnlyIn(Dist.CLIENT)
	public void registerSprites(TextureStitchEvent.Pre event) {
		for (ISpriteRegister spriteRegister : spriteRegisters) {
			spriteRegister.registerSprites(getInstance());
		}
	}

	@Override
	protected AtlasTexture.SheetData prepare(IResourceManager resourceManager, IProfiler profiler) {
		profiler.startTick();
		profiler.startSection("stitching");
		AtlasTexture.SheetData sheetData = textureMap.stitch(resourceManager, new LinkedList<>(), profiler);
		profiler.endSection();
		profiler.endTick();
		return sheetData;
	}

	protected void apply(AtlasTexture.SheetData sheetData, IResourceManager resourceManagerIn, IProfiler profilerIn) {
		profilerIn.startTick();
		profilerIn.startSection("upload");
		textureMap.upload(sheetData);
		profilerIn.endSection();
		profilerIn.endTick();
	}

	public void close() {
		textureMap.clear();
	}
}

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
package forestry.apiculture.proxy;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;

import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import forestry.apiculture.entities.ParticleSnow;
import forestry.apiculture.features.ApicultureBlocks;
import forestry.apiculture.features.ApicultureEntities;
import forestry.modules.IClientModuleHandler;

@OnlyIn(Dist.CLIENT)
public class ProxyApicultureClient extends ProxyApiculture implements IClientModuleHandler {

	@OnlyIn(Dist.CLIENT)
	@Nullable
	private static TextureAtlasSprite beeSprite;

	@OnlyIn(Dist.CLIENT)
	public static TextureAtlasSprite getBeeSprite() {
		Preconditions.checkNotNull(beeSprite, "Bee sprite has not been registered");
		return beeSprite;
	}

	@Override
	public void setupClient(FMLClientSetupEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(ApicultureEntities.APIARY_MINECART.entityType(), MinecartRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ApicultureEntities.BEE_HOUSE_MINECART.entityType(), MinecartRenderer::new);
		ApicultureBlocks.BEE_COMB.getBlocks().forEach((block) -> RenderTypeLookup.setRenderLayer(block, RenderType.cutout()));
		RenderTypeLookup.setRenderLayer(ApicultureBlocks.CANDLE.block(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(ApicultureBlocks.CANDLE_WALL.block(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(ApicultureBlocks.STUMP.block(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(ApicultureBlocks.STUMP_WALL.block(), RenderType.cutout());
	}

	@Override
	public void registerSprites(TextureStitchEvent.Pre event) {
		if (!event.getMap().location().equals(AtlasTexture.LOCATION_PARTICLES)) {
			return;
		}
		for (int i = 0; i < ParticleSnow.sprites.length; i++) {
			event.addSprite(new ResourceLocation("forestry:entity/particles/snow." + (i + 1)));
		}
		event.addSprite(new ResourceLocation("forestry:entity/particles/swarm_bee"));
	}

	@Override
	public void handleSprites(TextureStitchEvent.Post event) {
		AtlasTexture map = event.getMap();
		if (!map.location().equals(AtlasTexture.LOCATION_PARTICLES)) {
			return;
		}
		for (int i = 0; i < ParticleSnow.sprites.length; i++) {
			ParticleSnow.sprites[i] = map.getSprite(new ResourceLocation("forestry:entity/particles/snow." + (i + 1)));
		}
		beeSprite = map.getSprite(new ResourceLocation("forestry:entity/particles/swarm_bee"));
	}
}

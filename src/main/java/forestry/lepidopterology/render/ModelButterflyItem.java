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
package forestry.lepidopterology.render;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

import java.io.IOException;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.client.model.ISmartItemModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;

import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.IButterfly;

public class ModelButterflyItem implements ISmartItemModel {

	public IRetexturableModel modelButterfly;

	@Override
	public List<BakedQuad> getFaceQuads(EnumFacing p_177551_1_) {
		return null;
	}

	@Override
	public List<BakedQuad> getGeneralQuads() {
		return null;
	}

	@Override
	public boolean isAmbientOcclusion() {
		return true;
	}

	@Override
	public boolean isGui3d() {
		return true;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}
	
	@Override
	public TextureAtlasSprite getParticleTexture() {
		return null;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return null;
	}

	@Override
	public IBakedModel handleItemState(ItemStack item) {
		if (modelButterfly == null) {
			try {
				modelButterfly = (IRetexturableModel) ModelLoaderRegistry.getModel(new ModelResourceLocation("forestry:butterflyGE", "inventory"));
			} catch (IOException e) {
				return null;
			}
			if (modelButterfly == null) {
				return null;
			}
		}
		IButterfly butterfly = ButterflyManager.butterflyRoot.getMember(item);
		if (butterfly == null) {
			butterfly = ButterflyManager.butterflyRoot.templateAsIndividual(ButterflyManager.butterflyRoot.getDefaultTemplate());
		}
		return bakeModel(butterfly);
	}

	public IBakedModel bakeModel(IButterfly butterfly) {
		Function<ResourceLocation, TextureAtlasSprite> textureGetter = new ButterflyTextureGetter();
		ImmutableMap.Builder<String, String> textures = ImmutableMap.builder();
		textures.put("butterfly", butterfly.getGenome().getSecondary().getEntityTexture().replace(".png", "").replace("textures/", "").replace("entity", "items"));
		modelButterfly = (IRetexturableModel) modelButterfly.retexture(textures.build());
		return modelButterfly.bake(ModelRotation.X0_Y0, DefaultVertexFormats.ITEM, textureGetter);
	}

	private static class ButterflyTextureGetter implements Function<ResourceLocation, TextureAtlasSprite> {
		@Override
		public TextureAtlasSprite apply(ResourceLocation location) {
			return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
		}
	}
}

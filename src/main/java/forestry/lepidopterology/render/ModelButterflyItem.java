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
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.IButterfly;
import forestry.core.models.BlankItemModel;
import forestry.core.models.DefaultTextureGetter;
import forestry.core.models.TRSRBakedModel;

@SideOnly(Side.CLIENT)
public class ModelButterflyItem extends BlankItemModel {

	@SideOnly(Side.CLIENT)
	private IRetexturableModel modelButterfly;

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
	
	private Function<ResourceLocation, TextureAtlasSprite> textureGetter = new DefaultTextureGetter();

	private IBakedModel bakeModel(IButterfly butterfly) {
		ImmutableMap.Builder<String, String> textures = ImmutableMap.builder();
		textures.put("butterfly", butterfly.getGenome().getSecondary().getItemTexture());
		modelButterfly = (IRetexturableModel) modelButterfly.retexture(textures.build());
		return new TRSRBakedModel(modelButterfly.bake(ModelRotation.X0_Y0, DefaultVertexFormats.ITEM, textureGetter), -0.03125F, 0.0F, -0.03125F, butterfly.getSize() * 1.5F);
	}
}

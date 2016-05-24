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

import java.util.Collections;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.ModelProcessingHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.IButterfly;
import forestry.core.models.BlankItemModel;
import forestry.core.models.DefaultTextureGetter;
import forestry.core.models.TRSRBakedModel;

@SideOnly(Side.CLIENT)
public class ModelButterflyItem extends BlankItemModel {
	private final Function<ResourceLocation, TextureAtlasSprite> textureGetter = new DefaultTextureGetter();
	@SideOnly(Side.CLIENT)
	private IModel modelButterfly;

	@Override
	protected ItemOverrideList createOverrides() {
		return new ButterflyItemOverrideList();
	}

	private IBakedModel bakeModel(IButterfly butterfly) {
		ImmutableMap.Builder<String, String> textures = ImmutableMap.builder();
		textures.put("butterfly", butterfly.getGenome().getSecondary().getItemTexture());
		IModel retexturedModel =  ModelProcessingHelper.retexture(modelButterfly, textures.build());
		return new TRSRBakedModel(retexturedModel.bake(ModelRotation.X0_Y0, DefaultVertexFormats.ITEM, textureGetter), -0.03125F, 0.0F, -0.03125F, butterfly.getSize() * 1.5F);
	}

	private class ButterflyItemOverrideList extends ItemOverrideList {
		public ButterflyItemOverrideList() {
			super(Collections.emptyList());
		}

		@Override
		public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
			if (modelButterfly == null) {
				try {
					modelButterfly = ModelLoaderRegistry.getModel(new ResourceLocation("forestry:item/butterflyGE"));
				} catch (Exception e) {
					return null;
				}
				if (modelButterfly == null) {
					return null;
				}
			}
			IButterfly butterfly = ButterflyManager.butterflyRoot.getMember(stack);
			if (butterfly == null) {
				butterfly = ButterflyManager.butterflyRoot.templateAsIndividual(ButterflyManager.butterflyRoot.getDefaultTemplate());
			}
			return bakeModel(butterfly);
		}
	}
}

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

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import com.mojang.blaze3d.vertex.PoseStack;

import forestry.lepidopterology.entities.EntityButterfly;

public class ButterflyEntityRenderer extends MobRenderer<EntityButterfly, ButterflyModel> {

	public ButterflyEntityRenderer(EntityRenderDispatcher manager) {
		super(manager, new ButterflyModel(), 0.25f);
	}


	@Override
	public void render(EntityButterfly entity, float entityYaw, float partialTickTime, PoseStack transform, MultiBufferSource buffer, int packedLight) {
		if (!entity.isRenderable()) {
			return;
		}

		model.setScale(entity.getSize());
		super.render(entity, entityYaw, partialTickTime, transform, buffer, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(EntityButterfly entity) {
		return entity.getTexture();
	}

	@Override
	protected float getBob(EntityButterfly entity, float partialTickTime) {
		return entity.getWingFlap(partialTickTime);
	}

}

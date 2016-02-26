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

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.util.ResourceLocation;

import forestry.lepidopterology.entities.EntityButterfly;

public class RenderButterflyEntity extends RenderLiving<EntityButterfly> {

	private final ModelButterfly butterflyModel;

	public RenderButterflyEntity() {
		super(Minecraft.getMinecraft().getRenderManager(), new ModelButterfly(), 0.25f);
		butterflyModel = (ModelButterfly) mainModel;
	}

	private void renderButterfly(EntityButterfly entity, double x, double y, double z, float light, float partialTickTime) {
		if (!entity.isRenderable()) {
			return;
		}

		butterflyModel.setScale(entity.getSize());
		super.doRender(entity, x, y, z, light, partialTickTime);
	}
	
	@Override
	public void doRender(EntityButterfly entity, double x, double y, double z, float entityYaw, float partialTickTime) {
		this.renderButterfly(entity, x, y, z, entityYaw, partialTickTime);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityButterfly entity) {
		return entity.getTexture();
	}

	@Override
	protected float handleRotationFloat(EntityButterfly entity, float partialTickTime) {
		return entity.getWingFlap(partialTickTime);
	}

}

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

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import forestry.lepidopterology.entities.EntityButterfly;

public class RenderButterflyEntity extends RenderLiving {

	private final ModelButterfly butterflyModel;

	public RenderButterflyEntity() {
		super(new ModelButterfly(), 0.25f);
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
	public void doRender(Entity entity, double x, double y, double z, float light, float partialTickTime) {
		this.renderButterfly((EntityButterfly) entity, x, y, z, light, partialTickTime);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return ((EntityButterfly) entity).getTexture();
	}

	@Override
	protected float handleRotationFloat(EntityLivingBase entity, float partialTickTime) {
		return ((EntityButterfly) entity).getWingFlap(partialTickTime);
	}

}

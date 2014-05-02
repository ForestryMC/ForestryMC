/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.lepidopterology.render;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import forestry.lepidopterology.entities.EntityButterfly;

public class RenderButterfly extends RenderLiving {

	ModelButterfly butterflyModel;

	public RenderButterfly() {
		super(new ModelButterfly(), 0.25f);
		butterflyModel = (ModelButterfly)mainModel;
	}

	public void renderButterfly(EntityButterfly entity, double x, double y, double z, float par8, float par9) {
		if(!entity.isRenderable())
			return;

		butterflyModel.setScale(entity.getScale());
		super.doRender(entity, x, y, z, par8, entity.getWingFlap(par9));
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float par8, float par9) {
		this.renderButterfly((EntityButterfly)entity, x, y, z, par8, par9);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return ((EntityButterfly)entity).getTexture();
	}

}

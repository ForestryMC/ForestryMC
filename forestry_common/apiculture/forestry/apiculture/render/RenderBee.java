/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.apiculture.render;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import forestry.apiculture.entities.EntityBee;

public class RenderBee extends RenderLiving {

	ModelBee beeModel;

	public RenderBee() {
		super(new ModelBee(), 0.15f);
		beeModel = (ModelBee)mainModel;
	}

	public void renderBee(EntityBee entity, double posX, double posY, double posZ, float par8, float par9) {
		beeModel.setType(entity.getType());
		this.doRender(entity, posX, posY, posZ, par8, par9);
	}

	@Override
	public void doRender(Entity entity, double posX, double posY, double posZ, float par8, float par9) {
		renderBee((EntityBee) entity, posX, posY, posZ, par8, par9);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return ((EntityBee)entity).getTexture();
	}
}

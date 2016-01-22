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
package forestry.apiculture.render;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

import forestry.apiculture.entities.EntityBee;
import forestry.core.proxy.Proxies;

public class RenderBeeEntity extends RenderLiving {

	private final ModelBee beeModel;

	public RenderBeeEntity() {
		super(Proxies.common.getClientInstance().getRenderManager(), new ModelBee(), 0.15f);
		beeModel = (ModelBee) mainModel;
	}

	private void renderBee(EntityBee entity, double posX, double posY, double posZ, float par8, float par9) {
		beeModel.setType(entity.getType());
		this.doRender(entity, posX, posY, posZ, par8, par9);
	}
	
	@Override
	public void doRender(EntityLiving entity, double x, double y, double z, float entityYaw, float partialTicks) {
		renderBee((EntityBee) entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return ((EntityBee) entity).getTexture();
	}
}

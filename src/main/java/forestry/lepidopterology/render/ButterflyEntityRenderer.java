/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.lepidopterology.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import forestry.lepidopterology.entities.EntityButterfly;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class ButterflyEntityRenderer extends MobRenderer<EntityButterfly, ButterflyModel> {

    public ButterflyEntityRenderer(EntityRendererManager manager) {
        super(manager, new ButterflyModel(), 0.25f);
    }


    @Override
    public void render(
            EntityButterfly entity,
            float entityYaw,
            float partialTickTime,
            MatrixStack transform,
            IRenderTypeBuffer buffer,
            int packedLight
    ) {
        if (!entity.isRenderable()) {
            return;
        }

        entityModel.setScale(entity.getSize());
        super.render(entity, entityYaw, partialTickTime, transform, buffer, packedLight);
    }

    @Override
    public ResourceLocation getEntityTexture(EntityButterfly entity) {
        return entity.getTexture();
    }

    @Override
    protected float handleRotationFloat(EntityButterfly entity, float partialTickTime) {
        return entity.getWingFlap(partialTickTime);
    }

}

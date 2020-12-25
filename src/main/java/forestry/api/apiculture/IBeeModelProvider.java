/*
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 */
package forestry.api.apiculture;

import forestry.api.apiculture.genetics.EnumBeeType;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IBeeModelProvider {

    @OnlyIn(Dist.CLIENT)
    ModelResourceLocation getModel(EnumBeeType type);
}

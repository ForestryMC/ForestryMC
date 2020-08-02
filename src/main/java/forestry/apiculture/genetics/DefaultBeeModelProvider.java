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
package forestry.apiculture.genetics;

import java.util.EnumMap;

import net.minecraft.client.renderer.model.ModelResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.apiculture.IBeeModelProvider;
import forestry.api.apiculture.genetics.EnumBeeType;

public class DefaultBeeModelProvider implements IBeeModelProvider {

    @OnlyIn(Dist.CLIENT)
    private static final EnumMap<EnumBeeType, ModelResourceLocation> models = new EnumMap<>(EnumBeeType.class);
    public static final String MODEL_DIR = "bees/default/";

    public static final DefaultBeeModelProvider instance = new DefaultBeeModelProvider();

    private DefaultBeeModelProvider() {

    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ModelResourceLocation getModel(EnumBeeType type) {
        return models.get(type);
    }
}

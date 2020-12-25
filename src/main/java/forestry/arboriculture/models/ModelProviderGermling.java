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
package forestry.arboriculture.models;

import forestry.api.arboriculture.IGermlingModelProvider;
import forestry.api.arboriculture.ILeafSpriteProvider;
import forestry.api.arboriculture.genetics.EnumGermlingType;
import forestry.core.config.Constants;
import forestry.core.utils.StringUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ModelProviderGermling implements IGermlingModelProvider {

    private final ILeafSpriteProvider leafSpriteProvider;
    private final ResourceLocation itemModel;
    private final ResourceLocation blockModel;

    public ModelProviderGermling(String uid, ILeafSpriteProvider leafSpriteProvider) {
        String name = StringUtil.camelCaseToUnderscores(uid);
        this.leafSpriteProvider = leafSpriteProvider;
        itemModel = new ResourceLocation(Constants.MOD_ID, "germlings/sapling." + name);
        blockModel = new ResourceLocation(Constants.MOD_ID, "block/germlings/sapling." + name);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getItemModel() {
        return itemModel;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getBlockModel() {
        return blockModel;
    }

    @Override
    public int getSpriteColor(EnumGermlingType type, int renderPass) {
        if (type == EnumGermlingType.POLLEN) {
            return leafSpriteProvider.getColor(false);
        } else {
            return 0xFFFFFF;
        }
    }
}

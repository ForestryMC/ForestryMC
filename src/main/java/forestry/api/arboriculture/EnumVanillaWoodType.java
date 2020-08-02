/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import java.util.Locale;
import java.util.Random;

public enum EnumVanillaWoodType implements IWoodType {
    //TODO - think this should be a tag?
    OAK(),
    SPRUCE(),
    BIRCH(),
    JUNGLE(),
    ACACIA(),
    DARK_OAK();

    public static final EnumVanillaWoodType[] VALUES = values();

    public static EnumVanillaWoodType getRandom(Random random) {
        return VALUES[random.nextInt(VALUES.length)];
    }

    EnumVanillaWoodType() {

    }

    //	public BlockPlanks.EnumType getVanillaType() {
    //		return vanillaType;
    //	}

    @Override
    public String toString() {
        return super.toString().toLowerCase(Locale.ENGLISH);
    }

    @Override
    public String getString() {
        return toString();
    }

    @Override
    public float getHardness() {
        return 2.0F;
    }
}

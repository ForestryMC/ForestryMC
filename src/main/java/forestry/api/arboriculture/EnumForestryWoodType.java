/*
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 */
package forestry.api.arboriculture;

import java.util.Locale;
import java.util.Random;

public enum EnumForestryWoodType implements IWoodType {
    LARCH,
    TEAK,
    ACACIA_DESERT,
    LIME,
    CHESTNUT,
    WENGE,
    BAOBAB,
    SEQUOIA(4.0f),

    KAPOK,
    EBONY,
    MAHOGANY,
    BALSA(1.0f),
    WILLOW,
    WALNUT,
    GREENHEART(7.5f),
    CHERRY,

    MAHOE,
    POPLAR,
    PALM,
    PAPAYA,
    PINE(3.0f),
    PLUM,
    MAPLE,
    CITRUS,

    GIGANTEUM(4.0f),
    IPE,
    PADAUK,
    COCOBOLO,
    ZEBRAWOOD;

    public static final float DEFAULT_HARDNESS = 2.0f;
    public static final EnumForestryWoodType[] VALUES = values();

    private final float hardness;

    EnumForestryWoodType() {
        this(DEFAULT_HARDNESS);
    }

    EnumForestryWoodType(float hardness) {
        this.hardness = hardness;
    }

    public static EnumForestryWoodType getRandom(Random random) {
        return VALUES[random.nextInt(VALUES.length)];
    }

    @Override
    public float getHardness() {
        return hardness;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase(Locale.ENGLISH);
    }

    @Override
    public String getString() {
        return toString();
    }
}

package forestry.apiculture.blocks;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum BlockBeeProductType implements IStringSerializable {
    BEE_COMBS,
    BEESWAX;

    public static final BlockBeeProductType[] VALUES = values();

    @Override
    public String getString() {
        return name().toLowerCase(Locale.ENGLISH);
    }

    public int getMeta() {
        return ordinal();
    }

}

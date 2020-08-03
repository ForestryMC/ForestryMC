package forestry.core.items;

import forestry.api.core.IItemSubtype;

import java.util.Locale;

public enum EnumCraftingMaterial implements IItemSubtype {
    PULSATING_DUST,
    PULSATING_MESH,
    SILK_WISP,
    WOVEN_SILK,
    DISSIPATION_CHARGE,
    ICE_SHARD,
    SCENTED_PANELING,
    CAMOUFLAGED_PANELING;

    public static final EnumCraftingMaterial[] VALUES = values();

    private final String name;

    EnumCraftingMaterial() {
        this.name = toString().toLowerCase(Locale.ENGLISH);
    }

    @Override
    public String getString() {
        return name;
    }
}

package forestry.apiculture.blocks;

import forestry.api.core.IBlockSubtype;
import forestry.apiculture.features.ApicultureTiles;
import forestry.modules.features.FeatureTileType;

import java.util.Locale;

public enum BlockAlvearyType implements IBlockSubtype {
    PLAIN(false),
    SWARMER(true),
    FAN(true),
    HEATER(true),
    HYGRO(false),
    STABILISER(false),
    SIEVE(false);

    public static final BlockAlvearyType[] VALUES = values();

    public final boolean activatable;

    BlockAlvearyType(boolean activatable) {
        this.activatable = activatable;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase(Locale.ENGLISH);
    }

    @Override
    public String getString() {
        return super.toString().toLowerCase(Locale.ENGLISH);
    }

    public FeatureTileType<?> getTileType() {
        switch (this) {
            default:
            case PLAIN:
                return ApicultureTiles.ALVEARY_PLAIN;
            case SWARMER:
                return ApicultureTiles.ALVEARY_SWARMER;
            case FAN:
                return ApicultureTiles.ALVEARY_FAN;
            case HEATER:
                return ApicultureTiles.ALVEARY_HEATER;
            case HYGRO:
                return ApicultureTiles.ALVEARY_HYGROREGULATOR;
            case STABILISER:
                return ApicultureTiles.ALVEARY_STABILISER;
            case SIEVE:
                return ApicultureTiles.ALVEARY_SIEVE;
        }
    }
}

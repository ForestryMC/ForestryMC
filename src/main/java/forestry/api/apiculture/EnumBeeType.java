/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture;

import forestry.api.genetics.ISpeciesType;
import java.util.Locale;

public enum EnumBeeType implements ISpeciesType {
    DRONE,
    PRINCESS,
    QUEEN,
    LARVAE,
    NONE;

    public static final EnumBeeType[] VALUES = values();

    String name;

    EnumBeeType() {
        this.name = this.toString().toLowerCase(Locale.ENGLISH);
    }

    public String getName() {
        return name;
    }
}

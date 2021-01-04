/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.lepidopterology.genetics;

import genetics.api.organism.IOrganismType;

import java.util.Locale;

public enum EnumFlutterType implements IOrganismType {
    BUTTERFLY, SERUM, CATERPILLAR, COCOON;

    public static final EnumFlutterType[] VALUES = values();

    public String getName() {
        return toString().toLowerCase(Locale.ENGLISH);
    }
}

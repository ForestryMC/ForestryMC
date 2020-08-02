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
package forestry.core.items;

import java.util.Locale;

import forestry.api.core.IItemSubtype;

public enum EnumContainerType implements IItemSubtype {
    GLASS, JAR, CAN, CAPSULE, REFRACTORY;

    private final String name;

    EnumContainerType() {
        this.name = name().toLowerCase(Locale.ENGLISH).replace('_', '.');
    }

    @Override
    public String getString() {
        return name;
    }
}

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
package forestry.climatology.blocks;

import java.util.Locale;

import net.minecraft.state.EnumProperty;
import net.minecraft.util.IStringSerializable;

public enum State implements IStringSerializable {
    ON, OFF;

    public static final EnumProperty<State> PROPERTY = EnumProperty.create("state", State.class);

    public static State fromBool(boolean value) {
        return value ? ON : OFF;
    }

    @Override
    public String getString() {
        return name().toLowerCase(Locale.ENGLISH);
    }
}

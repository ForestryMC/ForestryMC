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
package forestry.core.circuits;

import forestry.api.core.IItemSubtype;
import forestry.core.render.ColourProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Locale;

public enum EnumCircuitBoardType implements IItemSubtype {
    BASIC(1),
    ENHANCED(2),
    REFINED(3),
    INTRICATE(4);

    private final int sockets;
    private final String name;

    EnumCircuitBoardType(int sockets) {
        this.name = toString().toLowerCase(Locale.ENGLISH);
        this.sockets = sockets;
    }

    public int getSockets() {
        return sockets;
    }

    @Override
    public String getString() {
        return name;
    }

    @OnlyIn(Dist.CLIENT)
    public int getPrimaryColor() {
        return ColourProperties.INSTANCE.get("item.circuit." + name().toLowerCase(Locale.ENGLISH) + ".primary");
    }

    @OnlyIn(Dist.CLIENT)
    public int getSecondaryColor() {
        return ColourProperties.INSTANCE.get("item.circuit." + name().toLowerCase(Locale.ENGLISH) + ".secondary");
    }
}

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
package forestry.apiculture.items;

import forestry.api.core.ItemGroups;
import forestry.core.items.ItemOverlay;

//TODO - create common superclass for items/blocks defined by an enum.
//Will help with automatic creation of stuff too.
public class ItemPropolis extends ItemOverlay {

    private final EnumPropolis type;

    public ItemPropolis(EnumPropolis type) {
        super(ItemGroups.tabApiculture, type);
        this.type = type;
    }
}

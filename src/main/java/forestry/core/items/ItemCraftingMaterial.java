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

import forestry.core.ItemGroupForestry;
import net.minecraft.item.Item;

public class ItemCraftingMaterial extends ItemForestry {

    private final EnumCraftingMaterial type;

    public ItemCraftingMaterial(EnumCraftingMaterial type) {
        super((new Item.Properties())
                .group(ItemGroupForestry.tabForestry));
        this.type = type;
    }

    public EnumCraftingMaterial getType() {
        return type;
    }
}

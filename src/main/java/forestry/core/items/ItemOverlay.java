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

import forestry.api.core.IItemSubtype;
import forestry.core.ItemGroupForestry;
import forestry.core.config.Config;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemOverlay extends ItemForestry implements IColoredItem {

    public interface IOverlayInfo extends IItemSubtype {
        int getPrimaryColor();

        int getSecondaryColor();

        boolean isSecret();
    }

    protected final IOverlayInfo overlay;

    public ItemOverlay(ItemGroup tab, IOverlayInfo overlay) {
        super((new Item.Properties())
                .group(tab)
                .group(ItemGroupForestry.tabForestry));

        this.overlay = overlay;
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public void fillItemGroup(ItemGroup tab, NonNullList<ItemStack> subItems) {
        if (this.isInGroup(tab)) {
            if (Config.isDebug || !overlay.isSecret()) {
                subItems.add(new ItemStack(this));
            }
        }
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int tintIndex) {
        if (tintIndex == 0 || overlay.getSecondaryColor() == 0) {
            return overlay.getPrimaryColor();
        } else {
            return overlay.getSecondaryColor();
        }
    }
}

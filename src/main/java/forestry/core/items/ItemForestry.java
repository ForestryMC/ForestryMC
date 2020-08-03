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
import forestry.core.utils.ItemTooltipUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ItemForestry extends Item {

    private final int burnTime;

    //TODO may be worth removing this
    public ItemForestry() {
        this(ItemGroupForestry.tabForestry);
    }

    public ItemForestry(ItemGroup group) {
        this(new Item.Properties(), group);
    }

    public ItemForestry(Item.Properties properties, ItemGroup creativeTab) {
        this(properties.group(creativeTab));
    }

    public ItemForestry(Item.Properties properties) {
        //TODO - do the below at registration
        super(properties);
        if (properties instanceof ItemProperties) {
            this.burnTime = ((ItemProperties) properties).burnTime;
        } else {
            burnTime = 0;
        }
    }

    @Override
    public int getBurnTime(ItemStack itemStack) {
        return burnTime;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        ItemTooltipUtil.addInformation(stack, world, tooltip, advanced);
    }

}

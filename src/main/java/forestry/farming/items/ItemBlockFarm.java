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
package forestry.farming.items;

import forestry.core.ItemGroupForestry;
import forestry.core.items.ItemBlockForestry;
import forestry.core.utils.ItemTooltipUtil;
import forestry.farming.blocks.BlockFarm;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBlockFarm extends ItemBlockForestry<BlockFarm> {

    public ItemBlockFarm(BlockFarm block) {
        super(block, new Item.Properties().group(ItemGroupForestry.tabForestry));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(new TranslationTextComponent("block.forestry.farm.tooltip"));
			/*BlockFarm block = getBlock();
			EnumFarmMaterial material = block.getFarmMaterial();
			tooltip.add(new TranslationTextComponent("block.forestry.farm.material.tooltip").setStyle((new Style()).setItalic(true).setColor(material.getFormatting())).appendText(" " + WordUtils.capitalize(material.getName().replace("_", ""))));*/
        } else {
            ItemTooltipUtil.addShiftInformation(stack, world, tooltip, flag);
        }
    }

    @Override
    public String getTranslationKey() {
        BlockFarm block = getBlock();
        return "block.forestry.farm_" + block.getType().getString();
    }
}

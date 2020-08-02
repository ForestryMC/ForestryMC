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

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import forestry.api.arboriculture.ArboricultureCapabilities;
import forestry.core.ItemGroupForestry;
import forestry.core.config.Constants;
import forestry.core.utils.ItemTooltipUtil;

public class ItemArmorNaturalist extends ArmorItem {

    public ItemArmorNaturalist() {
        super(ArmorMaterial.LEATHER, EquipmentSlotType.HEAD, (new Item.Properties())
                .maxDamage(100)
                .group(ItemGroupForestry.tabForestry));
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
        return Constants.MOD_ID + ":" + Constants.TEXTURE_NATURALIST_ARMOR_PRIMARY;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag advanced) {
        ItemTooltipUtil.addInformation(stack, world, tooltip, advanced);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new ICapabilityProvider() {

            @Override
            public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
                if (capability == ArboricultureCapabilities.ARMOR_NATURALIST &&
                        slot == EquipmentSlotType.HEAD) {
                    return LazyOptional.of(capability::getDefaultInstance);
                }
                return LazyOptional.empty();
            }
        };
    }

}

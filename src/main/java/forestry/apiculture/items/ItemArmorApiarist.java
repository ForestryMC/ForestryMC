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

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import forestry.api.apiculture.ApicultureCapabilities;
import forestry.api.arboriculture.ArboricultureCapabilities;
import forestry.api.core.ItemGroups;
import forestry.apiculture.features.ApicultureItems;
import forestry.core.config.Constants;
import forestry.core.features.CoreItems;
import forestry.core.items.EnumCraftingMaterial;

public class ItemArmorApiarist extends ArmorItem {

    public static final class ApiaristArmorMaterial implements IArmorMaterial {

        private static final int[] reductions = new int[]{1, 2, 3, 1};

        @Override
        public int getDurability(EquipmentSlotType slotIn) {
            return 5;
        }

        @Override
        public int getDamageReductionAmount(EquipmentSlotType slotIn) {
            return reductions[slotIn.getIndex()];
        }

        @Override
        public int getEnchantability() {
            return 15;
        }

        @Override
        public SoundEvent getSoundEvent() {
            return SoundEvents.ITEM_ARMOR_EQUIP_LEATHER;
        }

        @Override
        public Ingredient getRepairMaterial() {
            return Ingredient.fromStacks(CoreItems.CRAFTING_MATERIALS.stack(EnumCraftingMaterial.WOVEN_SILK, 1));
        }

        @Override
        public String getName() {
            return "APIARIST_ARMOR";
        }

        @Override
        public float getToughness() {
            return 0.0F;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.0F;
        }
    }

    public ItemArmorApiarist(EquipmentSlotType equipmentSlotIn) {
        super(new ApiaristArmorMaterial(), equipmentSlotIn, (new Item.Properties()).group(ItemGroups.tabApiculture));
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
        if (ApicultureItems.APIARIST_LEGS.itemEqual(stack)) {
            return Constants.MOD_ID + ":" + Constants.TEXTURE_APIARIST_ARMOR_SECONDARY;
        } else {
            return Constants.MOD_ID + ":" + Constants.TEXTURE_APIARIST_ARMOR_PRIMARY;
        }
    }

    @Override
    @Nullable
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new ICapabilityProvider() {

            //TODO - null issues
            @Override
            public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
                if (capability == ApicultureCapabilities.ARMOR_APIARIST) {
                    return LazyOptional.of(capability::getDefaultInstance);
                } else if (capability == ArboricultureCapabilities.ARMOR_NATURALIST &&
                        slot == EquipmentSlotType.HEAD) {
                    return LazyOptional.of(capability::getDefaultInstance);
                }
                return LazyOptional.empty();
            }
        };
    }
}

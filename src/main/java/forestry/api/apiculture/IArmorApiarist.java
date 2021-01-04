/*
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 */
package forestry.api.apiculture;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * When implemented as a capability by armor items, protects the wearer from negative bee effects.
 *
 * @see ApicultureCapabilities#ARMOR_APIARIST
 */
public interface IArmorApiarist {
    /**
     * Called when the apiarist's armor acts as protection against an attack.
     *
     * @param entity    Entity being attacked
     * @param armor     Armor item
     * @param cause     Optional cause of attack, such as a bee effect identifier
     * @param doProtect Whether or not to actually do the side effects of protection
     * @return Whether or not the armor should protect the player from that attack
     */
    boolean protectEntity(LivingEntity entity, ItemStack armor, @Nullable ResourceLocation cause, boolean doProtect);
}

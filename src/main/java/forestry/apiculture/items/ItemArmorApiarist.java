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

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import forestry.api.core.ItemGroups;
import forestry.apiculture.features.ApicultureItems;
import forestry.core.config.Constants;
import forestry.core.features.CoreItems;
import forestry.core.items.definitions.EnumCraftingMaterial;

public class ItemArmorApiarist extends ArmorItem {

	public static final class ApiaristArmorMaterial implements ArmorMaterial {

		private static final int[] reductions = new int[]{1, 2, 3, 1};

		@Override
		public int getDurabilityForSlot(EquipmentSlot slotIn) {
			return 5;
		}

		@Override
		public int getDefenseForSlot(EquipmentSlot slotIn) {
			return reductions[slotIn.getIndex()];
		}

		@Override
		public int getEnchantmentValue() {
			return 15;
		}

		@Override
		public SoundEvent getEquipSound() {
			return SoundEvents.ARMOR_EQUIP_LEATHER;
		}

		@Override
		public Ingredient getRepairIngredient() {
			return Ingredient.of(CoreItems.CRAFTING_MATERIALS.stack(EnumCraftingMaterial.WOVEN_SILK, 1));
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

	public ItemArmorApiarist(EquipmentSlot equipmentSlotIn) {
		super(new ApiaristArmorMaterial(), equipmentSlotIn, (new Item.Properties()).tab(ItemGroups.tabApiculture));
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
		if (ApicultureItems.APIARIST_LEGS.itemEqual(stack)) {
			return Constants.MOD_ID + ":" + Constants.TEXTURE_APIARIST_ARMOR_SECONDARY;
		} else {
			return Constants.MOD_ID + ":" + Constants.TEXTURE_APIARIST_ARMOR_PRIMARY;
		}
	}
}

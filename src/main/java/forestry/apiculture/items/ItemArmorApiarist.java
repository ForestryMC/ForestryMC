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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import forestry.api.apiculture.ApicultureCapabilities;
import forestry.api.arboriculture.ArboricultureCapabilities;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.api.core.Tabs;
import forestry.apiculture.PluginApiculture;
import forestry.core.config.Constants;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemArmorApiarist extends ItemArmor implements IItemModelRegister {

	public ItemArmorApiarist(EntityEquipmentSlot equipmentSlotIn) {
		super(ArmorMaterial.LEATHER, 0, equipmentSlotIn);
		this.setMaxDamage(100);
		setCreativeTab(Tabs.tabApiculture);
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		if (stack != null && stack.getItem() == PluginApiculture.items.apiaristLegs) {
			return Constants.MOD_ID + ":" + Constants.TEXTURE_APIARIST_ARMOR_SECONDARY;
		} else {
			return Constants.MOD_ID + ":" + Constants.TEXTURE_APIARIST_ARMOR_PRIMARY;
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, 0);
	}

	@Override
	public boolean hasColor(ItemStack itemstack) {
		return false;
	}

	@Nonnull
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		return new ICapabilityProvider() {
			@Override
			public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
				if (capability == ArboricultureCapabilities.ARMOR_NATURALIST) {
					return armorType == EntityEquipmentSlot.HEAD;
				}
				return capability == ApicultureCapabilities.ARMOR_APIARIST;
			}

			@Override
			public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
				if (capability == ApicultureCapabilities.ARMOR_APIARIST) {
					return capability.getDefaultInstance();
				} else if (capability == ArboricultureCapabilities.ARMOR_NATURALIST &&
						armorType == EntityEquipmentSlot.HEAD) {
					return capability.getDefaultInstance();
				}
				return null;
			}
		};
	}
}

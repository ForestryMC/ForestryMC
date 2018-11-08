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
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.ArboricultureCapabilities;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.core.CreativeTabForestry;
import forestry.core.config.Constants;
import forestry.core.utils.ItemTooltipUtil;

public class ItemArmorNaturalist extends ItemArmor implements IItemModelRegister {

	public ItemArmorNaturalist() {
		super(ArmorMaterial.LEATHER, 0, EntityEquipmentSlot.HEAD);
		this.setMaxDamage(100);
		setCreativeTab(CreativeTabForestry.tabForestry);
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return Constants.MOD_ID + ":" + Constants.TEXTURE_NATURALIST_ARMOR_PRIMARY;
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

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced) {
		ItemTooltipUtil.addInformation(stack, world, tooltip, advanced);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
		return new ICapabilityProvider() {
			@Override
			public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
				return capability == ArboricultureCapabilities.ARMOR_NATURALIST &&
					armorType == EntityEquipmentSlot.HEAD;
			}

			@Override
			public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
				if (capability == ArboricultureCapabilities.ARMOR_NATURALIST &&
					armorType == EntityEquipmentSlot.HEAD) {
					return capability.getDefaultInstance();
				}
				return null;
			}
		};
	}

}

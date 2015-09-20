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

import forestry.api.apiculture.IArmorApiaristHelper;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.apiculture.IArmorApiarist;
import forestry.api.core.IArmorNaturalist;
import forestry.api.core.Tabs;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.render.TextureManager;
import forestry.core.utils.StringUtil;

public class ItemArmorApiarist extends ItemArmor implements IArmorApiarist, IArmorNaturalist {

	public ItemArmorApiarist(int slot) {
		super(ArmorMaterial.CLOTH, 0, slot);
		this.setMaxDamage(100);
		setCreativeTab(Tabs.tabApiculture);
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		if (ForestryItem.apiaristLegs.isItemEqual(stack)) {
			return Defaults.ID + ":" + Defaults.TEXTURE_APIARIST_ARMOR_SECONDARY;
		} else {
			return Defaults.ID + ":" + Defaults.TEXTURE_APIARIST_ARMOR_PRIMARY;
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister register) {
		itemIcon = TextureManager.getInstance().registerTex(register, StringUtil.cleanItemName(this));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamageForRenderPass(int par1, int par2) {
		return itemIcon;
	}

	@Override
	public boolean requiresMultipleRenderPasses() {
		return false;
	}

	@Override
	public int getColorFromItemStack(ItemStack itemstack, int renderPass) {
		return 0xffffff;
	}

	@Override
	public boolean hasColor(ItemStack itemstack) {
		return false;
	}

	@Override
	public boolean protectPlayer(EntityPlayer player, ItemStack armor, String cause, boolean doProtect) {
		return true;
	}

	@Override
	public boolean canSeePollination(EntityPlayer player, ItemStack armor, boolean doSee) {
		return armorType == 0;
	}

	public static class ArmorApiaristHelper implements IArmorApiaristHelper {

		@Override
		public boolean isArmorApiarist(ItemStack stack, EntityPlayer player, String cause, boolean doProtect) {
			if(stack == null) {
				return false;
			}

			Item item = stack.getItem();
			return item instanceof IArmorApiarist && ((IArmorApiarist) item).protectPlayer(player, stack, cause, doProtect);
		}

		@Override
		public int wearsItems(EntityPlayer player, String cause, boolean doProtect) {
			int count = 0;

			for (ItemStack armorItem : player.inventory.armorInventory) {
				if (isArmorApiarist(armorItem, player, cause, doProtect)) {
					count++;
				}
			}

			return count;
		}
	}
}

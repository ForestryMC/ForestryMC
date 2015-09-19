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

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
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
		if (ForestryItem.apiaristLegs.isItemEqual(stack))
			return Defaults.ID + ":" + Defaults.TEXTURE_APIARIST_ARMOR_SECONDARY;
		else
			return Defaults.ID + ":" + Defaults.TEXTURE_APIARIST_ARMOR_PRIMARY;
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		return StringUtil.localize(getUnlocalizedName(itemstack));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister register) {
		itemIcon = TextureManager.getInstance().registerTex(register, getUnlocalizedName().replace("item.", ""));
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

	// Legacy methods

	@Deprecated
	public static boolean wearsHelmet(EntityPlayer player, String cause, boolean protect) {
		return ArmorApiaristHelper.wearsHelmet(player, cause, protect);
	}

	@Deprecated
	public static boolean wearsChest(EntityPlayer player, String cause, boolean protect) {
		return ArmorApiaristHelper.wearsChest(player, cause, protect);
	}

	@Deprecated
	public static boolean wearsLegs(EntityPlayer player, String cause, boolean protect) {
		return ArmorApiaristHelper.wearsLegs(player, cause, protect);
	}

	@Deprecated
	public static boolean wearsBoots(EntityPlayer player, String cause, boolean protect) {
		return ArmorApiaristHelper.wearsBoots(player, cause, protect);
	}

	@Deprecated
	public static int wearsItems(EntityPlayer player, String cause, boolean protect) {
		return ArmorApiaristHelper.wearsItems(player, cause, protect);
	}

}

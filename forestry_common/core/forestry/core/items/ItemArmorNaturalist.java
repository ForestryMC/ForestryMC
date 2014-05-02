/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.core.IArmorNaturalist;
import forestry.core.CreativeTabForestry;
import forestry.core.config.Defaults;
import forestry.core.render.TextureManager;
import forestry.core.utils.StringUtil;

public class ItemArmorNaturalist extends ItemArmor implements IArmorNaturalist {

	public ItemArmorNaturalist(int slot) {
		super(ArmorMaterial.CLOTH, 0, slot);
		this.setMaxDamage(100);
		setCreativeTab(CreativeTabForestry.tabForestry);
	}

	public String getArmorTexture(ItemStack stack, Entity entity, int slot, int layer) {
		return Defaults.ID + ":" + Defaults.TEXTURE_NATURALIST_ARMOR_PRIMARY;
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
	public boolean canSeePollination(EntityPlayer player, ItemStack armor, boolean doSee) {
		return armorType == 0;
	}

}

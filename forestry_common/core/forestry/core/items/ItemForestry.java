/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.core.CreativeTabForestry;
import forestry.core.proxy.Proxies;
import forestry.core.render.TextureManager;
import forestry.core.utils.StringUtil;

public class ItemForestry extends Item {

	private boolean isBonemeal = false;

	public ItemForestry() {
		super();
		maxStackSize = 64;
		setCreativeTab(CreativeTabForestry.tabForestry);
	}

	public ItemForestry setBonemeal(boolean isBonemeal) {
		this.isBonemeal = isBonemeal;
		return this;
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10) {
		if(isBonemeal) {
			if(ItemDye.applyBonemeal(itemstack, world, x, y, z, player)) {
				if(Proxies.common.isSimulating(world))
					world.playAuxSFX(2005, x, y, z, 0);

				return true;
			}
		}
		return super.onItemUse(itemstack, player, world, x, y, z, par7, par8, par9, par10);
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
}

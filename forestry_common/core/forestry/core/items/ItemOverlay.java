/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.core.config.Config;
import forestry.core.render.TextureManager;
import forestry.core.utils.StringUtil;

public class ItemOverlay extends ItemForestry {

	public static class OverlayInfo {

		public String name;
		public int primaryColor = 0;
		public int secondaryColor = 0;
		public boolean isSecret = false;

		public OverlayInfo(String name, int primaryColor, int secondaryColor) {
			this.name = name;
			this.primaryColor = primaryColor;
			this.secondaryColor = secondaryColor;
		}

		public OverlayInfo(String name, int primaryColor) {
			this(name, primaryColor, 0);
		}

		public OverlayInfo setIsSecret() {
			isSecret = true;
			return this;
		}
	}

	private final OverlayInfo[] overlays;

	public ItemOverlay(CreativeTabs tab, OverlayInfo... overlays) {
		super();
		setMaxDamage(0);
		setHasSubtypes(true);
		setCreativeTab(tab);

		this.overlays = overlays;
	}

	@Override
	public boolean isDamageable() {
		return false;
	}

	@Override
	public boolean isRepairable() {
		return false;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List itemList) {
		for (int i = 0; i < overlays.length; i++)
			if (Config.isDebug || !overlays[i].isSecret)
				itemList.add(new ItemStack(this, 1, i));
	}

	/* ICONS */
	@SideOnly(Side.CLIENT)
	private IIcon primaryIcon;
	@SideOnly(Side.CLIENT)
	private IIcon secondaryIcon;

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister register) {
		primaryIcon = TextureManager.getInstance().registerTex(register, getUnlocalizedName().replace("item.", "") + ".0");
		if(overlays[0].secondaryColor != 0)
			secondaryIcon = TextureManager.getInstance().registerTex(register, getUnlocalizedName().replace("item.", "") + ".1");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamageForRenderPass(int i, int j) {
		if (j > 0 && overlays[i].secondaryColor != 0)
			return secondaryIcon;
		else
			return primaryIcon;
	}

	@Override
	public int getRenderPasses(int metadata) {
		return overlays[metadata].secondaryColor != 0 ? 2 : 1;
	}

	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		if (itemstack.getItemDamage() < 0 || itemstack.getItemDamage() >= overlays.length)
			return null;

		return StringUtil.localize(getUnlocalizedName() + "." + overlays[itemstack.getItemDamage()].name);
	}

	@Override
	public int getColorFromItemStack(ItemStack itemstack, int j) {

		if (j == 0 || overlays[itemstack.getItemDamage()].secondaryColor == 0)
			return overlays[itemstack.getItemDamage()].primaryColor;
		else
			return overlays[itemstack.getItemDamage()].secondaryColor;
	}

}

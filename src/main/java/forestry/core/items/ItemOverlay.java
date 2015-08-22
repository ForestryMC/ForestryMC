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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.core.IVariantObject;
import forestry.core.config.Config;
import forestry.core.render.TextureManager;
import forestry.core.utils.StringUtil;

public class ItemOverlay extends ItemForestry implements IVariantObject {
	
	private static final List<String> variants = new ArrayList<String>();
	
	public static class OverlayInfo {

		public final String name;
		public final int primaryColor;
		public final int secondaryColor;
		public boolean isSecret = false;

		public OverlayInfo(String name, Color primaryColor, Color secondaryColor) {
			this(name, primaryColor.getRGB(), secondaryColor.getRGB());
		}

		public OverlayInfo(String name, int primaryColor, int secondaryColor) {
			this.name = name;
			this.primaryColor = primaryColor;
			this.secondaryColor = secondaryColor;
			variants.add(name);
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

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List itemList) {
		for (int i = 0; i < overlays.length; i++) {
			if (Config.isDebug || !overlays[i].isSecret) {
				itemList.add(new ItemStack(this, 1, i));
			}
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		if (stack.getItemDamage() < 0 || stack.getItemDamage() >= overlays.length) {
			return null;
		}

		return super.getUnlocalizedName(stack) + "." + overlays[stack.getItemDamage()].name;
	}

	@Override
	public int getColorFromItemStack(ItemStack itemstack, int j) {

		if (j == 0 || overlays[itemstack.getItemDamage()].secondaryColor == 0) {
			return overlays[itemstack.getItemDamage()].primaryColor;
		} else {
			return overlays[itemstack.getItemDamage()].secondaryColor;
		}
	}
	
	@Override
	public String[] getVariants() {
		return variants.toArray(new String[variants.size()]);
	}

}

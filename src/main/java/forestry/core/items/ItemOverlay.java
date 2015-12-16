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

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.core.IModelManager;
import forestry.core.config.Config;
import forestry.core.render.TextureManager;
import forestry.core.utils.StringUtil;

public class ItemOverlay extends ItemForestry {
	public interface IOverlayInfo {
		String getName();
		int getPrimaryColor();
		int getSecondaryColor();
		boolean isSecret();
	}

	protected final IOverlayInfo[] overlays;

	public ItemOverlay(CreativeTabs tab, IOverlayInfo... overlays) {
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
			if (Config.isDebug || !overlays[i].isSecret()) {
				itemList.add(new ItemStack(this, 1, i));
			}
		}
	}

	/* Models */
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		for (int i = 0; i < overlays.length; i++) {
			manager.registerItemModel(item, i, "");
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		if (stack.getItemDamage() < 0 || stack.getItemDamage() >= overlays.length) {
			return null;
		}

		return super.getUnlocalizedName(stack) + "." + overlays[stack.getItemDamage()].getName();
	}

	@Override
	public int getColorFromItemStack(ItemStack itemstack, int j) {
		IOverlayInfo overlayInfo = overlays[itemstack.getItemDamage()];
		if (j == 0 || overlayInfo.getSecondaryColor() == 0) {
			return overlayInfo.getPrimaryColor();
		} else {
			return overlayInfo.getSecondaryColor();
		}
	}
}

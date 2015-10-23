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

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.core.CreativeTabForestry;
import forestry.core.render.TextureManager;
import forestry.core.utils.StringUtil;

public class ItemForestryFood extends ItemFood {

	private boolean isDrink = false;

	public ItemForestryFood(int heal) {
		this(heal, 0.6f);
	}

	public ItemForestryFood(int heal, float saturation) {
		super(heal, saturation, false);
		setCreativeTab(CreativeTabForestry.tabForestry);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemstack) {
		if (isDrink) {
			return EnumAction.drink;
		} else {
			return EnumAction.eat;
		}
	}

	public ItemForestryFood setIsDrink() {
		isDrink = true;
		return this;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister register) {
		itemIcon = TextureManager.registerTex(register, StringUtil.cleanItemName(this));
	}

}

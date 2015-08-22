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

import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.core.IModelObject;
import forestry.core.CreativeTabForestry;
import forestry.core.render.TextureManager;
import forestry.core.utils.StringUtil;

public class ItemForestryFood extends ItemFood implements IModelObject {

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
			return EnumAction.DRINK;
		} else {
			return EnumAction.EAT;
		}
	}

	public ItemForestryFood setIsDrink() {
		isDrink = true;
		return this;
	}
	
	@Override
	public ModelType getModelType() {
		return ModelType.DEFAULT;
	}

}

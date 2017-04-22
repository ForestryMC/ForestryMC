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

import forestry.api.core.IToolScoop;
import forestry.api.core.Tabs;
import net.minecraft.item.ItemStack;

public class ItemScoop extends ItemForestryTool implements IToolScoop {
	public ItemScoop() {
		super(ItemStack.EMPTY);
		setEfficiencyOnProperMaterial(2.0f);
		setMaxDamage(10);
		setCreativeTab(Tabs.tabApiculture);
		setMaxStackSize(1);
	}
}

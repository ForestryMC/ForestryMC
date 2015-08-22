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

import net.minecraft.block.Block;
import forestry.api.core.IModelObject;
import forestry.api.core.IToolScoop;
import forestry.api.core.Tabs;
import forestry.core.config.ForestryBlock;

public class ItemScoop extends ItemForestryTool implements IToolScoop, IModelObject {

	public ItemScoop() {
		super(new Block[]{ForestryBlock.beehives.block()}, null);
		this.maxStackSize = 1;
		efficiencyOnProperMaterial = 4.0F;
		setMaxDamage(10);
		setCreativeTab(Tabs.tabApiculture);
		setMaxStackSize(1);
	}

	@Override
	public boolean isFull3D() {
		return true;
	}
}

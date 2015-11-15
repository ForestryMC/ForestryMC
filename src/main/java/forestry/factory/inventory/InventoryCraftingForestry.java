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
package forestry.factory.inventory;

import net.minecraft.inventory.InventoryCrafting;

import forestry.core.gui.ContainerDummy;
import forestry.factory.gui.ContainerWorktable;

public class InventoryCraftingForestry extends InventoryCrafting {
	public InventoryCraftingForestry(ContainerWorktable containerWorktable) {
		super(containerWorktable, 3, 3);
	}

	public InventoryCraftingForestry() {
		super(ContainerDummy.instance, 3, 3);
	}
}

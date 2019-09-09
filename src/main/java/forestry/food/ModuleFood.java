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
package forestry.food;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import forestry.api.modules.ForestryModule;
import forestry.core.config.Constants;
import forestry.food.items.ItemRegistryFood;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.FOOD, name = "Food", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.food.description")
public class ModuleFood extends BlankForestryModule {
	@Nullable
	private static ItemRegistryFood items;

	public static ItemRegistryFood getItems() {
		Preconditions.checkNotNull(items);
		return items;
	}

	@Override
	public void registerItems() {
		items = new ItemRegistryFood();
	}

}

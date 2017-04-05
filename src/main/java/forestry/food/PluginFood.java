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

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import forestry.core.config.Constants;
import forestry.food.items.ItemRegistryFood;
import forestry.plugins.BlankForestryPlugin;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;

@ForestryPlugin(pluginID = ForestryPluginUids.FOOD, name = "Food", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.plugin.food.description")
public class PluginFood extends BlankForestryPlugin {
	@Nullable
	private static ItemRegistryFood items;

	public static ItemRegistryFood getItems() {
		Preconditions.checkState(items != null);
		return items;
	}

	@Override
	public void registerItemsAndBlocks() {
		items = new ItemRegistryFood();
	}

}

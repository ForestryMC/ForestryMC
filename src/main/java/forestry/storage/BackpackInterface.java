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

package forestry.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import com.google.common.base.Preconditions;
import forestry.api.storage.EnumBackpackType;
import forestry.api.storage.IBackpackDefinition;
import forestry.api.storage.IBackpackInterface;
import forestry.core.proxy.Proxies;
import forestry.storage.items.ItemBackpack;
import forestry.storage.items.ItemBackpackNaturalist;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class BackpackInterface implements IBackpackInterface {

	@Override
	public Item createBackpack(IBackpackDefinition definition, EnumBackpackType type) {
		Preconditions.checkNotNull(definition, "definition must not be null");
		Preconditions.checkNotNull(type, "type must not be null");
		Preconditions.checkArgument(type != EnumBackpackType.NATURALIST, "type must not be NATURALIST. Use createNaturalistBackpack instead.");

		ItemBackpack backpack = new ItemBackpack(definition, type);
		Proxies.common.registerItem(backpack);
		return backpack;
	}

	@Override
	public Item createNaturalistBackpack(IBackpackDefinition definition, String rootUid, ItemGroup tab) {
		Preconditions.checkNotNull(definition, "definition must not be null");
		Preconditions.checkNotNull(rootUid, "rootUid must not be null");

		ItemBackpack backpack = new ItemBackpackNaturalist(rootUid, definition, tab);
		Proxies.common.registerItem(backpack);
		return backpack;
	}

	@Override
	public Predicate<ItemStack> createNaturalistBackpackFilter(String speciesRootUid) {
		return new BackpackFilterNaturalist(speciesRootUid);
	}
}

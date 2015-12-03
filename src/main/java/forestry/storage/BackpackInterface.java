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

import net.minecraft.item.Item;

import forestry.api.storage.BackpackManager;
import forestry.api.storage.EnumBackpackType;
import forestry.api.storage.IBackpackDefinition;
import forestry.api.storage.IBackpackInterface;
import forestry.storage.items.ItemBackpack;

public class BackpackInterface implements IBackpackInterface {
	@Override
	public Item addBackpack(IBackpackDefinition definition, EnumBackpackType type) {
		BackpackManager.definitions.put(definition.getKey(), definition);
		return new ItemBackpack(definition, type);
	}
}

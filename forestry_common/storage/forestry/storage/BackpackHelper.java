/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.storage;

import net.minecraft.item.Item;

import forestry.api.storage.BackpackManager;
import forestry.api.storage.EnumBackpackType;
import forestry.api.storage.IBackpackDefinition;
import forestry.api.storage.IBackpackInterface;
import forestry.storage.items.ItemBackpack;

public class BackpackHelper implements IBackpackInterface {

	@Override
	public Item addBackpack(IBackpackDefinition definition, EnumBackpackType type) {
		BackpackManager.definitions.put(definition.getKey(), definition);
		return new ItemBackpack(definition, type.ordinal() + 1);
	}

}

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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;

import forestry.api.storage.EnumBackpackType;
import forestry.api.storage.IBackpackDefinition;
import forestry.api.storage.IBackpackInterface;
import forestry.storage.items.ItemBackpack;

public class BackpackInterface implements IBackpackInterface {
	/**
	 * Only use this if you know what you are doing. Prefer backpackInterface.
	 */
	public final Map<String, IBackpackDefinition> definitions = new HashMap<>();

	@Nullable
	@Override
	public IBackpackDefinition getBackpack(@Nonnull String uid) {
		return definitions.get(uid);
	}

	@Override
	public void registerBackpack(@Nonnull String uid, @Nonnull IBackpackDefinition definition) {
		definitions.put(uid, definition);
	}

	@Nonnull
	@Override
	public Item createBackpack(@Nonnull IBackpackDefinition definition, @Nonnull EnumBackpackType type) {
		return new ItemBackpack(definition, type);
	}
}

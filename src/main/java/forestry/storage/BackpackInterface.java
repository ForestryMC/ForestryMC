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

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import forestry.api.genetics.ISpeciesRoot;
import forestry.api.storage.EnumBackpackType;
import forestry.api.storage.IBackpackDefinition;
import forestry.api.storage.IBackpackFilterConfigurable;
import forestry.api.storage.IBackpackInterface;
import forestry.core.utils.ItemStackUtil;
import forestry.storage.items.ItemBackpack;
import forestry.storage.items.ItemBackpackNaturalist;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BackpackInterface implements IBackpackInterface {

	private final Map<String, IBackpackDefinition> definitions = new HashMap<>();
	private final Multimap<String, String> backpackAcceptedItems = HashMultimap.create();

	public Multimap<String, String> getBackpackAcceptedItems() {
		return backpackAcceptedItems;
	}

	@Override
	public void addItemToForestryBackpack(String backpackUid, ItemStack itemStack) {
		Preconditions.checkNotNull(backpackUid, "backpackUid must not be null");
		Preconditions.checkNotNull(itemStack, "itemStack must not be null");
		Preconditions.checkArgument(!itemStack.isEmpty(), "itemStack must not be empty");

		String stringForItemStack = ItemStackUtil.getStringForItemStack(itemStack);
		backpackAcceptedItems.put(backpackUid, stringForItemStack);
	}

	@Override
	public void registerBackpackDefinition(String backpackUid, IBackpackDefinition definition) {
		Preconditions.checkNotNull(backpackUid, "backpackUid must not be null");
		Preconditions.checkNotNull(definition, "definition must not be null");

		definitions.put(backpackUid, definition);
	}

	@Nullable
	@Override
	public IBackpackDefinition getBackpackDefinition(String backpackUid) {
		Preconditions.checkNotNull(backpackUid, "backpackUid must not be null");

		return definitions.get(backpackUid);
	}

	@Override
	public Item createBackpack(String backpackUid, EnumBackpackType type) {
		Preconditions.checkNotNull(backpackUid, "backpackUid must not be null");
		Preconditions.checkNotNull(type, "type must not be null");
		Preconditions.checkArgument(type != EnumBackpackType.NATURALIST, "type must not be NATURALIST. Use createNaturalistBackpack instead.");

		IBackpackDefinition definition = definitions.get(backpackUid);
		if (definition == null) {
			throw new IllegalArgumentException("No backpack definition was registered for UID: " + backpackUid);
		}
		return new ItemBackpack(definition, type);
	}

	@Override
	public Item createNaturalistBackpack(String backpackUid, ISpeciesRoot speciesRoot) {
		Preconditions.checkNotNull(backpackUid, "backpackUid must not be null");
		Preconditions.checkNotNull(speciesRoot, "speciesRoot must not be null");

		IBackpackDefinition definition = definitions.get(backpackUid);
		if (definition == null) {
			throw new IllegalArgumentException("No backpack definition was registered for UID: " + backpackUid);
		}
		return new ItemBackpackNaturalist(speciesRoot, definition);
	}

	@Override
	public IBackpackFilterConfigurable createBackpackFilter() {
		return new BackpackFilter();
	}

	@Override
	public Predicate<ItemStack> createNaturalistBackpackFilter(String speciesRootUid) {
		return new BackpackFilterNaturalist(speciesRootUid);
	}
}

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
import java.util.function.Predicate;

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
	public void addItemToForestryBackpack(@Nullable String backpackUid, @Nullable ItemStack itemStack) {
		if (backpackUid == null) {
			throw new NullPointerException("backpackUid must not be null");
		}
		if (itemStack == null) {
			throw new NullPointerException("itemStack must not be null");
		}
		if (itemStack.getItem() == null) {
			throw new NullPointerException("itemStack.getItem() must not be null");
		}

		String stringForItemStack = ItemStackUtil.getStringForItemStack(itemStack);
		backpackAcceptedItems.put(backpackUid, stringForItemStack);
	}

	@Override
	public void registerBackpackDefinition(@Nullable String backpackUid, @Nullable IBackpackDefinition definition) {
		if (backpackUid == null) {
			throw new NullPointerException("backpackUid must not be null");
		}
		if (definition == null) {
			throw new NullPointerException("definition must not be null");
		}
		definitions.put(backpackUid, definition);
	}

	@Nullable
	@Override
	public IBackpackDefinition getBackpackDefinition(@Nullable String backpackUid) {
		if (backpackUid == null) {
			throw new NullPointerException("backpackUid must not be null");
		}
		return definitions.get(backpackUid);
	}

	@Nonnull
	@Override
	public Item createBackpack(@Nullable String backpackUid, @Nullable EnumBackpackType type) {
		if (backpackUid == null) {
			throw new NullPointerException("backpackUid must not be null");
		}
		if (type == null) {
			throw new NullPointerException("type must not be null");
		}
		if (type == EnumBackpackType.NATURALIST) {
			throw new IllegalArgumentException("type must not be NATURALIST. Use createNaturalistBackpack instead.");
		}

		IBackpackDefinition definition = definitions.get(backpackUid);
		if (definition == null) {
			throw new IllegalArgumentException("No backpack definition was registered for UID: " + backpackUid);
		}
		return new ItemBackpack(definition, type);
	}

	@Override
	public Item createNaturalistBackpack(@Nullable String backpackUid, @Nullable ISpeciesRoot speciesRoot) {
		if (backpackUid == null) {
			throw new NullPointerException("backpackUid must not be null");
		}
		if (speciesRoot == null) {
			throw new NullPointerException("speciesRoot must not be null");
		}

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

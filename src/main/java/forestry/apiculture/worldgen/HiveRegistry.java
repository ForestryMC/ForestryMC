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
package forestry.apiculture.worldgen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import forestry.api.apiculture.IHiveDrop;
import forestry.api.apiculture.hives.IHiveDescription;
import forestry.api.apiculture.hives.IHiveRegistry;

public class HiveRegistry implements IHiveRegistry {

	private final Map<String, Hive> hives = new HashMap<>();

	@Override
	public void registerHive(String hiveName, IHiveDescription hiveDescription) {
		if (hives.containsKey(hiveName)) {
			throw new IllegalArgumentException("Hive already exists with name: " + hiveName);
		}

		Hive hive = new Hive(hiveDescription);
		hives.put(hiveName, hive);
	}

	@Override
	public void addDrops(String hiveName, IHiveDrop... drops) {
		addDrops(hiveName, Arrays.asList(drops));
	}

	@Override
	public void addDrops(String hiveName, List<IHiveDrop> drops) {
		Hive hive = hives.get(hiveName);
		if (hive == null) {
			throw new IllegalArgumentException("No hive registered with name: " + hiveName);
		}

		hive.addDrops(drops);
	}

	public List<Hive> getHives() {
		return new ArrayList<>(hives.values());
	}

	public List<IHiveDrop> getDrops(String hiveName) {
		Hive hive = hives.get(hiveName);
		if (hive == null) {
			throw new IllegalArgumentException("No hive registered with name: " + hiveName);
		}

		return hive.getDrops();
	}
}

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
package forestry.greenhouse.climate;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.math.BlockPos;

import forestry.greenhouse.api.climate.IClimateSourceOwner;
import forestry.greenhouse.api.greenhouse.Position2D;

public class ClimateSourceWorldManager {

	private final Map<Position2D, Map<Integer, IClimateSourceOwner>> owners;

	public ClimateSourceWorldManager() {
		this.owners = new HashMap<>();
	}

	public void addSource(IClimateSourceOwner owner) {
		BlockPos pos = owner.getCoordinates();
		Position2D position = new Position2D(pos);
		Map<Integer, IClimateSourceOwner> positionedOwners = owners.computeIfAbsent(position, k -> new HashMap<>());
		positionedOwners.put(pos.getY(), owner);
	}

	public void removeSource(IClimateSourceOwner owner) {
		BlockPos pos = owner.getCoordinates();
		Position2D position = new Position2D(pos);
		Map<Integer, IClimateSourceOwner> positionedOwners = owners.computeIfAbsent(position, k -> new HashMap<>());
		positionedOwners.remove(pos.getY());
	}

	public Collection<IClimateSourceOwner> getSources(Position2D position) {
		Map<Integer, IClimateSourceOwner> positionedOwners = owners.get(position);
		if (positionedOwners == null) {
			return Collections.emptyList();
		}
		return positionedOwners.values();
	}

}

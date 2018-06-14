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
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.core.utils.World2ObjectMap;
import forestry.greenhouse.api.climate.IClimateContainer;
import forestry.greenhouse.api.climate.IClimateModifier;
import forestry.greenhouse.api.climate.IClimateSourceOwner;
import forestry.greenhouse.api.climate.IGreenhouseClimateManager;
import forestry.greenhouse.api.greenhouse.IGreenhouseBlock;
import forestry.greenhouse.api.greenhouse.IGreenhouseProvider;
import forestry.greenhouse.api.greenhouse.Position2D;
import forestry.greenhouse.multiblock.blocks.world.GreenhouseBlockManager;

public class GreenhouseClimateManager implements IGreenhouseClimateManager {

	private static final GreenhouseClimateManager INSTANCE = new GreenhouseClimateManager();

	private final World2ObjectMap<ClimateSourceWorldManager> managers;
	private final Set<IClimateModifier> modifiers;

	private GreenhouseClimateManager() {
		managers = new World2ObjectMap(world -> new ClimateSourceWorldManager());
		modifiers = new TreeSet<>((firstModifier, secondModifier) -> firstModifier.getPriority() > secondModifier.getPriority() ? 1 : -1);
	}

	public static GreenhouseClimateManager getInstance() {
		return INSTANCE;
	}

	@Override
	public IClimateContainer getContainer(World world, BlockPos pos) {
		IGreenhouseBlock logicBlock = GreenhouseBlockManager.getInstance().getBlock(world, pos);
		if (logicBlock != null) {
			IGreenhouseProvider provider = logicBlock.getProvider();
			if (!provider.isClosed()) {
				return null;
			}
			return provider.getClimateContainer();
		}
		return null;
	}

	@Override
	public void addSource(IClimateSourceOwner owner) {
		World world = owner.getWorldObj();
		ClimateSourceWorldManager manager = managers.get(world);
		if (manager == null) {
			return;
		}
		manager.addSource(owner);
	}

	@Override
	public void removeSource(IClimateSourceOwner owner) {
		World world = owner.getWorldObj();
		ClimateSourceWorldManager manager = managers.get(world);
		if (manager == null) {
			return;
		}
		manager.removeSource(owner);
	}

	@Override
	public Collection<IClimateSourceOwner> getSources(World world, Position2D position) {
		ClimateSourceWorldManager manager = managers.get(world);
		if (manager == null) {
			return Collections.emptyList();
		}
		return manager.getSources(position);
	}

	@Override
	public void registerModifier(IClimateModifier modifier) {
		modifiers.add(modifier);
	}

	@Override
	public Collection<IClimateModifier> getModifiers() {
		return modifiers;
	}

}

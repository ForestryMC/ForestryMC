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
package forestry.greenhouse;

import java.util.Collection;
import java.util.List;

import net.minecraft.world.World;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.greenhouse.IGreenhouseState;
import forestry.api.greenhouse.IInternalBlock;
import forestry.api.multiblock.IMultiblockComponent;
import forestry.greenhouse.multiblock.GreenhouseController;

public final class GreenhouseState implements IGreenhouseState {

	private final float humidity;
	private final float temperature;
	private final World world;
	private final List<IInternalBlock> internalBlocks;
	private final Collection<IMultiblockComponent> greenhouseComponents;
	
	public GreenhouseState(World world, float humidity, float temperature, List<IInternalBlock> internalBlocks, Collection<IMultiblockComponent> greenhouseComponents) {
		this.world = world;
		this.humidity = humidity;
		this.temperature = temperature;
		this.internalBlocks = internalBlocks;
		this.greenhouseComponents = greenhouseComponents;
	}
	
	public GreenhouseState(GreenhouseController controller) {
		this(controller.getWorld(), controller.getExactHumidity(), controller.getExactTemperature(), controller.getInternalBlocks(), controller.getComponents());
	}
	
	@Override
	public EnumTemperature getTemperature() {
		return EnumTemperature.getFromValue(temperature);
	}

	@Override
	public EnumHumidity getHumidity() {
		return EnumHumidity.getFromValue(humidity);
	}

	@Override
	public float getExactTemperature() {
		return temperature;
	}

	@Override
	public float getExactHumidity() {
		return humidity;
	}

	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public List<IInternalBlock> getInternalBlocks() {
		return internalBlocks;
	}
	
	@Override
	public Collection<IMultiblockComponent> getGreenhouseComponents() {
		return greenhouseComponents;
	}

}

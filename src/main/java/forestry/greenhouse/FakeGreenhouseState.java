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
import java.util.Collections;
import java.util.List;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.greenhouse.IGreenhouseState;
import forestry.api.greenhouse.IInternalBlock;
import forestry.api.multiblock.IMultiblockComponent;

public class FakeGreenhouseState implements IGreenhouseState {

	public static final FakeGreenhouseState instance = new FakeGreenhouseState();
	
	@Override
	public EnumTemperature getTemperature() {
		return null;
	}

	@Override
	public EnumHumidity getHumidity() {
		return null;
	}

	@Override
	public float getExactTemperature() {
		return 0;
	}

	@Override
	public float getExactHumidity() {
		return 0;
	}

	@Override
	public List<IInternalBlock> getInternalBlocks() {
		return Collections.emptyList();
	}

	@Override
	public Collection<IMultiblockComponent> getGreenhouseComponents() {
		return Collections.emptyList();
	}

}

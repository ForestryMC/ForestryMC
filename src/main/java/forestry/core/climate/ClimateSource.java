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
package forestry.core.climate;

import javax.annotation.Nullable;

import forestry.api.climate.IClimateRegion;
import forestry.api.climate.IClimateSource;
import forestry.api.climate.IClimateSourceProvider;

public abstract class ClimateSource<P extends IClimateSourceProvider> implements IClimateSource {
	@Nullable
	protected P provider;
	protected final int ticksForChange;
	
	public ClimateSource(int ticksForChange) {
		this.ticksForChange = ticksForChange;
	}
	
	public void setProvider(P provider) {
		this.provider = provider;
	}

	@Override
	public boolean changeClimate(int tickCount, IClimateRegion region) {
		return false;
	}

	@Override
	public int getTicksForChange(IClimateRegion region) {
		return ticksForChange;
	}

	@Override
	@Nullable
	public IClimateSourceProvider getProvider() {
		return provider;
	}

}

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
package forestry.greenhouse.tiles;

public class TileGreenhouseDryer extends TileGreenhouseClimatiser {
	
	private static final DryerDefinition definition = new DryerDefinition();

	public TileGreenhouseDryer() {
		super(definition);
	}

	private static class DryerDefinition implements IClimitiserDefinition {

		@Override
		public float getChange() {
			return 0.015F;
		}
		
		@Override
		public boolean isPositiv() {
			return false;
		}

		@Override
		public int getClimitiseRange() {
			return 5;
		}
		
		@Override
		public ClimitiserType getType() {
			return ClimitiserType.HUMIDITY;
		}
	}

}

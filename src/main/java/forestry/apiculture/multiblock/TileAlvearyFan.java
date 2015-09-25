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
package forestry.apiculture.multiblock;

import forestry.apiculture.blocks.BlockAlveary;

public class TileAlvearyFan extends TileAlvearyClimatiser {

	private static final FanDefinition definition = new FanDefinition();

	public TileAlvearyFan() {
		super(definition);
	}

	private static class FanDefinition implements IClimitiserDefinition {

		@Override
		public float getChangePerTransfer() {
			return -0.01f;
		}

		@Override
		public float getBoundaryUp() {
			return 2.5f;
		}

		@Override
		public float getBoundaryDown() {
			return 0.05f;
		}

		@Override
		public int getIconOff() {
			return BlockAlveary.ALVEARY_FAN_OFF;
		}

		@Override
		public int getIconOn() {
			return BlockAlveary.ALVEARY_FAN_ON;
		}
	}
}

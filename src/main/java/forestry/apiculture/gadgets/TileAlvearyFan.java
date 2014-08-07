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
package forestry.apiculture.gadgets;

public class TileAlvearyFan extends TileAlvearyClimatiser {
	
	/* CONSTANTS */
	public static final int BLOCK_META = 3;

	public TileAlvearyFan() {
		super(new ClimateControl(-0.01f, 0.05f, 2.5f), BlockAlveary.TX_71_FNOF, BlockAlveary.TX_72_FNON, BLOCK_META);
	}

}

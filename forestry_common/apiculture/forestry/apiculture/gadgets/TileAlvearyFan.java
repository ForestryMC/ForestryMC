/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.apiculture.gadgets;

public class TileAlvearyFan extends TileAlvearyClimatiser {
	
	/* CONSTANTS */
	public static final int BLOCK_META = 3;

	public TileAlvearyFan() {
		super(new ClimateControl(-0.01f, 0.05f, 2.5f), BlockAlveary.TX_71_FNOF, BlockAlveary.TX_72_FNON, BLOCK_META);
	}

}

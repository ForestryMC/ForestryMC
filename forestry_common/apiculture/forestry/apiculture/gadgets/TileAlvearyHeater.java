/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.apiculture.gadgets;

public class TileAlvearyHeater extends TileAlvearyClimatiser {

	public static final int TEXTURE_OFF = 57;
	public static final int TEXTURE_ON = 58;

	/* CONSTANTS */
	public static final int BLOCK_META = 4;

	public TileAlvearyHeater() {
		super(new ClimateControl(0.01f, 0.0f, 2.5f), BlockAlveary.TX_57_HTOF, BlockAlveary.TX_58_HTON, BLOCK_META);
	}

}

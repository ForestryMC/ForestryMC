/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.apiculture.gadgets;

import forestry.api.genetics.AlleleManager;
import forestry.apiculture.genetics.BeeHelper;
import forestry.core.gadgets.TileNaturalistChest;
import forestry.core.network.GuiId;

public class TileApiaristChest extends TileNaturalistChest {

	public TileApiaristChest() {
		super(AlleleManager.alleleRegistry.getSpeciesRoot(BeeHelper.UID), GuiId.ApiaristChestGUI.ordinal());
	}

}

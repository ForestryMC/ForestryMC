/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.lepidopterology.gadgets;

import forestry.api.genetics.AlleleManager;
import forestry.core.gadgets.TileNaturalistChest;
import forestry.core.network.GuiId;

public class TileLepidopteristChest extends TileNaturalistChest {

	public TileLepidopteristChest() {
		super(AlleleManager.alleleRegistry.getSpeciesRoot("rootButterflies"), GuiId.LepidopteristChestGUI.ordinal());
	}

}

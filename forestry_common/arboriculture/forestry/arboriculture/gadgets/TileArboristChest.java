/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.arboriculture.gadgets;

import forestry.api.genetics.AlleleManager;
import forestry.arboriculture.genetics.TreeHelper;
import forestry.core.gadgets.TileNaturalistChest;
import forestry.core.network.GuiId;

public class TileArboristChest extends TileNaturalistChest {
	public TileArboristChest() {
		super(AlleleManager.alleleRegistry.getSpeciesRoot(TreeHelper.UID), GuiId.ArboristChestGUI.ordinal());
	}


}

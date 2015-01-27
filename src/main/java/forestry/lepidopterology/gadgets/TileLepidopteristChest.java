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
package forestry.lepidopterology.gadgets;

import forestry.api.genetics.AlleleManager;
import forestry.core.gadgets.TileNaturalistChest;
import forestry.core.network.GuiId;

public class TileLepidopteristChest extends TileNaturalistChest {

	public TileLepidopteristChest() {
		super(AlleleManager.alleleRegistry.getSpeciesRoot("rootButterflies"), GuiId.LepidopteristChestGUI.ordinal());
	}

}

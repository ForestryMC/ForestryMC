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
package forestry.apiculture.tiles;

import java.util.Collections;

import forestry.api.apiculture.DefaultBeeListener;
import forestry.api.apiculture.IBeeHousingInventory;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.apiculture.BeehouseBeeModifier;
import forestry.apiculture.InventoryBeeHousing;
import forestry.core.network.GuiId;

public class TileBeehouse extends TileBeeHousingBase {
	private static final IBeeModifier beeModifier = new BeehouseBeeModifier();

	private final IBeeListener beeListener;
	private final InventoryBeeHousing beeInventory;

	public TileBeehouse() {
		super(GuiId.BeehouseGUI, "bee.house");
		this.beeListener = new DefaultBeeListener();

		beeInventory = new InventoryBeeHousing(12, "Items", getAccessHandler());
		beeInventory.disableAutomation();
		setInternalInventory(beeInventory);
	}

	@Override
	public IBeeHousingInventory getBeeInventory() {
		return beeInventory;
	}

	@Override
	public Iterable<IBeeModifier> getBeeModifiers() {
		return Collections.singleton(beeModifier);
	}

	@Override
	public Iterable<IBeeListener> getBeeListeners() {
		return Collections.singleton(beeListener);
	}
}

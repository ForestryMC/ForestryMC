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
import forestry.api.apiculture.DefaultBeeModifier;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousingInventory;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.apiculture.inventory.InventoryTileBeeHousing;
import forestry.core.network.GuiId;

public class TileBeehouse extends TileAbstractBeeHousing {
	private static final IBeeModifier beeModifier = new BeehouseBeeModifier();

	private final IBeeListener beeListener;
	private final IBeeHousingInventory beeInventory;

	public TileBeehouse() {
		super(GuiId.BeehouseGUI, "bee.house");
		this.beeListener = new DefaultBeeListener();

		InventoryTileBeeHousing beeHousingInventory = new InventoryTileBeeHousing(this, 12, "Items");
		beeHousingInventory.disableAutomation();

		this.beeInventory = beeHousingInventory;
		setInternalInventory(beeHousingInventory);
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

	public static class BeehouseBeeModifier extends DefaultBeeModifier {
		@Override
		public float getTerritoryModifier(IBeeGenome genome, float currentModifier) {
			return 1.0f;
		}

		@Override
		public float getProductionModifier(IBeeGenome genome, float currentModifier) {
			return 0.25f;
		}

		@Override
		public float getMutationModifier(IBeeGenome genome, IBeeGenome mate, float currentModifier) {
			return 0.0f;
		}

		@Override
		public float getLifespanModifier(IBeeGenome genome, IBeeGenome mate, float currentModifier) {
			return 3.0f;
		}

		@Override
		public float getFloweringModifier(IBeeGenome genome, float currentModifier) {
			return 3.0f;
		}

		@Override
		public float getGeneticDecay(IBeeGenome genome, float currentModifier) {
			return 0.0f;
		}
	}
}

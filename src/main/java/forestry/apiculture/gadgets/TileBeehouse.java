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

import java.util.Collections;

import net.minecraft.entity.player.EntityPlayer;

import forestry.api.apiculture.DefaultBeeListener;
import forestry.api.apiculture.DefaultBeeModifier;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousingInventory;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.core.ForestryAPI;
import forestry.core.gadgets.TileBase;
import forestry.core.network.GuiId;

public class TileBeehouse extends TileBeeHousing {
	private static final IBeeModifier beeModifier = new BeehouseBeeModifier();

	private final IBeeListener beeListener;
	private final IBeeHousingInventory beeInventory;

	public TileBeehouse() {
		this.beeListener = new DefaultBeeListener();

		TileBeeHousingInventory beeHousingInventory = new TileBeeHousingInventory(this, 12, "Items");
		beeHousingInventory.disableAutomation();

		this.beeInventory = beeHousingInventory;
		setInternalInventory(beeHousingInventory);
	}

	@Override
	public IBeeHousingInventory getBeeInventory() {
		return beeInventory;
	}

	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, GuiId.BeehouseGUI.ordinal(), worldObj, xCoord, yCoord, zCoord);
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

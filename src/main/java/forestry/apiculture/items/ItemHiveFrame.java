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
package forestry.apiculture.items;

import net.minecraft.item.ItemStack;

import forestry.api.apiculture.DefaultBeeModifier;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IHiveFrame;
import forestry.api.core.Tabs;
import forestry.core.items.ItemForestry;

public class ItemHiveFrame extends ItemForestry implements IHiveFrame {

	private final IBeeModifier beeModifier;

	public ItemHiveFrame(int maxDamage, float geneticDecay) {
		setMaxStackSize(1);
		setMaxDamage(maxDamage);
		setCreativeTab(Tabs.tabApiculture);

		this.beeModifier = new HiveFrameBeeModifier(geneticDecay);
	}

	@Override
	public ItemStack frameUsed(IBeeHousing housing, ItemStack frame, IBee queen, int wear) {
		frame.setItemDamage(frame.getItemDamage() + wear);
		if (frame.getItemDamage() >= frame.getMaxDamage()) {
			return null;
		} else {
			return frame;
		}
	}

	@Override
	public IBeeModifier getBeeModifier() {
		return beeModifier;
	}

	private static class HiveFrameBeeModifier extends DefaultBeeModifier {
		private final float geneticDecay;

		public HiveFrameBeeModifier(float geneticDecay) {
			this.geneticDecay = geneticDecay;
		}

		@Override
		public float getProductionModifier(IBeeGenome genome, float currentModifier) {
			return (currentModifier < 10f) ? 2f : 1f;
		}

		@Override
		public float getGeneticDecay(IBeeGenome genome, float currentModifier) {
			return this.geneticDecay;
		}
	}
}

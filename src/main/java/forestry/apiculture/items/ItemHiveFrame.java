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

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.apiculture.DefaultBeeModifier;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IHiveFrame;
import forestry.api.core.Tabs;
import forestry.core.items.ItemForestry;
import forestry.core.utils.Translator;

public class ItemHiveFrame extends ItemForestry implements IHiveFrame {

	private final HiveFrameBeeModifier beeModifier;

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
			return ItemStack.EMPTY;
		} else {
			return frame;
		}
	}

	@Override
	public IBeeModifier getBeeModifier(ItemStack frame) {
		return beeModifier;
	}

	@Override
	public IBeeModifier getBeeModifier() {
		return beeModifier;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced) {
		super.addInformation(stack, world, tooltip, advanced);
		beeModifier.addInformation(stack, world, tooltip, advanced);
		if (!stack.isItemDamaged()) {
			tooltip.add(Translator.translateToLocalFormatted("item.for.durability", stack.getMaxDamage()));
		}
	}

	private static class HiveFrameBeeModifier extends DefaultBeeModifier {
		private static final float production = 2f;
		private final float geneticDecay;

		public HiveFrameBeeModifier(float geneticDecay) {
			this.geneticDecay = geneticDecay;
		}

		@Override
		public float getProductionModifier(IBeeGenome genome, float currentModifier) {
			return currentModifier < 10f ? production : 1f;
		}

		@Override
		public float getGeneticDecay(IBeeGenome genome, float currentModifier) {
			return this.geneticDecay;
		}

		@SideOnly(Side.CLIENT)
		public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced) {
			tooltip.add(Translator.translateToLocalFormatted("item.for.bee.modifier.production", production));
			tooltip.add(Translator.translateToLocalFormatted("item.for.bee.modifier.genetic.decay", geneticDecay));
		}
	}
}

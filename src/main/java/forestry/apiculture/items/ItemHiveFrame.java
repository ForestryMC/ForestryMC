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

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.apiculture.DefaultBeeModifier;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.apiculture.hives.IHiveFrame;
import forestry.api.core.ItemGroups;
import forestry.core.items.ItemForestry;

import genetics.api.individual.IGenome;

public class ItemHiveFrame extends ItemForestry implements IHiveFrame {

	private final HiveFrameBeeModifier beeModifier;

	public ItemHiveFrame(int maxDamage, float geneticDecay) {
		super((new Item.Properties())
				.durability(maxDamage)
				.tab(ItemGroups.tabApiculture));

		this.beeModifier = new HiveFrameBeeModifier(geneticDecay);
	}

	@Override
	public ItemStack frameUsed(IBeeHousing housing, ItemStack frame, IBee queen, int wear) {
		if (frame.hurt(wear, housing.getWorldObj().getRandom(), null)) {
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
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag advanced) {
		super.appendHoverText(stack, world, tooltip, advanced);
		beeModifier.addInformation(stack, world, tooltip, advanced);
		if (!stack.isDamaged()) {
			tooltip.add(Component.translatable("item.forestry.durability", stack.getMaxDamage()));
		}
	}

	private static class HiveFrameBeeModifier extends DefaultBeeModifier {
		private static final float production = 2f;
		private final float geneticDecay;

		public HiveFrameBeeModifier(float geneticDecay) {
			this.geneticDecay = geneticDecay;
		}

		@Override
		public float getProductionModifier(IGenome genome, float currentModifier) {
			return currentModifier < 10f ? production : 1f;
		}

		@Override
		public float getGeneticDecay(IGenome genome, float currentModifier) {
			return this.geneticDecay;
		}

		@OnlyIn(Dist.CLIENT)
		public void addInformation(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag advanced) {
			tooltip.add(Component.translatable("item.forestry.bee.modifier.production", production));
			tooltip.add(Component.translatable("item.forestry.bee.modifier.genetic.decay", geneticDecay));
		}
	}
}

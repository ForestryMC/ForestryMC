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
package forestry.food;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;

import forestry.api.food.IBeverageEffect;
import forestry.api.food.IInfuserManager;

/**
 * contains the available mixtures.
 */
public class InfuserMixtureManager implements IInfuserManager {

	private final List<InfuserMixture> mixtures = new ArrayList<>();

	@Override
	public void addMixture(int meta, ItemStack ingredient, IBeverageEffect effect) {
		this.mixtures.add(new InfuserMixture(meta, ingredient, effect));
	}

	@Override
	public void addMixture(int meta, ItemStack[] ingredients, IBeverageEffect effect) {
		this.mixtures.add(new InfuserMixture(meta, ingredients, effect));
	}

	public boolean isIngredient(ItemStack itemstack) {
		for (InfuserMixture ingredient : mixtures) {
			if (ingredient.isIngredient(itemstack)) {
				return true;
			}
		}

		return false;
	}

	private InfuserMixture[] getMatchingMixtures(ItemStack[] ingredients) {

		ArrayList<InfuserMixture> matches = new ArrayList<>();

		for (InfuserMixture mixture : mixtures) {
			if (mixture.matches(ingredients)) {
				matches.add(mixture);
			}
		}

		return matches.toArray(new InfuserMixture[matches.size()]);
	}

	@Override
	public boolean hasMixtures(ItemStack[] ingredients) {
		return getMatchingMixtures(ingredients).length > 0;
	}

	@Override
	public ItemStack[] getRequired(ItemStack[] ingredients) {
		InfuserMixture[] mixtures = getMatchingMixtures(ingredients);
		ArrayList<ItemStack> required = new ArrayList<>();

		for (InfuserMixture mixture : mixtures) {
			required.addAll(Arrays.asList(mixture.getIngredients()));
		}

		return required.toArray(new ItemStack[required.size()]);
	}

	@Override
	public ItemStack getSeasoned(ItemStack base, ItemStack[] ingredients) {
		InfuserMixture[] mixtures = getMatchingMixtures(ingredients);
		List<IBeverageEffect> effects = BeverageEffect.loadEffects(base);

		int weight = 0;
		int meta = 0;
		for (InfuserMixture mixture : mixtures) {
			effects.add(mixture.getEffect());
			if (mixture.getWeight() > weight) {
				weight = mixture.getWeight();
				meta = mixture.getMeta();
			}
		}

		ItemStack seasoned = base.copy();
		seasoned.setItemDamage(meta);
		BeverageEffect.saveEffects(seasoned, effects);
		return seasoned;
	}
}

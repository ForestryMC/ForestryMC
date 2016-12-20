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
import java.util.List;

import forestry.api.food.IBeverageEffect;
import forestry.api.food.IInfuserManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

/**
 * contains the available mixtures.
 */
public class InfuserMixtureManager implements IInfuserManager {

	private final List<InfuserMixture> mixtures = new ArrayList<>();

	@Override
	public void addMixture(int meta, ItemStack ingredient, IBeverageEffect effect) {
		NonNullList<ItemStack> ingredients = NonNullList.create();
		ingredients.add(ingredient);
		this.mixtures.add(new InfuserMixture(meta, ingredients, effect));
	}

	@Override
	public void addMixture(int meta, NonNullList<ItemStack> ingredients, IBeverageEffect effect) {
		this.mixtures.add(new InfuserMixture(meta, ingredients, effect));
	}

	@Override
	public boolean isIngredient(ItemStack itemstack) {
		for (InfuserMixture ingredient : mixtures) {
			if (ingredient.isIngredient(itemstack)) {
				return true;
			}
		}

		return false;
	}

	private List<InfuserMixture> getMatchingMixtures(NonNullList<ItemStack> ingredients) {

		List<InfuserMixture> matches = new ArrayList<>();

		for (InfuserMixture mixture : mixtures) {
			if (mixture.matches(ingredients)) {
				matches.add(mixture);
			}
		}

		return matches;
	}

	@Override
	public boolean hasMixtures(NonNullList<ItemStack> ingredients) {
		return !getMatchingMixtures(ingredients).isEmpty();
	}

	@Override
	public NonNullList<ItemStack> getRequired(NonNullList<ItemStack> ingredients) {
		List<InfuserMixture> mixtures = getMatchingMixtures(ingredients);
		NonNullList<ItemStack> required = NonNullList.create();

		for (InfuserMixture mixture : mixtures) {
			required.addAll(mixture.getIngredients());
		}

		return required;
	}

	@Override
	public ItemStack getSeasoned(ItemStack base, NonNullList<ItemStack> ingredients) {
		List<InfuserMixture> mixtures = getMatchingMixtures(ingredients);
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

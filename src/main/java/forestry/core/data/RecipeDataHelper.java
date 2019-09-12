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
package forestry.core.data;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.function.Consumer;

import net.minecraft.data.IFinishedRecipe;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ICondition;

import forestry.core.config.Constants;
import forestry.core.recipes.ModuleEnabledCondition;

//Useful when there is either a recipe, or it is disabled. Convenience from not having to provide an
//ID when building
//
//also contains convienience method for module condition
public class RecipeDataHelper {

	private Consumer<IFinishedRecipe> consumer;

	public RecipeDataHelper(Consumer<IFinishedRecipe> consumer) {
		this.consumer = consumer;
	}

	public Consumer<IFinishedRecipe> getConsumer() {
		return consumer;
	}

	public void simpleConditionalRecipe(Consumer<Consumer<IFinishedRecipe>> recipe, ICondition... conditions) {
		simpleConditionalRecipe(recipe, null, conditions);
	}

	public void simpleConditionalRecipe(Consumer<Consumer<IFinishedRecipe>> recipe, @Nullable ResourceLocation id, ICondition... conditions) {
		ConditionalRecipe.Builder builder = ConditionalRecipe.builder();
		for (ICondition condition : conditions) {
			builder.addCondition(condition);
		}

		Holder<IFinishedRecipe> finishedRecipeHolder = new Holder<>();
		recipe.accept(finishedRecipeHolder::set);

		IFinishedRecipe finishedRecipe = finishedRecipeHolder.get();
		builder.addRecipe(finishedRecipe);
		builder.build(consumer, id == null ? finishedRecipe.getID() : id);
	}

	public void moduleConditionRecipe(Consumer<Consumer<IFinishedRecipe>> recipe, @Nullable ResourceLocation id, String... moduleUIDs) {
		simpleConditionalRecipe(recipe, id, Arrays.stream(moduleUIDs).map(u -> new ModuleEnabledCondition(Constants.MOD_ID, u)).toArray(ICondition[]::new));
	}

	public void moduleConditionRecipe(Consumer<Consumer<IFinishedRecipe>> recipe, String... moduleUIDs) {
		moduleConditionRecipe(recipe, null, moduleUIDs);
	}

	private static class Holder<T> {

		private T obj;

		private Holder() {

		}

		private void set(T obj) {
			this.obj = obj;
		}

		private T get() {
			return obj;
		}
	}
}

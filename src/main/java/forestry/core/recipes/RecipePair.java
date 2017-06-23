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
package forestry.core.recipes;

import javax.annotation.Nullable;

import forestry.api.recipes.IForestryRecipe;

public class RecipePair<R extends IForestryRecipe> {
	
	public static final RecipePair EMPTY = new RecipePair(null, null);
	
	@Nullable
	private final R recipe;
	@Nullable
	private final String[][] oreDictEntries;
	
	public RecipePair(R recipe, String[][] oreDictEntries) {
		this.recipe = recipe;
		this.oreDictEntries = oreDictEntries;
	}
	
	public boolean isEmpty(){
		return recipe == null;
	}
	
	public R getRecipe() {
		return recipe;
	}
	
	public String[][] getOreDictEntries() {
		return oreDictEntries;
	}
}

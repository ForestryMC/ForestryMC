/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.arboriculture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import forestry.api.arboriculture.ITreeGenome;
import forestry.api.genetics.IFruitFamily;
import forestry.plugins.PluginArboriculture;

/**
 * Simple fruit provider which drops from any leaf block according to yield and either marks all leave blocks as fruit leaves or none.
 */
public class FruitProviderRandom extends FruitProviderNone {

	HashMap<ItemStack, Float> products = new HashMap<ItemStack, Float>();
	int colour = 0xffffff;

	public FruitProviderRandom(String key, IFruitFamily family, ItemStack product, float modifier) {
		super(key, family);
		products.put(product, modifier);
	}

	public FruitProviderRandom setColour(int colour) {
		this.colour = colour;
		return this;
	}

	@Override
	public int getColour(ITreeGenome genome, IBlockAccess world, int x, int y, int z, int ripeningTime) {
		return colour;
	}

	@Override
	public ItemStack[] getFruits(ITreeGenome genome, World world, int x, int y, int z, int ripeningTime) {
		ArrayList<ItemStack> product = new ArrayList<ItemStack>();

		float modeYieldMod = PluginArboriculture.treeInterface.getTreekeepingMode(world).getYieldModifier(genome, 1f);

		for (Map.Entry<ItemStack, Float> entry : products.entrySet())
			if (world.rand.nextFloat() <= genome.getYield() * modeYieldMod * entry.getValue())
				product.add(entry.getKey().copy());

		return product.toArray(new ItemStack[0]);
	}

	@Override
	public ItemStack[] getProducts() {
		return products.keySet().toArray(new ItemStack[0]);
	}

	@Override
	public ItemStack[] getSpecialty() {
		return new ItemStack[0];
	}

	@Override
	public boolean markAsFruitLeaf(ITreeGenome genome, World world, int x, int y, int z) {
		return true;
	}

}

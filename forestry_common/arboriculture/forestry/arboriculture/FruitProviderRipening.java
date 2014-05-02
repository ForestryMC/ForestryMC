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

public class FruitProviderRipening extends FruitProviderNone {

	HashMap<ItemStack, Float> products = new HashMap<ItemStack, Float>();

	int colourRipe = 0xffffff;
	int colourCallow = 0xffffff;

	int diffR, diffG, diffB = 0;

	public FruitProviderRipening(String key, IFruitFamily family, ItemStack product, float modifier) {
		super(key, family);
		products.put(product, modifier);
	}

	public FruitProviderRipening setColours(int ripe, int callow) {
		colourRipe = ripe;
		colourCallow = callow;

		diffR = (ripe >> 16 & 255) - (callow >> 16 & 255);
		diffG = (ripe >> 8 & 255) - (callow >> 8 & 255);
		diffB = (ripe & 255) - (callow & 255);

		return this;
	}

	public FruitProviderRipening setRipeningPeriod(int period) {
		ripeningPeriod = period;
		return this;
	}

	private float getRipeningStage(int ripeningTime) {
		if (ripeningTime >= ripeningPeriod)
			return 1.0f;

		return (float) ripeningTime / ripeningPeriod;
	}

	@Override
	public ItemStack[] getFruits(ITreeGenome genome, World world, int x, int y, int z, int ripeningTime) {
		ArrayList<ItemStack> product = new ArrayList<ItemStack>();

		float stage = getRipeningStage(ripeningTime);
		if (stage < 0.5f)
			return new ItemStack[0];

		float modeYieldMod = PluginArboriculture.treeInterface.getTreekeepingMode(world).getYieldModifier(genome, 1f);

		for (Map.Entry<ItemStack, Float> entry : products.entrySet())
			if (world.rand.nextFloat() <= genome.getYield() * entry.getValue() * modeYieldMod * 5.0f * stage)
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

	@Override
	public int getColour(ITreeGenome genome, IBlockAccess world, int x, int y, int z, int ripeningTime) {
		float stage = getRipeningStage(ripeningTime);

		int r = (colourCallow >> 16 & 255) + (int) (diffR * stage);
		int g = (colourCallow >> 8 & 255) + (int) (diffG * stage);
		int b = (colourCallow & 255) + (int) (diffB * stage);

		// System.out.println(String.format("Calcultated rgb %s/%s/%s from %s and %s, resulting in %s",
		// r, g, b, colourCallow, stage, (r & 255) << 16 | (g & 255) << 8 | b & 255));
		return (r & 255) << 16 | (g & 255) << 8 | b & 255;
	}

}

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
package forestry.arboriculture;

import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import forestry.api.arboriculture.ITreeGenome;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.IFruitFamily;

/**
 * Simple fruit provider which drops from any leaf block according to yield and either marks all leave blocks as fruit leaves or none.
 */
public class FruitProviderRandom extends FruitProviderNone {
	private final Map<ItemStack, Float> products = new HashMap<>();
	private int colour = 0xffffff;

	public FruitProviderRandom(String unlocalizedDescription, IFruitFamily family, ItemStack product, float modifier) {
		super(unlocalizedDescription, family);
		products.put(product, modifier);
	}

	public FruitProviderRandom setColour(Color colour) {
		this.colour = colour.getRGB();
		return this;
	}

	@Override
	public int getColour(ITreeGenome genome, IBlockAccess world, BlockPos pos, int ripeningTime) {
		return colour;
	}

	@Override
	public int getDecorativeColor() {
		return colour;
	}

	@Override
	public NonNullList<ItemStack> getFruits(ITreeGenome genome, World world, BlockPos pos, int ripeningTime) {
		NonNullList<ItemStack> product = NonNullList.create();

		float modeYieldMod = TreeManager.treeRoot.getTreekeepingMode(world).getYieldModifier(genome, 1f);

		for (Map.Entry<ItemStack, Float> entry : products.entrySet()) {
			if (world.rand.nextFloat() <= genome.getYield() * modeYieldMod * entry.getValue()) {
				product.add(entry.getKey().copy());
			}
		}

		return product;
	}

	@Override
	public Map<ItemStack, Float> getProducts() {
		return Collections.unmodifiableMap(products);
	}

	@Override
	public boolean isFruitLeaf(ITreeGenome genome, World world, BlockPos pos) {
		return true;
	}

}

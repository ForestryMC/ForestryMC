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

import forestry.api.arboriculture.ITreeGenome;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.IFruitFamily;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class FruitProviderRipening extends FruitProviderNone {
	private final Map<ItemStack, Float> products = new HashMap<>();
	private int colourCallow = 0xffffff;
	private int diffR;
	private int diffG;
	private int diffB;

	public FruitProviderRipening(String unlocalizedDescription, IFruitFamily family, ItemStack product, float modifier) {
		super(unlocalizedDescription, family);
		products.put(product, modifier);
	}

	public FruitProviderRipening setColours(Color ripe, Color callow) {
		colourCallow = callow.getRGB();
		int ripeRGB = ripe.getRGB();

		diffR = (ripeRGB >> 16 & 255) - (colourCallow >> 16 & 255);
		diffG = (ripeRGB >> 8 & 255) - (colourCallow >> 8 & 255);
		diffB = (ripeRGB & 255) - (colourCallow & 255);

		return this;
	}

	public FruitProviderRipening setRipeningPeriod(int period) {
		ripeningPeriod = period;
		return this;
	}

	private float getRipeningStage(int ripeningTime) {
		if (ripeningTime >= ripeningPeriod) {
			return 1.0f;
		}

		return (float) ripeningTime / ripeningPeriod;
	}

	@Override
	public NonNullList<ItemStack> getFruits(ITreeGenome genome, World world, BlockPos pos, int ripeningTime) {
		NonNullList<ItemStack> product = NonNullList.create();

		float stage = getRipeningStage(ripeningTime);
		if (stage >= 0.5f) {
			float modeYieldMod = TreeManager.treeRoot.getTreekeepingMode(world).getYieldModifier(genome, 1f);

			for (Map.Entry<ItemStack, Float> entry : products.entrySet()) {
				if (world.rand.nextFloat() <= genome.getYield() * entry.getValue() * modeYieldMod * 5.0f * stage) {
					product.add(entry.getKey().copy());
				}
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

	@Override
	public int getColour(ITreeGenome genome, IBlockAccess world, BlockPos pos, int ripeningTime) {
		float stage = getRipeningStage(ripeningTime);
		return getColour(stage);
	}

	private int getColour(float stage) {
		int r = (colourCallow >> 16 & 255) + (int) (diffR * stage);
		int g = (colourCallow >> 8 & 255) + (int) (diffG * stage);
		int b = (colourCallow & 255) + (int) (diffB * stage);

		return (r & 255) << 16 | (g & 255) << 8 | b & 255;
	}

	@Override
	public int getDecorativeColor() {
		return getColour(1.0f);
	}
}

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

import forestry.api.genetics.IFruitFamily;
import forestry.api.genetics.products.IProductList;
import forestry.api.genetics.products.Product;
import forestry.core.genetics.ProductListWrapper;
import genetics.api.individual.IGenome;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import java.awt.*;
import java.util.function.Supplier;

public class FruitProviderRipening extends FruitProviderNone {
    private int colourCallow = 0xffffff;
    private int diffR;
    private int diffG;
    private int diffB;
    private ProductListWrapper products;

    public FruitProviderRipening(String unlocalizedDescription, IFruitFamily family, Supplier<ItemStack> product, float modifier) {
        super(unlocalizedDescription, family);
        this.products = ProductListWrapper.create();
        this.products.addProduct(product, modifier);
    }

    @Override
    public void onStartSetup() {
        this.products = products.bake();
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
    public NonNullList<ItemStack> getFruits(IGenome genome, World world, BlockPos pos, int ripeningTime) {
        NonNullList<ItemStack> product = NonNullList.create();
        products.addProducts(world, pos, product, Product::getChance, world.rand);

        return product;
    }

    @Override
    public IProductList getProducts() {
        return products;
    }

    @Override
    public boolean isFruitLeaf(IGenome genome, IWorld world, BlockPos pos) {
        return true;
    }

    @Override
    public int getColour(IGenome genome, IBlockReader world, BlockPos pos, int ripeningTime) {
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

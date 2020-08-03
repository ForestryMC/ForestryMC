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

import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.genetics.IAlleleFruit;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.api.genetics.IFruitFamily;
import forestry.api.genetics.products.IProductList;
import forestry.core.genetics.ProductListWrapper;
import forestry.core.utils.BlockUtil;
import genetics.api.individual.IGenome;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Random;
import java.util.function.Supplier;

public class FruitProviderPod extends FruitProviderNone {

    public enum EnumPodType {
        COCOA, DATES, PAPAYA;
        //, COCONUT;

        public String getModelName() {
            return toString().toLowerCase(Locale.ENGLISH);
        }
    }

    private final EnumPodType type;

    private ProductListWrapper products;

    public FruitProviderPod(String unlocalizedDescription, IFruitFamily family, EnumPodType type, Supplier<ItemStack> dropOnMature) {
        super(unlocalizedDescription, family);
        this.type = type;
        this.products = ProductListWrapper.create();
        this.products.addProduct(dropOnMature, 1.0F);
    }

    @Override
    public void onStartSetup() {
        products = products.bake();
    }

    @Override
    public boolean requiresFruitBlocks() {
        return true;
    }

    @Override
    public NonNullList<ItemStack> getFruits(@Nullable IGenome genome, World world, BlockPos pos, int ripeningTime) {
        if (ripeningTime >= 2) {
            return products.getPossibleStacks();
        }

        return NonNullList.create();
    }

    @Override
    public boolean trySpawnFruitBlock(IGenome genome, IWorld world, Random rand, BlockPos pos) {
        if (rand.nextFloat() > getFruitChance(genome, world, pos)) {
            return false;
        }

        if (type == EnumPodType.COCOA) {
            return BlockUtil.tryPlantCocoaPod(world, pos);
        } else {
            IAlleleFruit activeAllele = genome.getActiveAllele(TreeChromosomes.FRUITS);
            return TreeManager.treeRoot.setFruitBlock(world, genome, activeAllele, genome.getActiveValue(TreeChromosomes.YIELD), pos);
        }
    }

    @Override
    public ResourceLocation getSprite(IGenome genome, IBlockReader world, BlockPos pos, int ripeningTime) {
        return null;
    }

    @Override
    public ResourceLocation getDecorativeSprite() {
        return null;
    }

    @Override
    public IProductList getProducts() {
        return products;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerSprites(TextureStitchEvent.Pre event) {
    }

    @Override
    public String getModelName() {
        return type.getModelName();
    }
}

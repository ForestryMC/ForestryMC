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
package forestry.factory;

import com.google.common.collect.ImmutableSet;
import forestry.api.recipes.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class DummyManagers {

    public static abstract class DummyCraftingProvider<T extends IForestryRecipe> implements ICraftingProvider<T> {
        @Override
        public boolean addRecipe(T recipe) {
            return false;
        }

        @Override
        public boolean removeRecipe(T recipe) {
            return false;
        }

        @Override
        public Set<T> recipes() {
            return ImmutableSet.of();
        }
    }

    public static class DummyCarpenterManager extends DummyCraftingProvider<ICarpenterRecipe> implements ICarpenterManager {

        @Override
        public void addRecipe(ItemStack box, ItemStack product, Object... materials) {

        }

        @Override
        public void addRecipe(int packagingTime, ItemStack box, ItemStack product, Object... materials) {

        }

        @Override
        public void addRecipe(int packagingTime, @Nullable FluidStack liquid, ItemStack box, ItemStack product, Object... materials) {

        }
    }

    public static class DummyCentrifugeManager extends DummyCraftingProvider<ICentrifugeRecipe> implements ICentrifugeManager {

        @Override
        public void addRecipe(int timePerItem, ItemStack resource, Map<ItemStack, Float> products) {

        }
    }

    public static class DummyFabricatorManager extends DummyCraftingProvider<IFabricatorRecipe> implements IFabricatorManager {

        @Override
        public void addRecipe(ItemStack plan, FluidStack molten, ItemStack result, Object[] pattern) {

        }

    }

    public static class DummyFabricatorSmeltingManager extends DummyCraftingProvider<IFabricatorSmeltingRecipe> implements IFabricatorSmeltingManager {

        @Override
        public void addSmelting(ItemStack resource, FluidStack molten, int meltingPoint) {

        }
    }

    public static class DummyFermenterManager extends DummyCraftingProvider<IFermenterRecipe> implements IFermenterManager {

        @Override
        public void addRecipe(ItemStack resource, int fermentationValue, float modifier, FluidStack output, FluidStack liquid) {

        }

        @Override
        public void addRecipe(ItemStack resource, int fermentationValue, float modifier, FluidStack output) {

        }

        @Override
        public void addRecipe(String resource, int fermentationValue, float modifier, FluidStack output, FluidStack liquid) {

        }

        @Override
        public void addRecipe(String resource, int fermentationValue, float modifier, FluidStack output) {

        }
    }

    public static class DummyMoistenerManager extends DummyCraftingProvider<IMoistenerRecipe> implements IMoistenerManager {

        @Override
        public void addRecipe(ItemStack resource, ItemStack product, int timePerItem) {

        }
    }

    public static class DummySqueezerManager extends DummyCraftingProvider<ISqueezerRecipe> implements ISqueezerManager {

        @Override
        public void addRecipe(int timePerItem, NonNullList<ItemStack> resources, FluidStack liquid, ItemStack remnants, int chance) {

        }

        @Override
        public void addRecipe(int timePerItem, ItemStack resources, FluidStack liquid, ItemStack remnants, int chance) {

        }

        @Override
        public void addRecipe(int timePerItem, NonNullList<ItemStack> resources, FluidStack liquid) {

        }

        @Override
        public void addRecipe(int timePerItem, ItemStack resources, FluidStack liquid) {

        }

        @Override
        public void addContainerRecipe(int timePerItem, ItemStack emptyContainer, @Nullable ItemStack remnants, float chance) {

        }
    }

    public static class DummyStillManager extends DummyCraftingProvider<IStillRecipe> implements IStillManager {

        @Override
        public void addRecipe(int cyclesPerUnit, FluidStack input, FluidStack output) {

        }
    }
}

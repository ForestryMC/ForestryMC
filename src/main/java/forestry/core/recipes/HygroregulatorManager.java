package forestry.core.recipes;

import forestry.api.recipes.IHygroregulatorManager;
import forestry.api.recipes.IHygroregulatorRecipe;
import forestry.factory.recipes.AbstractCraftingProvider;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class HygroregulatorManager extends AbstractCraftingProvider<IHygroregulatorRecipe> implements IHygroregulatorManager {
    private final Set<Fluid> recipeFluids = new HashSet<>();

    public HygroregulatorManager() {
        super(IHygroregulatorRecipe.TYPE);
    }

    @Nullable
    public IHygroregulatorRecipe findMatchingRecipe(RecipeManager manager, FluidStack liquid) {
        if (liquid.getAmount() <= 0) {
            return null;
        }

        for (IHygroregulatorRecipe recipe : getRecipes(manager)) {
            FluidStack resource = recipe.getResource();
            if (resource.isFluidEqual(liquid)) {
                return recipe;
            }
        }

        return null;
    }

    public Set<Fluid> getRecipeFluids(RecipeManager manager) {
        if (recipeFluids.isEmpty()) {
            for (IHygroregulatorRecipe recipe : getRecipes(manager)) {
                FluidStack fluidStack = recipe.getResource();
                recipeFluids.add(fluidStack.getFluid());
            }
        }

        return Collections.unmodifiableSet(recipeFluids);
    }
}

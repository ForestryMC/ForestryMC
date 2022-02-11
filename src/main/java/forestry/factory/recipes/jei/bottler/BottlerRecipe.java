package forestry.factory.recipes.jei.bottler;

import javax.annotation.Nullable;

import net.minecraft.world.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;

public record BottlerRecipe(
		ItemStack inputStack,
		FluidStack fluid,
		@Nullable ItemStack outputStack,
		boolean fillRecipe) {}

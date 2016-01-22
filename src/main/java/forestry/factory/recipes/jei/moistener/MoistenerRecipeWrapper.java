package forestry.factory.recipes.jei.moistener;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import forestry.api.fuels.MoistenerFuel;
import forestry.api.recipes.IMoistenerRecipe;
import forestry.core.recipes.jei.ForestryRecipeWrapper;
import net.minecraft.item.ItemStack;

public class MoistenerRecipeWrapper extends ForestryRecipeWrapper<IMoistenerRecipe>{
	
	@Nonnull
	private MoistenerFuel fuel;
	
	public MoistenerRecipeWrapper(@Nonnull IMoistenerRecipe recipe, MoistenerFuel fuel) {
		super(recipe);
		this.fuel = fuel;
	}
	
	@Override
	public List getInputs() {
		List<ItemStack> inputs = new ArrayList<>();
		inputs.add(recipe.getProduct());
		if(fuel.item != null)
			inputs.add(fuel.item);
		return inputs;
	}

	@Override
	public List getOutputs() {
		List<ItemStack> outputs = new ArrayList<>();
		outputs.add(recipe.getProduct());
		if(fuel.product != null)
			outputs.add(fuel.product);
		return outputs;
	}
	
	public MoistenerFuel getFuel() {
		return fuel;
	}

}

package forestry.farming.compat;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuit;
import forestry.api.farming.IFarmCircuit;
import forestry.api.farming.IFarmLogic;
import forestry.api.farming.IFarmProperties;
import net.minecraft.world.item.crafting.RecipeManager;

import javax.annotation.Nullable;
import java.util.List;

public class FarmingInfoRecipeMaker {
	public static List<FarmingInfoRecipe> getRecipes(@Nullable RecipeManager manager) {
		return ChipsetManager.solderManager.getRecipes(manager)
			.<FarmingInfoRecipe>mapMulti((circuitRecipe, consumer) -> {
				ICircuit circuit = circuitRecipe.getCircuit();
				if (circuit instanceof IFarmCircuit farmCircuit) {
					IFarmLogic logic = farmCircuit.getFarmLogic();
					if (logic.isManual()) {
						IFarmProperties properties = logic.getProperties();
						consumer.accept(new FarmingInfoRecipe(circuitRecipe.getResource(), properties, circuit));
					}
				}
			})
			.toList();
	}
}

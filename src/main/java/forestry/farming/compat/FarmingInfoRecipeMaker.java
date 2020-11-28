package forestry.farming.compat;

import java.util.ArrayList;
import java.util.List;

public class FarmingInfoRecipeMaker {
    public static List<FarmingInfoRecipeWrapper> getRecipes() {
        List<FarmingInfoRecipeWrapper> recipes = new ArrayList<>();
//        for (CircuitRecipe circuitRecipe : ChipsetManager.solderManager.getRecipes()) {
//            ICircuit circuit = circuitRecipe.getCircuit();
//            if (circuit instanceof IFarmCircuit) {
//                IFarmCircuit farmCircuit = (IFarmCircuit) circuit;
//                IFarmLogic logic = farmCircuit.getFarmLogic();
//                if (logic.isManual()) {
//                    IFarmProperties properties = logic.getProperties();
//                    recipes.add(new FarmingInfoRecipeWrapper(circuitRecipe.getResource(), properties, circuit));
//                }
//            }
//        }

        return recipes;
    }
}

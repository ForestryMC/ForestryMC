package forestry.core.recipes.jei;

import forestry.api.fuels.RainSubstrate;
import forestry.api.recipes.ICarpenterRecipe;
import forestry.api.recipes.ICentrifugeRecipe;
import forestry.api.recipes.IFabricatorRecipe;
import forestry.api.recipes.IFermenterRecipe;
import forestry.api.recipes.IMoistenerRecipe;
import forestry.api.recipes.ISqueezerRecipe;
import forestry.api.recipes.IStillRecipe;
import forestry.core.config.Constants;
import forestry.factory.MachineUIDs;
import forestry.factory.recipes.jei.bottler.BottlerRecipe;
import mezz.jei.api.recipe.RecipeType;

public class ForestryRecipeType {
	public static final RecipeType<BottlerRecipe> BOTTLER = create(MachineUIDs.BOTTLER, BottlerRecipe.class);
	public static final RecipeType<ICarpenterRecipe> CARPENTER = create(MachineUIDs.CARPENTER, ICarpenterRecipe.class);
	public static final RecipeType<ICentrifugeRecipe> CENTRIFUGE = create(MachineUIDs.CENTRIFUGE, ICentrifugeRecipe.class);
	public static final RecipeType<IFabricatorRecipe> FABRICATOR = create(MachineUIDs.FABRICATOR, IFabricatorRecipe.class);
	public static final RecipeType<IFermenterRecipe> FERMENTER = create(MachineUIDs.FERMENTER, IFermenterRecipe.class);
	public static final RecipeType<IMoistenerRecipe> MOISTENER = create(MachineUIDs.MOISTENER, IMoistenerRecipe.class);
	public static final RecipeType<RainSubstrate> RAINMAKER = create(MachineUIDs.RAINMAKER, RainSubstrate.class);
	public static final RecipeType<ISqueezerRecipe> SQUEEZER = create(MachineUIDs.SQUEEZER, ISqueezerRecipe.class);
	public static final RecipeType<IStillRecipe> STILL = create(MachineUIDs.STILL, IStillRecipe.class);

	private static <T> RecipeType<T> create(String uid, Class<? extends T> recipeClass) {
		return RecipeType.create(Constants.MOD_ID, uid, recipeClass);
	}
}

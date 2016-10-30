package forestry.plugins.compat;

import forestry.api.core.ForestryAPI;
import forestry.api.farming.Farmables;
import forestry.api.recipes.RecipeManagers;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.utils.ModUtil;
import forestry.factory.recipes.StillRecipe;
import forestry.farming.logic.FarmableAgingCrop;
import forestry.plugins.BlankForestryPlugin;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

@ForestryPlugin(pluginID = ForestryPluginUids.ACT_ADD, name = "Actually Additions", author = "Ellpeck", url = "http://ellpeck.de/actadd", unlocalizedDescription = "for.plugin.actuallyadditions.description")
public class PluginActuallyAdditions extends BlankForestryPlugin{

    private static final String ACT_ADD = "actuallyadditions";

    @Override
    public boolean isAvailable(){
        return ModUtil.isModLoaded(ACT_ADD);
    }

    @Override
    public String getFailMessage(){
        return "Actually Additions not found!";
    }

    @Override
    public void registerRecipes(){
        Item canolaSeed = getItem("itemCanolaSeed");
        Item flaxSeed = getItem("itemFlaxSeed");
        Item riceSeed = getItem("itemRiceSeed");
        Item coffeeSeed = getItem("itemCoffeeSeed");

        //add farm seed planting
        if(ForestryAPI.enabledPlugins.contains(ForestryPluginUids.FARMING)){
            registerSeedPlant(canolaSeed, "blockCanola");
            registerSeedPlant(flaxSeed, "blockFlax");
            registerSeedPlant(riceSeed, "blockRice");
            registerSeedPlant(coffeeSeed, "blockCoffee");
        }

        //add seed squeezing
        int amount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");
        for(ItemStack seed : new ItemStack[]{new ItemStack(canolaSeed), new ItemStack(flaxSeed), new ItemStack(riceSeed), new ItemStack(coffeeSeed)}){
            RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{seed}, Fluids.SEED_OIL.getFluid(amount));
        }

        //add canola squeezing to canola oil
        Item misc = getItem("itemMisc");
        ItemStack canola = new ItemStack(misc, 1, 13);
        Fluid canolaOil = FluidRegistry.getFluid("canolaoil");
        RecipeManagers.squeezerManager.addRecipe(15, new ItemStack[]{canola}, new FluidStack(canolaOil, 80));

        //add canola oil fermenting in still
        Fluid oil = FluidRegistry.getFluid("oil");
        RecipeManagers.stillManager.addRecipe(200, new FluidStack(canolaOil, 5), new FluidStack(oil, 5));
    }

    private static void registerSeedPlant(Item seedItem, String blockName){
        Block plantBlock = Block.REGISTRY.getObject(new ResourceLocation(ACT_ADD, blockName));
        Farmables.farmables.get("farmWheat").add(new FarmableAgingCrop(new ItemStack(seedItem), plantBlock, BlockCrops.AGE, 7));
    }

    private static Item getItem(String itemName){
        return Item.REGISTRY.getObject(new ResourceLocation(ACT_ADD, itemName));
    }
}

package forestry.plugins;

import io.netty.handler.logging.LogLevel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import net.minecraftforge.oredict.OreDictionary;

import com.pahimar.ee3.api.exchange.EnergyValueRegistryProxy;
import com.pahimar.ee3.api.exchange.RecipeRegistryProxy;
import com.pahimar.ee3.api.knowledge.AbilityRegistryProxy;

import cpw.mods.fml.common.FMLLog;
import forestry.api.recipes.RecipeManagers;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.fluids.Fluids;
import forestry.core.proxy.Proxies;
import forestry.core.utils.ShapedRecipeCustom;
import forestry.factory.gadgets.MachineCarpenter;
import forestry.factory.gadgets.MachineFabricator;
import forestry.factory.gadgets.MachineFermenter;
import forestry.factory.gadgets.MachineMoistener;
import forestry.factory.gadgets.MachineSqueezer;
import forestry.factory.gadgets.MachineStill;

@Plugin(pluginID = "EE3", name = "Equivalent Exchange 3", author = "MysteriousAges", url = Defaults.URL, unlocalizedDescription = "for.plugin.ee3.description")
public class PluginEE3 extends ForestryPlugin {
	
	@Override
	public boolean isAvailable() {
		return Proxies.common.isModLoaded("EE3");
	}
	
	@Override
	public String getFailMessage() {
		return "Equivalent Exchange 3 not found.";
	}
	
	@Override
	protected void preInit() {
		if (isAvailable()) {
			setupDefaultEMV();
			registerTransmutationBlacklist();
		}
	}
	
	@Override
	protected void postInit() {
		for (MachineSqueezer.Recipe squeezerRecipe : MachineSqueezer.RecipeManager.recipes) {
			addEMVToCraftingRecipe(squeezerRecipe.liquid, (Object[])squeezerRecipe.resources);
		}
		for (MachineFermenter.Recipe fermenterRecipe : MachineFermenter.RecipeManager.recipes) {
			addEMVToCraftingRecipe(fermenterRecipe.output, fermenterRecipe.liquid, fermenterRecipe.resource);
		}
		for (MachineStill.Recipe stillRecipe : MachineStill.RecipeManager.recipes) {
			addEMVToCraftingRecipe(stillRecipe.output, stillRecipe.input);
		}
		for (MachineCarpenter.Recipe carpenterRecipe : MachineCarpenter.RecipeManager.recipes) {
			ShapedRecipeCustom recipe = (ShapedRecipeCustom)carpenterRecipe.asIRecipe();
			addEMVToCraftingRecipe(recipe.getRecipeOutput(), carpenterRecipe.getLiquid(), (Object[])recipe.getIngredients());
		}
		for (MachineMoistener.Recipe moistenerRecipe : MachineMoistener.RecipeManager.recipes) {
			addEMVToCraftingRecipe(moistenerRecipe.product, moistenerRecipe.resource);
		}
		// Centrifuge is odd, because 1 input -> many products.
		for (MachineFabricator.Smelting smelting : MachineFabricator.RecipeManager.smeltings) {
			addEMVToCraftingRecipe(smelting.getProduct(), smelting.getResource());
		}
		for (MachineFabricator.Recipe fabricatorRecipe : MachineFabricator.RecipeManager.recipes) {
			ShapedRecipeCustom recipe = (ShapedRecipeCustom)fabricatorRecipe.asIRecipe();
			addEMVToCraftingRecipe(recipe.getRecipeOutput(), fabricatorRecipe.getLiquid(), recipe.getIngredients());
		}
	}
	
	private void addEMVToCraftingRecipe(Object output, Object ...recipeInputs) {
		if (isAvailable()) {
			List<Object> inputs = new ArrayList<Object>(recipeInputs.length);
			for (Object o : recipeInputs) {
				inputs.add(o);
			}
			RecipeRegistryProxy.addRecipe(output, inputs);
		}
	}
	
	private void setupDefaultEMV() {
		assignEMV(ForestryBlock.resources.getItemStack(1, 0), 64f);
		assignEMV(ForestryItem.apatite.getItemStack(), 64f);
		// TODO: Set Copper & Tin to relative from iron once EnergyValueRegistry stops returning nulls.
		assignEMV(ForestryBlock.resources.getItemStack(1, 1), 128f);
		assignEMV(ForestryItem.ingotCopper.getItemStack(), 128f);
		assignEMV(ForestryBlock.resources.getItemStack(1, 2), 192f);
		assignEMV(ForestryItem.ingotTin.getItemStack(), 192f);
		
		assignEMV(ForestryItem.ash.getItemStack(), 8f);
		assignEMV(ForestryItem.phosphor.getItemStack(), 2f);
		
		assignEMV(ForestryItem.beeswax.getItemStack(), 2f);
		assignEMV(ForestryItem.refractoryWax.getItemStack(), 16f);
		assignEMV(ForestryItem.honeyDrop.getItemStack(), 4f);
		assignEMV(ForestryItem.honeydew.getItemStack(), 6f);
		assignEMV(ForestryItem.pollenCluster.getItemStack(1, 0), 8f);
		assignEMV(ForestryItem.pollenCluster.getItemStack(1, 1), 10f);
		assignEMV(ForestryItem.propolis.getItemStack(1, 0), 16f);
		assignEMV(ForestryItem.propolis.getItemStack(1, 2), 24f);
		assignEMV(ForestryItem.propolis.getItemStack(1, 3), 20f);
		assignEMV(ForestryItem.beeComb.getItemStack(1, OreDictionary.WILDCARD_VALUE), 16f);
		assignEMV(ForestryItem.craftingMaterial.getItemStack(1, 0), 25.6f);
		assignEMV(ForestryItem.craftingMaterial.getItemStack(1, 2), 24f);
		assignEMV(ForestryItem.craftingMaterial.getItemStack(1, 5), 12f);
	}
	
	private void assignEMV(Object item, float value) {
		EnergyValueRegistryProxy.addPreAssignedEnergyValue(item, value);
	}
	
	private void registerTransmutationBlacklist() {
		AbilityRegistryProxy.setAsNotLearnable(ForestryBlock.resources.getItemStack(1, 0));
		
		AbilityRegistryProxy.setAsNotLearnable(ForestryItem.beeComb.getItemStack(1, OreDictionary.WILDCARD_VALUE));
	}
	
}

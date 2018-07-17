package forestry.plugins;

import com.google.common.base.Optional;

import java.util.stream.Stream;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import forestry.api.core.ForestryAPI;
import forestry.api.farming.IFarmRegistry;
import forestry.api.fuels.FuelManager;
import forestry.api.fuels.GeneratorFuel;
import forestry.api.modules.ForestryModule;
import forestry.api.recipes.RecipeManagers;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.farming.logic.ForestryFarmIdentifier;
import forestry.farming.logic.farmables.FarmableDoubleCrop;
import forestry.modules.ForestryModuleUids;

@SuppressWarnings("unused")
@ForestryModule(containerID = ForestryCompatPlugins.ID, moduleID = ForestryModuleUids.IMMERSIVE_ENGINEERING, name = "ImmersiveEngineering", author = "marcin212", url = Constants.URL, unlocalizedDescription = "for.module.immersiveengineering.description")
public class PluginImmersiveEngineering extends CompatPlugin {

	public PluginImmersiveEngineering() {
		super("ImmersiveEngineering", "immersiveengineering");
	}

	@Override
	public void postInit() {
		IFarmRegistry farmRegistry = ForestryAPI.farmRegistry;
		ItemStack hempSeed = getItemStack("seed");
		Block hempCrop = getBlock("hemp");
		if (hempCrop != null) {
			int seedAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");
			Stream<IProperty<?>> propertyStream = hempCrop.getBlockState().getProperties().stream();
			IProperty age = propertyStream.filter(p -> p.getName().equals("type")).findAny().orElseGet(null);

			if (hempSeed != null && hempCrop != Blocks.AIR && age != null) {
				Optional bottom0 = age.parseValue("bottom0");
				Optional bottom4 = age.parseValue("bottom4");
				Optional top0 = age.parseValue("top0");
				if (bottom0.isPresent() && top0.isPresent()) {
					IBlockState defaultState = hempCrop.getDefaultState();
					IBlockState planted = defaultState.withProperty(age, (Comparable) bottom0.get());
					IBlockState mature = defaultState.withProperty(age, (Comparable) bottom4.get());
					IBlockState topMature = defaultState.withProperty(age, (Comparable) top0.get());

					farmRegistry.registerFarmables(ForestryFarmIdentifier.CROPS, new FarmableDoubleCrop(hempSeed, planted, mature, topMature, true));
					FluidStack seedOil = Fluids.SEED_OIL.getFluid(seedAmount);
					if (seedOil != null) {
						RecipeManagers.squeezerManager.addRecipe(10, hempSeed, seedOil);
					}
				}
			}
		}
		Fluid ethanol = FluidRegistry.getFluid("ethanol");
		if (ethanol != null) {
			GeneratorFuel ethanolFuel = new GeneratorFuel(new FluidStack(ethanol, 1), (int) (32 * ForestryAPI.activeMode.getFloatSetting("fuel.ethanol.generator")), 4);
			FuelManager.generatorFuel.put(ethanol, ethanolFuel);
		}
	}
}

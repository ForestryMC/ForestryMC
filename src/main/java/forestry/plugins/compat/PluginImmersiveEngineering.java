package forestry.plugins.compat;

import com.google.common.base.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import forestry.api.core.ForestryAPI;
import forestry.api.farming.ICrop;
import forestry.api.fuels.FuelManager;
import forestry.api.fuels.GeneratorFuel;
import forestry.api.recipes.RecipeManagers;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.farming.FarmRegistry;
import forestry.farming.logic.CropDestroy;
import forestry.farming.logic.FarmableBase;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;

@ForestryPlugin(pluginID = ForestryPluginUids.IMMERSIVE_ENGINEERING, name = "ImmersiveEngineering", author = "marcin212", url = Constants.URL, unlocalizedDescription = "for.plugin.immersiveengineering.description")
public class PluginImmersiveEngineering extends CompatPlugin {
	private static final String IE = "immersiveengineering";

	public PluginImmersiveEngineering() {
		super("ImmersiveEngineering", IE);
	}

	@Override
	public void postInit() {
		ItemStack hempSeed = getItemStack("seed");
		Block hempCrop = getBlock("hemp");
		if (hempCrop != null) {
			int seedAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");
			IProperty age = hempCrop.getBlockState().getProperties().stream().filter(p -> p.getName().equals("type")).findAny().orElseGet(null);

			if (hempSeed != null && hempCrop != Blocks.AIR && age != null) {
				Optional bottom0 = age.parseValue("bottom0");
				Optional top0 = age.parseValue("top0");
				if (bottom0.isPresent() && top0.isPresent()) {
					IBlockState defaultState = hempCrop.getDefaultState();
					IBlockState planted = defaultState.withProperty(age, (Comparable) bottom0.get());
					IBlockState mature = defaultState.withProperty(age, (Comparable) top0.get());
					FarmRegistry.getInstance().registerFarmables("farmWheat", new FarmableBase(hempSeed, planted, mature, false) {
						@Override
						public ICrop getCropAt(World world, BlockPos pos, IBlockState blockState) {
							IBlockState stateUp = world.getBlockState(pos.up());
							if (stateUp != matureState) {
								return null;
							}
							return new CropDestroy(world, stateUp, pos.up(), null);
						}
					});

					RecipeManagers.squeezerManager.addRecipe(10, hempSeed, Fluids.SEED_OIL.getFluid(seedAmount));
				}
			}
		}
		Fluid ethanol = FluidRegistry.getFluid("ethanol");
		if(ethanol != null){
			GeneratorFuel ethanolFuel = new GeneratorFuel(new FluidStack(ethanol, 1), (int) (32 * ForestryAPI.activeMode.getFloatSetting("fuel.ethanol.generator")), 4);
			FuelManager.generatorFuel.put(ethanol, ethanolFuel);
		}
	}
}

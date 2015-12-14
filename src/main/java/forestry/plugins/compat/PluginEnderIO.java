package forestry.plugins.compat;

import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.fluids.Fluid;

import cpw.mods.fml.common.event.FMLInterModComms;

import forestry.api.core.ForestryAPI;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.utils.ModUtil;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.Plugin;

@Plugin(pluginID = "EnderIO", name = "EnderIO", author = "mezz", url = Constants.URL, unlocalizedDescription = "for.plugin.enderIO.description")
public class PluginEnderIO extends ForestryPlugin {

	private static final String EnderIO = "EnderIO";

	private static final String FLUID_FUEL_ADD = "fluidFuel:add";
	private static final String FLUID_COOLANT_ADD = "fluidCoolant:add";

	private static final String KEY_FLUID_NAME = "fluidName";
	private static final String KEY_POWER_PER_CYCLE = "powerPerCycle";
	private static final String KEY_TOTAL_BURN_TIME = "totalBurnTime";
	private static final String KEY_COOLING_PER_MB = "coolingPerMb";

	private static final float WATER_COOLING_PER_MB = 0.0023f;

	@Override
	public boolean isAvailable() {
		return ModUtil.isModLoaded(EnderIO);
	}

	@Override
	public String getFailMessage() {
		return "EnderIO not found";
	}

	@Override
	public void doInit() {
		Fluid ethanol = Fluids.ETHANOL.getFluid();
		if (ethanol != null) {
			int ethanolBurnTime = Math.round(Constants.ENGINE_CYCLE_DURATION_ETHANOL * ForestryAPI.activeMode.getFloatSetting("fuel.ethanol.combustion"));
			addFuel(ethanol, 40, ethanolBurnTime);
		}

		Fluid crushedIce = Fluids.ICE.getFluid();
		if (crushedIce != null) {
			addCoolant(crushedIce, Constants.ICE_COOLING_MULTIPLIER);
		}
	}

	private static void addFuel(Fluid fluid, int powerPerCycle, int totalBurnTime) {
		NBTTagCompound fuelTag = new NBTTagCompound();
		fuelTag.setString(KEY_FLUID_NAME, fluid.getName());
		fuelTag.setInteger(KEY_POWER_PER_CYCLE, powerPerCycle);
		fuelTag.setInteger(KEY_TOTAL_BURN_TIME, totalBurnTime);

		FMLInterModComms.sendMessage(EnderIO, FLUID_FUEL_ADD, fuelTag);
	}

	private static void addCoolant(Fluid fluid, float coolingMultiplier) {
		float coolingPerMb = coolingMultiplier * WATER_COOLING_PER_MB;
		NBTTagCompound coolantTag = new NBTTagCompound();
		coolantTag.setString(KEY_FLUID_NAME, fluid.getName());
		coolantTag.setFloat(KEY_COOLING_PER_MB, coolingPerMb);

		FMLInterModComms.sendMessage(EnderIO, FLUID_COOLANT_ADD, coolantTag);
	}
}

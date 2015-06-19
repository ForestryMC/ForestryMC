package forestry.plugins;

import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.fluids.Fluid;

import cpw.mods.fml.common.event.FMLInterModComms;

import forestry.core.GameMode;
import forestry.core.config.Defaults;
import forestry.core.fluids.Fluids;
import forestry.core.proxy.Proxies;

@Plugin(pluginID = "EnderIO", name = "EnderIO", author = "mezz", url = Defaults.URL, unlocalizedDescription = "for.plugin.enderIO.description")
public class PluginEnderIO extends ForestryPlugin {

	private static final String EnderIO = "EnderIO";

	private static final String FLUID_FUEL_ADD = "fluidFuel:add";
	private static final String FLUID_COOLANT_ADD = "fluidCoolant:add";

	private static final String KEY_FLUID_NAME = "fluidName";
	private static final String KEY_POWER_PER_CYCLE = "powerPerCycle";
	private static final String KEY_TOTAL_BURN_TIME = "totalBurnTime";
	private static final String KEY_COOLING_PER_MB = "coolingPerMb";

	@Override
	public boolean isAvailable() {
		return Proxies.common.isModLoaded(EnderIO);
	}

	@Override
	public String getFailMessage() {
		return "EnderIO not found";
	}

	@Override
	public void doInit() {
		Fluid ethanol = Fluids.ETHANOL.getFluid();
		if (ethanol != null) {
			int ethanolBurnTime = Math.round(Defaults.ENGINE_CYCLE_DURATION_ETHANOL * GameMode.getGameMode().getFloatSetting("fuel.ethanol.combustion"));
			addFuel(ethanol, 40, ethanolBurnTime);
		}

		Fluid crushedIce = Fluids.ICE.getFluid();
		if (crushedIce != null) {
			addCoolant(crushedIce, 10.0f);
		}
	}

	private static void addFuel(Fluid fluid, int powerPerCycle, int totalBurnTime) {
		NBTTagCompound fuelTag = new NBTTagCompound();
		fuelTag.setString(KEY_FLUID_NAME, fluid.getName());
		fuelTag.setInteger(KEY_POWER_PER_CYCLE, powerPerCycle);
		fuelTag.setInteger(KEY_TOTAL_BURN_TIME, totalBurnTime);

		FMLInterModComms.sendMessage(EnderIO, FLUID_FUEL_ADD, fuelTag);
	}

	private static void addCoolant(Fluid fluid, float coolingPerMb) {
		NBTTagCompound coolantTag = new NBTTagCompound();
		coolantTag.setString(KEY_FLUID_NAME, fluid.getName());
		coolantTag.setFloat(KEY_COOLING_PER_MB, coolingPerMb);

		FMLInterModComms.sendMessage(EnderIO, FLUID_COOLANT_ADD, coolantTag);
	}
}

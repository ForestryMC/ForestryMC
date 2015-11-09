/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.core.errors;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.core.ForestryAPI;
import forestry.api.core.IErrorState;
import forestry.core.config.Constants;

public enum EnumErrorCode implements IErrorState {

	@Deprecated
	UNKNOWN("unknown"), // Congratulations, you found a glitch.
	@Deprecated
	WRONGSTACKSIZE("wrongStacksize"), // Only single items can be used here.
	@Deprecated
	NODISPOSAL("noDisposal"), // Insufficient space to dispose of waste products.
	@Deprecated
	INVALIDBIOME("invalidBiome"), // Machine or inhabitants cannot work in this biome.

	// Bees
	TOO_HOT("tooHot"), // The bees are melting in the heat here and unable to work. Use the habitat locator to find a cooler climate.
	TOO_COLD("tooCold"), // The bees are huddled together to survive the freezing cold here. Use the  habitat locator to find a warmer climate.
	TOO_HUMID("tooHumid"), // The damp climate here has made the bees' wings too damp to fly. Use the  habitat locator to find a dryer climate.
	TOO_ARID("tooArid", "invalidBiome"), // The dry climate here has made the bees parched and unable do work. Use the  habitat locator to find a wetter climate.
	IS_RAINING("isRaining"), // Only tolerant fliers can work in the rain.
	NOT_GLOOMY("notGloomy"), // The bees can only work in darkness.
	NOT_BRIGHT("notLucid"), // The bees have trouble navigating in the dark.
	NOT_DAY("notDay"), // The bees can only work during the daytime.
	NOT_NIGHT("notNight"), // The bees can only work during the night.
	NO_FLOWER("noFlower"), // Hive members are not finding the right flowers. Use the Beealyzer to discover their flower preference.
	NO_QUEEN("noQueen"), // Supply this hive with a queen or a princess and a drone.
	NO_DRONE("noDrone"), // Mating requires a drone present.
	NO_SKY("noSky"), // The hive requires direct sunlight from above.

	// Machines
	NO_RESOURCE("noResource"), // More resources need to be supplied for operation.
	NO_RESOURCE_INVENTORY("noResourceInventory", "noResource"), // Resources need to be added to the machine's inventory to craft this recipe.
	NO_RESOURCE_LIQUID("noResourceLiquid", "noLiquid"), // More liquid resources need to be supplied for operation.
	NO_RECIPE("noRecipe"), // No matching recipe was found for the supplied resources.
	NO_SPACE_INVENTORY("noSpace"), // Empty this machine's inventory.
	NO_SPACE_TANK("noSpaceTank", "noLiquid"), // Empty this machine's liquid tank.
	NO_POWER("noPower"), // This machine requires RF energy from an engine to function.
	NO_REDSTONE("noRedstone", "disabled"), // This machine requires a redstone signal to activate it.
	DISABLED_BY_REDSTONE("disabledRedstone", "disabled"), // This machine is being disabled by a redstone signal.
	NOT_DARK("notDark", "notGloomy"), // A lower light level is required for operation.

	// Rain Tank
	NOT_RAINING("notRaining"), // Operation is only possible when it is raining.
	NO_RAIN_BIOME("noRainBiome", "notRaining"), // This location never receives rain.
	NO_SKY_RAIN_TANK("noSkyRainTank", "noSky"), // Clear the area above this machine so it can gather rain.

	// Alyzer
	NO_HONEY("noHoney"), // This gadget requires honey drops or honeydew for operation.
	NO_SPECIMEN("noSpecimen"), // Supply specimen to analyze.

	// Engines
	FORCED_COOLDOWN("forcedCooldown"), // Engine has overheated and is forced into cooldown.
	NO_FUEL("noFuel"), // (Biogas & Peat-fired) Replenish this machine's fuel supplies.
	NO_HEAT("noHeat"), // (Biogas engine) Refill the heating tank.
	NO_ENERGY_NET("noEnergyNet"), // Your world is barren of any electricity. (Install IndustrialCraft\u00b2.)

	// Trade Station
	NO_STAMPS("noStamps"), // The trade station requires more stamps to pay postage.
	NO_PAPER("noPaper"), // The trade station requires more paper to send letters.
	NO_SUPPLIES("noSupplies", "noResource"), // The trade station requires more supplies to send.
	NO_TRADE("noTrade", "noResource"), // The trade station requires items to Send and Receive.

	// Trade Station naming
	NOT_ALPHANUMERIC("notAlphaNumeric"), // A Trade Station name must consist of letters and numbers only.
	NOT_UNIQUE("notUnique"), // Trade Station names must be unique and this name is already taken.

	// Letters
	NOT_POST_PAID("notPostpaid", "noStamps"), // Apply more stamps to pay the postal service.
	NO_RECIPIENT("noRecipient"), // You need to address your letter to a recipient to send it.

	// Circuit Boards
	NO_CIRCUIT_BOARD("noCircuitBoard"), // Insert a circuit board to solder the selected tubes onto it.
	NO_CIRCUIT_LAYOUT("noCircuitLayout"), // No layouts available due to the current game settings.
	CIRCUIT_MISMATCH("circuitMismatch"), // Amount of tubes does not match size of circuit board.

	// Farms
	NO_FERTILIZER("noFertilizer"), // Farms require fertilizer for function. Compost is insufficient.
	NO_FARMLAND("noFarmland"), // Smooth sandstone, bricks or stone bricks create a platform the farm will build on.
	NO_LIQUID_FARM("noLiquid") // Depending on rainfall, temperature and humidity farms need to be supplied with varying amounts of water.
	;

	private final String name;
	private final String iconName;
	@SideOnly(Side.CLIENT)
	private IIcon icon;

	EnumErrorCode(String name) {
		this(name, name);
	}

	EnumErrorCode(String name, String iconName) {
		this.name = name;
		this.iconName = iconName;
	}

	@Override
	public String getDescription() {
		return "errors." + name + ".desc";
	}

	@Override
	public String getHelp() {
		return "errors." + name + ".help";
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister register) {
		icon = register.registerIcon("forestry:errors/" + iconName);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon() {
		return icon;
	}

	@Override
	public short getID() {
		return (short) ordinal();
	}

	@Override
	public String getUniqueName() {
		return Constants.MOD + ":" + name;
	}

	public static void init() {
		for (IErrorState code : values()) {
			ForestryAPI.errorStateRegistry.registerErrorState(code);
		}
	}
}

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

	// Bees
	INVALIDBIOME("invalidBiome"), // Machine or inhabitants cannot work in this biome.
	ISRAINING("isRaining"), // Operation is only possible when it is not raining.
	NOTRAINING("notRaining"), // Operation is only possible when it is raining.
	NOTGLOOMY("notGloomy"), // A lower light level is required for operation.
	NOTLUCID("notLucid"), // A higher light level is required for operation
	NOTDAY("notDay"), // Operation is only possible during the day.
	NOTNIGHT("notNight"), // Operation is only possible during the night.
	NOFLOWER("noFlower"), // Hive members are not finding the right flowers.
	NOQUEEN("noQueen"), // Supply this hive with a queen or a princess and a drone.
	NODRONE("noDrone"), // Mating requires a drone present.
	NOSKY("noSky"), // Clear the area above the machine.
	
	// Machines
	NORESOURCE("noResource"), // More resources need to be supplied for operation.
	NORECIPE("noRecipe"), // No matching recipe was found for the supplied resources.
	NOSPACE("noSpace"), // Empty this machine's inventory.
	NOSPACETANK("noSpaceTank", "noLiquid"), // Empty this machine's liquid tank.

	// Alyzer
	NOHONEY("noHoney"), // This gadget requires honey drops or honeydew for operation.
	NOTHINGANALYZE("noSpecimen"), // Supply specimen to analyze.

	// Engines
	FORCEDCOOLDOWN("forcedCooldown"), // Engine has overheated and is forced into cooldown.
	NOFUEL("noFuel"), // (Biogas & Peat-fired) Replenish this machine's fuel supplies.
	NOHEAT("noHeat"), // (Biogas engine) Refill the heating tank.
	NOENERGYNET("noEnergyNet"), // Your world is barren of any electricity. (Install IndustrialCraft\u00b2.)

	// Trade Station
	NOSTAMPS("noStamps"), // The trade station requires more stamps to pay postage.
	NOPAPER("noPaper"), // The trade station requires more paper to send letters.
	NOSUPPLIES("noSupplies", "noResource"), // The trade station requires more supplies to send.
	NOTRADE("noTrade", "noResource"), // The trade station requires items to Send and Receive.

	// Trade Station naming
	NOTALPHANUMERIC("notAlphaNumeric"), // Given string must consist of letters and numbers only.
	NOTUNIQUE("notUnique"), // Given string must be unique in this world. Choose a different one.

	// Letters
	NOTPOSTPAID("notPostpaid", "noStamps"), // Apply more stamps to pay the postal service.
	NORECIPIENT("noRecipient"), // You need to address your letter to a recipient to send it.

	// Circuit Boards
	NOCIRCUITBOARD("noCircuitBoard"), // Insert a circuit board to solder the selected tubes onto it.
	NOCIRCUITLAYOUT("noCircuitLayout"), // No layouts available due to the current game settings.
	CIRCUITMISMATCH("circuitMismatch"), // Amount of tubes does not match size of circuit board.

	NOFERTILIZER("noFertilizer"),
	NOFARMLAND("noFarmland"),

	NOLIQUID("noLiquid"),
	NOPOWER("noPower"), // This machine requires RF power to do work.
	NOREDSTONE("noRedstone", "disabled"), // This machine requires a redstone signal to activate it.
	DISABLED("disabledRedstone", "disabled") // This machine is being disabled by a redstone signal.
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

/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.core.render.TextureManager;

public enum EnumErrorCode {

	UNKNOWN("unknown", 0),

	OK("ok", 1),

	INVALIDBIOME("invalidBiome", 2),

	ISRAINING("isRaining", 3),

	NOTRAINING("notRaining", 4),

	NOFUEL("noFuel", 5), // Biogas & Peat-fired

	NOHEAT("noHeat", 6), // Biogas engine

	NODISPOSAL("noDisposal", 7),

	NORESOURCE("noResource", 8),

	NOTGLOOMY("notGloomy", 9),

	NOTLUCID("notLucid", 10),

	NOTDAY("notDay", 11),

	NOTNIGHT("notNight", 12),

	NOFLOWER("noFlower", 13),

	NOQUEEN("noQueen", 14),

	NODRONE("noDrone", 15),

	NOSKY("noSky", 16),

	NOSPACE("noSpace", 17),

	NORECIPE("noRecipe", 19),

	NOENERGYNET("noEnergyNet", 20),

	NOTHINGANALYZE("noSpecimen", 15),

	FORCEDCOOLDOWN("forcedCooldown", 21),

	NOHONEY("noHoney", 22),

	NOTPOSTPAID("notPostpaid", 23),

	NORECIPIENT("noRecipient", 24),

	NOTALPHANUMERIC("notAlphaNumeric", 25),

	NOTUNIQUE("notUnique", 26),

	NOSTAMPS("noStamps", 23),

	NOCIRCUITBOARD("noCircuitBoard", 28),

	WRONGSTACKSIZE("wrongStacksize", 26),

	NOFERTILIZER("noFertilizer", 5),

	NOFARMLAND("noFarmland", 27),

	CIRCUITMISMATCH("circuitMismatch", 26),

	NOLIQUID("noLiquid", 29);

	private String name;
	@SideOnly(Side.CLIENT)
	private IIcon icon;

	private EnumErrorCode(String name, int iconIndex) {
		this.name = name;
	}

	public String getDescription() {
		return "errors." + name + ".desc";
	}

	public String getHelp() {
		return "errors." + name + ".help";
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		icon = TextureManager.getInstance().registerTex(register, "errors/" + name);
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIcon() {
		return icon;
	}

	@SideOnly(Side.CLIENT)
	public static void initIcons(IIconRegister register) {
		for (EnumErrorCode code : values())
			code.registerIcons(register);
	}
}

/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.core;

import forestry.api.core.ErrorStateRegistry;
import forestry.api.core.IErrorState;
import forestry.core.config.Defaults;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum EnumErrorCode implements IErrorState {

	UNKNOWN("unknown"),
	OK("ok"),
	INVALIDBIOME("invalidBiome"),
	ISRAINING("isRaining"),
	NOTRAINING("notRaining"),
	NOFUEL("noFuel"), // Biogas & Peat-fired
	NOHEAT("noHeat"), // Biogas engine
	NODISPOSAL("noDisposal"),
	NORESOURCE("noResource"),
	NOTGLOOMY("notGloomy"),
	NOTLUCID("notLucid"),
	NOTDAY("notDay"),
	NOTNIGHT("notNight"),
	NOFLOWER("noFlower"),
	NOQUEEN("noQueen"),
	NODRONE("noDrone"),
	NOSKY("noSky"),
	NOSPACE("noSpace"),
	NOSPACETANK("noSpaceTank", "noLiquid"),
	NORECIPE("noRecipe"),
	NOENERGYNET("noEnergyNet"),
	NOTHINGANALYZE("noSpecimen"),
	FORCEDCOOLDOWN("forcedCooldown"),
	NOHONEY("noHoney"),
	NOTPOSTPAID("notPostpaid", "noStamps"),
	NORECIPIENT("noRecipient"),
	NOTALPHANUMERIC("notAlphaNumeric"),
	NOTUNIQUE("notUnique"),
	NOSTAMPS("noStamps"),
	NOCIRCUITBOARD("noCircuitBoard"),
	NOCIRCUITLAYOUT("noCircuitLayout"),
	WRONGSTACKSIZE("wrongStacksize"),
	NOFERTILIZER("noFertilizer"),
	NOFARMLAND("noFarmland"),
	CIRCUITMISMATCH("circuitMismatch"),
	NOLIQUID("noLiquid"),
	NOPAPER("noPaper"),
	NOSTAMPSNOPAPER("noStampsNoPaper", "noStamps"),
	NOSUPPLIES("noSupplies", "noResource"),
	NOTRADE("noTrade", "noResource"),
	NOPOWER("noPower");

	private final String name;
	private final String iconName;
	@SideOnly(Side.CLIENT)
	private TextureAtlasSprite icon;

	private EnumErrorCode(String name) {
		this(name, name);
	}

	private EnumErrorCode(String name, String iconName) {
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
	public void registerIcons(TextureMap map) {
		icon = map.registerSprite(new ResourceLocation("forestry:textures/items/errors/" + iconName + ".png"));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public TextureAtlasSprite getIcon() {
		return icon;
	}

	@Override
	public short getID() {
		return (short) ordinal();
	}

	@Override
	public String getUniqueName() {
		return Defaults.MOD + ":" + name;
	}

	public static void init() {
		for (IErrorState code : values()) {
			ErrorStateRegistry.registerErrorState(code);
		}
	}
}

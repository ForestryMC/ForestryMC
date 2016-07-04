/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture;

import javax.annotation.Nonnull;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import com.mojang.authlib.GameProfile;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.IErrorLogicSource;
import forestry.api.genetics.IHousing;

public interface IBeeHousing extends IHousing, IErrorLogicSource {

	/**
	 * Used by BeeManager.beeRoot.createBeeHousingModifier(IBeeHousing housing)
	 * to combine bee modifiers from several sources that can change over time.
	 * @return IBeeModifiers from the housing, frames, etc.
	 */
	Iterable<IBeeModifier> getBeeModifiers();

	/**
	 * Used by BeeManager.beeRoot.createBeeHousingListener(IBeeHousing housing)
	 * to combine bee listeners from several sources that can change over time.
	 * @return IBeeListeners from the housing, multiblock parts, etc.
	 */
	Iterable<IBeeListener> getBeeListeners();

	@Nonnull
	IBeeHousingInventory getBeeInventory();

	IBeekeepingLogic getBeekeepingLogic();

	EnumTemperature getTemperature();

	EnumHumidity getHumidity();

	int getBlockLightValue();

	boolean canBlockSeeTheSky();

	/** Must not be named "getWorld" to avoid SpecialSource issue https://github.com/md-5/SpecialSource/issues/12 */
	World getWorldObj();

	Biome getBiome();

	GameProfile getOwner();

	/**
	 * @since Forestry 4.2
	 * @return exact coordinates where bee FX should spawn from
	 */
	Vec3d getBeeFXCoordinates();
}

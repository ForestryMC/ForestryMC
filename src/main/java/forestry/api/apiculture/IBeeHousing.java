/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture;

import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.api.core.IClimateProvider;
import forestry.api.core.IErrorLogicSource;
import forestry.api.genetics.IHousing;

public interface IBeeHousing extends IHousing, IClimateProvider, IErrorLogicSource {

    /**
     * Used by BeeManager.beeRoot.createBeeHousingModifier(IBeeHousing housing) to combine bee modifiers from several
     * sources that can change over time.
     * 
     * @return IBeeModifiers from the housing, frames, etc.
     */
    Iterable<IBeeModifier> getBeeModifiers();

    /**
     * Used by BeeManager.beeRoot.createBeeHousingListener(IBeeHousing housing) to combine bee listeners from several
     * sources that can change over time.
     * 
     * @return IBeeListeners from the housing, multiblock parts, etc.
     */
    Iterable<IBeeListener> getBeeListeners();

    IBeeHousingInventory getBeeInventory();

    IBeekeepingLogic getBeekeepingLogic();

    int getBlockLightValue();

    boolean canBlockSeeTheSky();

    World getWorld();

    GameProfile getOwner();

    /**
     * @since Forestry 4.2
     * @return exact coordinates where bee FX should spawn from
     */
    Vec3 getBeeFXCoordinates();
}

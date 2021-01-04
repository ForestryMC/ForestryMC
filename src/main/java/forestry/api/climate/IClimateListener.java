/*
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 */
package forestry.api.climate;

import forestry.api.core.ILocatable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * A climate listener listens to the climate at a specific location. It automatically gets and caches the climate state
 * of the climate at the specific location if you call {@link #getClimateState()}, {@link #getExactTemperature()}
 * or {@link #getExactHumidity()}.
 * <p>
 * You can create an instance of the climate listener with {@link IClimateFactory#createListener(ILocatable)}.
 */
public interface IClimateListener extends ILocatable, IClimateProvider {

    /**
     * @return Returns the climate state of this listener.
     */
    IClimateState getClimateState();

    /**
     * Sets the cached state to the given state.
     */
    @OnlyIn(Dist.CLIENT)
    void setClimateState(IClimateState climateState);

    /**
     * @return Returns the temperature value of this listener.
     */
    float getExactTemperature();

    /* CLIENT */

    /**
     * @return Returns the temperature value of this listener.
     */
    float getExactHumidity();

    /**
     * Updates the listener on the client side.
     *
     * @param spawnParticles If the listener should spawn particles around its location.
     */
    @OnlyIn(Dist.CLIENT)
    void updateClientSide(boolean spawnParticles);

    /**
     * Sends a packet if needed to all players that are currently "watching" the chunk that the listener is located in.
     */
    void syncToClient();

    /**
     * Sends a packet to the given players.
     */
    void syncToClient(ServerPlayerEntity player);

    void markLocatableDirty();
}

/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import net.minecraft.entity.player.EntityPlayerMP;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.ILocatable;

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
	 * @return Returns the temperature value of this listener.
	 */
	float getExactTemperature();

	/**
	 * @return Returns the temperature value of this listener.
	 */
	float getExactHumidity();

	/* CLIENT */

	/**
	 * Updates the listener on the client side.
	 *
	 * @param spawnParticles If the listener should spawn particles around its location.
	 */
	@SideOnly(Side.CLIENT)
	void updateClientSide(boolean spawnParticles);

	/**
	 * Sets the cached state to the given state.
	 */
	@SideOnly(Side.CLIENT)
	void setClimateState(IClimateState climateState);

	/**
	 * Sends a packet if needed to all players that are currently "watching" the chunk that the listener is located in.
	 */
	void syncToClient();

	/**
	 * Sends a packet to the given players.
	 */
	void syncToClient(EntityPlayerMP player);

	void markLocatableDirty();
}

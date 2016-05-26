/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture;

import javax.annotation.Nonnull;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;

/**
 * Stores beekeeping logic for bee housings.
 * Get one with BeeManager.beeRoot.createBeekeepingLogic(IBeeHousing housing)
 * Save and load it to NBT using the INbtWritable methods.
 */
public interface IBeekeepingLogic extends INbtWritable, INbtReadable {

	/* SERVER */

	/**
	 * Checks that the bees can work, setting error conditions on the housing where needed
	 * @return true if no errors are present and doWork should be called
	 */
	boolean canWork();

	/**
	 * Performs actual work, breeding, production, etc.
	 */
	void doWork();


	/* CLIENT */

	/**
	 * Call this when the housing comes into view of the client.
	 * (i.e. when {@link TileEntity#getUpdateTag()} is called)
	 */
	void syncToClient();

	void syncToClient(EntityPlayerMP player);

	/**
	 * Get the progress bar for breeding and production.
	 * To avoid network spam, this is only available server-side,
	 * and must be synced manually to the client when a GUI is open.
	 */
	int getBeeProgressPercent();

	/**
	 * Whether bee fx should be active.
	 * Internally, this is automatically synced to the client.
	 */
	boolean canDoBeeFX();

	/**
	 * Display bee fx. Calls IBee.doFX(IEffectData[] storedData, IBeeHousing housing) on the queen.
	 * Internally, the queen is automatically synced to the client for the fx.
	 */
	void doBeeFX();

	/**
	 * Used by bee fx to direct bees to nearby flowers.
	 * These positions are synced to the client from the server.
	 */
	@Nonnull
	List<BlockPos> getFlowerPositions();
}

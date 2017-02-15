/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture;

import forestry.api.genetics.IAlleleEffect;
import forestry.api.genetics.IEffectData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IAlleleBeeEffect extends IAlleleEffect {

	/**
	 * Called by apiaries to cause an effect in the world. (server)
	 *
	 * @param genome     Genome of the bee queen causing this effect
	 * @param storedData Object containing the stored effect data for the apiary/hive the bee is in.
	 * @param housing    {@link IBeeHousing} the bee currently resides in.
	 * @return storedData, may have been manipulated.
	 */
	IEffectData doEffect(IBeeGenome genome, IEffectData storedData, IBeeHousing housing);

	/**
	 * Is called to produce visual bee effects. (client)
	 *
	 * @param genome     Genome of the bee queen causing this effect
	 * @param storedData Object containing the stored effect data for the apiary/hive the bee is in.
	 * @param housing    {@link IBeeHousing} the bee currently resides in.
	 * @return storedData, may have been manipulated.
	 */
	@SideOnly(Side.CLIENT)
	IEffectData doFX(IBeeGenome genome, IEffectData storedData, IBeeHousing housing);

}

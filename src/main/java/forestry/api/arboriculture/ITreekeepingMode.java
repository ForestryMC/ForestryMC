/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.arboriculture;

import javax.annotation.Nonnull;
import java.util.List;

import forestry.api.genetics.ISpeciesMode;

public interface ITreekeepingMode extends ISpeciesMode<TreeChromosome> {

	/**
	 * @return Localized list of strings outlining the behaviour of this treekeeping mode.
	 */
	List<String> getDescription();

	@Nonnull
	ITreeModifier getTreeModifier();
}

/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.apiculture;

import net.minecraft.command.CommandException;

public class SpeciesNotFoundException extends CommandException {

	private static final long serialVersionUID = 1L;

	public SpeciesNotFoundException(String title) {
		super("Could not find bee species with Name or UID %s", title);
	}

}

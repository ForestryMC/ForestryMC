/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.commands;

import net.minecraft.command.CommandException;
import net.minecraft.util.text.TranslationTextComponent;

import forestry.api.genetics.IAlleleForestrySpecies;

public class TemplateNotFoundException extends CommandException {

	private static final long serialVersionUID = 1L;

	public TemplateNotFoundException(IAlleleForestrySpecies species) {
		super(new TranslationTextComponent("Could not find template for species %s with UID %s", species.getDisplayName().getFormattedText(), species.getRegistryName().toString()));
	}

}

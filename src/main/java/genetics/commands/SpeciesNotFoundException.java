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
package genetics.commands;

import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SpeciesNotFoundException extends CommandRuntimeException {

	private static final long serialVersionUID = 1L;

	public SpeciesNotFoundException(ResourceLocation title) {
		super(Component.translatable("Could not find species with Name or UID %s", title));
	}

}

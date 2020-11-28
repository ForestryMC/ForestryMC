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
package forestry.arboriculture.commands;

import forestry.api.arboriculture.genetics.ITree;
import genetics.commands.SpeciesNotFoundException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;

@FunctionalInterface
public interface ITreeSpawner {
    int spawn(CommandSource source, ITree treeName, PlayerEntity player) throws SpeciesNotFoundException;
}

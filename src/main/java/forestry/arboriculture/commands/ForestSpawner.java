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

import forestry.core.commands.SpeciesNotFoundException;
import forestry.core.commands.TemplateNotFoundException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.feature.WorldGenerator;

public class ForestSpawner implements ITreeSpawner {

	@Override
	public boolean spawn(ICommandSender sender, String treeName, EntityPlayer player) throws SpeciesNotFoundException, TemplateNotFoundException {
		Vec3d look = player.getLookVec();

		int x = (int) Math.round(player.posX + 16 * look.xCoord);
		int y = (int) Math.round(player.posY);
		int z = (int) Math.round(player.posZ + 16 * look.zCoord);

		for (int i = 0; i < 16; i++) {
			int spawnX = x + player.world.rand.nextInt(32) - 16;
			int spawnZ = z + player.world.rand.nextInt(32) - 16;
			BlockPos pos = new BlockPos(spawnX, y, spawnZ);

			WorldGenerator gen = TreeGenHelper.getWorldGen(treeName, player, pos);
			TreeGenHelper.generateTree(gen, player.world, pos);
		}
		return true;
	}

}

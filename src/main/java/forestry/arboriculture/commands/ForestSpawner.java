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

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraft.world.gen.feature.WorldGenerator;

public class ForestSpawner implements ITreeSpawner {

	public boolean spawn(ICommandSender sender, String treeName, EntityPlayer player) {
		Vec3 look = player.getLookVec();

		int x = (int) Math.round(player.posX + (16 * look.xCoord));
		int y = (int) Math.round(player.posY);
		int z = (int) Math.round(player.posZ + (16 * look.zCoord));

		for (int i = 0; i < 16; i++) {
			int spawnX = x + player.worldObj.rand.nextInt(32) - 16;
			int spawnZ = z + player.worldObj.rand.nextInt(32) - 16;

			WorldGenerator gen = TreeGenHelper.getWorldGen(treeName, player, spawnX, y, spawnZ);
			if (gen == null) {
				return false;
			}
			TreeGenHelper.generateTree(gen, player, spawnX, y, spawnZ);
		}
		return true;
	}

}

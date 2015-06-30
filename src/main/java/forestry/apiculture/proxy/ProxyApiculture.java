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
package forestry.apiculture.proxy;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import forestry.core.interfaces.IBlockRenderer;
import forestry.core.vect.IVect;

public class ProxyApiculture {
	public void addBeeHiveFX(String texture, World world, ChunkCoordinates coordinates, int color, IVect area) {
	}

	public void addBeeSwarmFX(String texture, World world, double xCoord, double yCoord, double zCoord, int color) {
	}

	public IBlockRenderer getRendererAnalyzer(String string) {
		return null;
	}

	public void initializeRendering() {
	}

}

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
package forestry.core.tiles;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;

import forestry.core.fluids.ITankManager;

public interface ILiquidTankTile extends ILocatable {

	ITankManager getTankManager();

	void getGUINetworkData(int messageId, int data);

	void sendGUINetworkData(Container container, ICrafting iCrafting);
}

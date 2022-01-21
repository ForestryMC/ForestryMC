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
package forestry.core.gui;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public interface IPagedInventory extends Container {

	void flipPage(ServerPlayer player, short page);
}

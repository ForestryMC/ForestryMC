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
package forestry.core.access;

import net.minecraft.entity.player.EntityPlayer;

import forestry.api.core.INBTTagable;
import forestry.core.network.IStreamable;

public interface IAccessHandler extends IOwnable, IStreamable, INBTTagable {

	boolean switchAccess(EntityPlayer player);

	EnumAccess getAccess();

	boolean allowsRemoval(EntityPlayer player);

	boolean allowsAlteration(EntityPlayer player);

	boolean allowsViewing(EntityPlayer player);

	boolean allowsPipeConnections();

	void addOwnerListener(IAccessOwnerListener accessListener);

	void removeOwnerListener(IAccessOwnerListener accessListener);

}

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
package forestry.core.delegates;

import net.minecraft.entity.player.EntityPlayer;

import com.mojang.authlib.GameProfile;

import forestry.core.interfaces.IAccessHandler;
import forestry.core.utils.EnumAccess;

public class FakeAccessHandler implements IAccessHandler {
	private static FakeAccessHandler instance;

	public static FakeAccessHandler getInstance() {
		if (instance == null) {
			instance = new FakeAccessHandler();
		}
		return instance;
	}

	private FakeAccessHandler() {

	}

	@Override
	public boolean switchAccessRule(EntityPlayer player) {
		return false;
	}

	@Override
	public EnumAccess getAccessType() {
		return EnumAccess.SHARED;
	}

	@Override
	public boolean allowsRemoval(EntityPlayer player) {
		return true;
	}

	@Override
	public boolean allowsAlteration(EntityPlayer player) {
		return true;
	}

	@Override
	public boolean allowsViewing(EntityPlayer player) {
		return true;
	}

	@Override
	public boolean allowsPipeConnections() {
		return true;
	}

	@Override
	public boolean isOwned() {
		return false;
	}

	@Override
	public GameProfile getOwner() {
		return null;
	}

	@Override
	public void setOwner(GameProfile owner) {

	}

	@Override
	public boolean isOwner(EntityPlayer player) {
		return true;
	}
}

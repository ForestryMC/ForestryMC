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
package forestry.core.owner;

import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;

public class FakeOwnerHandler implements IOwnerHandler {
	@Nullable
	private static FakeOwnerHandler instance;

	public static FakeOwnerHandler getInstance() {
		if (instance == null) {
			instance = new FakeOwnerHandler();
		}
		return instance;
	}

	private FakeOwnerHandler() {

	}

	@Override
	public GameProfile getOwner() {
		return null;
	}

	@Override
	public void setOwner(GameProfile owner) {

	}
}

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
package forestry.apiculture;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import forestry.api.apiculture.IBeekeepingLogic;

public class FakeBeekeepingLogic implements IBeekeepingLogic {
	public static final FakeBeekeepingLogic instance = new FakeBeekeepingLogic();

	private FakeBeekeepingLogic() {

	}

	@Override
	public boolean canWork() {
		return false;
	}

	@Override
	public void doWork() {

	}

	@Override
	public void syncToClient() {

	}

	@Override
	public void syncToClient(EntityPlayerMP player) {

	}

	@Override
	public int getBeeProgressPercent() {
		return 0;
	}

	@Override
	public boolean canDoBeeFX() {
		return false;
	}

	@Override
	public void doBeeFX() {

	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {

	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {

	}
}

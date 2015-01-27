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
package forestry.factory.gadgets;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import forestry.api.fuels.FuelManager;
import forestry.api.fuels.RainSubstrate;
import forestry.core.gadgets.Mill;
import forestry.core.gadgets.TileBase;
import forestry.core.proxy.Proxies;

public class MillRainmaker extends Mill {

	private int duration;
	private boolean reverse;

	public MillRainmaker() {
		speed = 0.01f;
	}

	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		if (!Proxies.common.isSimulating(player.worldObj)) {
			return;
		}

		if (player.inventory.getCurrentItem() == null) {
			return;
		}

		// We don't have a gui, but we can be activated
		if (FuelManager.rainSubstrate.containsKey(player.inventory.getCurrentItem()) && charge == 0) {
			RainSubstrate substrate = FuelManager.rainSubstrate.get(player.inventory.getCurrentItem());
			if (substrate.item.isItemEqual(player.inventory.getCurrentItem())) {
				addCharge(substrate);
				player.inventory.getCurrentItem().stackSize--;
			}
		}
		sendNetworkUpdate();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		charge = nbttagcompound.getInteger("Charge");
		progress = nbttagcompound.getFloat("Progress");
		stage = nbttagcompound.getInteger("Stage");
		duration = nbttagcompound.getInteger("Duration");
		reverse = nbttagcompound.getBoolean("Reverse");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("Charge", charge);
		nbttagcompound.setFloat("Progress", progress);
		nbttagcompound.setInteger("Stage", stage);
		nbttagcompound.setInteger("Duration", duration);
		nbttagcompound.setBoolean("Reverse", reverse);
	}

	public void addCharge(RainSubstrate substrate) {
		charge = 1;
		speed = substrate.speed;
		duration = substrate.duration;
		reverse = substrate.reverse;
	}

	@Override
	public void activate() {
		if (Proxies.render.hasRendering()) {
			worldObj.playSoundEffect(xCoord, yCoord, zCoord, "ambient.weather.thunder", 4F,
					(1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);

			float f = xCoord + 0.5F;
			float f1 = yCoord + 0.0F + (worldObj.rand.nextFloat() * 6F) / 16F;
			float f2 = zCoord + 0.5F;
			float f3 = 0.52F;
			float f4 = worldObj.rand.nextFloat() * 0.6F - 0.3F;

			Proxies.common.addEntityExplodeFX(worldObj, (f - f3), f1, (f2 + f4), 0F, 0F, 0F);
			Proxies.common.addEntityExplodeFX(worldObj, (f + f3), f1, (f2 + f4), 0F, 0F, 0F);
			Proxies.common.addEntityExplodeFX(worldObj, (f + f4), f1, (f2 - f3), 0F, 0F, 0F);
			Proxies.common.addEntityExplodeFX(worldObj, (f + f4), f1, (f2 + f3), 0F, 0F, 0F);
		}

		if (Proxies.common.isSimulating(worldObj)) {
			if (reverse) {
				worldObj.getWorldInfo().setRaining(false);
			} else {
				worldObj.getWorldInfo().setRaining(true);
				worldObj.getWorldInfo().setRainTime(duration);
			}
			charge = 0;
			duration = 0;
			reverse = false;
			sendNetworkUpdate();
		}
	}

	// TODO: Give Rainmaker a real inventory and a GUI with slots
	//	@Override
	//	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {
	//		if (charge != 0)
	//			return 0;
	//
	//		if (!FuelManager.rainSubstrate.containsKey(stack))
	//			return 0;
	//
	//		RainSubstrate substrate = FuelManager.rainSubstrate.get(stack);
	//		if (!substrate.item.isItemEqual(stack))
	//			return 0;
	//
	//		if (doAdd)
	//			addCharge(substrate);
	//		return 1;
	//	}
	//
	//	@Override
	//	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {
	//		return null;
	//	}
	//
	//	@Override
	//	public int getSizeInventory() {
	//		return 0;
	//	}
	//
	//	@Override
	//	public ItemStack getStackInSlot(int var1) {
	//		return null;
	//	}
	//
	//	@Override
	//	public ItemStack decrStackSize(int var1, int var2) {
	//		return null;
	//	}
	//
	//	@Override
	//	public ItemStack getStackInSlotOnClosing(int var1) {
	//		return null;
	//	}
	//
	//	@Override
	//	public void setInventorySlotContents(int var1, ItemStack var2) {
	//	}
	//
	//	@Override
	//	public int getInventoryStackLimit() {
	//		return 0;
	//	}
	//
	//	@Override
	//	public void openInventory() {
	//	}
	//
	//	@Override
	//	public void closeInventory() {
	//	}
	//
	//	@Override
	//	public boolean isUseableByPlayer(EntityPlayer player) {
	//		return Utils.isUseableByPlayer(player, this);
	//	}
	//
	//	@Override
	//	public boolean hasCustomInventoryName() {
	//		return false;
	//	}
	//
	//	@Override
	//	public boolean isItemValidForSlot(int slotIndex, ItemStack itemstack) {
	//		return false;
	//	}
}

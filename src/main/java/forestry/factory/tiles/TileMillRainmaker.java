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
package forestry.factory.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import forestry.api.fuels.FuelManager;
import forestry.api.fuels.RainSubstrate;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.TileMill;
import forestry.factory.inventory.InventoryRainmaker;

public class TileMillRainmaker extends TileMill {
	private int duration;
	private boolean reverse;

	public TileMillRainmaker() {
		super(null);
		speed = 0.01f;
		setInternalInventory(new InventoryRainmaker(this));
	}

	@Override
	public void openGui(EntityPlayer player) {
		if (player.worldObj.isRemote) {
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
		sendNetworkUpdate();
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

			Proxies.render.addEntityExplodeFX(worldObj, (f - f3), f1, (f2 + f4));
			Proxies.render.addEntityExplodeFX(worldObj, (f + f3), f1, (f2 + f4));
			Proxies.render.addEntityExplodeFX(worldObj, (f + f4), f1, (f2 - f3));
			Proxies.render.addEntityExplodeFX(worldObj, (f + f4), f1, (f2 + f3));
		}

		if (!worldObj.isRemote) {
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

	@Override
	public Object getGui(EntityPlayer player, int data) {
		return null;
	}

	@Override
	public Object getContainer(EntityPlayer player, int data) {
		return null;
	}
}

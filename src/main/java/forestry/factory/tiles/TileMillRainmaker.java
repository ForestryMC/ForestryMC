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

import javax.annotation.Nullable;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;

import forestry.api.fuels.FuelManager;
import forestry.api.fuels.RainSubstrate;
import forestry.core.render.ParticleRender;
import forestry.core.tiles.TileMill;
import forestry.factory.inventory.InventoryRainmaker;

public class TileMillRainmaker extends TileMill {
	private int duration;
	private boolean reverse;

	public TileMillRainmaker() {
		speed = 0.01f;
		setInternalInventory(new InventoryRainmaker(this));
	}

	@Override
	public void openGui(EntityPlayer player, ItemStack heldItem) {
		if (!player.world.isRemote && !heldItem.isEmpty()) {
			// We don't have a gui, but we can be activated
			if (FuelManager.rainSubstrate.containsKey(heldItem) && charge == 0) {
				RainSubstrate substrate = FuelManager.rainSubstrate.get(heldItem);
				if (substrate.getItem().isItemEqual(heldItem)) {
					addCharge(substrate);
					heldItem.shrink(1);
				}
			}
			sendNetworkUpdate();
		}
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
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound = super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("Charge", charge);
		nbttagcompound.setFloat("Progress", progress);
		nbttagcompound.setInteger("Stage", stage);
		nbttagcompound.setInteger("Duration", duration);
		nbttagcompound.setBoolean("Reverse", reverse);
		return nbttagcompound;
	}

	public void addCharge(RainSubstrate substrate) {
		charge = 1;
		speed = substrate.getSpeed();
		duration = substrate.getDuration();
		reverse = substrate.isReverse();
		sendNetworkUpdate();
	}

	@Override
	public void activate() {
		if (world.isRemote) {
			world.playSound(null, getPos(), SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.WEATHER, 10000.0F, 0.8F + world.rand.nextFloat() * 0.2F);

			float f = getPos().getX() + 0.5F;
			float f1 = getPos().getY() + 0.0F + world.rand.nextFloat() * 6F / 16F;
			float f2 = getPos().getZ() + 0.5F;
			float f3 = 0.52F;
			float f4 = world.rand.nextFloat() * 0.6F - 0.3F;

			ParticleRender.addEntityExplodeFX(world, f - f3, f1, f2 + f4);
			ParticleRender.addEntityExplodeFX(world, f + f3, f1, f2 + f4);
			ParticleRender.addEntityExplodeFX(world, f + f4, f1, f2 - f3);
			ParticleRender.addEntityExplodeFX(world, f + f4, f1, f2 + f3);
		} else {
			if (reverse) {
				world.getWorldInfo().setRaining(false);
			} else {
				world.getWorldInfo().setRaining(true);
				world.getWorldInfo().setRainTime(duration);
			}
			charge = 0;
			duration = 0;
			reverse = false;
			sendNetworkUpdate();
		}
	}

	@Override
	@Nullable
	public GuiContainer getGui(EntityPlayer player, int data) {
		return null;
	}

	@Override
	@Nullable
	public Container getContainer(EntityPlayer player, int data) {
		return null;
	}
}

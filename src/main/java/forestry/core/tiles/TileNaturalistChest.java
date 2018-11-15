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

import java.io.IOException;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.genetics.ISpeciesRoot;
import forestry.core.gui.ContainerNaturalistInventory;
import forestry.core.gui.GuiHandler;
import forestry.core.gui.GuiNaturalistInventory;
import forestry.core.gui.IPagedInventory;
import forestry.core.inventory.InventoryNaturalistChest;
import forestry.core.network.PacketBufferForestry;

public abstract class TileNaturalistChest extends TileBase implements IPagedInventory {
	private static final float lidAngleVariationPerTick = 0.1F;
	public static final AxisAlignedBB chestBoundingBox = new AxisAlignedBB(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);

	private final ISpeciesRoot speciesRoot;
	public float lidAngle;
	public float prevLidAngle;
	private int numPlayersUsing;

	public TileNaturalistChest(ISpeciesRoot speciesRoot) {
		this.speciesRoot = speciesRoot;
		setInternalInventory(new InventoryNaturalistChest(this, speciesRoot));
	}

	public void increaseNumPlayersUsing() {
		numPlayersUsing++;
		setNeedsNetworkUpdate();
	}

	public void decreaseNumPlayersUsing() {
		numPlayersUsing--;
		if (numPlayersUsing < 0) {
			numPlayersUsing = 0;
		}
		setNeedsNetworkUpdate();
	}

	@Override
	protected void updateClientSide() {
		updates();
	}

	@Override
	protected void updateServerSide() {
		updates();
	}

	private void updates() {
		prevLidAngle = lidAngle;

		if (numPlayersUsing > 0 && lidAngle == 0.0F) {
			playLidSound(SoundEvents.BLOCK_CHEST_OPEN);
		}

		if (numPlayersUsing == 0 && lidAngle > 0.0F || numPlayersUsing > 0 && lidAngle < 1.0F) {
			float oldAngle = lidAngle;

			if (numPlayersUsing > 0) {
				lidAngle += lidAngleVariationPerTick;
			} else {
				lidAngle -= lidAngleVariationPerTick;
			}

			lidAngle = Math.max(Math.min(lidAngle, 1), 0);

			if (lidAngle < 0.5F && oldAngle >= 0.5F) {
				playLidSound(SoundEvents.BLOCK_CHEST_CLOSE);
			}
		}
	}

	private void playLidSound(SoundEvent sound) {
		this.world.playSound(null, getPos(), sound, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
	}

	@Override
	public void flipPage(EntityPlayer player, short page) {
		GuiHandler.openGui(player, this, page);
	}

	/* IStreamable */
	@Override
	public void writeData(PacketBufferForestry data) {
		super.writeData(data);
		data.writeInt(numPlayersUsing);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void readData(PacketBufferForestry data) throws IOException {
		super.readData(data);
		numPlayersUsing = data.readInt();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer getGui(EntityPlayer player, int page) {
		ContainerNaturalistInventory container = new ContainerNaturalistInventory(player.inventory, this, page);
		return new GuiNaturalistInventory(speciesRoot, player, container, page, 5);
	}

	@Override
	public Container getContainer(EntityPlayer player, int page) {
		return new ContainerNaturalistInventory(player.inventory, this, page);
	}
}

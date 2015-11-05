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

import net.minecraft.entity.player.EntityPlayer;

import forestry.api.core.ForestryAPI;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.GuiHandler;
import forestry.core.gui.IPagedInventory;
import forestry.core.inventory.InventoryNaturalistChest;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.GuiId;

public abstract class TileNaturalistChest extends TileBase implements IPagedInventory {
	private static final float lidAngleVariationPerTick = 0.1F;

	public float lidAngle;
	public float prevLidAngle;
	private int numPlayersUsing;

	public TileNaturalistChest(ISpeciesRoot speciesRoot, GuiId guiId) {
		super(guiId, "naturalist.chest");
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
		update();
	}
	
	@Override
	protected void updateServerSide() {
		update();
	}

	private void update() {
		prevLidAngle = lidAngle;

		if (numPlayersUsing > 0 && lidAngle == 0.0F) {
			playLidSound("random.chestopen");
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
				playLidSound("random.chestclosed");
			}
		}
	}

	private void playLidSound(String sound) {
		worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, sound, 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
	}

	@Override
	public void flipPage(EntityPlayer player, int page) {
		player.openGui(ForestryAPI.instance, GuiHandler.encodeGuiData(guiId, page), player.worldObj, xCoord, yCoord, zCoord);
	}

	/* IStreamable */
	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		data.writeInt(numPlayersUsing);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		numPlayersUsing = data.readInt();
	}
}

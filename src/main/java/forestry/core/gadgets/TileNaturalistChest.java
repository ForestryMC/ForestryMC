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
package forestry.core.gadgets;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import forestry.api.core.ForestryAPI;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.GuiHandler;
import forestry.core.config.Config;
import forestry.core.gui.IPagedInventory;
import forestry.core.inventory.TileInventoryAdapter;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;

public abstract class TileNaturalistChest extends TileBase implements IPagedInventory {
	private static final float lidAngleVariationPerTick = 0.1F;

	private final int guiID;
	public float lidAngle;
	public float prevLidAngle;
	private int numPlayersUsing;

	public TileNaturalistChest(ISpeciesRoot speciesRoot, int guiId) {
		setInternalInventory(new NaturalistInventoryAdapter(this, speciesRoot));
		setHints(Config.hints.get("apiarist.chest"));
		this.guiID = guiId;
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
	public void openGui(EntityPlayer player) {
		player.openGui(ForestryAPI.instance, guiID, player.worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	public void flipPage(EntityPlayer player, int page) {
		player.openGui(ForestryAPI.instance, GuiHandler.encodeGuiData(guiID, page), player.worldObj, xCoord, yCoord, zCoord);
	}

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

	private static class NaturalistInventoryAdapter extends TileInventoryAdapter<TileNaturalistChest> {
		private final ISpeciesRoot speciesRoot;

		public NaturalistInventoryAdapter(TileNaturalistChest tile, ISpeciesRoot speciesRoot) {
			super(tile, 125, "Items");
			this.speciesRoot = speciesRoot;
		}

		@Override
		public boolean canSlotAccept(int slotIndex, ItemStack itemstack) {
			return speciesRoot.isMember(itemstack);
		}

		@Override
		public boolean canExtractItem(int slotIndex, ItemStack stack, int side) {
			return true;
		}

		@Override
		public void openInventory() {
			tile.numPlayersUsing++;
			tile.setNeedsNetworkUpdate();
		}

		@Override
		public void closeInventory() {
			tile.numPlayersUsing--;
			if (tile.numPlayersUsing < 0) {
				tile.numPlayersUsing = 0;
			}
			tile.setNeedsNetworkUpdate();
		}
	}
}

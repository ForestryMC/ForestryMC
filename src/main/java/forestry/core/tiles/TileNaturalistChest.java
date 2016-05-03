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
import net.minecraft.util.math.AxisAlignedBB;

import forestry.api.genetics.ISpeciesRoot;
import forestry.core.gui.ContainerNaturalistInventory;
import forestry.core.gui.GuiHandler;
import forestry.core.gui.GuiNaturalistInventory;
import forestry.core.gui.IPagedInventory;
import forestry.core.inventory.InventoryNaturalistChest;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;

public abstract class TileNaturalistChest extends TileBase implements IPagedInventory {
	private static final float lidAngleVariationPerTick = 0.1F;
	public static final AxisAlignedBB chestBoundingBox = new AxisAlignedBB(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);

	private final ISpeciesRoot speciesRoot;
	public float lidAngle;
	public float prevLidAngle;
	private int numPlayersUsing;

	public TileNaturalistChest(ISpeciesRoot speciesRoot) {
		super("naturalist.chest");
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
		worldObj.playSoundEffect(getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5, sound, 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
	}

	@Override
	public void flipPage(EntityPlayer player, short page) {
		GuiHandler.openGui(player, this, page);
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

	@Override
	public Object getGui(EntityPlayer player, int page) {
		ContainerNaturalistInventory container = new ContainerNaturalistInventory(player.inventory, this, page);
		return new GuiNaturalistInventory(speciesRoot, player, container, this, page, 5);
	}

	@Override
	public Object getContainer(EntityPlayer player, int page) {
		return new ContainerNaturalistInventory(player.inventory, this, page);
	}
}

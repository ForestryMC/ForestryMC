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

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.network.PacketBufferForestry;

//TODO - move to factory?
public abstract class TileMill extends TileBase {

	protected float speed;
	protected int stage = 0;
	public int charge = 0;
	public float progress;

	protected TileMill(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		speed = 0.01F;
	}

	@Override
	public void updateClientSide() {
		update(false);
	}

	@Override
	public void updateServerSide() {
		update(true);
	}

	@Override
	public void writeData(PacketBufferForestry data) {
		super.writeData(data);
		data.writeInt(charge);
		data.writeFloat(speed);
		data.writeInt(stage);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void readData(PacketBufferForestry data) throws IOException {
		super.readData(data);
		charge = data.readInt();
		speed = data.readFloat();
		stage = data.readInt();
	}

	private void update(boolean isSimulating) {

		// Stop gracefully if discharged.
		if (charge <= 0) {
			if (stage > 0) {
				progress += speed;
			}
			if (progress > 0.5) {
				stage = 2;
			}
			if (progress > 1) {
				progress = 0;
				stage = 0;
			}
			return;
		}

		// Update blades
		progress += speed;
		if (stage <= 0) {
			stage = 1;
		}

		if (progress > 0.5 && stage == 1) {
			stage = 2;
			if (charge < 7 && isSimulating) {
				charge++;
				setNeedsNetworkUpdate();
			}
		}
		if (progress > 1) {
			progress = 0;
			stage = 0;

			// Fully charged! Do something!
			if (charge >= 7) {
				activate();
			}
		}

	}

	protected abstract void activate();
}

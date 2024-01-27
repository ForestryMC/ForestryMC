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

import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraftforge.network.NetworkHooks;

import forestry.api.genetics.IForestrySpeciesRoot;
import forestry.core.gui.ContainerNaturalistInventory;
import forestry.core.gui.IPagedInventory;
import forestry.core.inventory.InventoryNaturalistChest;
import forestry.core.network.PacketBufferForestry;

public abstract class TileNaturalistChest extends TileBase implements IPagedInventory {
	private static final float lidAngleVariationPerTick = 0.1F;
	public static final VoxelShape CHEST_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);

	private final IForestrySpeciesRoot speciesRoot;
	public float lidAngle;
	public float prevLidAngle;
	private int numPlayersUsing;

	public TileNaturalistChest(BlockEntityType type, BlockPos pos, BlockState state, IForestrySpeciesRoot speciesRoot) {
		super(type, pos, state);
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
			playLidSound(SoundEvents.CHEST_OPEN);
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
				playLidSound(SoundEvents.CHEST_CLOSE);
			}
		}
	}

	private void playLidSound(SoundEvent sound) {
		this.level.playSound(null, getBlockPos(), sound, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
	}

	@Override
	public void flipPage(ServerPlayer player, short page) {
		NetworkHooks.openScreen(player, this, p -> {
			p.writeBlockPos(this.worldPosition);
			p.writeVarInt(page);
		});
	}

	@Override
	public void openGui(ServerPlayer player, BlockPos pos) {
		NetworkHooks.openScreen(player, this, p -> {
			p.writeBlockPos(this.worldPosition);
			p.writeVarInt(0);
		});
	}

	/* IStreamable */
	@Override
	public void writeData(PacketBufferForestry data) {
		super.writeData(data);
		data.writeInt(numPlayersUsing);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void readData(PacketBufferForestry data) throws IOException {
		super.readData(data);
		numPlayersUsing = data.readInt();
	}

	//TODO page stuff.
	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new ContainerNaturalistInventory(windowId, inv, this, 5);
	}

	public IForestrySpeciesRoot getSpeciesRoot() {
		return speciesRoot;
	}
}

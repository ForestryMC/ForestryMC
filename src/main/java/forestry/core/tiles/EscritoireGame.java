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

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Random;

import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IIndividual;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class EscritoireGame implements INbtWritable, INbtReadable, IStreamable {
	private static final Random rand = new Random();
	public static final int BOUNTY_MAX = 16;

	public enum Status {
		EMPTY, PLAYING, FAILURE, SUCCESS;
		public static final Status[] VALUES = values();
	}

	private EscritoireGameBoard gameBoard;
	private long lastUpdate;
	private int bountyLevel;
	private Status status = Status.EMPTY;

	public EscritoireGame() {
		gameBoard = new EscritoireGameBoard();
	}

	@Nullable
	public EscritoireGameToken getToken(int index) {
		return gameBoard.getToken(index);
	}

	public Status getStatus() {
		return status;
	}

	public long getLastUpdate() {
		return lastUpdate;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setInteger("bountyLevel", bountyLevel);
		nbttagcompound.setLong("lastUpdate", lastUpdate);
		gameBoard.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("Status", status.ordinal());
		return nbttagcompound;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		bountyLevel = nbt.getInteger("bountyLevel");
		lastUpdate = nbt.getLong("lastUpdate");
		gameBoard = new EscritoireGameBoard(nbt);

		if (nbt.hasKey("Status")) {
			int statusOrdinal = nbt.getInteger("Status");
			status = Status.values()[statusOrdinal];
		}

		lastUpdate = System.currentTimeMillis();
	}

	/* NETWORK */
	@Override
	public void writeData(PacketBufferForestry data) {
		data.writeInt(bountyLevel);
		gameBoard.writeData(data);
		data.writeEnum(status, Status.VALUES);
	}

	@Override
	public void readData(PacketBufferForestry data) throws IOException {
		bountyLevel = data.readInt();
		gameBoard.readData(data);
		status = data.readEnum(Status.VALUES);
	}

	/* INTERACTION */
	public void initialize(ItemStack specimen) {
		if (gameBoard.initialize(specimen)) {
			status = Status.PLAYING;
			bountyLevel = BOUNTY_MAX;
			lastUpdate = System.currentTimeMillis();
		}
	}

	public void probe(ItemStack specimen, IInventory inventory, int startSlot, int slotCount) {
		if (status != Status.PLAYING) {
			return;
		}

		IIndividual individual = AlleleManager.alleleRegistry.getIndividual(specimen);
		if (individual == null) {
			return;
		}

		if (bountyLevel > 1) {
			bountyLevel--;
		}

		IAlleleSpecies species = individual.getGenome().getPrimary();
		gameBoard.hideProbedTokens();

		int revealCount = getSampleSize(slotCount);
		for (int i = 0; i < revealCount; i++) {
			ItemStack sample = inventory.decrStackSize(startSlot + i, 1);
			if (!sample.isEmpty()) {
				if (rand.nextFloat() < species.getResearchSuitability(sample)) {
					gameBoard.probe();
				}
			}
		}

		lastUpdate = System.currentTimeMillis();
	}

	public void reset() {
		bountyLevel = BOUNTY_MAX;
		gameBoard.reset();
		status = Status.EMPTY;

		lastUpdate = System.currentTimeMillis();
	}

	public void choose(int tokenIndex) {
		if (getStatus() != Status.PLAYING) {
			return;
		}

		EscritoireGameToken token = gameBoard.getToken(tokenIndex);
		if (token != null) {
			status = gameBoard.choose(token);
			lastUpdate = System.currentTimeMillis();
		}
	}

	public int getBountyLevel() {
		return bountyLevel;
	}

	/* RETRIEVAL */
	public int getSampleSize(int slotCount) {
		if (status == Status.EMPTY) {
			return 0;
		}

		int samples = gameBoard.getTokenCount() / 4;
		samples = Math.max(samples, 2);
		return Math.min(samples, slotCount);
	}
}

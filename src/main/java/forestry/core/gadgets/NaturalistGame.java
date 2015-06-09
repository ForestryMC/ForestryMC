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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import forestry.api.core.INBTTagable;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamable;
import forestry.core.utils.StringUtil;
import forestry.core.utils.Utils;

public class NaturalistGame implements INBTTagable, IStreamable {

	public static final int BOUNTY_MAX = 16;

	public static class GameToken implements INBTTagable, IStreamable {

		private static final String[] OVERLAY_NONE = new String[0];
		private static final String[] OVERLAY_FAILED = new String[]{"errors/errored"};

		public ItemStack tokenStack;

		protected boolean isFailed = false;
		protected boolean isProbed = false;
		protected boolean isRevealed = false;

		public GameToken() {

		}

		public GameToken(ItemStack tokenStack) {
			this.tokenStack = tokenStack;
		}

		public GameToken(NBTTagCompound nbttagcompound) {
			readFromNBT(nbttagcompound);
		}

		@Override
		public void readFromNBT(NBTTagCompound nbttagcompound) {
			isFailed = nbttagcompound.getBoolean("isFailed");
			isProbed = nbttagcompound.getBoolean("isProbed");
			isRevealed = nbttagcompound.getBoolean("isRevealed");

			if (nbttagcompound.hasKey("tokenStack")) {
				tokenStack = ItemStack.loadItemStackFromNBT(nbttagcompound.getCompoundTag("tokenStack"));
			}
		}

		@Override
		public void writeToNBT(NBTTagCompound nbttagcompound) {
			nbttagcompound.setBoolean("isFailed", isFailed);
			nbttagcompound.setBoolean("isProbed", isProbed);
			nbttagcompound.setBoolean("isRevealed", isRevealed);

			if (tokenStack != null) {
				NBTTagCompound stackcompound = new NBTTagCompound();
				tokenStack.writeToNBT(stackcompound);
				nbttagcompound.setTag("tokenStack", stackcompound);
			}
		}

		/* NETWORK */
		@Override
		public void writeData(DataOutputStreamForestry data) throws IOException {
			data.writeBoolean(isFailed);
			data.writeBoolean(isProbed);
			data.writeBoolean(isRevealed);
			data.writeItemStack(tokenStack);
		}

		@Override
		public void readData(DataInputStreamForestry data) throws IOException {
			isFailed = data.readBoolean();
			isProbed = data.readBoolean();
			isRevealed = data.readBoolean();
			tokenStack = data.readItemStack();
		}

		public boolean isVisible() {
			return isRevealed;
		}

		public int getTokenColour() {
			if (tokenStack == null || !isVisible()) {
				return 0xffffff;
			}

			if (isProbed) {
				return Utils.multiplyRGBComponents(AlleleManager.alleleRegistry.getIndividual(tokenStack).getGenome().getPrimary().getIconColour(0), 0.7f);
			} else {
				return AlleleManager.alleleRegistry.getIndividual(tokenStack).getGenome().getPrimary().getIconColour(0);
			}
		}

		public String getTooltip() {
			return tokenStack != null ? tokenStack.getDisplayName() : StringUtil.localize("gui.unknown");
		}

		public String[] getOverlayIcons() {
			return isFailed ? OVERLAY_FAILED : OVERLAY_NONE;
		}

		public boolean matches(GameToken other) {
			return tokenStack.isItemEqual(other.tokenStack) && ItemStack.areItemStackTagsEqual(tokenStack, other.tokenStack);
		}
	}

	private final Random rand = new Random();
	private GameToken[] gameTokens;
	private long lastUpdate;

	private boolean isEnded;
	private int bountyLevel;

	public long getLastUpdate() {
		return lastUpdate;
	}

	/* SAVING & LOADING */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setBoolean("isEnded", isEnded);
		nbttagcompound.setInteger("bountyLevel", bountyLevel);
		nbttagcompound.setLong("lastUpdate", lastUpdate);

		if (gameTokens != null) {
			nbttagcompound.setInteger("TokenCount", gameTokens.length);
			NBTTagList nbttaglist = new NBTTagList();

			for (int i = 0; i < gameTokens.length; i++) {
				if (gameTokens[i] == null) {
					continue;
				}

				NBTTagCompound nbttagcompound2 = new NBTTagCompound();
				nbttagcompound2.setByte("Slot", (byte) i);
				gameTokens[i].writeToNBT(nbttagcompound2);
				nbttaglist.appendTag(nbttagcompound2);
			}

			nbttagcompound.setTag("GameTokens", nbttaglist);
		} else {
			nbttagcompound.setInteger("TokenCount", 0);
		}

	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		isEnded = nbttagcompound.getBoolean("isEnded");
		bountyLevel = nbttagcompound.getInteger("bountyLevel");
		lastUpdate = nbttagcompound.getLong("lastUpdate");

		int tokenCount = nbttagcompound.getInteger("TokenCount");
		if (tokenCount > 0) {
			gameTokens = new GameToken[tokenCount];
			NBTTagList nbttaglist = nbttagcompound.getTagList("GameTokens", 10);

			for (int j = 0; j < nbttaglist.tagCount(); ++j) {
				NBTTagCompound nbttagcompound2 = nbttaglist.getCompoundTagAt(j);
				int index = nbttagcompound2.getByte("Slot");
				gameTokens[index] = new GameToken(nbttagcompound2);
			}
		}

		lastUpdate = System.currentTimeMillis();
	}

	/* NETWORK */
	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		data.writeBoolean(isEnded);
		data.writeInt(bountyLevel);
		data.writeLong(lastUpdate);

		List<GameToken> gameTokensList = gameTokens == null ? null : Arrays.asList(gameTokens);
		data.writeStreamables(gameTokensList);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		isEnded = data.readBoolean();
		bountyLevel = data.readInt();
		lastUpdate = data.readLong();

		List<GameToken> gameTokensList = data.readStreamables(GameToken.class);
		if (gameTokensList != null) {
			this.gameTokens = gameTokensList.toArray(new GameToken[gameTokensList.size()]);
		}
	}

	/* INTERACTION */
	public void initialize(ItemStack specimen) {
		IIndividual individual = AlleleManager.alleleRegistry.getIndividual(specimen);
		if (individual == null) {
			return;
		}

		int boardSize = individual.getGenome().getPrimary().getComplexity() + individual.getGenome().getSecondary().getComplexity();
		if (boardSize % 2 != 0) {
			boardSize = Math.round((float) boardSize / 2) * 2;
		}
		boardSize = boardSize <= 22 ? boardSize >= 6 ? boardSize : 6 : 22;

		isEnded = false;
		bountyLevel = BOUNTY_MAX;
		gameTokens = new GameToken[boardSize];

		ISpeciesRoot root = individual.getGenome().getPrimary().getRoot();
		ArrayList<ItemStack> pairs = new ArrayList<ItemStack>();
		for (int i = 0; i < boardSize / 2; i++) {
			IIndividual token = root.templateAsIndividual(root.getRandomTemplate(rand));
			pairs.add(root.getMemberStack(token, 0));
		}

		boolean first = true;
		for (ItemStack pair : pairs) {
			if (first) {
				gameTokens[0] = new GameToken(pair.copy());
				first = false;
			} else {
				gameTokens[getFreeTokenIndex()] = new GameToken(pair.copy());
			}
			gameTokens[getFreeTokenIndex()] = new GameToken(pair.copy());
		}

		lastUpdate = System.currentTimeMillis();
	}

	private int getFreeTokenIndex() {
		while (true) {
			int probe = rand.nextInt(gameTokens.length);
			if (gameTokens[probe] == null) {
				return probe;
			}
		}
	}

	private int countUnrevealedTokens() {
		int count = 0;
		for (GameToken token : gameTokens) {
			if (!token.isRevealed) {
				count++;
			}
		}

		return count;
	}

	private int[] getUnrevealedTokens(int count) {
		ArrayList<Integer> existing = new ArrayList<Integer>();
		int remaining = countUnrevealedTokens();
		int[] unrevealed = new int[count < remaining ? count : remaining];

		for (int i = 0; i < unrevealed.length; i++) {

			int found = -1;
			while (found < 0) {
				int probe = rand.nextInt(gameTokens.length);
				if (gameTokens[probe].isRevealed) {
					continue;
				}

				boolean taken = false;
				for (int exist : existing) {
					if (exist == probe) {
						taken = true;
						break;
					}
				}
				if (!taken) {
					existing.add(probe);
					found = probe;
				}
			}
			unrevealed[i] = found;
		}

		return unrevealed;
	}

	public void probe(ItemStack specimen, IInventory inventory, int startSlot, int slotCount) {
		if (gameTokens == null) {
			return;
		}
		if (isEnded) {
			return;
		}
		IIndividual individual = AlleleManager.alleleRegistry.getIndividual(specimen);
		if (individual == null) {
			return;
		}

		hideProbedTokens();
		if (bountyLevel > 1) {
			bountyLevel--;
		}

		int[] tokenIndices = getUnrevealedTokens(getSampleSize() <= slotCount ? getSampleSize() : slotCount);
		int processedTokens = 0;

		for (int i = 0; i < slotCount; i++) {
			ItemStack sample = inventory.getStackInSlot(startSlot + i);
			if (sample == null || sample.stackSize <= 0) {
				continue;
			}

			sample = inventory.decrStackSize(startSlot + i, 1);
			if (rand.nextFloat() >= individual.getGenome().getPrimary().getResearchSuitability(sample)) {
				continue;
			}

			GameToken token = gameTokens[tokenIndices[processedTokens]];
			token.isProbed = true;
			token.isRevealed = true;

			processedTokens++;
			if (processedTokens >= tokenIndices.length) {
				break;
			}
		}

		lastUpdate = System.currentTimeMillis();
	}

	public void choose(final int tokenIndex) {
		if (isEnded) {
			return;
		}
		if (gameTokens == null || tokenIndex >= gameTokens.length) {
			return;
		}

		GameToken token = gameTokens[tokenIndex];
		if (token.isRevealed && !token.isProbed) {
			return;
		}
		hideProbedTokens();

		Collection<GameToken> singles = getRevealedSingles(token);
		if (singles.size() > 0) {
			boolean matched = false;
			for (GameToken single : singles) {
				if (single.matches(token)) {
					matched = true;
					break;
				}
			}
			if (!matched) {
				token.isFailed = true;
			}
		}
		token.isRevealed = true;
		checkGameEnd();
		lastUpdate = System.currentTimeMillis();
	}

	public void reset() {
		gameTokens = null;
		lastUpdate = System.currentTimeMillis();
	}

	public boolean isInited() {
		return gameTokens != null;
	}

	public boolean isEnded() {
		return isEnded;
	}

	public int getBountyLevel() {
		return bountyLevel;
	}

	public boolean isWon() {
		if (!isEnded) {
			return false;
		}

		return !isLost();
	}

	private void checkGameEnd() {
		if (isLost()) {
			isEnded = true;
		}
		if (isRevealed()) {
			isEnded = true;
		}
	}

	private boolean isRevealed() {
		for (GameToken token : gameTokens) {
			if (!token.isRevealed) {
				return false;
			}
		}

		return true;
	}

	private boolean isLost() {
		for (GameToken token : gameTokens) {
			if (token.isFailed) {
				return true;
			}
		}

		return false;
	}

	private void hideProbedTokens() {
		for (GameToken token : gameTokens) {
			if (token.isProbed) {
				token.isRevealed = false;
				token.isProbed = false;
			}
		}
	}

	private Collection<GameToken> getRevealedSingles(GameToken exclude) {
		ArrayList<GameToken> singles = new ArrayList<GameToken>();

		for (GameToken token : gameTokens) {
			if (!token.isRevealed) {
				continue;
			}
			if (token == exclude) {
				continue;
			}

			GameToken matching = null;
			for (GameToken single : singles) {
				if (!single.matches(token)) {
					continue;
				}
				matching = single;
				break;
			}

			if (matching == null) {
				singles.add(token);
			} else {
				singles.remove(matching);
			}
		}

		return singles;
	}

	/* RETRIEVAL */
	public int getSampleSize() {
		if (gameTokens == null) {
			return 0;
		}

		int samples = gameTokens.length / 4;
		return samples >= 2 ? samples : 2;
	}

	public GameToken getToken(int index) {
		return gameTokens != null ? index < gameTokens.length ? gameTokens[index] : null : null;
	}
}

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import forestry.api.core.INBTTagable;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamable;

public class EscritoireGameBoard implements INBTTagable, IStreamable {
	private static final Random rand = new Random();
	private static final int TOKEN_COUNT_MAX = 22;
	private static final int TOKEN_COUNT_MIN = 6;

	private final List<EscritoireGameToken> gameTokens = new ArrayList<>(TOKEN_COUNT_MAX);
	private int tokenCount;


	public boolean initialize(ItemStack specimen) {
		IIndividual individual = AlleleManager.alleleRegistry.getIndividual(specimen);
		if (individual == null) {
			return false;
		}

		IGenome genome = individual.getGenome();
		ISpeciesRoot root = genome.getPrimary().getRoot();

		tokenCount = getTokenCount(genome);

		for (int i = 0; i < tokenCount / 2; i++) {
			IIndividual token = root.templateAsIndividual(root.getRandomTemplate(rand));
			ItemStack pair = root.getMemberStack(token, 0);
			gameTokens.add(new EscritoireGameToken(pair));
			gameTokens.add(new EscritoireGameToken(pair));
		}
		Collections.shuffle(gameTokens);
		return true;
	}

	public EscritoireGameToken getToken(int index) {
		if (index >= tokenCount) {
			return null;
		}
		return gameTokens.get(index);
	}

	public int getTokenCount() {
		return tokenCount;
	}

	public void hideProbedTokens() {
		for (EscritoireGameToken token : gameTokens) {
			if (token.isProbed()) {
				token.setProbed(false);
			}
		}
	}

	private List<EscritoireGameToken> getUnrevealedTokens() {
		List<EscritoireGameToken> unrevealed = new ArrayList<>();
		for (EscritoireGameToken token : gameTokens) {
			if (!token.isVisible()) {
				unrevealed.add(token);
			}
		}

		return unrevealed;
	}

	private EscritoireGameToken getSelected() {
		for (EscritoireGameToken token : gameTokens) {
			if (token.isSelected()) {
				return token;
			}
		}

		return null;
	}

	private boolean isBoardCleared() {
		for (EscritoireGameToken token : gameTokens) {
			if (!token.isMatched()) {
				return false;
			}
		}

		return true;
	}

	public void probe() {
		List<EscritoireGameToken> tokens = getUnrevealedTokens();
		int index = rand.nextInt(tokens.size());

		EscritoireGameToken token = tokens.get(index);
		token.setProbed(true);
	}

	public EscritoireGame.Status choose(EscritoireGameToken token) {
		EscritoireGame.Status status = EscritoireGame.Status.PLAYING;
		if (token.isMatched() || token.isSelected()) {
			return status;
		}

		EscritoireGameToken selected = getSelected();
		if (selected == null) {
			token.setSelected();
			hideProbedTokens();
		} else if (token.matches(selected)) {
			selected.setMatched();
			token.setMatched();
			if (isBoardCleared()) {
				status = EscritoireGame.Status.SUCCESS;
			}
			hideProbedTokens();
		} else {
			token.setFailed();
			selected.setFailed();
			status = EscritoireGame.Status.FAILURE;
		}

		return status;
	}

	public void reset() {
		gameTokens.clear();
		tokenCount = 0;
	}

	private static int getTokenCount(IGenome genome) {
		IAlleleSpecies species1 = genome.getPrimary();
		IAlleleSpecies species2 = genome.getSecondary();

		int tokenCount = species1.getComplexity() + species2.getComplexity();

		if (tokenCount % 2 != 0) {
			tokenCount = Math.round((float) tokenCount / 2) * 2;
		}

		if (tokenCount > TOKEN_COUNT_MAX) {
			tokenCount = TOKEN_COUNT_MAX;
		} else if (tokenCount < TOKEN_COUNT_MIN) {
			tokenCount = TOKEN_COUNT_MIN;
		}

		return tokenCount;
	}

	/* INBTTagable */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		tokenCount = nbttagcompound.getInteger("TokenCount");
		gameTokens.clear();

		if (tokenCount > 0) {
			EscritoireGameToken[] tokens = new EscritoireGameToken[tokenCount];
			NBTTagList nbttaglist = nbttagcompound.getTagList("GameTokens", 10);

			for (int j = 0; j < nbttaglist.tagCount(); ++j) {
				NBTTagCompound nbttagcompound2 = nbttaglist.getCompoundTagAt(j);
				int index = nbttagcompound2.getByte("Slot");
				tokens[index] = new EscritoireGameToken(nbttagcompound2);
			}

			Collections.addAll(gameTokens, tokens);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		if (tokenCount > 0) {
			nbttagcompound.setInteger("TokenCount", tokenCount);
			NBTTagList nbttaglist = new NBTTagList();

			for (int i = 0; i < tokenCount; i++) {
				EscritoireGameToken token = gameTokens.get(i);
				if (token == null) {
					continue;
				}

				NBTTagCompound nbttagcompound2 = new NBTTagCompound();
				nbttagcompound2.setByte("Slot", (byte) i);
				token.writeToNBT(nbttagcompound2);
				nbttaglist.appendTag(nbttagcompound2);
			}

			nbttagcompound.setTag("GameTokens", nbttaglist);
		} else {
			nbttagcompound.setInteger("TokenCount", 0);
		}
	}

	/* IStreamable */
	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		data.writeVarInt(tokenCount);
		data.writeStreamables(gameTokens);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		tokenCount = data.readVarInt();
		data.readStreamables(gameTokens, EscritoireGameToken.class);
	}
}

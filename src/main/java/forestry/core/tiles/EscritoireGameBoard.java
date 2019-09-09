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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

import genetics.api.GeneticsAPI;
import genetics.api.alleles.IAllele;
import genetics.api.individual.IGenome;
import genetics.api.individual.IIndividual;
import genetics.api.individual.IKaryotype;
import genetics.api.root.IIndividualRoot;

import forestry.api.core.INbtWritable;
import forestry.api.genetics.IAlleleForestrySpecies;
import forestry.core.network.IStreamable;
import forestry.core.network.PacketBufferForestry;

public class EscritoireGameBoard implements INbtWritable, IStreamable {
	private static final Random rand = new Random();
	private static final int TOKEN_COUNT_MAX = 22;
	private static final int TOKEN_COUNT_MIN = 6;

	private final List<EscritoireGameToken> gameTokens = new ArrayList<>(TOKEN_COUNT_MAX);
	private int tokenCount;

	public EscritoireGameBoard() {

	}

	public EscritoireGameBoard(CompoundNBT nbt) {
		tokenCount = nbt.getInt("TokenCount");

		if (tokenCount > 0) {
			EscritoireGameToken[] tokens = new EscritoireGameToken[tokenCount];
			ListNBT nbttaglist = nbt.getList("GameTokens", 10);

			for (int j = 0; j < nbttaglist.size(); ++j) {
				CompoundNBT CompoundNBT2 = nbttaglist.getCompound(j);
				int index = CompoundNBT2.getByte("Slot");
				tokens[index] = new EscritoireGameToken(CompoundNBT2);
			}

			Collections.addAll(gameTokens, tokens);
		}
	}

	public boolean initialize(ItemStack specimen) {
		Optional<IIndividual> optional = GeneticsAPI.apiInstance.getRootHelper().getIndividual(specimen);
		if (!optional.isPresent()) {
			return false;
		}
		IIndividual individual = optional.get();

		IGenome genome = individual.getGenome();
		IKaryotype karyotype = genome.getKaryotype();
		IIndividualRoot root = genome.getPrimary().getRoot();

		tokenCount = getTokenCount(genome);

		for (int i = 0; i < tokenCount / 2; i++) {
			IAllele[] randomTemplate = root.getTemplates().getRandomTemplate(rand);
			String speciesUid = randomTemplate[karyotype.getSpeciesType().getIndex()].getRegistryName().toString();
			gameTokens.add(new EscritoireGameToken(speciesUid));
			gameTokens.add(new EscritoireGameToken(speciesUid));
		}
		Collections.shuffle(gameTokens);
		return true;
	}

	@Nullable
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

	@Nullable
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
		IAlleleForestrySpecies species1 = genome.getPrimary(IAlleleForestrySpecies.class);
		IAlleleForestrySpecies species2 = genome.getSecondary(IAlleleForestrySpecies.class);

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

	@Override
	public CompoundNBT write(CompoundNBT compoundNBT) {
		if (tokenCount > 0) {
			compoundNBT.putInt("TokenCount", tokenCount);
			ListNBT nbttaglist = new ListNBT();

			for (int i = 0; i < tokenCount; i++) {
				EscritoireGameToken token = gameTokens.get(i);
				if (token == null) {
					continue;
				}

				CompoundNBT compoundNBT2 = new CompoundNBT();
				compoundNBT2.putByte("Slot", (byte) i);
				token.write(compoundNBT2);
				nbttaglist.add(compoundNBT2);
			}

			compoundNBT.put("GameTokens", nbttaglist);
		} else {
			compoundNBT.putInt("TokenCount", 0);
		}
		return compoundNBT;
	}

	/* IStreamable */
	@Override
	public void writeData(PacketBufferForestry data) {
		data.writeVarInt(tokenCount);
		data.writeStreamables(gameTokens);
	}

	@Override
	public void readData(PacketBufferForestry data) throws IOException {
		tokenCount = data.readVarInt();
		data.readStreamables(gameTokens, EscritoireGameToken::new);
	}
}

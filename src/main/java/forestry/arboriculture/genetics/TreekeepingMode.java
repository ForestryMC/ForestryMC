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
package forestry.arboriculture.genetics;

import java.util.ArrayList;
import java.util.Locale;

import forestry.api.arboriculture.ITreeGenome;
import forestry.api.arboriculture.ITreekeepingMode;

public class TreekeepingMode implements ITreekeepingMode {

	public static final ITreekeepingMode easy = new TreekeepingMode("EASY", 1.4f, 1.3f, 1.2f, 1.2f);
	public static final ITreekeepingMode normal = new TreekeepingMode("NORMAL", 1f, 1f, 1f, 1f);
	public static final ITreekeepingMode hard = new TreekeepingMode("HARD", 0.9f, 0.9f, 0.9f, 0.9f);
	public static final ITreekeepingMode hardcore = new TreekeepingMode("HARDCORE", 0.7f, 0.7f, 0.5f, 0.5f);
	public static final ITreekeepingMode insane = new TreekeepingMode("INSANE", 0.5f, 0.5f, 0.2f, 0.1f);

	private final String name;
	private final float yieldModifier;
	private final float sappinessModifier;
	private final float maturationModifier;
	private final float mutationModifier;

	public TreekeepingMode(String name, float yieldModifier, float sappinessModifier, float maturationModifier, float mutationModifier) {
		this.name = name;
		this.yieldModifier = yieldModifier;
		this.sappinessModifier = sappinessModifier;
		this.maturationModifier = maturationModifier;
		this.mutationModifier = mutationModifier;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public ArrayList<String> getDescription() {
		ArrayList<String> ret = new ArrayList<>();
		ret.add("treemode." + name.toLowerCase(Locale.ENGLISH) + ".desc");
		return ret;
	}

	@Override
	public float getHeightModifier(ITreeGenome genome, float currentModifier) {
		return 1f;
	}

	@Override
	public float getYieldModifier(ITreeGenome genome, float currentModifier) {
		return yieldModifier;
	}

	@Override
	public float getSappinessModifier(ITreeGenome genome, float currentModifier) {
		return sappinessModifier;
	}

	@Override
	public float getMaturationModifier(ITreeGenome genome, float currentModifier) {
		return maturationModifier;
	}

	@Override
	public float getMutationModifier(ITreeGenome genome, ITreeGenome mate, float currentModifier) {
		return mutationModifier;
	}
}

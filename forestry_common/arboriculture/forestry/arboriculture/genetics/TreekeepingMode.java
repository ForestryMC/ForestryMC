/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.arboriculture.genetics;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import forestry.api.arboriculture.ITreeGenome;
import forestry.api.arboriculture.ITreekeepingMode;

public class TreekeepingMode implements ITreekeepingMode {

	public static ITreekeepingMode easy = new TreekeepingMode("EASY", 1.4f, 1.3f, 1.2f, 1.2f);
	public static ITreekeepingMode normal = new TreekeepingMode("NORMAL", 1f, 1f, 1f, 1f);
	public static ITreekeepingMode hard = new TreekeepingMode("HARD", 0.9f, 0.9f, 0.9f, 0.9f);
	public static ITreekeepingMode hardcore = new TreekeepingMode("HARDCORE", 0.7f, 0.7f, 0.5f, 0.5f);
	public static ITreekeepingMode insane = new TreekeepingMode("INSANE", 0.5f, 0.5f, 0.2f, 0.1f);

	final Random rand;
	final String name;
	private float yieldModifier;
	private float sappinessModifier;
	private float maturationModifier;
	private float mutationModifier;

	public TreekeepingMode(String name, float yieldModifier, float sappinessModifier, float maturationModifier, float mutationModifier) {
		this.rand = new Random();
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
		ArrayList<String> ret = new ArrayList<String>();
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

package forestry.apiculture.genetics.alleles;

import java.util.Arrays;
import java.util.List;

import net.minecraft.potion.Effects;

import genetics.api.alleles.IAlleleRegistry;

import forestry.api.apiculture.genetics.BeeChromosomes;
import forestry.api.apiculture.genetics.IAlleleBeeEffect;

public class AlleleEffects {
	public static final IAlleleBeeEffect effectNone;
	public static final IAlleleBeeEffect effectAggressive;
	public static final IAlleleBeeEffect effectHeroic;
	public static final IAlleleBeeEffect effectBeatific;
	public static final IAlleleBeeEffect effectMiasmic;
	public static final IAlleleBeeEffect effectMisanthrope;
	public static final IAlleleBeeEffect effectGlacial;
	public static final IAlleleBeeEffect effectRadioactive;
	public static final IAlleleBeeEffect effectCreeper;
	public static final IAlleleBeeEffect effectIgnition;
	public static final IAlleleBeeEffect effectExploration;
	public static final IAlleleBeeEffect effectFestiveEaster;
	public static final IAlleleBeeEffect effectSnowing;
	public static final IAlleleBeeEffect effectDrunkard;
	public static final IAlleleBeeEffect effectReanimation;
	public static final IAlleleBeeEffect effectResurrection;
	public static final IAlleleBeeEffect effectRepulsion;
	public static final IAlleleBeeEffect effectFertile;
	public static final IAlleleBeeEffect effectMycophilic;
	private static final List<IAlleleBeeEffect> beeEffects;

	static {
		beeEffects = Arrays.asList(
			effectNone = new AlleleEffectNone("none", true),
			effectAggressive = new AlleleEffectAggressive(),
			effectHeroic = new AlleleEffectHeroic(),
			effectBeatific = new AlleleEffectPotion("beatific", false, Effects.REGENERATION, 100),
			effectMiasmic = new AlleleEffectPotion("miasmic", false, Effects.POISON, 600, 100, 0.1f),
			effectMisanthrope = new AlleleEffectMisanthrope(),
			effectGlacial = new AlleleEffectGlacial(),
			effectRadioactive = new AlleleEffectRadioactive(),
			effectCreeper = new AlleleEffectCreeper(),
			effectIgnition = new AlleleEffectIgnition(),
			effectExploration = new AlleleEffectExploration(),
			effectFestiveEaster = new AlleleEffectNone("festive_easter", true),
			effectSnowing = new AlleleEffectSnowing(),
			effectDrunkard = new AlleleEffectPotion("drunkard", false, Effects.NAUSEA, 100),
			effectReanimation = new AlleleEffectResurrection("reanimation", AlleleEffectResurrection.getReanimationList()),
			effectResurrection = new AlleleEffectResurrection("resurrection", AlleleEffectResurrection.getResurrectionList()),
			effectRepulsion = new AlleleEffectRepulsion(),
			effectFertile = new AlleleEffectFertile(),
			effectMycophilic = new AlleleEffectFungification()
		);
	}

	public static void registerAlleles(IAlleleRegistry registry) {
		for (IAlleleBeeEffect beeEffect : beeEffects) {
			registry.registerAllele(beeEffect, BeeChromosomes.EFFECT);
		}
	}
}

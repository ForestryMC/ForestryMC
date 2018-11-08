package forestry.apiculture.genetics.alleles;

import java.util.Arrays;
import java.util.List;

import net.minecraft.init.MobEffects;

import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.IAlleleBeeEffect;
import forestry.api.genetics.AlleleManager;

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
			effectBeatific = new AlleleEffectPotion("beatific", false, MobEffects.REGENERATION, 100),
			effectMiasmic = new AlleleEffectPotion("miasmic", false, MobEffects.POISON, 600, 100, 0.1f),
			effectMisanthrope = new AlleleEffectMisanthrope(),
			effectGlacial = new AlleleEffectGlacial(),
			effectRadioactive = new AlleleEffectRadioactive(),
			effectCreeper = new AlleleEffectCreeper(),
			effectIgnition = new AlleleEffectIgnition(),
			effectExploration = new AlleleEffectExploration(),
			effectFestiveEaster = new AlleleEffectNone("festiveEaster", true),
			effectSnowing = new AlleleEffectSnowing(),
			effectDrunkard = new AlleleEffectPotion("drunkard", false, MobEffects.NAUSEA, 100),
			effectReanimation = new AlleleEffectResurrection("reanimation", AlleleEffectResurrection.getReanimationList()),
			effectResurrection = new AlleleEffectResurrection("resurrection", AlleleEffectResurrection.getResurrectionList()),
			effectRepulsion = new AlleleEffectRepulsion(),
			effectFertile = new AlleleEffectFertile(),
			effectMycophilic = new AlleleEffectFungification()
		);
	}

	public static void registerAlleles() {
		for (IAlleleBeeEffect beeEffect : beeEffects) {
			AlleleManager.alleleRegistry.registerAllele(beeEffect, EnumBeeChromosome.EFFECT);
		}
	}
}

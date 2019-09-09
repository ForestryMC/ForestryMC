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

import javax.annotation.Nullable;
import java.awt.Color;
import java.util.Locale;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LogBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import com.mojang.authlib.GameProfile;

import genetics.api.alleles.IAlleleRegistry;
import genetics.api.alleles.IAlleleTemplate;
import genetics.api.alleles.IAlleleTemplateBuilder;
import genetics.api.individual.IGenome;
import genetics.api.root.ITemplateContainer;
import genetics.api.root.components.ComponentKey;
import genetics.api.root.components.ComponentKeys;
import genetics.api.root.components.IRootComponent;

import forestry.api.arboriculture.EnumForestryWoodType;
import forestry.api.arboriculture.EnumFruitFamily;
import forestry.api.arboriculture.EnumLeafType;
import forestry.api.arboriculture.EnumVanillaWoodType;
import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.IGermlingModelProvider;
import forestry.api.arboriculture.ILeafProvider;
import forestry.api.arboriculture.ILeafSpriteProvider;
import forestry.api.arboriculture.ITreeGenerator;
import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.api.arboriculture.genetics.EnumGermlingType;
import forestry.api.arboriculture.genetics.IAlleleTreeSpecies;
import forestry.api.arboriculture.genetics.IAlleleTreeSpeciesBuilder;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.ITreeMutationBuilder;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.world.ITreeGenData;
import forestry.arboriculture.ModuleArboriculture;
import forestry.arboriculture.genetics.alleles.AlleleFruits;
import forestry.arboriculture.models.ModelProviderFactory;
import forestry.arboriculture.tiles.TileLeaves;
import forestry.arboriculture.worldgen.FeatureAcacia;
import forestry.arboriculture.worldgen.FeatureAcaciaVanilla;
import forestry.arboriculture.worldgen.FeatureBalsa;
import forestry.arboriculture.worldgen.FeatureBaobab;
import forestry.arboriculture.worldgen.FeatureBirch;
import forestry.arboriculture.worldgen.FeatureCherry;
import forestry.arboriculture.worldgen.FeatureChestnut;
import forestry.arboriculture.worldgen.FeatureCocobolo;
import forestry.arboriculture.worldgen.FeatureDarkOak;
import forestry.arboriculture.worldgen.FeatureDate;
import forestry.arboriculture.worldgen.FeatureEbony;
import forestry.arboriculture.worldgen.FeatureGiganteum;
import forestry.arboriculture.worldgen.FeatureGreenheart;
import forestry.arboriculture.worldgen.FeatureIpe;
import forestry.arboriculture.worldgen.FeatureJungle;
import forestry.arboriculture.worldgen.FeatureKapok;
import forestry.arboriculture.worldgen.FeatureLarch;
import forestry.arboriculture.worldgen.FeatureLemon;
import forestry.arboriculture.worldgen.FeatureMahoe;
import forestry.arboriculture.worldgen.FeatureMahogany;
import forestry.arboriculture.worldgen.FeatureMaple;
import forestry.arboriculture.worldgen.FeatureOak;
import forestry.arboriculture.worldgen.FeaturePadauk;
import forestry.arboriculture.worldgen.FeaturePapaya;
import forestry.arboriculture.worldgen.FeaturePine;
import forestry.arboriculture.worldgen.FeaturePlum;
import forestry.arboriculture.worldgen.FeaturePoplar;
import forestry.arboriculture.worldgen.FeatureSequoia;
import forestry.arboriculture.worldgen.FeatureSilverLime;
import forestry.arboriculture.worldgen.FeatureSpruce;
import forestry.arboriculture.worldgen.FeatureTeak;
import forestry.arboriculture.worldgen.FeatureWalnut;
import forestry.arboriculture.worldgen.FeatureWenge;
import forestry.arboriculture.worldgen.FeatureWillow;
import forestry.arboriculture.worldgen.FeatureZebrawood;
import forestry.core.config.Constants;
import forestry.core.genetics.TemplateMatcher;
import forestry.core.genetics.alleles.EnumAllele;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.RenderUtil;

public enum TreeDefinition implements ITreeDefinition, ITreeGenerator, IStringSerializable {
	Oak(TreeBranchDefinition.QUERCUS, "appleOak", "robur", false, EnumLeafType.DECIDUOUS, new Color(4764952), new Color(4764952).brighter(), EnumVanillaWoodType.OAK) {
		@Override
		public Feature<NoFeatureConfig> getTreeFeature(ITreeGenData tree) {
			return new FeatureOak(tree);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(TreeChromosomes.FRUITS, AlleleFruits.fruitApple);
			template.set(TreeChromosomes.FERTILITY, EnumAllele.Saplings.AVERAGE);
			template.set(TreeChromosomes.MATURATION, EnumAllele.Maturation.FASTER);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.POMES);
		}

		@Override
		protected void registerMutations() {
			// vanilla
		}

		@Override
		public boolean hasFruitLeaves() {
			return true;
		}
	},
	DarkOak(TreeBranchDefinition.QUERCUS, "darkOak", "velutina", false, EnumLeafType.DECIDUOUS, new Color(4764952), new Color(4764952).brighter(), EnumVanillaWoodType.DARK_OAK) {
		@Override
		public Feature<NoFeatureConfig> getTreeFeature(ITreeGenData tree) {
			return new FeatureDarkOak(tree);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(TreeChromosomes.FERTILITY, EnumAllele.Saplings.AVERAGE);
			template.set(TreeChromosomes.MATURATION, EnumAllele.Maturation.FASTER);
			template.set(TreeChromosomes.GIRTH, 2);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.POMES);
		}

		@Override
		protected void registerMutations() {
			// vanilla
		}
	},
	Birch(TreeBranchDefinition.BETULA, "silverBirch", "pendula", false, EnumLeafType.DECIDUOUS, new Color(8431445), new Color(0xb0c648), EnumVanillaWoodType.BIRCH) {
		@Override
		public Feature<NoFeatureConfig> getTreeFeature(ITreeGenData tree) {
			return new FeatureBirch(tree);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(TreeChromosomes.FERTILITY, EnumAllele.Saplings.AVERAGE);
			template.set(TreeChromosomes.MATURATION, EnumAllele.Maturation.FASTER);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {

		}

		@Override
		protected void registerMutations() {
			// vanilla
		}
	},
	Lime(TreeBranchDefinition.TILIA, "silverLime", "pendula", true, EnumLeafType.DECIDUOUS, new Color(0x5ea107), new Color(0x5ea107).brighter(), EnumForestryWoodType.LIME) {
		@Override
		public Feature<NoFeatureConfig> getTreeFeature(ITreeGenData tree) {
			return new FeatureSilverLime(tree);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(TreeChromosomes.FERTILITY, EnumAllele.Saplings.LOW);
			template.set(TreeChromosomes.SAPPINESS, EnumAllele.Sappiness.LOWER);
			template.set(TreeChromosomes.YIELD, EnumAllele.Yield.LOWER);
			//Allele.helper.set(alleles, EnumTreeChromosome.EFFECT, Allele.leavesBrimstone);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.NUX)
				.addFruitFamily(EnumFruitFamily.PRUNES)
				.addFruitFamily(EnumFruitFamily.POMES).setRarity(0.005F);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Birch, Oak, 15);
		}
	},
	Walnut(TreeBranchDefinition.JUGLANS, "commonWalnut", "regia", true, EnumLeafType.DECIDUOUS, new Color(0x798c55), new Color(0xb0c648), EnumForestryWoodType.WALNUT) {
		@Override
		public Feature<NoFeatureConfig> getTreeFeature(ITreeGenData tree) {
			return new FeatureWalnut(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.NUX)
				.addFruitFamily(EnumFruitFamily.PRUNES)
				.addFruitFamily(EnumFruitFamily.POMES);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(TreeChromosomes.FRUITS, AlleleFruits.fruitWalnut);
			template.set(TreeChromosomes.FERTILITY, EnumAllele.Saplings.LOWER);
			template.set(TreeChromosomes.YIELD, EnumAllele.Yield.AVERAGE);
			template.set(TreeChromosomes.SAPPINESS, EnumAllele.Sappiness.LOWER);
			template.set(TreeChromosomes.HEIGHT, EnumAllele.Height.AVERAGE);
			template.set(TreeChromosomes.GIRTH, 2);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Lime, Cherry, 10);
		}

		@Override
		public boolean hasFruitLeaves() {
			return true;
		}
	},
	Chestnut(TreeBranchDefinition.CASTANEA, "sweetChestnut", "sativa", true, EnumLeafType.DECIDUOUS, new Color(0x5ea107), new Color(0xb0c648), EnumForestryWoodType.CHESTNUT) {
		@Override
		public Feature<NoFeatureConfig> getTreeFeature(ITreeGenData tree) {
			return new FeatureChestnut(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.NUX)
				.addFruitFamily(EnumFruitFamily.PRUNES)
				.addFruitFamily(EnumFruitFamily.POMES);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(TreeChromosomes.FRUITS, AlleleFruits.fruitChestnut);
			template.set(TreeChromosomes.YIELD, EnumAllele.Yield.AVERAGE);
			template.set(TreeChromosomes.SAPPINESS, EnumAllele.Sappiness.LOWER);
			template.set(TreeChromosomes.HEIGHT, EnumAllele.Height.LARGE);
			template.set(TreeChromosomes.GIRTH, 2);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Walnut, Lime, 10);
			registerMutation(Walnut, Cherry, 10);
		}

		@Override
		public boolean hasFruitLeaves() {
			return true;
		}
	},
	Cherry(TreeBranchDefinition.PRUNUS, "hillCherry", "serrulata", true, EnumLeafType.DECIDUOUS, new Color(0xe691da), new Color(0xe63e59), EnumForestryWoodType.CHERRY) {
		@Override
		public Feature<NoFeatureConfig> getTreeFeature(ITreeGenData tree) {
			return new FeatureCherry(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.PRUNES)
				.addFruitFamily(EnumFruitFamily.POMES).setRarity(0.0015F);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(TreeChromosomes.FRUITS, AlleleFruits.fruitCherry);
			template.set(TreeChromosomes.FERTILITY, EnumAllele.Saplings.LOW);
			template.set(TreeChromosomes.YIELD, EnumAllele.Yield.AVERAGE);
			template.set(TreeChromosomes.SAPPINESS, EnumAllele.Sappiness.LOW);
			template.set(TreeChromosomes.HEIGHT, EnumAllele.Height.SMALLER);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Lime, Oak, 10);
			registerMutation(Lime, Birch, 10);
		}

		@Override
		public boolean hasFruitLeaves() {
			return true;
		}
	},
	Lemon(TreeBranchDefinition.CITRUS, "lemon", "limon", true, EnumLeafType.DECIDUOUS, new Color(0x88af54), new Color(0xa3b850), EnumForestryWoodType.CITRUS) {
		@Override
		public Feature<NoFeatureConfig> getTreeFeature(ITreeGenData tree) {
			return new FeatureLemon(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.PRUNES)
				.addFruitFamily(EnumFruitFamily.POMES);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(TreeChromosomes.FRUITS, AlleleFruits.fruitLemon);
			template.set(TreeChromosomes.YIELD, EnumAllele.Yield.LOWER);
			template.set(TreeChromosomes.SAPPINESS, EnumAllele.Sappiness.AVERAGE);
			template.set(TreeChromosomes.HEIGHT, EnumAllele.Height.SMALLEST);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Lime, Cherry, 5);
		}

		@Override
		public boolean hasFruitLeaves() {
			return true;
		}
	},
	Plum(TreeBranchDefinition.PRUNUS, "plum", "domestica", true, EnumLeafType.DECIDUOUS, new Color(0x589246), new Color(0xa3b850), EnumForestryWoodType.PLUM) {
		@Override
		public Feature<NoFeatureConfig> getTreeFeature(ITreeGenData tree) {
			return new FeaturePlum(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.PRUNES)
				.addFruitFamily(EnumFruitFamily.POMES).setRarity(0.005F).setTemperature(EnumTemperature.WARM).setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(TreeChromosomes.FRUITS, AlleleFruits.fruitPlum);
			template.set(TreeChromosomes.YIELD, EnumAllele.Yield.HIGH);
			template.set(TreeChromosomes.SAPPINESS, EnumAllele.Sappiness.AVERAGE);
			template.set(TreeChromosomes.HEIGHT, EnumAllele.Height.SMALLEST);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Lemon, Cherry, 5);
		}

		@Override
		public boolean hasFruitLeaves() {
			return true;
		}
	},
	Maple(TreeBranchDefinition.ACER, "sugarMaple", "saccharum", true, EnumLeafType.MAPLE, new Color(0xd4f425), new Color(0x619a3c), EnumForestryWoodType.MAPLE) {
		@Override
		public Feature<NoFeatureConfig> getTreeFeature(ITreeGenData tree) {
			return new FeatureMaple(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.PRUNES)
				.addFruitFamily(EnumFruitFamily.POMES).setRarity(0.0025F);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(TreeChromosomes.FERTILITY, EnumAllele.Saplings.LOW);
			template.set(TreeChromosomes.SAPPINESS, EnumAllele.Sappiness.LOWER);
			template.set(TreeChromosomes.HEIGHT, EnumAllele.Height.AVERAGE);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Spruce, Larch, 5);
		}
	},
	Spruce(TreeBranchDefinition.PICEA, "redSpruce", "abies", false, EnumLeafType.CONIFERS, new Color(6396257), new Color(0x539d12), EnumVanillaWoodType.SPRUCE) {
		@Override
		public Feature<NoFeatureConfig> getTreeFeature(ITreeGenData tree) {
			return new FeatureSpruce(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {

		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(TreeChromosomes.FERTILITY, EnumAllele.Saplings.AVERAGE);
			template.set(TreeChromosomes.HEIGHT, EnumAllele.Height.AVERAGE);
			template.set(TreeChromosomes.MATURATION, EnumAllele.Maturation.FASTER);
		}

		@Override
		protected void registerMutations() {
			// vanilla
		}
	},
	Larch(TreeBranchDefinition.LARIX, "mundaneLarch", "decidua", true, EnumLeafType.CONIFERS, new Color(0x698f90), new Color(0x569896), EnumForestryWoodType.LARCH) {
		@Override
		public Feature<NoFeatureConfig> getTreeFeature(ITreeGenData tree) {
			return new FeatureLarch(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.setRarity(0.0025F).setTemperature(EnumTemperature.COLD);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(TreeChromosomes.FERTILITY, EnumAllele.Saplings.LOW);
			template.set(TreeChromosomes.SAPPINESS, EnumAllele.Sappiness.LOWER);
			template.set(TreeChromosomes.HEIGHT, EnumAllele.Height.AVERAGE);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Spruce, Birch, 10);
			registerMutation(Spruce, Oak, 10);
		}
	},
	Pine(TreeBranchDefinition.PINUS, "bullPine", "sabiniana", true, EnumLeafType.CONIFERS, new Color(0xfeff8f), new Color(0xffd98f), EnumForestryWoodType.PINE) {
		@Override
		public Feature<NoFeatureConfig> getTreeFeature(ITreeGenData tree) {
			return new FeaturePine(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.setRarity(0.0025F).setTemperature(EnumTemperature.COLD);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(TreeChromosomes.FERTILITY, EnumAllele.Saplings.LOW);
			template.set(TreeChromosomes.SAPPINESS, EnumAllele.Sappiness.LOWER);
			template.set(TreeChromosomes.HEIGHT, EnumAllele.Height.AVERAGE);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Spruce, Larch, 10);
		}
	},
	Sequoia(TreeBranchDefinition.SEQUOIA, "coastSequoia", "sempervirens", false, EnumLeafType.CONIFERS, new Color(0x418e71), new Color(0x569896), EnumForestryWoodType.SEQUOIA) {
		@Override
		public Feature<NoFeatureConfig> getTreeFeature(ITreeGenData tree) {
			return new FeatureSequoia(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {

		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(TreeChromosomes.HEIGHT, EnumAllele.Height.LARGEST);
			template.set(TreeChromosomes.SAPPINESS, EnumAllele.Sappiness.LOWER);
			template.set(TreeChromosomes.MATURATION, EnumAllele.Maturation.SLOWER);
			template.set(TreeChromosomes.GIRTH, 3);
			template.set(TreeChromosomes.FIREPROOF, EnumAllele.Fireproof.TRUE);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Larch, Pine, 5);
		}
	},
	Gigant(TreeBranchDefinition.SEQUOIADENDRON, "giantSequoia", "giganteum", false, EnumLeafType.CONIFERS, new Color(0x738434), new Color(0x738434).brighter(), EnumForestryWoodType.GIGANTEUM) {
		@Override
		public Feature<NoFeatureConfig> getTreeFeature(ITreeGenData tree) {
			return new FeatureGiganteum(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.setComplexity(10);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(TreeChromosomes.HEIGHT, EnumAllele.Height.GIGANTIC);
			template.set(TreeChromosomes.SAPPINESS, EnumAllele.Sappiness.LOWEST);
			template.set(TreeChromosomes.MATURATION, EnumAllele.Maturation.SLOWEST);
			template.set(TreeChromosomes.GIRTH, 4);
			template.set(TreeChromosomes.FIREPROOF, EnumAllele.Fireproof.TRUE);
		}

		@Override
		protected void registerMutations() {
			// only available by rare villager trade
		}
	},
	Jungle(TreeBranchDefinition.TROPICAL, "jungle", "tectona", false, EnumLeafType.JUNGLE, new Color(4764952), new Color(0x658917), EnumVanillaWoodType.JUNGLE) {
		@Override
		public Feature<NoFeatureConfig> getTreeFeature(ITreeGenData tree) {
			return new FeatureJungle(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(TreeChromosomes.FRUITS, AlleleFruits.fruitCocoa);
			template.set(TreeChromosomes.HEIGHT, EnumAllele.Height.LARGER);
			template.set(TreeChromosomes.MATURATION, EnumAllele.Maturation.FAST);
		}

		@Override
		protected void registerMutations() {
			// vanilla
		}
	},
	Teak(TreeBranchDefinition.TECTONA, "teak", "grandis", true, EnumLeafType.JUNGLE, new Color(0xfeff8f), new Color(0xffd98f), EnumForestryWoodType.TEAK) {
		@Override
		public Feature<NoFeatureConfig> getTreeFeature(ITreeGenData tree) {
			return new FeatureTeak(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE).setRarity(0.0025F).setTemperature(EnumTemperature.WARM).setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(TreeChromosomes.SAPPINESS, EnumAllele.Sappiness.LOWER);
		}

		@Override
		protected void registerMutations() {
			registerMutation(DarkOak, Jungle, 10);
		}
	},
	Ipe(TreeBranchDefinition.TABEBUIA, "ipe", "serratifolia", true, EnumLeafType.JUNGLE, new Color(0xfdd207), new Color(0xad8f04), EnumForestryWoodType.IPE) {
		@Override
		public Feature<NoFeatureConfig> getTreeFeature(ITreeGenData tree) {
			return new FeatureIpe(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(TreeChromosomes.SAPPINESS, EnumAllele.Sappiness.LOWER);
			template.set(TreeChromosomes.HEIGHT, EnumAllele.Height.LARGE);
			template.set(TreeChromosomes.GIRTH, 2);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Teak, DarkOak, 10);
		}
	},
	Kapok(TreeBranchDefinition.CEIBA, "kapok", "pentandra", true, EnumLeafType.JUNGLE, new Color(0x89987b), new Color(0x89aa9e), EnumForestryWoodType.KAPOK) {
		@Override
		public Feature<NoFeatureConfig> getTreeFeature(ITreeGenData tree) {
			return new FeatureKapok(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE)
				.addFruitFamily(EnumFruitFamily.PRUNES);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(TreeChromosomes.HEIGHT, EnumAllele.Height.LARGE);
			template.set(TreeChromosomes.SAPPINESS, EnumAllele.Sappiness.LOW);
			template.set(TreeChromosomes.MATURATION, EnumAllele.Maturation.SLOW);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Jungle, Teak, 10);
		}
	},
	Ebony(TreeBranchDefinition.EBONY, "myrtleEbony", "pentamera", true, EnumLeafType.JUNGLE, new Color(0xa2d24a), new Color(0xc4d24a), EnumForestryWoodType.EBONY) {
		@Override
		public Feature<NoFeatureConfig> getTreeFeature(ITreeGenData tree) {
			return new FeatureEbony(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE)
				.addFruitFamily(EnumFruitFamily.PRUNES).setRarity(0.0005F).setTemperature(EnumTemperature.WARM).setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(TreeChromosomes.HEIGHT, EnumAllele.Height.AVERAGE);
			template.set(TreeChromosomes.SAPPINESS, EnumAllele.Sappiness.LOW);
			template.set(TreeChromosomes.MATURATION, EnumAllele.Maturation.SLOWER);
			template.set(TreeChromosomes.GIRTH, 3);
		}

		@Override
		protected void registerMutations() {
			registerMutation(DarkOak, Kapok, 10);
		}
	},
	Zebrawood(TreeBranchDefinition.ASTRONIUM, "zebrawood", "graveolens", false, EnumLeafType.JUNGLE, new Color(0xa2d24a), new Color(0xc4d24a), EnumForestryWoodType.ZEBRAWOOD) {
		@Override
		public Feature<NoFeatureConfig> getTreeFeature(ITreeGenData tree) {
			return new FeatureZebrawood(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.NUX).setRarity(0.0005F).setTemperature(EnumTemperature.WARM).setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(TreeChromosomes.HEIGHT, EnumAllele.Height.LARGE);
			template.set(TreeChromosomes.GIRTH, 2);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Ebony, Poplar, 5);
		}
	},
	Mahogony(TreeBranchDefinition.MAHOGANY, "yellowMeranti", "gibbosa", true, EnumLeafType.JUNGLE, new Color(0x8ab154), new Color(0xa9b154), EnumForestryWoodType.MAHOGANY) {
		@Override
		public Feature<NoFeatureConfig> getTreeFeature(ITreeGenData tree) {
			return new FeatureMahogany(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE).setRarity(0.0005F).setTemperature(EnumTemperature.WARM).setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(TreeChromosomes.HEIGHT, EnumAllele.Height.LARGE);
			template.set(TreeChromosomes.SAPPINESS, EnumAllele.Sappiness.LOW);
			template.set(TreeChromosomes.MATURATION, EnumAllele.Maturation.SLOW);
			template.set(TreeChromosomes.GIRTH, 2);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Kapok, Ebony, 10);
		}
	},
	AcaciaVanilla(TreeBranchDefinition.ACACIA, "acacia", "aneura", true, EnumLeafType.DECIDUOUS, new Color(0x616101), new Color(0xb3b302), EnumVanillaWoodType.ACACIA) {
		@Override
		public Feature<NoFeatureConfig> getTreeFeature(ITreeGenData tree) {
			return new FeatureAcaciaVanilla(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE)
				.addFruitFamily(EnumFruitFamily.NUX);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {

		}

		@Override
		protected void registerMutations() {
			// vanilla
		}
	},
	Acacia(TreeBranchDefinition.ACACIA, "desertAcacia", "erioloba", true, EnumLeafType.DECIDUOUS, new Color(0x748C1C), new Color(0xb3b302), EnumForestryWoodType.ACACIA_FORESTRY) {
		@Override
		public Feature<NoFeatureConfig> getTreeFeature(ITreeGenData tree) {
			return new FeatureAcacia(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE)
				.addFruitFamily(EnumFruitFamily.NUX).setRarity(0.005F).setTemperature(EnumTemperature.WARM).setHumidity(EnumHumidity.ARID);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {

		}

		@Override
		protected void registerMutations() {
			registerMutation(Teak, Balsa, 10);
		}
	},
	Padauk(TreeBranchDefinition.PTEROCARPUS, "padauk", "soyauxii", true, EnumLeafType.DECIDUOUS, new Color(0xd0df8c), new Color(0x435c32), EnumForestryWoodType.PADAUK) {
		@Override
		public Feature<NoFeatureConfig> getTreeFeature(ITreeGenData tree) {
			return new FeaturePadauk(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE).setRarity(0.005F).setTemperature(EnumTemperature.WARM);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(TreeChromosomes.SAPPINESS, EnumAllele.Sappiness.LOWER);
			template.set(TreeChromosomes.HEIGHT, EnumAllele.Height.LARGE);
		}

		@Override
		protected void registerMutations() {
			registerMutation(AcaciaVanilla, Jungle, 10);
		}
	},
	Balsa(TreeBranchDefinition.OCHROMA, "balsa", "pyramidale", true, EnumLeafType.DECIDUOUS, new Color(0x59ac00), new Color(0xfeff8f), EnumForestryWoodType.BALSA) {
		@Override
		public Feature<NoFeatureConfig> getTreeFeature(ITreeGenData tree) {
			return new FeatureBalsa(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE)
				.addFruitFamily(EnumFruitFamily.NUX).setRarity(0.0005F).setHumidity(EnumHumidity.DAMP).setTemperature(EnumTemperature.WARM);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(TreeChromosomes.FERTILITY, EnumAllele.Saplings.HIGH);
			template.set(TreeChromosomes.SAPPINESS, EnumAllele.Sappiness.LOWER);
			template.set(TreeChromosomes.HEIGHT, EnumAllele.Height.LARGE);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Teak, AcaciaVanilla, 10);
		}
	},
	Cocobolo(TreeBranchDefinition.DALBERGIA, "cocobolo", "retusa", false, EnumLeafType.DECIDUOUS, new Color(0x6aa17a), new Color(0x487d4c), EnumForestryWoodType.COCOBOLO) {
		@Override
		public Feature<NoFeatureConfig> getTreeFeature(ITreeGenData tree) {
			return new FeatureCocobolo(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE).setRarity(0.0005F).setTemperature(EnumTemperature.WARM);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(TreeChromosomes.HEIGHT, EnumAllele.Height.LARGEST);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Acacia, DarkOak, 10);
		}
	},
	Wenge(TreeBranchDefinition.MILLETTIA, "wenge", "laurentii", true, EnumLeafType.DECIDUOUS, new Color(0xada157), new Color(0xad8a57), EnumForestryWoodType.WENGE) {
		@Override
		public Feature<NoFeatureConfig> getTreeFeature(ITreeGenData tree) {
			return new FeatureWenge(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE)
				.addFruitFamily(EnumFruitFamily.NUX).setRarity(0.0005F).setHumidity(EnumHumidity.DAMP).setTemperature(EnumTemperature.WARM);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(TreeChromosomes.FERTILITY, EnumAllele.Saplings.LOWEST);
			template.set(TreeChromosomes.GIRTH, 2);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Cocobolo, Balsa, 10);
		}
	},
	Baobab(TreeBranchDefinition.ADANSONIA, "grandidierBaobab", "digitata", true, EnumLeafType.DECIDUOUS, new Color(0xfeff8f), new Color(0xffd98f), EnumForestryWoodType.BAOBAB) {
		@Override
		public Feature<NoFeatureConfig> getTreeFeature(ITreeGenData tree) {
			return new FeatureBaobab(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE)
				.addFruitFamily(EnumFruitFamily.NUX).setRarity(0.005F).setTemperature(EnumTemperature.HOT).setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(TreeChromosomes.HEIGHT, EnumAllele.Height.LARGE);
			template.set(TreeChromosomes.SAPPINESS, EnumAllele.Sappiness.LOWER);
			template.set(TreeChromosomes.MATURATION, EnumAllele.Maturation.SLOW);
			template.set(TreeChromosomes.GIRTH, 3);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Balsa, Wenge, 10);
		}
	},
	Mahoe(TreeBranchDefinition.TALIPARITI, "blueMahoe", "elatum", true, EnumLeafType.DECIDUOUS, new Color(0xa0ba1b), new Color(0x79a175), EnumForestryWoodType.MAHOE) {
		@Override
		public Feature<NoFeatureConfig> getTreeFeature(ITreeGenData tree) {
			return new FeatureMahoe(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE)
				.addFruitFamily(EnumFruitFamily.POMES)
				.addFruitFamily(EnumFruitFamily.PRUNES).setRarity(0.000005F).setTemperature(EnumTemperature.WARM);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(TreeChromosomes.HEIGHT, EnumAllele.Height.SMALL);
			template.set(TreeChromosomes.SAPPINESS, EnumAllele.Sappiness.HIGH);
			template.set(TreeChromosomes.MATURATION, EnumAllele.Maturation.SLOWEST);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Balsa, Acacia, 5);
		}
	},
	Willow(TreeBranchDefinition.SALIX, "whiteWillow", "alba", true, EnumLeafType.WILLOW, new Color(0xa3b8a5), new Color(0xa3b850), EnumForestryWoodType.WILLOW) {
		@Override
		public Feature<NoFeatureConfig> getTreeFeature(ITreeGenData tree) {
			return new FeatureWillow(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.NUX)
				.addFruitFamily(EnumFruitFamily.PRUNES)
				.addFruitFamily(EnumFruitFamily.POMES)
				.setRarity(0.0025F).setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(TreeChromosomes.HEIGHT, EnumAllele.Height.AVERAGE);
			template.set(TreeChromosomes.SAPPINESS, EnumAllele.Sappiness.LOW);
			template.set(TreeChromosomes.MATURATION, EnumAllele.Maturation.FASTER);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Oak, Birch, 10)
				.restrictTemperature(EnumTemperature.WARM, EnumTemperature.HOT)
				.restrictHumidity(EnumHumidity.DAMP);
			registerMutation(Oak, Lime, 10)
				.restrictTemperature(EnumTemperature.WARM, EnumTemperature.HOT)
				.restrictHumidity(EnumHumidity.DAMP);
			registerMutation(Lime, Birch, 10)
				.restrictTemperature(EnumTemperature.WARM, EnumTemperature.HOT)
				.restrictHumidity(EnumHumidity.DAMP);
		}
	},
	Sipiri(TreeBranchDefinition.CHLOROCARDIUM, "sipiri", "rodiei", true, EnumLeafType.DECIDUOUS, new Color(0x678911), new Color(0x79a175), EnumForestryWoodType.GREENHEART) {
		@Override
		public Feature<NoFeatureConfig> getTreeFeature(ITreeGenData tree) {
			return new FeatureGreenheart(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE).setRarity(0.0025F).setTemperature(EnumTemperature.WARM)
				.setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(TreeChromosomes.HEIGHT, EnumAllele.Height.LARGE);
			template.set(TreeChromosomes.SAPPINESS, EnumAllele.Sappiness.LOW);
			template.set(TreeChromosomes.MATURATION, EnumAllele.Maturation.SLOW);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Kapok, Mahogony, 10)
				.restrictTemperature(EnumTemperature.WARM, EnumTemperature.HOT)
				.restrictHumidity(EnumHumidity.DAMP);
		}
	},
	Papaya(TreeBranchDefinition.CARICA, "papaya", "papaya", true, EnumLeafType.PALM, new Color(0x6d9f58), new Color(0x75E675), EnumForestryWoodType.PAPAYA) {
		@Override
		public Feature<NoFeatureConfig> getTreeFeature(ITreeGenData tree) {
			return new FeaturePapaya(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE)
				.addFruitFamily(EnumFruitFamily.NUX).setRarity(0.005F).setTemperature(EnumTemperature.WARM).setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(TreeChromosomes.FRUITS, AlleleFruits.fruitPapaya);
			template.set(TreeChromosomes.FERTILITY, EnumAllele.Saplings.LOW);
			template.set(TreeChromosomes.SAPPINESS, EnumAllele.Sappiness.LOWER);
			template.set(TreeChromosomes.HEIGHT, EnumAllele.Height.AVERAGE);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Jungle, Cherry, 5);
		}
	},
	Date(TreeBranchDefinition.PHOENIX, "datePalm", "dactylifera", true, EnumLeafType.PALM, new Color(0xcbcd79), new Color(0xB3F370), EnumForestryWoodType.PALM) {
		@Override
		public Feature<NoFeatureConfig> getTreeFeature(ITreeGenData tree) {
			return new FeatureDate(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE)
				.addFruitFamily(EnumFruitFamily.NUX).setRarity(0.005F).setTemperature(EnumTemperature.WARM).setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(TreeChromosomes.FRUITS, AlleleFruits.fruitDates);
			template.set(TreeChromosomes.FERTILITY, EnumAllele.Saplings.LOW);
			template.set(TreeChromosomes.YIELD, EnumAllele.Yield.LOW);
			template.set(TreeChromosomes.SAPPINESS, EnumAllele.Sappiness.LOW);
			template.set(TreeChromosomes.HEIGHT, EnumAllele.Height.AVERAGE);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Jungle, Papaya, 5);
		}
	},
	Poplar(TreeBranchDefinition.POPULUS, "whitePoplar", "alba", true, EnumLeafType.DECIDUOUS, new Color(0xa3b8a5), new Color(0x539d12), EnumForestryWoodType.POPLAR) {
		@Override
		public Feature<NoFeatureConfig> getTreeFeature(ITreeGenData tree) {
			return new FeaturePoplar(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.PRUNES)
				.addFruitFamily(EnumFruitFamily.POMES);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(TreeChromosomes.HEIGHT, EnumAllele.Height.SMALL);
			template.set(TreeChromosomes.SAPPINESS, EnumAllele.Sappiness.LOW);
			template.set(TreeChromosomes.MATURATION, EnumAllele.Maturation.SLOWER);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Birch, Willow, 5);
			registerMutation(Oak, Willow, 5);
			registerMutation(Lime, Willow, 5);
		}
	};

	public static final TreeDefinition[] VALUES = values();

	private final TreeBranchDefinition branch;
	private final IAlleleTreeSpecies species;

	private final IWoodType woodType;

	@Nullable
	private IAlleleTemplate template;
	@Nullable
	private IGenome genome;

	TreeDefinition(TreeBranchDefinition branch, String speciesName, String binomial, boolean dominant, EnumLeafType leafType, Color primary, Color secondary, IWoodType woodType) {
		String uid = "tree_" + getName();
		String unlocalizedDescription = "for.description.tree" + this;
		String unlocalizedName = "for.trees.species." + speciesName;

		this.branch = branch;

		ILeafSpriteProvider leafIconProvider = TreeManager.treeFactory.getLeafIconProvider(leafType, primary, secondary);
		IGermlingModelProvider germlingIconProvider = ModelProviderFactory.create(woodType, uid, leafIconProvider);

		ILeafProvider leafProvider = new LeafProvider();

		IAlleleTreeSpeciesBuilder speciesBuilder = TreeManager.treeFactory.createSpecies(uid, unlocalizedName, "Sengir", unlocalizedDescription, dominant, branch.getBranch(), binomial, Constants.MOD_ID, leafIconProvider, germlingIconProvider, this, leafProvider);
		setSpeciesProperties(speciesBuilder);
		this.species = speciesBuilder.build();
		this.woodType = woodType;
	}

	protected abstract void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies);

	protected abstract void setAlleles(IAlleleTemplateBuilder template);

	protected abstract void registerMutations();

	public boolean hasFruitLeaves() {
		return false;
	}

	@Override
	public boolean setLogBlock(IGenome genome, IWorld world, BlockPos pos, Direction facing) {
		boolean fireproof = genome.getActiveValue(TreeChromosomes.FIREPROOF);
		BlockState logBlock = TreeManager.woodAccess.getBlock(woodType, WoodBlockKind.LOG, fireproof);

		Direction.Axis axis = facing.getAxis();
		return world.setBlockState(pos, logBlock.with(LogBlock.AXIS, axis), 18);
	}

	@Override
	public boolean setLeaves(IGenome genome, IWorld world, @Nullable GameProfile owner, BlockPos pos, Random rand) {
		if (owner == null && new TemplateMatcher(genome).matches()) {
			IFruitProvider fruitProvider = genome.getActiveAllele(TreeChromosomes.FRUITS).getProvider();
			String speciesUid = genome.getPrimary().getRegistryName().toString();
			BlockState defaultLeaves;
			if (fruitProvider.isFruitLeaf(genome, world, pos) && rand.nextFloat() <= fruitProvider.getFruitChance(genome, world, pos)) {
				defaultLeaves = ModuleArboriculture.getBlocks().getDefaultLeavesFruit(speciesUid);
			} else {
				defaultLeaves = ModuleArboriculture.getBlocks().getDefaultLeaves(speciesUid);
			}
			return world.setBlockState(pos, defaultLeaves, 18);
		} else {
			BlockState leaves = ModuleArboriculture.getBlocks().leaves.getDefaultState();
			boolean placed = world.setBlockState(pos, leaves, 18);
			if (!placed) {
				return false;
			}

			TileLeaves tileLeaves = TileUtil.getTile(world, pos, TileLeaves.class);
			if (tileLeaves == null) {
				world.setBlockState(pos, Blocks.AIR.getDefaultState(), 18);
				return false;
			}

			if (owner != null) {
				tileLeaves.getOwnerHandler().setOwner(owner);
			}
			tileLeaves.setTree(new Tree(genome));

			RenderUtil.markForUpdate(pos);
			return true;
		}
	}

	public static void initTrees() {
		for (TreeDefinition tree : values()) {
			tree.registerMutations();
		}
	}

	@Override
	public void registerAlleles(IAlleleRegistry registry) {
		registry.registerAllele(species, TreeChromosomes.SPECIES);
	}

	@Override
	public <C extends IRootComponent<ITree>> void onComponentSetup(C component) {
		ComponentKey key = component.getKey();
		if (key == ComponentKeys.TEMPLATES) {
			ITemplateContainer registry = (ITemplateContainer) component;
			IAlleleTemplateBuilder templateBuilder = branch.getTemplateBuilder();
			templateBuilder.set(TreeChromosomes.SPECIES, species);
			setAlleles(templateBuilder);

			this.template = templateBuilder.build();
			this.genome = template.toGenome();
			registry.registerTemplate(this.template);
		}
	}

	protected final ITreeMutationBuilder registerMutation(TreeDefinition parent1, TreeDefinition parent2, int chance) {
		return TreeManager.treeMutationFactory.createMutation(parent1.species, parent2.species, getTemplate().alleles(), chance);
	}

	@Override
	public final IAlleleTemplate getTemplate() {
		return template;
	}

	@Override
	public final IGenome getGenome() {
		return genome;
	}

	@Override
	public IAlleleTreeSpecies getSpecies() {
		return species;
	}

	public final String getUID() {
		return species.getRegistryName().toString();
	}

	public final String getUnlocalizedName() {
		return species.getLocalisationKey();
	}

	@Override
	public final ITree createIndividual() {
		return new Tree(genome);
	}

	@Override
	public final ItemStack getMemberStack(EnumGermlingType treeType) {
		ITree tree = createIndividual();
		return TreeManager.treeRoot.getTypes().createStack(tree, treeType);
	}

	@Override
	public String getName() {
		return name().toLowerCase(Locale.ENGLISH);
	}

	public int getMetadata() {
		return ordinal();
	}

	public static TreeDefinition byMetadata(int meta) {
		if (meta < 0 || meta >= VALUES.length) {
			meta = 0;
		}
		return VALUES[meta];
	}
}

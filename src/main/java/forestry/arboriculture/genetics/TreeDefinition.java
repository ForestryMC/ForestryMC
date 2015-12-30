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

import java.awt.Color;
import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.api.arboriculture.EnumFruitFamily;
import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.EnumLeafType;
import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.EnumWoodType;
import forestry.api.arboriculture.IAlleleTreeSpeciesCustom;
import forestry.api.arboriculture.IGermlingIconProvider;
import forestry.api.arboriculture.ILeafIconProvider;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeGenerator;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.arboriculture.ITreeMutationCustom;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.IAllele;
import forestry.api.world.ITreeGenData;
import forestry.arboriculture.blocks.BlockForestryLeaves;
import forestry.arboriculture.genetics.alleles.AlleleFruit;
import forestry.arboriculture.genetics.alleles.AlleleGrowth;
import forestry.arboriculture.render.IconProviderGermling;
import forestry.arboriculture.render.IconProviderGermlingVanilla;
import forestry.arboriculture.tiles.TileLeaves;
import forestry.arboriculture.worldgen.BlockTypeLog;
import forestry.arboriculture.worldgen.WorldGenAcacia;
import forestry.arboriculture.worldgen.WorldGenAcaciaVanilla;
import forestry.arboriculture.worldgen.WorldGenBalsa;
import forestry.arboriculture.worldgen.WorldGenBaobab;
import forestry.arboriculture.worldgen.WorldGenBirch;
import forestry.arboriculture.worldgen.WorldGenCherry;
import forestry.arboriculture.worldgen.WorldGenChestnut;
import forestry.arboriculture.worldgen.WorldGenCocobolo;
import forestry.arboriculture.worldgen.WorldGenDarkOak;
import forestry.arboriculture.worldgen.WorldGenDate;
import forestry.arboriculture.worldgen.WorldGenEbony;
import forestry.arboriculture.worldgen.WorldGenGiganteum;
import forestry.arboriculture.worldgen.WorldGenGreenheart;
import forestry.arboriculture.worldgen.WorldGenIpe;
import forestry.arboriculture.worldgen.WorldGenJungle;
import forestry.arboriculture.worldgen.WorldGenKapok;
import forestry.arboriculture.worldgen.WorldGenLarch;
import forestry.arboriculture.worldgen.WorldGenLemon;
import forestry.arboriculture.worldgen.WorldGenMahoe;
import forestry.arboriculture.worldgen.WorldGenMahogany;
import forestry.arboriculture.worldgen.WorldGenMaple;
import forestry.arboriculture.worldgen.WorldGenOak;
import forestry.arboriculture.worldgen.WorldGenPadauk;
import forestry.arboriculture.worldgen.WorldGenPapaya;
import forestry.arboriculture.worldgen.WorldGenPine;
import forestry.arboriculture.worldgen.WorldGenPlum;
import forestry.arboriculture.worldgen.WorldGenPoplar;
import forestry.arboriculture.worldgen.WorldGenSequoia;
import forestry.arboriculture.worldgen.WorldGenSilverLime;
import forestry.arboriculture.worldgen.WorldGenSpruce;
import forestry.arboriculture.worldgen.WorldGenTeak;
import forestry.arboriculture.worldgen.WorldGenWalnut;
import forestry.arboriculture.worldgen.WorldGenWenge;
import forestry.arboriculture.worldgen.WorldGenWillow;
import forestry.arboriculture.worldgen.WorldGenZebrawood;
import forestry.core.config.Constants;
import forestry.core.genetics.alleles.AlleleBoolean;
import forestry.core.genetics.alleles.AlleleHelper;
import forestry.core.genetics.alleles.AllelePlantType;
import forestry.core.genetics.alleles.EnumAllele;
import forestry.plugins.PluginArboriculture;

public enum TreeDefinition implements ITreeDefinition, ITreeGenerator {
	Oak(TreeBranchDefinition.QUERCUS, "appleOak", "robur", false, EnumLeafType.DECIDUOUS, new Color(4764952), new Color(4764952).brighter(), 0, new ItemStack(Blocks.log, 1, 0)) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenOak(tree);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.FRUITS, AlleleFruit.fruitApple);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.AVERAGE);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.FASTER);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.POMES);
		}

		@Override
		protected void registerMutations() {
			// vanilla
		}
	},
	DarkOak(TreeBranchDefinition.QUERCUS, "darkOak", "velutina", false, EnumLeafType.DECIDUOUS, new Color(4764952), new Color(4764952).brighter(), 5, new ItemStack(Blocks.log2, 1, 1)) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenDarkOak(tree);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.AVERAGE);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.FASTER);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.GIRTH, 2);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.POMES);
		}

		@Override
		protected void registerMutations() {
			// vanilla
		}
	},
	Birch(TreeBranchDefinition.BETULA, "silverBirch", "pendula", false, EnumLeafType.DECIDUOUS, new Color(8431445), new Color(0xb0c648), 2, new ItemStack(Blocks.log, 1, 2)) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenBirch(tree);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.AVERAGE);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.FASTER);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {

		}

		@Override
		protected void registerMutations() {
			// vanilla
		}
	},
	Lime(TreeBranchDefinition.TILIA, "silverLime", "pendula", true, EnumLeafType.DECIDUOUS, new Color(0x5ea107), new Color(0x5ea107).brighter(), EnumWoodType.LIME) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenSilverLime(tree);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOW);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.YIELD, EnumAllele.Yield.LOWER);
			//Allele.helper.set(alleles, EnumTreeChromosome.EFFECT, Allele.leavesBrimstone);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.NUX)
					.addFruitFamily(EnumFruitFamily.PRUNES)
					.addFruitFamily(EnumFruitFamily.POMES);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Birch, Oak, 15);
		}
	},
	Walnut(TreeBranchDefinition.JUGLANS, "commonWalnut", "regia", true, EnumLeafType.DECIDUOUS, new Color(0x798c55), new Color(0xb0c648), EnumWoodType.WALNUT) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenWalnut(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.NUX)
					.addFruitFamily(EnumFruitFamily.PRUNES)
					.addFruitFamily(EnumFruitFamily.POMES);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.FRUITS, AlleleFruit.fruitWalnut);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOWER);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.YIELD, EnumAllele.Yield.AVERAGE);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.AVERAGE);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.GIRTH, 2);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Lime, Cherry, 10);
		}
	},
	Chestnut(TreeBranchDefinition.CASTANEA, "sweetChestnut", "sativa", true, EnumLeafType.DECIDUOUS, new Color(0x5ea107), new Color(0xb0c648), EnumWoodType.CHESTNUT) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenChestnut(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.NUX)
					.addFruitFamily(EnumFruitFamily.PRUNES)
					.addFruitFamily(EnumFruitFamily.POMES);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.FRUITS, AlleleFruit.fruitChestnut);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.YIELD, EnumAllele.Yield.AVERAGE);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGE);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.GIRTH, 2);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Walnut, Lime, 10);
			registerMutation(Walnut, Cherry, 10);
		}
	},
	Cherry(TreeBranchDefinition.PRUNUS, "hillCherry", "serrulata", true, EnumLeafType.DECIDUOUS, new Color(0xe691da), new Color(0xe63e59), EnumWoodType.CHERRY) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenCherry(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.PRUNES)
					.addFruitFamily(EnumFruitFamily.POMES);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.FRUITS, AlleleFruit.fruitCherry);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOW);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.YIELD, EnumAllele.Yield.AVERAGE);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOW);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.SMALLER);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Lime, Oak, 10);
			registerMutation(Lime, Birch, 10);
		}
	},
	Lemon(TreeBranchDefinition.CITRUS, "lemon", "limon", true, EnumLeafType.DECIDUOUS, new Color(0x88af54), new Color(0xa3b850), EnumWoodType.CITRUS) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenLemon(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.PRUNES)
					.addFruitFamily(EnumFruitFamily.POMES);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.FRUITS, AlleleFruit.fruitLemon);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.YIELD, EnumAllele.Yield.LOWER);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.AVERAGE);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.SMALLEST);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Lime, Cherry, 5);
		}
	},
	Plum(TreeBranchDefinition.PRUNUS, "plum", "domestica", true, EnumLeafType.DECIDUOUS, new Color(0x589246), new Color(0xa3b850), EnumWoodType.PLUM) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenPlum(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.PRUNES)
					.addFruitFamily(EnumFruitFamily.POMES);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.FRUITS, AlleleFruit.fruitPlum);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.YIELD, EnumAllele.Yield.HIGH);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.AVERAGE);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.SMALLEST);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Lemon, Cherry, 5);
		}
	},
	Maple(TreeBranchDefinition.ACER, "sugarMaple", "saccharum", true, EnumLeafType.MAPLE, new Color(0xd4f425), new Color(0x619a3c), EnumWoodType.MAPLE) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenMaple(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.PRUNES)
					.addFruitFamily(EnumFruitFamily.POMES);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOW);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.AVERAGE);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Spruce, Larch, 5);
		}
	},
	Spruce(TreeBranchDefinition.PICEA, "redSpruce", "abies", false, EnumLeafType.CONIFERS, new Color(6396257), new Color(0x539d12), 1, new ItemStack(Blocks.log, 1, 1)) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenSpruce(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {

		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.AVERAGE);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.AVERAGE);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.FASTER);
		}

		@Override
		protected void registerMutations() {
			// vanilla
		}
	},
	Larch(TreeBranchDefinition.LARIX, "mundaneLarch", "decidua", true, EnumLeafType.CONIFERS, new Color(0x698f90), new Color(0x569896), EnumWoodType.LARCH) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenLarch(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {

		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOW);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.AVERAGE);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Spruce, Birch, 10);
			registerMutation(Spruce, Oak, 10);
		}
	},
	Pine(TreeBranchDefinition.PINUS, "bullPine", "sabiniana", true, EnumLeafType.CONIFERS, new Color(0xfeff8f), new Color(0xffd98f), EnumWoodType.PINE) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenPine(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {

		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOW);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.AVERAGE);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Spruce, Larch, 10);
		}
	},
	Sequioa(TreeBranchDefinition.SEQUOIA, "coastSequoia", "sempervirens", false, EnumLeafType.CONIFERS, new Color(0x418e71), new Color(0x569896), EnumWoodType.SEQUOIA) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenSequoia(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {

		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGEST);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.SLOWER);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.GIRTH, 3);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.FIREPROOF, EnumAllele.Fireproof.TRUE);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Larch, Pine, 5);
		}
	},
	Gigant(TreeBranchDefinition.SEQUOIADENDRON, "giantSequoia", "giganteum", false, EnumLeafType.CONIFERS, new Color(0x738434), new Color(0x738434).brighter(), EnumWoodType.GIGANTEUM) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenGiganteum(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {

		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.GIGANTIC);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWEST);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.SLOWEST);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.GIRTH, 4);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.FIREPROOF, EnumAllele.Fireproof.TRUE);
		}

		@Override
		protected void registerMutations() {
			// only available by rare villager trade
		}
	},
	Jungle(TreeBranchDefinition.TROPICAL, "jungle", "tectona", false, EnumLeafType.JUNGLE, new Color(4764952), new Color(0x658917), 3, new ItemStack(Blocks.log, 1, 3)) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenJungle(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.FRUITS, AlleleFruit.fruitCocoa);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGER);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.FAST);
		}

		@Override
		protected void registerMutations() {
			// vanilla
		}
	},
	Teak(TreeBranchDefinition.TECTONA, "teak", "grandis", true, EnumLeafType.JUNGLE, new Color(0xfeff8f), new Color(0xffd98f), EnumWoodType.TEAK) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenTeak(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
		}

		@Override
		protected void registerMutations() {
			registerMutation(DarkOak, Jungle, 10);
		}
	},
	Ipe(TreeBranchDefinition.TABEBUIA, "ipe", "serratifolia", true, EnumLeafType.JUNGLE, new Color(0xfdd207), new Color(0xad8f04), EnumWoodType.IPE) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenIpe(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGE);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.GIRTH, 2);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Teak, DarkOak, 10);
		}
	},
	Kapok(TreeBranchDefinition.CEIBA, "kapok", "pentandra", true, EnumLeafType.JUNGLE, new Color(0x89987b), new Color(0x89aa9e), EnumWoodType.KAPOK) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenKapok(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE)
					.addFruitFamily(EnumFruitFamily.PRUNES);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGE);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOW);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.SLOW);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Jungle, Teak, 10);
		}
	},
	Ebony(TreeBranchDefinition.EBONY, "myrtleEbony", "pentamera", true, EnumLeafType.JUNGLE, new Color(0xa2d24a), new Color(0xc4d24a), EnumWoodType.EBONY) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenEbony(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE)
					.addFruitFamily(EnumFruitFamily.PRUNES);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.AVERAGE);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOW);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.SLOWER);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.GIRTH, 3);
		}

		@Override
		protected void registerMutations() {
			registerMutation(DarkOak, Kapok, 10);
		}
	},
	Zebrawood(TreeBranchDefinition.ASTRONIUM, "zebrawood", "graveolens", false, EnumLeafType.JUNGLE, new Color(0xa2d24a), new Color(0xc4d24a), EnumWoodType.ZEBRAWOOD) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenZebrawood(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.NUX);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGE);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.GIRTH, 2);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Ebony, Poplar, 5);
		}
	},
	Mahogony(TreeBranchDefinition.MAHOGANY, "yellowMeranti", "gibbosa", true, EnumLeafType.JUNGLE, new Color(0x8ab154), new Color(0xa9b154), EnumWoodType.MAHOGANY) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenMahogany(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGE);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOW);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.SLOW);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.GIRTH, 2);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Kapok, Ebony, 10);
		}
	},
	AcaciaVanilla(TreeBranchDefinition.ACACIA, "acacia", "aneura", true, EnumLeafType.DECIDUOUS, new Color(0x616101), new Color(0xb3b302), 4, new ItemStack(Blocks.log2, 1, 0)) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenAcaciaVanilla(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE)
					.addFruitFamily(EnumFruitFamily.NUX);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {

		}

		@Override
		protected void registerMutations() {
			// vanilla
		}
	},
	Acacia(TreeBranchDefinition.ACACIA, "desertAcacia", "erioloba", true, EnumLeafType.DECIDUOUS, new Color(0x748C1C), new Color(0xb3b302), EnumWoodType.ACACIA) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenAcacia(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE)
					.addFruitFamily(EnumFruitFamily.NUX);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.PLANT, AllelePlantType.plantTypeDesert);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Teak, Balsa, 10);
		}
	},
	Padauk(TreeBranchDefinition.PTEROCARPUS, "padauk", "soyauxii", true, EnumLeafType.DECIDUOUS, new Color(0xd0df8c), new Color(0x435c32), EnumWoodType.PADAUK) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenPadauk(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGE);
		}

		@Override
		protected void registerMutations() {
			registerMutation(AcaciaVanilla, Jungle, 10);
		}
	},
	Balsa(TreeBranchDefinition.OCHROMA, "balsa", "pyramidale", true, EnumLeafType.DECIDUOUS, new Color(0x59ac00), new Color(0xfeff8f), EnumWoodType.BALSA) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenBalsa(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE)
					.addFruitFamily(EnumFruitFamily.NUX);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.HIGH);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGE);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Teak, AcaciaVanilla, 10);
		}
	},
	Cocobolo(TreeBranchDefinition.DALBERGIA, "cocobolo", "retusa", false, EnumLeafType.DECIDUOUS, new Color(0x6aa17a), new Color(0x487d4c), EnumWoodType.COCOBOLO) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenCocobolo(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGEST);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Acacia, DarkOak, 10);
		}
	},
	Wenge(TreeBranchDefinition.MILLETTIA, "wenge", "laurentii", true, EnumLeafType.DECIDUOUS, new Color(0xada157), new Color(0xad8a57), EnumWoodType.WENGE) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenWenge(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE)
					.addFruitFamily(EnumFruitFamily.NUX);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOWEST);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.GIRTH, 2);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Cocobolo, Balsa, 10);
		}
	},
	Baobab(TreeBranchDefinition.ADANSONIA, "grandidierBaobab", "digitata", true, EnumLeafType.DECIDUOUS, new Color(0xfeff8f), new Color(0xffd98f), EnumWoodType.BAOBAB) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenBaobab(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE)
					.addFruitFamily(EnumFruitFamily.NUX);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGE);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.PLANT, AllelePlantType.plantTypeDesert);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.SLOW);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.GIRTH, 3);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Balsa, Wenge, 10);
		}
	},
	Mahoe(TreeBranchDefinition.TALIPARITI, "blueMahoe", "elatum", true, EnumLeafType.DECIDUOUS, new Color(0xa0ba1b), new Color(0x79a175), EnumWoodType.MAHOE) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenMahoe(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE)
					.addFruitFamily(EnumFruitFamily.POMES)
					.addFruitFamily(EnumFruitFamily.PRUNES);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.SMALL);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.HIGH);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.SLOWEST);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Balsa, Acacia, 5);
		}
	},
	Willow(TreeBranchDefinition.SALIX, "whiteWillow", "alba", true, EnumLeafType.WILLOW, new Color(0xa3b8a5), new Color(0xa3b850), EnumWoodType.WILLOW) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenWillow(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.NUX)
					.addFruitFamily(EnumFruitFamily.PRUNES)
					.addFruitFamily(EnumFruitFamily.POMES);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.AVERAGE);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOW);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.FASTER);
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
	Sipiri(TreeBranchDefinition.CHLOROCARDIUM, "sipiri", "rodiei", true, EnumLeafType.DECIDUOUS, new Color(0x678911), new Color(0x79a175), EnumWoodType.GREENHEART) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenGreenheart(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.GROWTH, AlleleGrowth.growthTropical);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGE);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOW);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.SLOW);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Kapok, Mahogony, 10)
					.restrictTemperature(EnumTemperature.WARM, EnumTemperature.HOT)
					.restrictHumidity(EnumHumidity.DAMP);
		}
	},
	Papaya(TreeBranchDefinition.CARICA, "papaya", "papaya", true, EnumLeafType.PALM, new Color(0x6d9f58), new Color(0x75E675), EnumWoodType.PAPAYA) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenPapaya(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE)
					.addFruitFamily(EnumFruitFamily.NUX);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.FRUITS, AlleleFruit.fruitPapaya);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOW);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.AVERAGE);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Jungle, Cherry, 5);
		}
	},
	Date(TreeBranchDefinition.PHOENIX, "datePalm", "dactylifera", true, EnumLeafType.PALM, new Color(0xcbcd79), new Color(0xB3F370), EnumWoodType.PALM) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenDate(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE)
					.addFruitFamily(EnumFruitFamily.NUX);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.FRUITS, AlleleFruit.fruitDates);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOW);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.YIELD, EnumAllele.Yield.LOW);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOW);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.AVERAGE);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Jungle, Papaya, 5);
		}
	},
	Poplar(TreeBranchDefinition.POPULUS, "whitePoplar", "alba", true, EnumLeafType.DECIDUOUS, new Color(0xa3b8a5), new Color(0x539d12), EnumWoodType.POPLAR) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenPoplar(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.PRUNES)
					.addFruitFamily(EnumFruitFamily.POMES);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.SMALL);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOW);
			AlleleHelper.instance.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.SLOWER);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Birch, Willow, 5);
			registerMutation(Oak, Willow, 5);
			registerMutation(Lime, Willow, 5);
		}
	};

	private final TreeBranchDefinition branch;
	private final IAlleleTreeSpeciesCustom species;

	private final EnumWoodType woodType; // for forestry trees
	private final ItemStack vanillaWood; // for vanilla trees

	private IAllele[] template;
	private ITreeGenome genome;

	// vanilla tree constructor
	TreeDefinition(TreeBranchDefinition branch, String speciesName, String binomial, boolean dominant, EnumLeafType leafType, Color primary, Color secondary, int vanillaMeta, ItemStack vanillaWood) {
		String uid = "forestry.tree" + this;
		String unlocalizedDescription = "for.description.tree" + this;
		String unlocalizedName = "for.trees.species." + speciesName;

		this.branch = branch;

		ILeafIconProvider leafIconProvider = TreeManager.treeFactory.getLeafIconProvider(leafType, primary, secondary);
		IGermlingIconProvider germlingIconProvider = new IconProviderGermlingVanilla(vanillaMeta);

		this.species = TreeManager.treeFactory.createSpecies(uid, unlocalizedName, "Sengir", unlocalizedDescription, dominant, branch.getBranch(), binomial, leafIconProvider, germlingIconProvider, this);
		this.woodType = null;
		this.vanillaWood = vanillaWood;
	}

	// forestry tree constructor
	TreeDefinition(TreeBranchDefinition branch, String speciesName, String binomial, boolean dominant, EnumLeafType leafType, Color primary, Color secondary, EnumWoodType woodType) {
		String uid = "forestry.tree" + this;
		String unlocalizedDescription = "for.description.tree" + this;
		String unlocalizedName = "for.trees.species." + speciesName;

		this.branch = branch;

		ILeafIconProvider leafIconProvider = TreeManager.treeFactory.getLeafIconProvider(leafType, primary, secondary);
		IGermlingIconProvider germlingIconProvider = new IconProviderGermling(uid);

		this.species = TreeManager.treeFactory.createSpecies(uid, unlocalizedName, "Sengir", unlocalizedDescription, dominant, branch.getBranch(), binomial, leafIconProvider, germlingIconProvider, this);
		this.woodType = woodType;
		this.vanillaWood = null;
	}

	protected abstract void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies);

	protected abstract void setAlleles(IAllele[] alleles);

	protected abstract void registerMutations();

	@Override
	public void setLogBlock(ITreeGenome genome, World world, int x, int y, int z, ForgeDirection facing) {
		if (woodType == null) {
			Block vanillaWoodBlock = Block.getBlockFromItem(vanillaWood.getItem());
			int vanillaWoodMeta = vanillaWood.getItemDamage();
			switch (facing) {
				case NORTH:
				case SOUTH:
					vanillaWoodMeta |= 2 << 2;
					break;
				case EAST:
				case WEST:
					vanillaWoodMeta |= 1 << 2;
					break;
			}

			world.setBlock(x, y, z, vanillaWoodBlock, vanillaWoodMeta, Constants.FLAG_BLOCK_SYNCH);
		} else {
			AlleleBoolean fireproofAllele = (AlleleBoolean) genome.getActiveAllele(EnumTreeChromosome.FIREPROOF);
			boolean fireproof = fireproofAllele.getValue();
			ItemStack log = TreeManager.woodItemAccess.getLog(woodType, fireproof);

			BlockTypeLog logBlock = new BlockTypeLog(log);
			logBlock.setDirection(facing);
			logBlock.setBlock(world, x, y, z);
		}
	}

	@Override
	public void setLogBlock(World world, int x, int y, int z, ForgeDirection facing) {
		setLogBlock(genome, world, x, y, z, facing);
	}

	@Override
	public void setLeaves(ITreeGenome genome, World world, GameProfile owner, int x, int y, int z, boolean decorative) {
		boolean placed = world.setBlock(x, y, z, PluginArboriculture.blocks.leaves, 0, Constants.FLAG_BLOCK_SYNCH_AND_UPDATE);
		if (!placed) {
			return;
		}

		Block block = world.getBlock(x, y, z);
		if (PluginArboriculture.blocks.leaves != block) {
			world.setBlockToAir(x, y, z);
			return;
		}

		TileLeaves tileLeaves = BlockForestryLeaves.getLeafTile(world, x, y, z);
		if (tileLeaves == null) {
			world.setBlockToAir(x, y, z);
			return;
		}

		tileLeaves.setOwner(owner);
		if (decorative) {
			tileLeaves.setDecorative();
		}
		tileLeaves.setTree(new Tree(genome));

		world.markBlockForUpdate(x, y, z);
	}

	@Override
	public void setLeaves(World world, GameProfile owner, int x, int y, int z, boolean decorative) {
		setLeaves(genome, world, owner, x, y, z, decorative);
	}

	public static void initTrees() {
		for (TreeDefinition tree : values()) {
			tree.init();
		}
		for (TreeDefinition tree : values()) {
			tree.registerMutations();
		}
	}

	private void init() {
		setSpeciesProperties(species);

		template = branch.getTemplate();
		AlleleHelper.instance.set(template, EnumTreeChromosome.SPECIES, species);
		setAlleles(template);

		genome = TreeManager.treeRoot.templateAsGenome(template);

		TreeManager.treeRoot.registerTemplate(template);
	}

	protected final ITreeMutationCustom registerMutation(TreeDefinition parent1, TreeDefinition parent2, int chance) {
		return TreeManager.treeMutationFactory.createMutation(parent1.species, parent2.species, getTemplate(), chance);
	}

	@Override
	public final IAllele[] getTemplate() {
		return Arrays.copyOf(template, template.length);
	}

	public final String getUID() {
		return species.getUID();
	}

	@Override
	public final ITreeGenome getGenome() {
		return genome;
	}

	@Override
	public final ITree getIndividual() {
		return new Tree(genome);
	}

	@Override
	public final ItemStack getMemberStack(EnumGermlingType treeType) {
		ITree tree = getIndividual();
		return TreeManager.treeRoot.getMemberStack(tree, treeType.ordinal());
	}
}

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
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import com.mojang.authlib.GameProfile;

import forestry.api.arboriculture.EnumFruitFamily;
import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.EnumLeafType;
import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleTreeSpeciesCustom;
import forestry.api.arboriculture.IGermlingIconProvider;
import forestry.api.arboriculture.IGermlingModelProvider;
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
import forestry.arboriculture.WoodType;
import forestry.arboriculture.gadgets.ForestryBlockLeaves;
import forestry.arboriculture.gadgets.TileLeaves;
import forestry.arboriculture.render.GermlingIconProvider;
import forestry.arboriculture.render.GermlingModelProvider;
import forestry.arboriculture.render.GermlingVanillaModelProvider;
import forestry.arboriculture.render.LeafIconProvider;
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
import forestry.arboriculture.worldgen.WorldGenLime;
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
import forestry.arboriculture.worldgen.WorldGenSpruce;
import forestry.arboriculture.worldgen.WorldGenTeak;
import forestry.arboriculture.worldgen.WorldGenWalnut;
import forestry.arboriculture.worldgen.WorldGenWenge;
import forestry.arboriculture.worldgen.WorldGenWillow;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.genetics.alleles.Allele;
import forestry.core.genetics.alleles.AlleleBoolean;
import forestry.core.genetics.alleles.EnumAllele;

public enum TreeDefinition implements ITreeDefinition,ITreeGenerator {
	Oak(TreeBranchDefinition.QUERCUS, "appleOak", "robur", false, EnumLeafType.DECIDUOUS, new Color(4764952),
			new Color(4764952).brighter(), 0, new ItemStack(Blocks.log, 1, 0)) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenOak(tree);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			Allele.helper.set(alleles, EnumTreeChromosome.FRUITS, Allele.fruitApple);
			Allele.helper.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.AVERAGE);
			Allele.helper.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.FASTER);
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
	DarkOak(TreeBranchDefinition.QUERCUS, "darkOak", "velutina", false, EnumLeafType.DECIDUOUS, new Color(4764952),
			new Color(4764952).brighter(), 5, new ItemStack(Blocks.log2, 1, 1)) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenDarkOak(tree);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			Allele.helper.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.AVERAGE);
			Allele.helper.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.FASTER);
			Allele.helper.set(alleles, EnumTreeChromosome.GIRTH, 2);
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
	Birch(TreeBranchDefinition.BETULA, "silverBirch", "pendula", false, EnumLeafType.DECIDUOUS, new Color(8431445),
			new Color(0xb0c648), 2, new ItemStack(Blocks.log, 1, 2)) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenBirch(tree);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			Allele.helper.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.AVERAGE);
			Allele.helper.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.FASTER);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {

		}

		@Override
		protected void registerMutations() {
			// vanilla
		}
	},
	Lime(TreeBranchDefinition.TILIA, "silverLime", "pendula", true, EnumLeafType.DECIDUOUS, new Color(0x5ea107),
			new Color(0x5ea107).brighter(), WoodType.LIME) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenLime(tree);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			Allele.helper.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOW);
			Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
			Allele.helper.set(alleles, EnumTreeChromosome.YIELD, EnumAllele.Yield.LOWER);
			// Allele.helper.set(alleles, EnumTreeChromosome.EFFECT,
			// Allele.leavesBrimstone);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.NUX).addFruitFamily(EnumFruitFamily.PRUNES)
					.addFruitFamily(EnumFruitFamily.POMES);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Birch, Oak, 15);
		}
	},
	Walnut(TreeBranchDefinition.JUGLANS, "commonWalnut", "regia", true, EnumLeafType.DECIDUOUS, new Color(0x798c55),
			new Color(0xb0c648), WoodType.WALNUT) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenWalnut(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.NUX).addFruitFamily(EnumFruitFamily.PRUNES)
					.addFruitFamily(EnumFruitFamily.POMES);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			Allele.helper.set(alleles, EnumTreeChromosome.FRUITS, Allele.fruitWalnut);
			Allele.helper.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOWER);
			Allele.helper.set(alleles, EnumTreeChromosome.YIELD, EnumAllele.Yield.AVERAGE);
			Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
			Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.AVERAGE);
			Allele.helper.set(alleles, EnumTreeChromosome.GIRTH, 2);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Lime, Cherry, 10);
		}
	},
	Chestnut(TreeBranchDefinition.CASTANEA, "sweetChestnut", "sativa", true, EnumLeafType.DECIDUOUS,
			new Color(0x5ea107), new Color(0xb0c648), WoodType.CHESTNUT) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenChestnut(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.NUX).addFruitFamily(EnumFruitFamily.PRUNES)
					.addFruitFamily(EnumFruitFamily.POMES);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			Allele.helper.set(alleles, EnumTreeChromosome.FRUITS, Allele.fruitChestnut);
			Allele.helper.set(alleles, EnumTreeChromosome.YIELD, EnumAllele.Yield.AVERAGE);
			Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
			Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGE);
			Allele.helper.set(alleles, EnumTreeChromosome.GIRTH, 2);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Walnut, Lime, 10);
			registerMutation(Walnut, Cherry, 10);
		}
	},
	Cherry(TreeBranchDefinition.PRUNUS, "hillCherry", "serrulata", true, EnumLeafType.DECIDUOUS, new Color(0xe691da),
			new Color(0xe63e59), WoodType.CHERRY) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenCherry(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.PRUNES).addFruitFamily(EnumFruitFamily.POMES);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			Allele.helper.set(alleles, EnumTreeChromosome.FRUITS, Allele.fruitCherry);
			Allele.helper.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOW);
			Allele.helper.set(alleles, EnumTreeChromosome.YIELD, EnumAllele.Yield.AVERAGE);
			Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOW);
			Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.SMALLER);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Lime, Oak, 10);
			registerMutation(Lime, Birch, 10);
		}
	},
	Lemon(TreeBranchDefinition.CITRUS, "lemon", "limon", true, EnumLeafType.DECIDUOUS, new Color(0x88af54),
			new Color(0xa3b850), WoodType.CITRUS) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenLemon(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.PRUNES).addFruitFamily(EnumFruitFamily.POMES);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			Allele.helper.set(alleles, EnumTreeChromosome.FRUITS, Allele.fruitLemon);
			Allele.helper.set(alleles, EnumTreeChromosome.YIELD, EnumAllele.Yield.LOWER);
			Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.AVERAGE);
			Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.SMALLEST);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Lime, Cherry, 5);
		}
	},
	Plum(TreeBranchDefinition.PRUNUS, "plum", "domestica", true, EnumLeafType.DECIDUOUS, new Color(0x589246),
			new Color(0xa3b850), WoodType.PLUM) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenPlum(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.PRUNES).addFruitFamily(EnumFruitFamily.POMES);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			Allele.helper.set(alleles, EnumTreeChromosome.FRUITS, Allele.fruitPlum);
			Allele.helper.set(alleles, EnumTreeChromosome.YIELD, EnumAllele.Yield.HIGH);
			Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.AVERAGE);
			Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.SMALLEST);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Lemon, Cherry, 5);
		}
	},
	Maple(TreeBranchDefinition.ACER, "sugarMaple", "saccharum", true, EnumLeafType.MAPLE, new Color(0xd4f425),
			new Color(0x619a3c), WoodType.MAPLE) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenMaple(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.PRUNES).addFruitFamily(EnumFruitFamily.POMES);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			Allele.helper.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOW);
			Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
			Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.AVERAGE);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Spruce, Larch, 5);
		}
	},
	Spruce(TreeBranchDefinition.PICEA, "redSpruce", "abies", false, EnumLeafType.CONIFERS, new Color(6396257),
			new Color(0x539d12), 1, new ItemStack(Blocks.log, 1, 1)) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenSpruce(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {

		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			Allele.helper.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.AVERAGE);
			Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.AVERAGE);
			Allele.helper.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.FASTER);
		}

		@Override
		protected void registerMutations() {
			// vanilla
		}
	},
	Larch(TreeBranchDefinition.LARIX, "mundaneLarch", "decidua", true, EnumLeafType.CONIFERS, new Color(0x698f90),
			new Color(0x569896), WoodType.LARCH) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenLarch(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {

		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			Allele.helper.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOW);
			Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
			Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.AVERAGE);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Spruce, Birch, 10);
			registerMutation(Spruce, Oak, 10);
		}
	},
	Pine(TreeBranchDefinition.PINUS, "bullPine", "sabiniana", true, EnumLeafType.CONIFERS, new Color(0xfeff8f),
			new Color(0xffd98f), WoodType.PINE) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenPine(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {

		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			Allele.helper.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOW);
			Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
			Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.AVERAGE);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Spruce, Larch, 10);
		}
	},
	Sequioa(TreeBranchDefinition.SEQUOIA, "coastSequoia", "sempervirens", false, EnumLeafType.CONIFERS,
			new Color(0x418e71), new Color(0x569896), WoodType.SEQUOIA) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenSequoia(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {

		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGEST);
			Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
			Allele.helper.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.SLOWER);
			Allele.helper.set(alleles, EnumTreeChromosome.GIRTH, 3);
			Allele.helper.set(alleles, EnumTreeChromosome.FIREPROOF, EnumAllele.Fireproof.TRUE);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Larch, Pine, 5);
		}
	},
	Gigant(TreeBranchDefinition.SEQUOIADENDRON, "giantSequoia", "giganteum", false, EnumLeafType.CONIFERS,
			new Color(0x738434), new Color(0x738434).brighter(), WoodType.GIGANTEUM) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenGiganteum(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {

		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.GIGANTIC);
			Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWEST);
			Allele.helper.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.SLOWEST);
			Allele.helper.set(alleles, EnumTreeChromosome.GIRTH, 4);
			Allele.helper.set(alleles, EnumTreeChromosome.FIREPROOF, EnumAllele.Fireproof.TRUE);
		}

		@Override
		protected void registerMutations() {
			// only available by rare villager trade
		}
	},
	Jungle(TreeBranchDefinition.TROPICAL, "jungle", "tectona", false, EnumLeafType.JUNGLE, new Color(4764952),
			new Color(0x539d12), 3, new ItemStack(Blocks.log, 1, 3)) {
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
			Allele.helper.set(alleles, EnumTreeChromosome.FRUITS, Allele.fruitCocoa);
			Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGER);
			Allele.helper.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.FAST);
		}

		@Override
		protected void registerMutations() {
			// vanilla
		}
	},
	Teak(TreeBranchDefinition.TECTONA, "teak", "grandis", true, EnumLeafType.JUNGLE, new Color(0xfeff8f),
			new Color(0xffd98f), WoodType.TEAK) {
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
			Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
		}

		@Override
		protected void registerMutations() {
			registerMutation(DarkOak, Jungle, 10);
		}
	},
	Ipe(TreeBranchDefinition.TABEBUIA, "ipe", "serratifolia", true, EnumLeafType.JUNGLE, new Color(0xfdd207),
			new Color(0xad8f04), WoodType.IPE) {
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
			Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
			Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGE);
			Allele.helper.set(alleles, EnumTreeChromosome.GIRTH, 2);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Teak, DarkOak, 10);
		}
	},
	Kapok(TreeBranchDefinition.CEIBA, "kapok", "pentandra", true, EnumLeafType.JUNGLE, new Color(0x89987b),
			new Color(0x89aa9e), WoodType.KAPOK) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenKapok(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE).addFruitFamily(EnumFruitFamily.PRUNES);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGE);
			Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOW);
			Allele.helper.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.SLOW);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Jungle, Teak, 10);
		}
	},
	Ebony(TreeBranchDefinition.EBONY, "myrtleEbony", "pentamera", true, EnumLeafType.JUNGLE, new Color(0xa2d24a),
			new Color(0xc4d24a), WoodType.EBONY) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenEbony(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE).addFruitFamily(EnumFruitFamily.PRUNES);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.AVERAGE);
			Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOW);
			Allele.helper.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.SLOWER);
			Allele.helper.set(alleles, EnumTreeChromosome.GIRTH, 3);
		}

		@Override
		protected void registerMutations() {
			registerMutation(DarkOak, Kapok, 10);
		}
	},
	/*
	 * Zebrawood(TreeBranchDefinition.ASTRONIUM, "zebrawood", "graveolens",
	 * false, EnumLeafType.JUNGLE, new Color(0xa2d24a), new Color(0xc4d24a),
	 * WoodType.ZEBRAWOOD) {
	 * 
	 * @Override public WorldGenerator getWorldGenerator(ITreeGenData tree) {
	 * return new WorldGenZebrawood(tree); }
	 * 
	 * @Override protected void setSpeciesProperties(IAlleleTreeSpeciesCustom
	 * treeSpecies) { treeSpecies.addFruitFamily(EnumFruitFamily.NUX); }
	 * 
	 * @Override protected void setAlleles(IAllele[] alleles) {
	 * Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT,
	 * EnumAllele.Height.LARGE); Allele.helper.set(alleles,
	 * EnumTreeChromosome.GIRTH, 2); }
	 * 
	 * @Override protected void registerMutations() { registerMutation(Ebony,
	 * Poplar, 5); } },
	 */
	Mahogony(TreeBranchDefinition.MAHOGANY, "yellowMeranti", "gibbosa", true, EnumLeafType.JUNGLE, new Color(0x8ab154),
			new Color(0xa9b154), WoodType.MAHOGANY) {
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
			Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGE);
			Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOW);
			Allele.helper.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.SLOW);
			Allele.helper.set(alleles, EnumTreeChromosome.GIRTH, 2);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Kapok, Ebony, 10);
		}
	},
	AcaciaVanilla(TreeBranchDefinition.ACACIA, "acacia", "aneura", true, EnumLeafType.DECIDUOUS, new Color(0x616101),
			new Color(0xb3b302), 4, new ItemStack(Blocks.log2, 1, 0)) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenAcaciaVanilla(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE).addFruitFamily(EnumFruitFamily.NUX);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {

		}

		@Override
		protected void registerMutations() {
			// vanilla
		}
	},
	Acacia(TreeBranchDefinition.ACACIA, "desertAcacia", "erioloba", true, EnumLeafType.DECIDUOUS, new Color(0x748C1C),
			new Color(0xb3b302), WoodType.ACACIA) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenAcacia(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE).addFruitFamily(EnumFruitFamily.NUX);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			Allele.helper.set(alleles, EnumTreeChromosome.PLANT, Allele.plantTypeDesert);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Teak, Balsa, 10);
		}
	},
	Padauk(TreeBranchDefinition.PTEROCARPUS, "padauk", "soyauxii", true, EnumLeafType.DECIDUOUS, new Color(0xd0df8c),
			new Color(0x435c32), WoodType.PADAUK) {
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
			Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
			Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGE);
		}

		@Override
		protected void registerMutations() {
			registerMutation(AcaciaVanilla, Jungle, 10);
		}
	},
	Balsa(TreeBranchDefinition.OCHROMA, "balsa", "pyramidale", true, EnumLeafType.DECIDUOUS, new Color(0x59ac00),
			new Color(0xfeff8f), WoodType.BALSA) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenBalsa(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE).addFruitFamily(EnumFruitFamily.NUX);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			Allele.helper.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.HIGH);
			Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
			Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGE);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Teak, AcaciaVanilla, 10);
		}
	},
	Cocobolo(TreeBranchDefinition.DALBERGIA, "cocobolo", "retusa", false, EnumLeafType.DECIDUOUS, new Color(0x6aa17a),
			new Color(0x487d4c), WoodType.COCOBOLO) {
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
			Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGEST);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Acacia, DarkOak, 10);
		}
	},
	Wenge(TreeBranchDefinition.MILLETTIA, "wenge", "laurentii", true, EnumLeafType.DECIDUOUS, new Color(0xada157),
			new Color(0xad8a57), WoodType.WENGE) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenWenge(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE).addFruitFamily(EnumFruitFamily.NUX);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			Allele.helper.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOWEST);
			Allele.helper.set(alleles, EnumTreeChromosome.GIRTH, 2);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Cocobolo, Balsa, 10);
		}
	},
	Baobab(TreeBranchDefinition.ADANSONIA, "grandidierBaobab", "digitata", true, EnumLeafType.DECIDUOUS,
			new Color(0xfeff8f), new Color(0xffd98f), WoodType.BAOBAB) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenBaobab(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE).addFruitFamily(EnumFruitFamily.NUX);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGE);
			Allele.helper.set(alleles, EnumTreeChromosome.PLANT, Allele.plantTypeDesert);
			Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
			Allele.helper.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.SLOW);
			Allele.helper.set(alleles, EnumTreeChromosome.GIRTH, 3);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Balsa, Wenge, 10);
		}
	},
	Mahoe(TreeBranchDefinition.TALIPARITI, "blueMahoe", "elatum", true, EnumLeafType.DECIDUOUS, new Color(0xa0ba1b),
			new Color(0x79a175), WoodType.MAHOE) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenMahoe(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE).addFruitFamily(EnumFruitFamily.POMES)
					.addFruitFamily(EnumFruitFamily.PRUNES);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.SMALL);
			Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.HIGH);
			Allele.helper.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.SLOWEST);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Balsa, Acacia, 5);
		}
	},
	Willow(TreeBranchDefinition.SALIX, "whiteWillow", "alba", true, EnumLeafType.WILLOW, new Color(0xa3b8a5),
			new Color(0xa3b850), WoodType.WILLOW) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenWillow(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.NUX).addFruitFamily(EnumFruitFamily.PRUNES)
					.addFruitFamily(EnumFruitFamily.POMES);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.AVERAGE);
			Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOW);
			Allele.helper.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.FASTER);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Oak, Birch, 10).restrictTemperature(EnumTemperature.WARM, EnumTemperature.HOT)
					.restrictHumidity(EnumHumidity.DAMP);
			registerMutation(Oak, Lime, 10).restrictTemperature(EnumTemperature.WARM, EnumTemperature.HOT)
					.restrictHumidity(EnumHumidity.DAMP);
			registerMutation(Lime, Birch, 10).restrictTemperature(EnumTemperature.WARM, EnumTemperature.HOT)
					.restrictHumidity(EnumHumidity.DAMP);
		}
	},
	Sipiri(TreeBranchDefinition.CHLOROCARDIUM, "sipiri", "rodiei", true, EnumLeafType.DECIDUOUS, new Color(0x678911),
			new Color(0x79a175), WoodType.GREENHEART) {
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
			Allele.helper.set(alleles, EnumTreeChromosome.GROWTH, Allele.growthTropical);
			Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGE);
			Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOW);
			Allele.helper.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.SLOW);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Kapok, Mahogony, 10).restrictTemperature(EnumTemperature.WARM, EnumTemperature.HOT)
					.restrictHumidity(EnumHumidity.DAMP);
		}
	},
	Papaya(TreeBranchDefinition.CARICA, "papaya", "papaya", true, EnumLeafType.PALM, new Color(0x6d9f58),
			new Color(0x9ee67f), WoodType.PAPAYA) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenPapaya(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE).addFruitFamily(EnumFruitFamily.NUX);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			Allele.helper.set(alleles, EnumTreeChromosome.FRUITS, Allele.fruitPapaya);
			Allele.helper.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOW);
			Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
			Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.AVERAGE);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Jungle, Cherry, 5);
		}
	},
	Date(TreeBranchDefinition.PHOENIX, "datePalm", "dactylifera", true, EnumLeafType.PALM, new Color(0xcbcd79),
			new Color(0xf0f38f), WoodType.PALM) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenDate(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE).addFruitFamily(EnumFruitFamily.NUX);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			Allele.helper.set(alleles, EnumTreeChromosome.FRUITS, Allele.fruitDates);
			Allele.helper.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOW);
			Allele.helper.set(alleles, EnumTreeChromosome.YIELD, EnumAllele.Yield.LOW);
			Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOW);
			Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.AVERAGE);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Jungle, Papaya, 5);
		}
	},
	Poplar(TreeBranchDefinition.POPULUS, "whitePoplar", "alba", true, EnumLeafType.DECIDUOUS, new Color(0xa3b8a5),
			new Color(0x539d12), WoodType.POPLAR) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenPoplar(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.PRUNES).addFruitFamily(EnumFruitFamily.POMES);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.SMALL);
			Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOW);
			Allele.helper.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.SLOWER);
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

	private final WoodType woodType; // for forestry trees
	private final ItemStack vanillaWood; // for vanilla trees

	private IAllele[] template;
	private ITreeGenome genome;

	// vanilla tree constructor
	TreeDefinition(TreeBranchDefinition branch, String speciesName, String binomial, boolean dominant,
			EnumLeafType leafType, Color primary, Color secondary, int vanillaMeta, ItemStack vanillaWood) {
		String uid = "forestry.tree" + this.toString();
		String unlocalizedDescription = "for.description.tree" + this.toString();
		String unlocalizedName = "for.trees.species." + speciesName;

		this.branch = branch;

		ILeafIconProvider leafIconProvider = new LeafIconProvider(leafType, primary, secondary);
		IGermlingModelProvider germlingModelProvider = new GermlingVanillaModelProvider(vanillaMeta);
		IGermlingIconProvider germlingIconProvider = new GermlingIconProvider(uid);

		this.species = TreeManager.treeFactory.createSpecies(uid, unlocalizedName, "Sengir", unlocalizedDescription,
				dominant, branch.getBranch(), binomial, leafIconProvider, germlingModelProvider, germlingIconProvider,
				this);
		this.woodType = null;
		this.vanillaWood = vanillaWood;
	}

	// forestry tree constructor
	TreeDefinition(TreeBranchDefinition branch, String speciesName, String binomial, boolean dominant,
			EnumLeafType leafType, Color primary, Color secondary, WoodType woodType) {
		String uid = "forestry.tree" + this.toString();
		String unlocalizedDescription = "for.description.tree" + this.toString();
		String unlocalizedName = "for.trees.species." + speciesName;

		this.branch = branch;

		ILeafIconProvider leafIconProvider = new LeafIconProvider(leafType, primary, secondary);
		IGermlingModelProvider germlingModelProvider = new GermlingModelProvider(uid);
		IGermlingIconProvider germlingIconProvider = new GermlingIconProvider(uid);

		this.species = TreeManager.treeFactory.createSpecies(uid, unlocalizedName, "Sengir", unlocalizedDescription,
				dominant, branch.getBranch(), binomial, leafIconProvider, germlingModelProvider, germlingIconProvider,
				this);
		this.woodType = woodType;
		this.vanillaWood = null;
	}

	protected abstract void setSpeciesProperties(IAlleleTreeSpeciesCustom treeSpecies);

	protected abstract void setAlleles(IAllele[] alleles);

	protected abstract void registerMutations();

	@Override
	public void setLogBlock(World world, BlockPos pos, EnumFacing facing) {
		if (woodType == null) {
			Block vanillaWoodBlock = Block.getBlockFromItem(vanillaWood.getItem());
			int vanillaWoodMeta = vanillaWood.getItemDamage();
			world.setBlockState(pos, vanillaWoodBlock.getStateFromMeta(vanillaWoodMeta), Defaults.FLAG_BLOCK_SYNCH);
		} else {
			AlleleBoolean fireproofAllele = (AlleleBoolean) genome.getActiveAllele(EnumTreeChromosome.FIREPROOF);
			boolean fireproof = fireproofAllele.getValue();
			ItemStack log = woodType.getLog(fireproof);

			BlockTypeLog logBlock = new BlockTypeLog(log);
			logBlock.setDirection(facing);
			logBlock.setBlock(world, pos);
		}
	}

	@Override
	public void setLeaves(World world, GameProfile owner, BlockPos pos, boolean decorative) {
		boolean placed = ForestryBlock.leaves.setBlock(world, pos, 0);
		if (!placed) {
			return;
		}

		if (!ForestryBlock.leaves.isBlockEqual(world, pos)) {
			world.setBlockToAir(pos);
			return;
		}

		TileLeaves tileLeaves = ForestryBlockLeaves.getLeafTile(world, pos);
		if (tileLeaves == null) {
			world.setBlockToAir(pos);
			return;
		}

		tileLeaves.setOwner(owner);
		if (decorative) {
			tileLeaves.setDecorative();
		}
		tileLeaves.setTree(getIndividual());

		world.markBlockForUpdate(pos);
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
		Allele.helper.set(template, EnumTreeChromosome.SPECIES, species);
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

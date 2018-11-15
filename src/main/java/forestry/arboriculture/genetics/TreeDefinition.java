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
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.common.MinecraftForge;

import forestry.api.arboriculture.EnumForestryWoodType;
import forestry.api.arboriculture.EnumFruitFamily;
import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.EnumLeafType;
import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.EnumVanillaWoodType;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.IAlleleTreeSpeciesBuilder;
import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.IGermlingModelProvider;
import forestry.api.arboriculture.ILeafProvider;
import forestry.api.arboriculture.ILeafSpriteProvider;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeGenerator;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.arboriculture.ITreeMutationBuilder;
import forestry.api.arboriculture.IWoodProvider;
import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.AlleleSpeciesRegisterEvent;
import forestry.api.genetics.IAllele;
import forestry.api.world.ITreeGenData;
import forestry.arboriculture.ModuleArboriculture;
import forestry.arboriculture.genetics.alleles.AlleleFruits;
import forestry.arboriculture.models.ModelProviderFactory;
import forestry.arboriculture.tiles.TileLeaves;
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
import forestry.core.genetics.alleles.EnumAllele;
import forestry.core.tiles.TileUtil;

public enum TreeDefinition implements ITreeDefinition, ITreeGenerator, IStringSerializable {
	Oak(TreeBranchDefinition.QUERCUS, "appleOak", "robur", false, EnumLeafType.DECIDUOUS, new Color(4764952), new Color(4764952).brighter(), EnumVanillaWoodType.OAK) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenOak(tree);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.FRUITS, AlleleFruits.fruitApple);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.AVERAGE);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.FASTER);
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
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenDarkOak(tree);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.AVERAGE);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.FASTER);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.GIRTH, 2);
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
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenBirch(tree);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.AVERAGE);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.FASTER);
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
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenSilverLime(tree);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOW);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.YIELD, EnumAllele.Yield.LOWER);
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
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenWalnut(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.NUX)
				.addFruitFamily(EnumFruitFamily.PRUNES)
				.addFruitFamily(EnumFruitFamily.POMES);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.FRUITS, AlleleFruits.fruitWalnut);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOWER);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.YIELD, EnumAllele.Yield.AVERAGE);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.AVERAGE);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.GIRTH, 2);
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
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenChestnut(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.NUX)
				.addFruitFamily(EnumFruitFamily.PRUNES)
				.addFruitFamily(EnumFruitFamily.POMES);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.FRUITS, AlleleFruits.fruitChestnut);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.YIELD, EnumAllele.Yield.AVERAGE);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGE);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.GIRTH, 2);
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
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenCherry(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.PRUNES)
				.addFruitFamily(EnumFruitFamily.POMES).setRarity(0.0015F);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.FRUITS, AlleleFruits.fruitCherry);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOW);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.YIELD, EnumAllele.Yield.AVERAGE);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOW);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.SMALLER);
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
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenLemon(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.PRUNES)
				.addFruitFamily(EnumFruitFamily.POMES);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.FRUITS, AlleleFruits.fruitLemon);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.YIELD, EnumAllele.Yield.LOWER);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.AVERAGE);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.SMALLEST);
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
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenPlum(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.PRUNES)
				.addFruitFamily(EnumFruitFamily.POMES).setRarity(0.005F).setTemperature(EnumTemperature.WARM).setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.FRUITS, AlleleFruits.fruitPlum);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.YIELD, EnumAllele.Yield.HIGH);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.AVERAGE);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.SMALLEST);
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
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenMaple(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.PRUNES)
				.addFruitFamily(EnumFruitFamily.POMES).setRarity(0.0025F);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOW);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.AVERAGE);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Spruce, Larch, 5);
		}
	},
	Spruce(TreeBranchDefinition.PICEA, "redSpruce", "abies", false, EnumLeafType.CONIFERS, new Color(6396257), new Color(0x539d12), EnumVanillaWoodType.SPRUCE) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenSpruce(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {

		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.AVERAGE);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.AVERAGE);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.FASTER);
		}

		@Override
		protected void registerMutations() {
			// vanilla
		}
	},
	Larch(TreeBranchDefinition.LARIX, "mundaneLarch", "decidua", true, EnumLeafType.CONIFERS, new Color(0x698f90), new Color(0x569896), EnumForestryWoodType.LARCH) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenLarch(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.setRarity(0.0025F).setTemperature(EnumTemperature.COLD);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOW);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.AVERAGE);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Spruce, Birch, 10);
			registerMutation(Spruce, Oak, 10);
		}
	},
	Pine(TreeBranchDefinition.PINUS, "bullPine", "sabiniana", true, EnumLeafType.CONIFERS, new Color(0xfeff8f), new Color(0xffd98f), EnumForestryWoodType.PINE) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenPine(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.setRarity(0.0025F).setTemperature(EnumTemperature.COLD);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOW);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.AVERAGE);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Spruce, Larch, 10);
		}
	},
	Sequoia(TreeBranchDefinition.SEQUOIA, "coastSequoia", "sempervirens", false, EnumLeafType.CONIFERS, new Color(0x418e71), new Color(0x569896), EnumForestryWoodType.SEQUOIA) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenSequoia(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {

		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGEST);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.SLOWER);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.GIRTH, 3);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.FIREPROOF, EnumAllele.Fireproof.TRUE);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Larch, Pine, 5);
		}
	},
	Gigant(TreeBranchDefinition.SEQUOIADENDRON, "giantSequoia", "giganteum", false, EnumLeafType.CONIFERS, new Color(0x738434), new Color(0x738434).brighter(), EnumForestryWoodType.GIGANTEUM) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenGiganteum(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.setComplexity(10);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.GIGANTIC);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWEST);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.SLOWEST);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.GIRTH, 4);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.FIREPROOF, EnumAllele.Fireproof.TRUE);
		}

		@Override
		protected void registerMutations() {
			// only available by rare villager trade
		}
	},
	Jungle(TreeBranchDefinition.TROPICAL, "jungle", "tectona", false, EnumLeafType.JUNGLE, new Color(4764952), new Color(0x658917), EnumVanillaWoodType.JUNGLE) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenJungle(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.FRUITS, AlleleFruits.fruitCocoa);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGER);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.FAST);
		}

		@Override
		protected void registerMutations() {
			// vanilla
		}
	},
	Teak(TreeBranchDefinition.TECTONA, "teak", "grandis", true, EnumLeafType.JUNGLE, new Color(0xfeff8f), new Color(0xffd98f), EnumForestryWoodType.TEAK) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenTeak(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE).setRarity(0.0025F).setTemperature(EnumTemperature.WARM).setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
		}

		@Override
		protected void registerMutations() {
			registerMutation(DarkOak, Jungle, 10);
		}
	},
	Ipe(TreeBranchDefinition.TABEBUIA, "ipe", "serratifolia", true, EnumLeafType.JUNGLE, new Color(0xfdd207), new Color(0xad8f04), EnumForestryWoodType.IPE) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenIpe(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGE);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.GIRTH, 2);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Teak, DarkOak, 10);
		}
	},
	Kapok(TreeBranchDefinition.CEIBA, "kapok", "pentandra", true, EnumLeafType.JUNGLE, new Color(0x89987b), new Color(0x89aa9e), EnumForestryWoodType.KAPOK) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenKapok(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE)
				.addFruitFamily(EnumFruitFamily.PRUNES);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGE);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOW);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.SLOW);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Jungle, Teak, 10);
		}
	},
	Ebony(TreeBranchDefinition.EBONY, "myrtleEbony", "pentamera", true, EnumLeafType.JUNGLE, new Color(0xa2d24a), new Color(0xc4d24a), EnumForestryWoodType.EBONY) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenEbony(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE)
				.addFruitFamily(EnumFruitFamily.PRUNES).setRarity(0.0005F).setTemperature(EnumTemperature.WARM).setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.AVERAGE);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOW);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.SLOWER);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.GIRTH, 3);
		}

		@Override
		protected void registerMutations() {
			registerMutation(DarkOak, Kapok, 10);
		}
	},
	Zebrawood(TreeBranchDefinition.ASTRONIUM, "zebrawood", "graveolens", false, EnumLeafType.JUNGLE, new Color(0xa2d24a), new Color(0xc4d24a), EnumForestryWoodType.ZEBRAWOOD) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenZebrawood(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.NUX).setRarity(0.0005F).setTemperature(EnumTemperature.WARM).setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGE);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.GIRTH, 2);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Ebony, Poplar, 5);
		}
	},
	Mahogony(TreeBranchDefinition.MAHOGANY, "yellowMeranti", "gibbosa", true, EnumLeafType.JUNGLE, new Color(0x8ab154), new Color(0xa9b154), EnumForestryWoodType.MAHOGANY) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenMahogany(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE).setRarity(0.0005F).setTemperature(EnumTemperature.WARM).setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGE);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOW);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.SLOW);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.GIRTH, 2);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Kapok, Ebony, 10);
		}
	},
	AcaciaVanilla(TreeBranchDefinition.ACACIA, "acacia", "aneura", true, EnumLeafType.DECIDUOUS, new Color(0x616101), new Color(0xb3b302), EnumVanillaWoodType.ACACIA) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenAcaciaVanilla(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
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
	Acacia(TreeBranchDefinition.ACACIA, "desertAcacia", "erioloba", true, EnumLeafType.DECIDUOUS, new Color(0x748C1C), new Color(0xb3b302), EnumForestryWoodType.ACACIA) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenAcacia(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE)
				.addFruitFamily(EnumFruitFamily.NUX).setRarity(0.005F).setTemperature(EnumTemperature.WARM).setHumidity(EnumHumidity.ARID);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {

		}

		@Override
		protected void registerMutations() {
			registerMutation(Teak, Balsa, 10);
		}
	},
	Padauk(TreeBranchDefinition.PTEROCARPUS, "padauk", "soyauxii", true, EnumLeafType.DECIDUOUS, new Color(0xd0df8c), new Color(0x435c32), EnumForestryWoodType.PADAUK) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenPadauk(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE).setRarity(0.005F).setTemperature(EnumTemperature.WARM);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGE);
		}

		@Override
		protected void registerMutations() {
			registerMutation(AcaciaVanilla, Jungle, 10);
		}
	},
	Balsa(TreeBranchDefinition.OCHROMA, "balsa", "pyramidale", true, EnumLeafType.DECIDUOUS, new Color(0x59ac00), new Color(0xfeff8f), EnumForestryWoodType.BALSA) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenBalsa(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE)
				.addFruitFamily(EnumFruitFamily.NUX).setRarity(0.0005F).setHumidity(EnumHumidity.DAMP).setTemperature(EnumTemperature.WARM);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.HIGH);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGE);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Teak, AcaciaVanilla, 10);
		}
	},
	Cocobolo(TreeBranchDefinition.DALBERGIA, "cocobolo", "retusa", false, EnumLeafType.DECIDUOUS, new Color(0x6aa17a), new Color(0x487d4c), EnumForestryWoodType.COCOBOLO) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenCocobolo(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE).setRarity(0.0005F).setTemperature(EnumTemperature.WARM);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGEST);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Acacia, DarkOak, 10);
		}
	},
	Wenge(TreeBranchDefinition.MILLETTIA, "wenge", "laurentii", true, EnumLeafType.DECIDUOUS, new Color(0xada157), new Color(0xad8a57), EnumForestryWoodType.WENGE) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenWenge(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE)
				.addFruitFamily(EnumFruitFamily.NUX).setRarity(0.0005F).setHumidity(EnumHumidity.DAMP).setTemperature(EnumTemperature.WARM);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOWEST);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.GIRTH, 2);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Cocobolo, Balsa, 10);
		}
	},
	Baobab(TreeBranchDefinition.ADANSONIA, "grandidierBaobab", "digitata", true, EnumLeafType.DECIDUOUS, new Color(0xfeff8f), new Color(0xffd98f), EnumForestryWoodType.BAOBAB) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenBaobab(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE)
				.addFruitFamily(EnumFruitFamily.NUX).setRarity(0.005F).setTemperature(EnumTemperature.HOT).setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGE);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.SLOW);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.GIRTH, 3);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Balsa, Wenge, 10);
		}
	},
	Mahoe(TreeBranchDefinition.TALIPARITI, "blueMahoe", "elatum", true, EnumLeafType.DECIDUOUS, new Color(0xa0ba1b), new Color(0x79a175), EnumForestryWoodType.MAHOE) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenMahoe(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE)
				.addFruitFamily(EnumFruitFamily.POMES)
				.addFruitFamily(EnumFruitFamily.PRUNES).setRarity(0.000005F).setTemperature(EnumTemperature.WARM);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.SMALL);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.HIGH);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.SLOWEST);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Balsa, Acacia, 5);
		}
	},
	Willow(TreeBranchDefinition.SALIX, "whiteWillow", "alba", true, EnumLeafType.WILLOW, new Color(0xa3b8a5), new Color(0xa3b850), EnumForestryWoodType.WILLOW) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenWillow(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.NUX)
				.addFruitFamily(EnumFruitFamily.PRUNES)
				.addFruitFamily(EnumFruitFamily.POMES)
				.setRarity(0.0025F).setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.AVERAGE);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOW);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.FASTER);
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
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenGreenheart(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE).setRarity(0.0025F).setTemperature(EnumTemperature.WARM)
				.setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGE);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOW);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.SLOW);
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
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenPapaya(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE)
				.addFruitFamily(EnumFruitFamily.NUX).setRarity(0.005F).setTemperature(EnumTemperature.WARM).setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.FRUITS, AlleleFruits.fruitPapaya);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOW);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.AVERAGE);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Jungle, Cherry, 5);
		}
	},
	Date(TreeBranchDefinition.PHOENIX, "datePalm", "dactylifera", true, EnumLeafType.PALM, new Color(0xcbcd79), new Color(0xB3F370), EnumForestryWoodType.PALM) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenDate(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.JUNGLE)
				.addFruitFamily(EnumFruitFamily.NUX).setRarity(0.005F).setTemperature(EnumTemperature.WARM).setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.FRUITS, AlleleFruits.fruitDates);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOW);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.YIELD, EnumAllele.Yield.LOW);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOW);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.AVERAGE);
		}

		@Override
		protected void registerMutations() {
			registerMutation(Jungle, Papaya, 5);
		}
	},
	Poplar(TreeBranchDefinition.POPULUS, "whitePoplar", "alba", true, EnumLeafType.DECIDUOUS, new Color(0xa3b8a5), new Color(0x539d12), EnumForestryWoodType.POPLAR) {
		@Override
		public WorldGenerator getWorldGenerator(ITreeGenData tree) {
			return new WorldGenPoplar(tree);
		}

		@Override
		protected void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies) {
			treeSpecies.addFruitFamily(EnumFruitFamily.PRUNES)
				.addFruitFamily(EnumFruitFamily.POMES);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.SMALL);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOW);
			AlleleHelper.getInstance().set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.SLOWER);
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

	private IAllele[] template;
	private ITreeGenome genome;

	TreeDefinition(TreeBranchDefinition branch, String speciesName, String binomial, boolean dominant, EnumLeafType leafType, Color primary, Color secondary, IWoodType woodType) {
		String uid = Constants.MOD_ID + ".tree" + this;
		String unlocalizedDescription = "for.description.tree" + this;
		String unlocalizedName = "for.trees.species." + speciesName;

		this.branch = branch;

		ILeafSpriteProvider leafIconProvider = TreeManager.treeFactory.getLeafIconProvider(leafType, primary, secondary);
		IGermlingModelProvider germlingIconProvider = ModelProviderFactory.create(woodType, uid, leafIconProvider);
		IWoodProvider woodProvider = WoodProviderFactory.create(woodType);

		ILeafProvider leafProvider = new LeafProvider();

		IAlleleTreeSpeciesBuilder speciesBuilder = TreeManager.treeFactory.createSpecies(uid, unlocalizedName, "Sengir", unlocalizedDescription, dominant, branch.getBranch(), binomial, Constants.MOD_ID, leafIconProvider, germlingIconProvider, woodProvider, this, leafProvider);
		setSpeciesProperties(speciesBuilder);
		this.species = speciesBuilder.build();
		this.woodType = woodType;
	}

	protected abstract void setSpeciesProperties(IAlleleTreeSpeciesBuilder treeSpecies);

	protected abstract void setAlleles(IAllele[] alleles);

	protected abstract void registerMutations();

	public boolean hasFruitLeaves() {
		return false;
	}

	@Override
	public boolean setLogBlock(ITreeGenome genome, World world, BlockPos pos, EnumFacing facing) {
		AlleleBoolean fireproofAllele = (AlleleBoolean) genome.getActiveAllele(EnumTreeChromosome.FIREPROOF);
		boolean fireproof = fireproofAllele.getValue();
		IBlockState logBlock = TreeManager.woodAccess.getBlock(woodType, WoodBlockKind.LOG, fireproof);

		BlockLog.EnumAxis axis = BlockLog.EnumAxis.fromFacingAxis(facing.getAxis());
		return world.setBlockState(pos, logBlock.withProperty(BlockLog.LOG_AXIS, axis));
	}

	@Override
	public boolean setLeaves(ITreeGenome genome, World world, @Nullable GameProfile owner, BlockPos pos) {
		return setLeaves(genome, world, owner, pos, world.rand);
	}

	@Override
	public boolean setLeaves(ITreeGenome genome, World world, @Nullable GameProfile owner, BlockPos pos, Random rand) {
		if (owner == null && genome.matchesTemplateGenome()) {
			IFruitProvider fruitProvider = genome.getFruitProvider();
			String speciesUid = genome.getPrimary().getUID();
			IBlockState defaultLeaves;
			if (fruitProvider.isFruitLeaf(genome, world, pos) && rand.nextFloat() <= fruitProvider.getFruitChance(genome, world, pos)) {
				defaultLeaves = ModuleArboriculture.getBlocks().getDefaultLeavesFruit(speciesUid);
			} else {
				defaultLeaves = ModuleArboriculture.getBlocks().getDefaultLeaves(speciesUid);
			}
			return world.setBlockState(pos, defaultLeaves);
		} else {
			IBlockState leaves = ModuleArboriculture.getBlocks().leaves.getDefaultState();
			boolean placed = world.setBlockState(pos, leaves);
			if (!placed) {
				return false;
			}

			TileLeaves tileLeaves = TileUtil.getTile(world, pos, TileLeaves.class);
			if (tileLeaves == null) {
				world.setBlockToAir(pos);
				return false;
			}

			if (owner != null) {
				tileLeaves.getOwnerHandler().setOwner(owner);
			}
			tileLeaves.setTree(new Tree(genome));

			world.markBlockRangeForRenderUpdate(pos, pos);
			return true;
		}
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
		template = branch.getTemplate();
		AlleleHelper.getInstance().set(template, EnumTreeChromosome.SPECIES, species);
		setAlleles(template);

		genome = TreeManager.treeRoot.templateAsGenome(template);

		TreeManager.treeRoot.registerTemplate(template);
	}

	protected final ITreeMutationBuilder registerMutation(TreeDefinition parent1, TreeDefinition parent2, int chance) {
		return TreeManager.treeMutationFactory.createMutation(parent1.species, parent2.species, getTemplate(), chance);
	}

	@Override
	public final IAllele[] getTemplate() {
		return Arrays.copyOf(template, template.length);
	}

	public final String getUID() {
		return species.getUID();
	}

	public final String getUnlocalizedName() {
		return species.getUnlocalizedName();
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
		return TreeManager.treeRoot.getMemberStack(tree, treeType);
	}

	public static void preInit() {
		MinecraftForge.EVENT_BUS.post(new AlleleSpeciesRegisterEvent(TreeManager.treeRoot, IAlleleTreeSpecies.class));
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

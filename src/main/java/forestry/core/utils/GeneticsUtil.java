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
package forestry.core.utils;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.common.util.LazyOptional;

import forestry.api.apiculture.genetics.IAlleleBeeSpecies;
import forestry.api.arboriculture.ArboricultureCapabilities;
import forestry.api.arboriculture.genetics.IAlleleTreeSpecies;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.core.IArmorNaturalist;
import forestry.api.genetics.ICheckPollinatable;
import forestry.api.genetics.IPollinatable;
import forestry.api.genetics.ISpeciesRootPollinatable;
import forestry.api.genetics.alleles.IAlleleForestrySpecies;
import forestry.api.lepidopterology.IButterflyNursery;
import forestry.api.lepidopterology.genetics.IAlleleButterflySpecies;
import forestry.api.lepidopterology.genetics.IButterfly;
import forestry.arboriculture.capabilities.ArmorNaturalist;
import forestry.core.genetics.ItemGE;
import forestry.core.tiles.TileUtil;

import genetics.api.GeneticHelper;
import genetics.api.GeneticsAPI;
import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleSpecies;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IIndividual;
import genetics.api.mutation.IMutation;
import genetics.api.mutation.IMutationContainer;
import genetics.api.organism.IOrganismType;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.IRootDefinition;
import genetics.api.root.components.ComponentKeys;
import genetics.utils.AlleleUtils;
import genetics.utils.RootUtils;

public class GeneticsUtil {

	private static String getKeyPrefix(IAllele allele) {
		if (allele instanceof IAlleleBeeSpecies) {
			return "for.bees";
		} else if (allele instanceof IAlleleTreeSpecies) {
			return "for.trees";
		} else if (allele instanceof IAlleleButterflySpecies) {
			return "for.butterflies";
		}
		throw new IllegalStateException();
	}

	public static Component getAlyzerName(IOrganismType type, IAlleleForestrySpecies allele) {
		String customKey = getKeyPrefix(allele) +
				".custom.alyzer." +
				type.getName() +
				'.' +
				allele.getSpeciesIdentifier();
		return Translator.tryTranslate(customKey, allele::getDisplayName);
	}

	public static Component getItemName(IOrganismType type, IAlleleForestrySpecies allele) {
		String prefix = getKeyPrefix(allele);
		String customKey = prefix +
				".custom." +
				type.getName() +
				'.' +
				allele.getSpeciesIdentifier();
		return Translator.tryTranslate(customKey, () -> {
			Component speciesName = allele.getDisplayName();
			Component typeName = Component.translatable(prefix + ".grammar." + type.getName() + ".type");
			return Component.translatable(prefix + ".grammar." + type.getName(), speciesName, typeName);
		});
	}

	public static boolean hasNaturalistEye(Player player) {
		ItemStack armorItemStack = player.getItemBySlot(EquipmentSlot.HEAD);
		if (armorItemStack.isEmpty()) {
			return false;
		}

		final IArmorNaturalist armorNaturalist;
		LazyOptional<IArmorNaturalist> armorCap = armorItemStack.getCapability(ArboricultureCapabilities.ARMOR_NATURALIST);
		if (armorCap.isPresent()) {
			armorNaturalist = armorCap.orElse(ArmorNaturalist.INSTANCE);
		} else {
			return false;
		}

		return armorNaturalist.canSeePollination(player, armorItemStack, true);
	}

	public static boolean canNurse(IButterfly butterfly, Level world, final BlockPos pos) {
		IButterflyNursery tile = TileUtil.getTile(world, pos, IButterflyNursery.class);
		return tile != null && tile.canNurse(butterfly);
	}

	/**
	 * Returns an ICheckPollinatable that can be checked but not mated.
	 * Used to check for pollination traits without altering the world by changing vanilla leaves to forestry ones.
	 */
	@Nullable
	public static ICheckPollinatable getCheckPollinatable(Level world, final BlockPos pos) {
		IPollinatable tile = TileUtil.getTile(world, pos, IPollinatable.class);
		if (tile != null) {
			return tile;
		}

		Optional<IIndividual> optionalPollen = GeneticsUtil.getPollen(world, pos);
		if (optionalPollen.isPresent()) {
			IIndividual pollen = optionalPollen.get();
			IIndividualRoot root = pollen.getRoot();
			if (root instanceof ISpeciesRootPollinatable) {
				return ((ISpeciesRootPollinatable) root).createPollinatable(pollen);
			}
		}

		return null;
	}

	/**
	 * Returns an IPollinatable that can be mated. This will convert vanilla leaves to Forestry leaves.
	 */
	@Nullable
	public static IPollinatable getOrCreatePollinatable(@Nullable GameProfile owner, Level world, final BlockPos pos, boolean convertVanilla) {
		IPollinatable pollinatable = TileUtil.getTile(world, pos, IPollinatable.class);
		if (pollinatable == null && convertVanilla) {
			Optional<IIndividual> optionalPollen = GeneticsUtil.getPollen(world, pos);
			if (optionalPollen.isPresent()) {
				final IIndividual pollen = optionalPollen.get();
				IIndividualRoot root = pollen.getRoot();
				if (root instanceof ISpeciesRootPollinatable rootPollinatable) {
					pollinatable = rootPollinatable.tryConvertToPollinatable(owner, world, pos, pollen);
				}
			}
		}
		return pollinatable;
	}

	@Nullable
	public static IButterflyNursery getOrCreateNursery(@Nullable GameProfile gameProfile, LevelAccessor world, BlockPos pos, boolean convertVanilla) {
		IButterflyNursery nursery = getNursery(world, pos);
		if (nursery == null && convertVanilla) {
			Optional<IIndividual> optionalPollen = GeneticsUtil.getPollen(world, pos);
			if (optionalPollen.isPresent()) {
				IIndividual pollen = optionalPollen.get();
				if (pollen instanceof ITree treeLeave) {
					if (treeLeave.setLeaves(world, gameProfile, pos, world.getRandom())) {
						nursery = getNursery(world, pos);
					}
				}
			}
		}
		return nursery;
	}

	public static boolean canCreateNursery(LevelAccessor world, BlockPos pos) {
		Optional<IIndividual> optional = GeneticsUtil.getPollen(world, pos);
		return optional.filter(pollen -> pollen instanceof ITree).isPresent();
	}

	@Nullable
	public static IButterflyNursery getNursery(LevelAccessor world, BlockPos pos) {
		return TileUtil.getTile(world, pos, IButterflyNursery.class);
	}

	/**
	 * Gets pollen from a location. Does not affect the pollen source.
	 */
	public static Optional<IIndividual> getPollen(LevelAccessor world, final BlockPos pos) {
		if (!world.hasChunkAt(pos)) {
			return Optional.empty();
		}

		ICheckPollinatable checkPollinatable = TileUtil.getTile(world, pos, ICheckPollinatable.class);
		if (checkPollinatable != null) {
			return Optional.of(checkPollinatable.getPollen());
		}

		BlockState blockState = world.getBlockState(pos);

		for (IRootDefinition<?> definition : GeneticsAPI.apiInstance.getRoots().values()) {
			IIndividualRoot<IIndividual> root = definition.cast();
			Optional<IIndividual> individual = root.translateMember(blockState);
			if (individual.isPresent()) {
				return individual;
			}
		}

		return Optional.empty();
	}

	public static <I extends IIndividual> Optional<I> getGeneticEquivalent(ItemStack itemStack) {
		Item item = itemStack.getItem();
		if (item instanceof ItemGE) {
			return GeneticHelper.getIndividual(itemStack);
		}

		for (IRootDefinition<?> definition : GeneticsAPI.apiInstance.getRoots().values()) {
			if (!definition.isPresent()) {
				continue;
			}
			IIndividualRoot<I> root = definition.cast();
			Optional<I> individual = root.translateMember(itemStack);
			if (individual.isPresent()) {
				return individual;
			}
		}

		return Optional.empty();
	}

	//unfortunately quite a few unchecked casts
	public static ItemStack convertToGeneticEquivalent(ItemStack foreign) {
		if (!RootUtils.hasRoot(foreign)) {
			Optional<? extends IIndividual> optionalIndividual = getGeneticEquivalent(foreign);

			return optionalIndividual.map(individual -> {
				IIndividualRoot<? super IIndividual> root = individual.getRoot().cast();
				Optional<IOrganismType> type = root.getType(foreign);
				if (type.isPresent()) {
					ItemStack equivalent = root.createStack(individual, type.get());
					equivalent.setCount(foreign.getCount());
					return equivalent;
				}
				return null;
			}).orElse(foreign);
		}
		return foreign;
	}

	public static int getResearchComplexity(IAlleleSpecies species, IChromosomeType speciesChromosome) {
		return 1 + getGeneticAdvancement(species, new HashSet<>(), speciesChromosome);
	}

	private static int getGeneticAdvancement(IAlleleSpecies species, Set<IAlleleSpecies> exclude, IChromosomeType speciesChromosome) {
		int highest = 0;
		exclude.add(species);

		IIndividualRoot<IIndividual> root = species.getRoot().cast();
		IMutationContainer<IIndividual, ? extends IMutation> container = root.getComponent(ComponentKeys.MUTATIONS);
		for (IMutation mutation : container.getPaths(species, speciesChromosome)) {
			highest = getHighestAdvancement(mutation.getFirstParent(), highest, exclude, speciesChromosome);
			highest = getHighestAdvancement(mutation.getSecondParent(), highest, exclude, speciesChromosome);
		}

		return 1 + highest;
	}

	private static int getHighestAdvancement(IAlleleSpecies mutationSpecies, int highest, Set<IAlleleSpecies> exclude, IChromosomeType speciesChromosome) {
		if (exclude.contains(mutationSpecies) || AlleleUtils.isBlacklisted(mutationSpecies.getRegistryName().toString())) {
			return highest;
		}

		int otherAdvance = getGeneticAdvancement(mutationSpecies, exclude, speciesChromosome);
		return Math.max(otherAdvance, highest);
	}
}

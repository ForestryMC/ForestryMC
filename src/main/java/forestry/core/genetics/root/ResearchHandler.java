package forestry.core.genetics.root;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import genetics.api.alleles.IAlleleSpecies;
import genetics.api.individual.IIndividual;
import genetics.api.mutation.IMutation;
import genetics.api.mutation.IMutationContainer;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.components.ComponentKey;
import genetics.api.root.components.ComponentKeys;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.ForestryComponentKeys;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IForestrySpeciesRoot;
import forestry.api.genetics.IResearchHandler;
import forestry.core.utils.ItemStackUtil;

public class ResearchHandler<I extends IIndividual> implements IResearchHandler<I> {
	private final Map<ItemStack, Float> catalysts = new LinkedHashMap<>();
	private final List<IResearchPlugin> plugins = new LinkedList<>();
	private final IIndividualRoot<I> root;

	public ResearchHandler(IIndividualRoot<I> root) {
		this.root = root;
	}

	@Override
	public IIndividualRoot<I> getRoot() {
		return root;
	}

	@Override
	public void setResearchSuitability(ItemStack stack, float suitability) {
		catalysts.put(stack, suitability);
	}

	@Override
	public void addPlugin(IResearchPlugin plugin) {
		plugins.add(plugin);
	}

	@Override
	public Map<ItemStack, Float> getResearchCatalysts() {
		return catalysts;
	}

	@Override
	public float getResearchSuitability(IAlleleSpecies species, ItemStack itemstack) {
		if (itemstack.isEmpty()) {
			return 0f;
		}

		for (IResearchPlugin plugin : plugins) {
			float suitability = plugin.getResearchSuitability(species, itemstack);
			if (suitability < 0) {
				continue;
			}
			return suitability;
		}

		if (root.isMember(itemstack)) {
			return 1.0f;
		}

		for (Map.Entry<ItemStack, Float> entry : catalysts.entrySet()) {
			if (ItemStackUtil.isIdenticalItem(entry.getKey(), itemstack)) {
				return entry.getValue();
			}
		}

		return 0f;
	}

	@Override
	public NonNullList<ItemStack> getResearchBounty(IAlleleSpecies species, World world, GameProfile gameProfile, I individual, int bountyLevel) {
		NonNullList<ItemStack> bounty = NonNullList.create();
		if (world.rand.nextFloat() < bountyLevel / 16.0f) {
			IMutationContainer<I, ? extends IMutation> container = root.getComponent(ComponentKeys.MUTATIONS);
			List<? extends IMutation> allMutations = container.getCombinations(species);
			if (!allMutations.isEmpty()) {
				List<IMutation> unresearchedMutations = new ArrayList<>();
				IBreedingTracker tracker = ((IForestrySpeciesRoot<I>) root).getBreedingTracker(world, gameProfile);
				for (IMutation mutation : allMutations) {
					if (!tracker.isResearched(mutation)) {
						unresearchedMutations.add(mutation);
					}
				}

				IMutation chosenMutation;
				if (!unresearchedMutations.isEmpty()) {
					chosenMutation = unresearchedMutations.get(world.rand.nextInt(unresearchedMutations.size()));
				} else {
					chosenMutation = allMutations.get(world.rand.nextInt(allMutations.size()));
				}

				ItemStack researchNote = AlleleManager.geneticRegistry.getMutationNoteStack(gameProfile, chosenMutation);
				bounty.add(researchNote);
			}
		}

		plugins.forEach(plugin -> bounty.addAll(plugin.getResearchBounty(species, world, gameProfile, individual, bountyLevel)));
		return bounty;
	}

	@Override
	public ComponentKey<IResearchHandler> getKey() {
		return ForestryComponentKeys.RESEARCH;
	}
}

package forestry.core.genetics.analyzer;

import java.awt.Insets;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IForestrySpeciesRoot;
import forestry.core.gui.GuiConstants;
import forestry.core.gui.elements.Alignment;
import forestry.core.gui.elements.DatabaseElement;
import forestry.core.gui.elements.GuiElementFactory;
import forestry.core.gui.elements.layouts.LayoutHelper;
import forestry.core.utils.Translator;

import genetics.api.alleles.IAlleleSpecies;
import genetics.api.individual.IGenome;
import genetics.api.individual.IIndividual;
import genetics.api.mutation.IMutation;
import genetics.api.mutation.IMutationContainer;
import genetics.api.root.components.ComponentKeys;

public class MutationsTab extends DatabaseTab {
	public MutationsTab(Supplier<ItemStack> stackSupplier) {
		super("mutations", stackSupplier);
	}

	@Override
	public void createElements(DatabaseElement container, IIndividual individual, ItemStack itemStack) {
		IGenome genome = individual.getGenome();
		IForestrySpeciesRoot<IIndividual> speciesRoot = (IForestrySpeciesRoot<IIndividual>) individual.getRoot();
		IAlleleSpecies species = genome.getPrimary();
		IMutationContainer<IIndividual, IMutation> mutationContainer = speciesRoot.getComponent(ComponentKeys.MUTATIONS);

		Player player = Minecraft.getInstance().player;
		IBreedingTracker breedingTracker = speciesRoot.getBreedingTracker(player.level, player.getGameProfile());

		LayoutHelper groupHelper = container.layoutHelper((x, y) -> GuiElementFactory.horizontal(16, 0, new Insets(0, 1, 0, 0)), 100, 16);
		Collection<? extends IMutation> mutations = getValidMutations(mutationContainer.getCombinations(species));
		if (!mutations.isEmpty()) {
			container.label(Translator.translateToLocal("for.gui.database.mutations.further"), Alignment.TOP_CENTER, GuiConstants.UNDERLINED_STYLE);
			mutations.forEach(mutation -> groupHelper.add(GuiElementFactory.INSTANCE.createMutation(0, 0, 50, 16, mutation, species, breedingTracker)));
			groupHelper.finish(true);
		}
		mutations = getValidMutations(mutationContainer.getResultantMutations(species));
		if (mutations.isEmpty()) {
			return;
		}
		container.label(Translator.translateToLocal("for.gui.database.mutations.resultant"), Alignment.TOP_CENTER, GuiConstants.UNDERLINED_STYLE);
		mutations.forEach(mutation -> groupHelper.add(GuiElementFactory.INSTANCE.createMutationResultant(0, 0, 50, 16, mutation, breedingTracker)));
		groupHelper.finish(true);
	}

	private Collection<? extends IMutation> getValidMutations(List<? extends IMutation> mutations) {
		mutations.removeIf(IMutation::isSecret);
		return mutations;
	}
}

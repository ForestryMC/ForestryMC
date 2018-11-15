package forestry.core.genetics.analyzer;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IMutation;
import forestry.api.genetics.ISpeciesRoot;
import forestry.api.gui.GuiConstants;
import forestry.api.gui.GuiElementAlignment;
import forestry.api.gui.IDatabaseElement;
import forestry.api.gui.IElementLayoutHelper;
import forestry.core.gui.elements.GuiElementFactory;
import forestry.core.utils.Translator;

public class MutationsTab extends DatabaseTab {
	public MutationsTab(Supplier<ItemStack> stackSupplier) {
		super("mutations", stackSupplier);
	}

	@Override
	public void createElements(IDatabaseElement container, IIndividual individual, ItemStack itemStack) {
		IGenome genome = individual.getGenome();
		ISpeciesRoot speciesRoot = genome.getSpeciesRoot();
		IAlleleSpecies species = genome.getPrimary();

		EntityPlayer player = Minecraft.getMinecraft().player;
		IBreedingTracker breedingTracker = speciesRoot.getBreedingTracker(player.world, player.getGameProfile());

		IElementLayoutHelper groupHelper = container.layoutHelper((x, y) -> GuiElementFactory.INSTANCE.createHorizontal(x + 1, y, 16), 100, 0);
		Collection<? extends IMutation> mutations = getValidMutations(speciesRoot.getCombinations(species));
		if (!mutations.isEmpty()) {
			container.label(Translator.translateToLocal("for.gui.database.mutations.further"), GuiElementAlignment.TOP_CENTER, GuiConstants.UNDERLINED_STYLE);
			mutations.forEach(mutation -> groupHelper.add(GuiElementFactory.INSTANCE.createMutation(0, 0, 50, 16, mutation, species, breedingTracker)));
			groupHelper.finish(true);
		}
		mutations = getValidMutations(speciesRoot.getResultantMutations(species));
		if (mutations.isEmpty()) {
			return;
		}
		container.label(Translator.translateToLocal("for.gui.database.mutations.resultant"), GuiElementAlignment.TOP_CENTER, GuiConstants.UNDERLINED_STYLE);
		mutations.forEach(mutation -> groupHelper.add(GuiElementFactory.INSTANCE.createMutationResultant(0, 0, 50, 16, mutation, breedingTracker)));
		groupHelper.finish(true);
	}

	private Collection<? extends IMutation> getValidMutations(List<? extends IMutation> mutations) {
		mutations.removeIf(IMutation::isSecret);
		return mutations;
	}
}

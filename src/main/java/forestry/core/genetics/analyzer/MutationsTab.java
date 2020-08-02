package forestry.core.genetics.analyzer;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import genetics.api.alleles.IAlleleSpecies;
import genetics.api.individual.IGenome;
import genetics.api.individual.IIndividual;
import genetics.api.mutation.IMutation;
import genetics.api.mutation.IMutationContainer;
import genetics.api.root.components.ComponentKeys;

import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IForestrySpeciesRoot;
import forestry.core.gui.elements.GuiElementFactory;
import forestry.core.gui.elements.lib.GuiConstants;
import forestry.core.gui.elements.lib.GuiElementAlignment;
import forestry.core.gui.elements.lib.IDatabaseElement;
import forestry.core.gui.elements.lib.IElementLayoutHelper;
import forestry.core.utils.Translator;

public class MutationsTab extends DatabaseTab {
    public MutationsTab(Supplier<ItemStack> stackSupplier) {
        super("mutations", stackSupplier);
    }

    @Override
    public void createElements(IDatabaseElement container, IIndividual individual, ItemStack itemStack) {
        IGenome genome = individual.getGenome();
        IForestrySpeciesRoot<IIndividual> speciesRoot = (IForestrySpeciesRoot<IIndividual>) individual.getRoot();
        IAlleleSpecies species = genome.getPrimary();
        IMutationContainer<IIndividual, IMutation> mutationContainer = speciesRoot.getComponent(ComponentKeys.MUTATIONS);

        //TODO This will crash because use clientside player so have clientside world. Not sure how else to handle world saved data
        //TODO check map code?
        PlayerEntity player = Minecraft.getInstance().player;
        IBreedingTracker breedingTracker = speciesRoot.getBreedingTracker(player.world, player.getGameProfile());

        IElementLayoutHelper groupHelper = container.layoutHelper((x, y) -> GuiElementFactory.INSTANCE.createHorizontal(x + 1, y, 16), 100, 0);
        Collection<? extends IMutation> mutations = getValidMutations(mutationContainer.getCombinations(species));
        if (!mutations.isEmpty()) {
            container.label(Translator.translateToLocal("for.gui.database.mutations.further"), GuiElementAlignment.TOP_CENTER, GuiConstants.UNDERLINED_STYLE);
            mutations.forEach(mutation -> groupHelper.add(GuiElementFactory.INSTANCE.createMutation(0, 0, 50, 16, mutation, species, breedingTracker)));
            groupHelper.finish(true);
        }
        mutations = getValidMutations(mutationContainer.getResultantMutations(species));
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

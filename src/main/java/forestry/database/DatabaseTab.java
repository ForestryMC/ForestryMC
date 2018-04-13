package forestry.database;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

import forestry.api.apiculture.IBee;
import forestry.api.arboriculture.ITree;
import forestry.api.core.GuiElementAlignment;
import forestry.api.core.IGuiElementHelper;
import forestry.api.core.IGuiElementLayoutHelper;
import forestry.api.genetics.EnumDatabaseTab;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IDatabaseTab;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IMutation;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.gui.elements.GuiElementItemStack;
import forestry.core.utils.Translator;

public enum DatabaseTab implements IDatabaseTab {
	PRODUCTS {
		@Override
		public void createElements(IGuiElementHelper layoutHelper, IIndividual individual, ItemStack itemStack) {
			IGuiElementLayoutHelper groupHelper = layoutHelper.layoutHelper((x, y) -> layoutHelper.factory().createHorizontal(x + 4, y, 18).setDistance(2), 90, 0);
			Collection<ItemStack> products = getProducts(individual);
			if(!products.isEmpty()) {
				layoutHelper.addText(Translator.translateToLocal("for.gui.beealyzer.produce"), GuiElementAlignment.CENTER);
				products.forEach(product -> groupHelper.add(new GuiElementItemStack(0, 0, product)));
				groupHelper.finish();
			}

			Collection<ItemStack> specialties = getSpecialties(individual);
			if(specialties.isEmpty()){
				return;
			}

			layoutHelper.addText(Translator.translateToLocal("for.gui.beealyzer.specialty"), GuiElementAlignment.CENTER);
			specialties.forEach(specialty -> groupHelper.add(new GuiElementItemStack(0, 0, specialty)));
			groupHelper.finish();
		}

		public Collection<ItemStack> getSpecialties(IIndividual individual){
			if(individual instanceof IBee){
				IBee bee = (IBee) individual;
				return bee.getSpecialtyList();
			}else if(individual instanceof ITree){
				ITree tree = (ITree) individual;
				return tree.getSpecialties().keySet();
			}
			return Collections.emptyList();
		}

		public Collection<ItemStack> getProducts(IIndividual individual){
			if(individual instanceof IBee){
				IBee bee = (IBee) individual;
				return bee.getProduceList();
			}else if(individual instanceof ITree){
				ITree tree = (ITree) individual;
				return tree.getProducts().keySet();
			}
			return Collections.emptyList();
		}

		@Override
		public EnumDatabaseTab getTab() {
			return EnumDatabaseTab.PRODUCTS;
		}

	},
	MUTATIONS {
		@Override
		public void createElements(IGuiElementHelper layoutHelper, IIndividual individual, ItemStack itemStack) {
			IGenome genome = individual.getGenome();
			ISpeciesRoot speciesRoot = genome.getSpeciesRoot();
			IAlleleSpecies species = genome.getPrimary();

			EntityPlayer player = Minecraft.getMinecraft().player;
			IBreedingTracker breedingTracker = speciesRoot.getBreedingTracker(player.world, player.getGameProfile());

			IGuiElementLayoutHelper groupHelper = layoutHelper.layoutHelper((x, y) -> layoutHelper.factory().createHorizontal(x + 1, y, 16), 100, 0);
			Collection<? extends IMutation> mutations = getValidMutations(speciesRoot.getCombinations(species));
			if(!mutations.isEmpty()) {
				layoutHelper.addText(TextFormatting.UNDERLINE + Translator.translateToLocal("for.gui.database.mutations.further"), GuiElementAlignment.CENTER);
				mutations.forEach(mutation -> groupHelper.add(layoutHelper.factory().createMutation(0, 0, 50, 16, mutation, species, breedingTracker)));
				groupHelper.finish();
			}
			mutations = getValidMutations(speciesRoot.getResultantMutations(species));
			if(mutations.isEmpty()){
				return;
			}
			layoutHelper.addText(TextFormatting.UNDERLINE + Translator.translateToLocal("for.gui.database.mutations.resultant"), GuiElementAlignment.CENTER);
			mutations.forEach(mutation -> groupHelper.add(layoutHelper.factory().createMutationResultant(0, 0, 50, 16, mutation, breedingTracker)));
			groupHelper.finish();
		}

		public Collection<? extends IMutation> getValidMutations(List<? extends IMutation> mutations){
			Iterator<? extends IMutation> iterator = mutations.iterator();
			while(iterator.hasNext()){
				IMutation mutation  = iterator.next();
				if(mutation.isSecret()){
					iterator.remove();
				}
			}
			return mutations;
		}

		@Override
		public EnumDatabaseTab getTab() {
			return EnumDatabaseTab.MUTATIONS;
		}
	};

	public String getTooltip(IIndividual individual){
		return I18n.translateToLocal("for.gui.database.tab." + getTab().toString().toLowerCase() + ".name");
	}
}

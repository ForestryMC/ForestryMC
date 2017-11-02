package forestry.database;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IDatabaseTab;
import forestry.api.genetics.ISpeciesPlugin;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.ISpeciesRoot;


public class DatabaseScreenLogic {
	@Nullable
	public ISpeciesRoot speciesRoot;
	@Nullable
	public ISpeciesPlugin databasePlugin;
	@Nullable
	public IIndividual individual;
	public ItemStack itemStack = ItemStack.EMPTY;
	@Nullable
	public IDatabaseTab selectedTab;
	public IDatabaseTab[] tabs = new IDatabaseTab[4];

	public ScreenState onTabChange(IDatabaseTab selectedTab){
		//Check if a individual is selected and analyzed
		if(individual == null){
			return ScreenState.NO_INDIVIDUAL;
		}else if(!individual.isAnalyzed()){
			return ScreenState.NOT_ANALYZED;
		}

		if(this.selectedTab == selectedTab){
			return ScreenState.SUCCESS;
		}

		this.selectedTab = selectedTab;
		return ScreenState.SUCCESS;
	}

	public ScreenState onItemChange(ItemStack itemStack){
		ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(itemStack);

		// No individual, abort
		if (speciesRoot == null) {
			return ScreenState.NO_INDIVIDUAL;
		}
		IIndividual individual = speciesRoot.getMember(itemStack);

		//Check if a individual is selected and analyzed
		if(individual == null){
			this.individual = null;
			this.itemStack = ItemStack.EMPTY;
			return ScreenState.NO_INDIVIDUAL;
		}else if(!individual.isAnalyzed()){
			this.individual = null;
			this.itemStack = ItemStack.EMPTY;
			return ScreenState.NOT_ANALYZED;
		}
		this.individual = individual;
		this.itemStack = itemStack;
		if(speciesRoot != this.speciesRoot){
			IDatabaseTab[] tabs = getTabs(speciesRoot);
			if(tabs == null){
				this.tabs = new IDatabaseTab[4];
				return ScreenState.NO_PLUGIN;
			}
			this.tabs = tabs;
			//Select the first tab
			onTabChange(tabs[0]);
		}else{
			//Update the gui
			onTabChange(selectedTab);
		}
		this.speciesRoot = speciesRoot;
		return ScreenState.SUCCESS;
	}

	private IDatabaseTab[] getTabs(ISpeciesRoot speciesRoot){
		ISpeciesPlugin databasePlugin = speciesRoot.getSpeciesPlugin();
		if(databasePlugin == null){
			//no plugin ofr this species
			return null;
		}
		this.databasePlugin = databasePlugin;
		IDatabaseTab[] tabs = new IDatabaseTab[4];
		IDatabaseTab speciesTab = databasePlugin.getSpeciesTab(true);
		if(speciesTab == null){
			//the plugin does not support the database
			return null;
		}
		IDatabaseTab productsTab = databasePlugin.getProductsTab();
		IDatabaseTab mutationTab = databasePlugin.getMutationTab();
		if(productsTab == null){
			productsTab = DatabaseTab.PRODUCTS;
		}
		if(mutationTab == null){
			mutationTab = DatabaseTab.MUTATIONS;
		}
		tabs[0] = speciesTab;
		tabs[1] = databasePlugin.getSpeciesTab(false);
		tabs[2] = productsTab;
		tabs[3] = mutationTab;
		return tabs;
	}

	public enum ScreenState{
		NO_INDIVIDUAL,
		//The individual is not analyzed
		NOT_ANALYZED,
		//The ISpeciesRoot has no IGeneticDevicePlugin for this individual
		NO_PLUGIN,
		SUCCESS;
	}
}

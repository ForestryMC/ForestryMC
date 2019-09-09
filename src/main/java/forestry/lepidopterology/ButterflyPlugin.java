package forestry.lepidopterology;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import genetics.api.GeneticPlugin;
import genetics.api.GeneticsAPI;
import genetics.api.IGeneticApiInstance;
import genetics.api.IGeneticFactory;
import genetics.api.IGeneticPlugin;
import genetics.api.alleles.IAlleleRegistry;
import genetics.api.alleles.IAlleleSpecies;
import genetics.api.classification.IClassificationRegistry;
import genetics.api.individual.IIndividual;
import genetics.api.organism.IOrganismTypes;
import genetics.api.root.IGeneticListenerRegistry;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.IIndividualRootBuilder;
import genetics.api.root.IRootDefinition;
import genetics.api.root.IRootManager;
import genetics.api.root.components.ComponentKeys;

import forestry.api.genetics.ForestryComponentKeys;
import forestry.api.genetics.IResearchHandler;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.api.lepidopterology.genetics.EnumFlutterType;
import forestry.api.lepidopterology.genetics.IAlleleButterflySpecies;
import forestry.api.lepidopterology.genetics.IButterfly;
import forestry.core.config.Constants;
import forestry.core.genetics.root.IResearchPlugin;
import forestry.core.genetics.root.ResearchHandler;
import forestry.lepidopterology.genetics.ButterflyBranchDefinition;
import forestry.lepidopterology.genetics.ButterflyDefinition;
import forestry.lepidopterology.genetics.ButterflyHelper;
import forestry.lepidopterology.genetics.ButterflyRoot;
import forestry.lepidopterology.genetics.MothDefinition;
import forestry.lepidopterology.genetics.alleles.ButterflyAlleles;

@GeneticPlugin(modId = Constants.MOD_ID)
public class ButterflyPlugin implements IGeneticPlugin {
	public static final IRootDefinition<ButterflyRoot> ROOT = GeneticsAPI.apiInstance.getRoot(ButterflyRoot.UID);

	@Override
	public void registerClassifications(IClassificationRegistry registry) {
		ButterflyBranchDefinition.createClassifications(registry);
	}

	@Override
	public void registerListeners(IGeneticListenerRegistry registry) {
		registry.add(ButterflyRoot.UID, ButterflyDefinition.values());
		registry.add(ButterflyRoot.UID, MothDefinition.values());
	}

	@Override
	public void registerAlleles(IAlleleRegistry registry) {
		ButterflyAlleles.registerAlleles(registry);
	}

	@Override
	public void createRoot(IRootManager rootManager, IGeneticFactory geneticFactory) {
		IIndividualRootBuilder<IButterfly> rootBuilder = rootManager.createRoot(ButterflyRoot.UID);
		rootBuilder
			.setRootFactory(ButterflyRoot::new)
			.setSpeciesType(ButterflyChromosomes.SPECIES)
			.addListener(ComponentKeys.TYPES, (IOrganismTypes<IButterfly> builder) -> {
				builder.registerType(EnumFlutterType.SERUM, () -> new ItemStack(ModuleLepidopterology.getItems().serumGE));
				builder.registerType(EnumFlutterType.CATERPILLAR, () -> new ItemStack(ModuleLepidopterology.getItems().caterpillarGE));
				builder.registerType(EnumFlutterType.COCOON, () -> new ItemStack(ModuleLepidopterology.getItems().cocoonGE));
				builder.registerType(EnumFlutterType.BUTTERFLY, () -> new ItemStack(ModuleLepidopterology.getItems().butterflyGE));
			})
			.addComponent(ComponentKeys.TRANSLATORS)
			.addComponent(ComponentKeys.MUTATIONS)
			.addComponent(ForestryComponentKeys.RESEARCH, ResearchHandler::new)
			.addListener(ForestryComponentKeys.RESEARCH, (IResearchHandler<IButterfly> component) -> {
				component.addPlugin(new IResearchPlugin() {
					@Override
					public float getResearchSuitability(IAlleleSpecies species, ItemStack itemstack) {
						if (itemstack.isEmpty() || !(species instanceof IAlleleButterflySpecies)) {
							return -1;
						}
						IAlleleButterflySpecies butterflySpecies = (IAlleleButterflySpecies) species;

						if (itemstack.getItem() == Items.GLASS_BOTTLE) {
							return 0.9f;
						}

						for (ItemStack stack : butterflySpecies.getButterflyLoot().keySet()) {
							if (stack.isItemEqual(itemstack)) {
								return 1.0f;
							}
						}
						for (ItemStack stack : butterflySpecies.getCaterpillarLoot().keySet()) {
							if (stack.isItemEqual(itemstack)) {
								return 1.0f;
							}
						}
						return -1;
					}

					@Override
					public NonNullList<ItemStack> getResearchBounty(IAlleleSpecies species, World world, GameProfile researcher, IIndividual individual, int bountyLevel) {
						ItemStack serum = ((IIndividualRoot<IIndividual>) species.getRoot()).getTypes().createStack(individual.copy(), EnumFlutterType.SERUM);
						NonNullList<ItemStack> bounty = NonNullList.create();
						bounty.add(serum);
						return bounty;
					}
				});
			})
			.setDefaultTemplate(ButterflyHelper::createDefaultTemplate);
	}

	@Override
	public void onFinishRegistration(IRootManager manager, IGeneticApiInstance instance) {
		ButterflyManager.butterflyRoot = ButterflyManager.butterflyRootDefinition.get();
	}
}

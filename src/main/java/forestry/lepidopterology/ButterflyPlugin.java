package forestry.lepidopterology;

import com.mojang.authlib.GameProfile;
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
import forestry.lepidopterology.features.LepidopterologyItems;
import forestry.lepidopterology.genetics.*;
import forestry.lepidopterology.genetics.alleles.ButterflyAlleles;
import genetics.api.*;
import genetics.api.alleles.IAlleleRegistry;
import genetics.api.alleles.IAlleleSpecies;
import genetics.api.classification.IClassificationRegistry;
import genetics.api.individual.IIndividual;
import genetics.api.organism.IOrganismTypes;
import genetics.api.root.*;
import genetics.api.root.components.ComponentKeys;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

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
                    builder.registerType(EnumFlutterType.SERUM, LepidopterologyItems.SERUM_GE::stack);
                    builder.registerType(EnumFlutterType.CATERPILLAR, LepidopterologyItems.CATERPILLAR_GE::stack);
                    builder.registerType(EnumFlutterType.COCOON, LepidopterologyItems.COCOON_GE::stack);
                    builder.registerType(EnumFlutterType.BUTTERFLY, LepidopterologyItems.BUTTERFLY_GE::stack);
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

                            for (ItemStack stack : butterflySpecies.getButterflyLoot().getPossibleStacks()) {
                                if (stack.isItemEqual(itemstack)) {
                                    return 1.0f;
                                }
                            }
                            for (ItemStack stack : butterflySpecies.getCaterpillarLoot().getPossibleStacks()) {
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

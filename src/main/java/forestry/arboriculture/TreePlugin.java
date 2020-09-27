package forestry.arboriculture;

import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.genetics.EnumGermlingType;
import forestry.api.arboriculture.genetics.IAlleleTreeSpecies;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.api.genetics.ForestryComponentKeys;
import forestry.api.genetics.IFruitFamily;
import forestry.api.genetics.IResearchHandler;
import forestry.api.genetics.products.IProductList;
import forestry.arboriculture.blocks.BlockDefaultLeaves;
import forestry.arboriculture.blocks.BlockDefaultLeavesFruit;
import forestry.arboriculture.features.ArboricultureBlocks;
import forestry.arboriculture.features.ArboricultureItems;
import forestry.arboriculture.genetics.*;
import forestry.arboriculture.genetics.alleles.AlleleFruits;
import forestry.arboriculture.genetics.alleles.AlleleLeafEffects;
import forestry.core.config.Constants;
import forestry.core.genetics.alleles.EnumAllele;
import forestry.core.genetics.root.ResearchHandler;
import forestry.modules.features.FeatureBlock;
import genetics.api.*;
import genetics.api.alleles.IAlleleRegistry;
import genetics.api.classification.IClassificationRegistry;
import genetics.api.organism.IOrganismTypes;
import genetics.api.root.IGeneticListenerRegistry;
import genetics.api.root.IIndividualRootBuilder;
import genetics.api.root.IRootDefinition;
import genetics.api.root.IRootManager;
import genetics.api.root.components.ComponentKeys;
import genetics.api.root.translator.IBlockTranslator;
import genetics.api.root.translator.IIndividualTranslator;
import genetics.api.root.translator.IItemTranslator;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

@GeneticPlugin(modId = Constants.MOD_ID)
public class TreePlugin implements IGeneticPlugin {
    public static final IRootDefinition<TreeRoot> ROOT = GeneticsAPI.apiInstance.getRoot(TreeRoot.UID);

    @Override
    public void registerClassifications(IClassificationRegistry registry) {
        TreeBranchDefinition.registerBranches(registry);
    }

    @Override
    public void registerListeners(IGeneticListenerRegistry registry) {
        registry.add(TreeRoot.UID, TreeDefinition.VALUES);
    }

    @Override
    public void registerAlleles(IAlleleRegistry registry) {
        registry.registerAlleles(EnumAllele.Height.values(), TreeChromosomes.HEIGHT);
        registry.registerAlleles(EnumAllele.Saplings.values(), TreeChromosomes.FERTILITY);
        registry.registerAlleles(EnumAllele.Yield.values(), TreeChromosomes.YIELD);
        registry.registerAlleles(EnumAllele.Fireproof.values(), TreeChromosomes.FIREPROOF);
        registry.registerAlleles(EnumAllele.Maturation.values(), TreeChromosomes.MATURATION);
        registry.registerAlleles(EnumAllele.Sappiness.values(), TreeChromosomes.SAPPINESS);
        AlleleFruits.registerAlleles(registry);
        AlleleLeafEffects.registerAlleles(registry);
    }

    @Override
    public void createRoot(IRootManager rootManager, IGeneticFactory geneticFactory) {
        //TODO tags?
        IIndividualRootBuilder<ITree> rootBuilder = rootManager.createRoot(TreeRoot.UID);
        rootBuilder
                .setRootFactory(TreeRoot::new)
                .setSpeciesType(TreeChromosomes.SPECIES)
                .addListener(ComponentKeys.TYPES, (IOrganismTypes<ITree> builder) -> {
                    builder.registerType(EnumGermlingType.SAPLING, ArboricultureItems.SAPLING::stack);
                    builder.registerType(EnumGermlingType.POLLEN, ArboricultureItems.POLLEN_FERTILE::stack);
                })
                .addComponent(ComponentKeys.TRANSLATORS)
                .addComponent(ComponentKeys.MUTATIONS)
                .addComponent(ForestryComponentKeys.RESEARCH, ResearchHandler::new)
                .addListener(ForestryComponentKeys.RESEARCH, (IResearchHandler<ITree> builder) -> {
                    builder.setResearchSuitability(new ItemStack(Blocks.OAK_SAPLING), 1.0f);
                    builder.addPlugin((species, itemstack) -> {
                        if (itemstack.isEmpty() || !(species instanceof IAlleleTreeSpecies)) {
                            return -1F;
                        }
                        IAlleleTreeSpecies treeSpecies = (IAlleleTreeSpecies) species;

                        Collection<IFruitFamily> suitableFruit = treeSpecies.getSuitableFruit();
                        for (IFruitFamily fruitFamily : suitableFruit) {
                            Collection<IFruitProvider> fruitProviders = TreeManager.treeRoot.getFruitProvidersForFruitFamily(
                                    fruitFamily);
                            for (IFruitProvider fruitProvider : fruitProviders) {
                                IProductList products = fruitProvider.getProducts();
                                for (ItemStack stack : products.getPossibleStacks()) {
                                    if (stack.isItemEqual(itemstack)) {
                                        return 1.0f;
                                    }
                                }
                                IProductList specialtyChances = fruitProvider.getSpecialty();
                                for (ItemStack stack : specialtyChances.getPossibleStacks()) {
                                    if (stack.isItemEqual(itemstack)) {
                                        return 1.0f;
                                    }
                                }
                            }
                        }
                        return -1F;
                    });
                })
                .addListener(ComponentKeys.TRANSLATORS, (IIndividualTranslator<ITree> builder) -> {
                            Function<TreeDefinition, IBlockTranslator<ITree>> leavesFactory = (definition) ->
                                    (BlockState blockState) -> {
                                        if (blockState.get(LeavesBlock.PERSISTENT)) {
                                            return null;
                                        }
                                        return definition.createIndividual();
                                    };
                            Function<TreeDefinition, IItemTranslator<ITree>> saplingFactory = definition -> new IItemTranslator<ITree>() {
                                @Override
                                public ITree getIndividualFromObject(ItemStack itemStack) {
                                    return definition.createIndividual();
                                }

                                @Override
                                public ItemStack getGeneticEquivalent(ItemStack itemStack) {
                                    return definition.getMemberStack(EnumGermlingType.SAPLING);
                                }
                            };
                            builder.registerTranslator(leavesFactory.apply(TreeDefinition.Oak), Blocks.OAK_LEAVES);
                            builder.registerTranslator(leavesFactory.apply(TreeDefinition.Birch), Blocks.BIRCH_LEAVES);
                            builder.registerTranslator(leavesFactory.apply(TreeDefinition.Spruce), Blocks.SPRUCE_LEAVES);
                            builder.registerTranslator(leavesFactory.apply(TreeDefinition.Jungle), Blocks.JUNGLE_LEAVES);
                            builder.registerTranslator(leavesFactory.apply(TreeDefinition.Acacia), Blocks.ACACIA_LEAVES);
                            builder.registerTranslator(leavesFactory.apply(TreeDefinition.DarkOak), Blocks.DARK_OAK_LEAVES);

                            builder.registerTranslator(saplingFactory.apply(TreeDefinition.Oak), Items.OAK_SAPLING);
                            builder.registerTranslator(saplingFactory.apply(TreeDefinition.Birch), Items.BIRCH_LEAVES);
                            builder.registerTranslator(saplingFactory.apply(TreeDefinition.Spruce), Items.SPRUCE_LEAVES);
                            builder.registerTranslator(saplingFactory.apply(TreeDefinition.Jungle), Items.JUNGLE_LEAVES);
                            builder.registerTranslator(saplingFactory.apply(TreeDefinition.Acacia), Items.ACACIA_LEAVES);
                            builder.registerTranslator(saplingFactory.apply(TreeDefinition.DarkOak), Items.DARK_OAK_LEAVES);

                            for (Map.Entry<TreeDefinition, FeatureBlock<BlockDefaultLeaves, BlockItem>> leaves : ArboricultureBlocks.LEAVES_DEFAULT
                                    .getFeatureByType()
                                    .entrySet()) {
                                builder.registerTranslator(
                                        blockState -> leaves.getKey().createIndividual(),
                                        leaves.getValue().block()
                                );
                            }
                            for (Map.Entry<TreeDefinition, FeatureBlock<BlockDefaultLeavesFruit, BlockItem>> leaves : ArboricultureBlocks.LEAVES_DEFAULT_FRUIT
                                    .getFeatureByType()
                                    .entrySet()) {
                                builder.registerTranslator(
                                        blockState -> leaves.getKey().createIndividual(),
                                        leaves.getValue().block()
                                );
                            }
                        }
                )
                .setDefaultTemplate(TreeHelper::createDefaultTemplate);
    }

    @Override
    public void onFinishRegistration(IRootManager manager, IGeneticApiInstance instance) {
        TreeManager.treeRoot = TreeManager.treeRootDefinition.get();

        // Modes
        TreeManager.treeRoot.registerTreekeepingMode(TreekeepingMode.easy);
        TreeManager.treeRoot.registerTreekeepingMode(TreekeepingMode.normal);
        TreeManager.treeRoot.registerTreekeepingMode(TreekeepingMode.hard);
        TreeManager.treeRoot.registerTreekeepingMode(TreekeepingMode.hardcore);
        TreeManager.treeRoot.registerTreekeepingMode(TreekeepingMode.insane);
    }
}

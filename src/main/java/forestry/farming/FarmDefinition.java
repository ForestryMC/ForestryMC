package forestry.farming;

import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.genetics.EnumGermlingType;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.ITreeRoot;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuit;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.farming.FarmPropertiesEvent;
import forestry.api.farming.IFarmLogic;
import forestry.api.farming.IFarmProperties;
import forestry.api.farming.IFarmPropertiesBuilder;
import forestry.arboriculture.genetics.alleles.AlleleFruits;
import forestry.core.circuits.Circuits;
import forestry.core.features.CoreBlocks;
import forestry.core.features.CoreItems;
import forestry.core.items.EnumElectronTube;
import forestry.core.items.ItemFruit;
import forestry.core.utils.ForgeUtils;
import forestry.farming.circuits.CircuitFarmLogic;
import forestry.farming.logic.*;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IStringSerializable;
import org.apache.commons.lang3.text.WordUtils;

import java.util.function.BiFunction;

public enum FarmDefinition implements IStringSerializable {
    CROPS("crops", EnumElectronTube.BRONZE, FarmLogicCrops::new) {
        @Override
        protected void initProperties(IFarmPropertiesBuilder properties) {
            wateredProperties(properties).addSoil(Blocks.DIRT)
                                         .addFarmables("Wheat")
                                         .setIcon(() -> new ItemStack(Items.WHEAT));
        }
    },
    GOURD("gourd", EnumElectronTube.LAPIS, FarmLogicGourd::new) {
        @Override
        protected void initProperties(IFarmPropertiesBuilder properties) {
            properties.setIcon(() -> new ItemStack(Items.MELON))
                      .addSoil(Blocks.DIRT)
                      .setFertilizer(10)
                      .setWater(hydrationModifier -> (int) (40 * hydrationModifier));
        }
    },
    SHROOM("shroom", EnumElectronTube.APATITE, FarmLogicMushroom::new) {
        @Override
        protected void initProperties(IFarmPropertiesBuilder properties) {
            properties.addSoil(Blocks.MYCELIUM)
                      .addSoil(Blocks.PODZOL)
                      .setWater(hydrationModifier -> (int) (80 * hydrationModifier))
                      .setFertilizer(20)
                      .setIcon(() -> new ItemStack(Blocks.RED_MUSHROOM));
        }
    },
    INFERNAL("infernal", EnumElectronTube.BLAZE, FarmLogicInfernal::new) {
        @Override
        protected void initProperties(IFarmPropertiesBuilder properties) {
            properties.addSoil(Blocks.SOUL_SAND)
                      .setWater(0)
                      .setFertilizer(20)
                      .setIcon(() -> new ItemStack(Items.NETHER_WART));
        }
    },
    POALES("poales", EnumElectronTube.TIN, FarmLogicReeds::new) {
        @Override
        protected void initProperties(IFarmPropertiesBuilder properties) {
            properties.addSoil(Blocks.SAND)
                      .addSoil(Blocks.DIRT).setFertilizer(10)
                      .setWater(hydrationModifier -> (int) (20 * hydrationModifier))
                      .setIcon(() -> new ItemStack(Items.SUGAR_CANE));
        }
    },
    SUCCULENTES("succulentes", EnumElectronTube.COPPER, FarmLogicSucculent::new) {
        @Override
        protected void initProperties(IFarmPropertiesBuilder properties) {
            properties.addSoil(Blocks.SAND)
                      .setFertilizer(10)
                      .setWater(1)
                      .setIcon(() -> new ItemStack(Items.GREEN_DYE));
        }
    },
    ENDER("ender", EnumElectronTube.ENDER, FarmLogicEnder::new) {
        @Override
        protected void initProperties(IFarmPropertiesBuilder properties) {
            properties.addSoil(Blocks.END_STONE)
                      .setIcon(() -> new ItemStack(Items.ENDER_EYE))
                      .setFertilizer(20)
                      .setWater(0);
        }
    },
    ARBOREAL("arboreal", EnumElectronTube.GOLD, FarmLogicArboreal::new) {
        @Override
        protected void initProperties(IFarmPropertiesBuilder properties) {
            properties.addSoil(new ItemStack(Blocks.DIRT), CoreBlocks.HUMUS.defaultState())
                      .addSoil(CoreBlocks.HUMUS.stack(), CoreBlocks.HUMUS.defaultState())
                      .addProducts(new ItemStack(Blocks.SAND))
                      .setFertilizer(10)
                      .setWater(hydrationModifier -> (int) (10 * hydrationModifier))
                      .setIcon(() -> new ItemStack(Blocks.OAK_SAPLING));
        }
    },
    PEAT("peat", EnumElectronTube.OBSIDIAN, FarmLogicPeat::new) {
        @Override
        protected void initProperties(IFarmPropertiesBuilder properties) {
            wateredProperties(properties).addSoil(CoreBlocks.BOG_EARTH.stack(), CoreBlocks.BOG_EARTH.defaultState())
                                         .addProducts(CoreItems.PEAT.stack(), new ItemStack(Blocks.DIRT))
                                         .setIcon(CoreItems.PEAT::stack)
                                         .setFertilizer(2);
        }
    },
    ORCHARD("orchard", EnumElectronTube.EMERALD, FarmLogicOrchard::new) {
        @Override
        protected void initProperties(IFarmPropertiesBuilder properties) {
            properties.setFertilizer(10)
                      .setWater(hydrationModifier -> (int) (40 * hydrationModifier))
                      .setIcon(() -> CoreItems.FRUITS.stack(ItemFruit.EnumFruit.CHERRY));
            if (ModuleHelper.isEnabled(ForestryModuleUids.ARBORICULTURE)) {
                ITreeRoot treeRoot = TreeManager.treeRoot;
                if (treeRoot != null) {
                    for (ITree tree : treeRoot.getIndividualTemplates()) {
                        IFruitProvider fruitProvider = tree.getGenome()
                                                           .getActiveAllele(TreeChromosomes.FRUITS)
                                                           .getProvider();
                        if (fruitProvider != AlleleFruits.fruitNone.getProvider()) {
                            properties.addSeedlings(treeRoot.getTypes().createStack(tree, EnumGermlingType.SAPLING))
                                      .addProducts(fruitProvider.getProducts().getPossibleStacks())
                                      .addProducts(fruitProvider.getSpecialty().getPossibleStacks());
                        }
                    }
                }
            }
        }
    },
    COCOA("cocoa", EnumElectronTube.DIAMOND, FarmLogicCocoa::new) {
        @Override
        protected void initProperties(IFarmPropertiesBuilder properties) {
            properties.addSoil(Blocks.JUNGLE_LOG)
                      .addSeedlings(new ItemStack(Items.COCOA_BEANS))
                      .addProducts(new ItemStack(Items.COCOA_BEANS))
                      .setFertilizer(120)
                      .setWater(hydrationModifier -> (int) (20 * hydrationModifier))
                      .setIcon(() -> new ItemStack(Items.COCOA_BEANS));
        }
    }/*,
	//TODO: Mod combat
	RUBBER("rubber", EnumElectronTube.RUBBER, FarmLogicRubber::new, ForestryModuleUids.INDUSTRIALCRAFT2){
		@Override
		protected void initProperties(IFarmPropertiesBuilder properties) {
			properties.setFertilizer(40)
				.setWater(hydrationModifier->(int) (5 * hydrationModifier))
			.setIcon(()->{
				//		if (ModUtil.isModLoaded(PluginIC2.MOD_ID)) {
				//			return PluginIC2.resin;
				//		} else if (ModUtil.isModLoaded(PluginTechReborn.MOD_ID)) {
				//			return PluginTechReborn.sap;
				//		}
				return ItemStack.EMPTY;
			});
		}
	},
	ORCHID("orchid", EnumElectronTube.ORCHID, FarmLogicOrchard::new, ForestryModuleUids.EXTRA_UTILITIES){
		@Override
		protected void initProperties(IFarmPropertiesBuilder properties) {
			properties.setFertilizer(20)
				.setWater(0)
				.setIcon(()->PluginExtraUtilities.orchidStack);
		}
	}*/;

    private final String name;
    private final EnumElectronTube tube;
    protected final IFarmProperties properties;
    private final String module;
    private final ICircuit managed;
    private final ICircuit manual;

    FarmDefinition(String identifier, EnumElectronTube tube, BiFunction<IFarmProperties, Boolean, IFarmLogic> factory) {
        this(identifier, tube, factory, ForestryModuleUids.FARMING);
    }

    FarmDefinition(
            String identifier,
            EnumElectronTube tube,
            BiFunction<IFarmProperties, Boolean, IFarmLogic> factory,
            String module
    ) {
        String camelCase = WordUtils.capitalize(identifier);
        IFarmPropertiesBuilder builder = FarmRegistry.getInstance().getPropertiesBuilder("farm" + camelCase)
                                                     .setFactory(factory)
                                                     .setTranslationKey("for.farm." + identifier)
                                                     .addFarmables("farm" + camelCase);
        initProperties(builder);
        ForgeUtils.postEvent(new FarmPropertiesEvent(identifier, builder));
        this.properties = builder.create();
        this.managed = new CircuitFarmLogic("managed" + camelCase, properties, false);
        this.manual = new CircuitFarmLogic("manual" + camelCase, properties, true);
        this.name = identifier;
        this.tube = tube;
        this.module = module;
    }

    protected IFarmPropertiesBuilder wateredProperties(IFarmPropertiesBuilder builder) {
        return builder.setWater((hydrationModifier) -> (int) (20 * hydrationModifier))
                      .setFertilizer(5);
    }

    protected void initProperties(IFarmPropertiesBuilder properties) {
        //Default Implementation
    }

    @Override
    public String getString() {
        return name;
    }

    public IFarmProperties getProperties() {
        return properties;
    }

    public static void init() {
        Circuits.farmArborealManaged = ARBOREAL.managed;
        Circuits.farmArborealManual = ARBOREAL.manual;

        Circuits.farmShroomManaged = SHROOM.managed;
        Circuits.farmShroomManual = SHROOM.manual;

        Circuits.farmPeatManaged = PEAT.managed;
        Circuits.farmPeatManual = PEAT.manual;

        Circuits.farmCropsManaged = CROPS.managed;
        Circuits.farmCropsManual = CROPS.manual;

        Circuits.farmInfernalManaged = INFERNAL.managed;
        Circuits.farmInfernalManual = INFERNAL.manual;

        Circuits.farmOrchardManaged = ORCHARD.managed;
        Circuits.farmOrchardManual = ORCHARD.manual;

        Circuits.farmSucculentManaged = SUCCULENTES.managed;
        Circuits.farmSucculentManual = SUCCULENTES.manual;

        Circuits.farmPoalesManaged = POALES.managed;
        Circuits.farmPoalesManual = POALES.manual;

        Circuits.farmGourdManaged = GOURD.managed;
        Circuits.farmGourdManual = GOURD.manual;

        Circuits.farmCocoaManaged = COCOA.managed;
        Circuits.farmCocoaManual = COCOA.manual;

        Circuits.farmEnderManaged = ENDER.managed;
        Circuits.farmEnderManual = ENDER.manual;
    }
}

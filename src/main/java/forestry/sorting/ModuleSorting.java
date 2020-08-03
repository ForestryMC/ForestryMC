package forestry.sorting;

import forestry.api.genetics.alleles.AlleleManager;
import forestry.api.genetics.filter.IFilterLogic;
import forestry.api.modules.ForestryModule;
import forestry.core.capabilities.NullStorage;
import forestry.core.config.Constants;
import forestry.core.network.IPacketRegistry;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.sorting.features.SortingContainers;
import forestry.sorting.gui.GuiGeneticFilter;
import forestry.sorting.network.PacketRegistrySorting;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.CapabilityManager;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.SORTING, name = "Sorting", author = "Nedelosk", url = Constants.URL, unlocalizedDescription = "for.module.sorting.description")
public class ModuleSorting extends BlankForestryModule {

    @Override
    public IPacketRegistry getPacketRegistry() {
        return new PacketRegistrySorting();
    }

    @Override
    public void setupAPI() {
        AlleleManager.filterRegistry = new FilterRegistry();
    }

    @Override
    public void disabledSetupAPI() {
        AlleleManager.filterRegistry = new DummyFilterRegistry();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerGuiFactories() {
        ScreenManager.registerFactory(SortingContainers.GENETIC_FILTER.containerType(), GuiGeneticFilter::new);
    }

    @Override
    public void preInit() {
        CapabilityManager.INSTANCE.register(IFilterLogic.class, new NullStorage<>(), () -> FakeFilterLogic.INSTANCE);

        DefaultFilterRuleType.init();
    }

    @Override
    public void doInit() {
        ((FilterRegistry) AlleleManager.filterRegistry).init();
    }
}

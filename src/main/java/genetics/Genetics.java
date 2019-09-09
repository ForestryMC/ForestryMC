package genetics;

import javax.annotation.Nullable;
import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import genetics.api.GeneticHelper;
import genetics.api.GeneticsAPI;
import genetics.api.IGeneTemplate;
import genetics.api.alleles.IAllele;
import genetics.api.individual.IChromosomeType;
import genetics.api.organism.IOrganism;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.IRootDefinition;
import genetics.api.root.components.DefaultStage;

import genetics.individual.GeneticSaveHandler;
import genetics.individual.SaveFormat;
import genetics.plugins.PluginManager;

@Mod(Genetics.MOD_ID)
public class Genetics {
	public static final String MOD_ID = "geneticsapi";

	/**
	 * Capability for {@link IOrganism}.
	 */
	@CapabilityInject(IOrganism.class)
	public static Capability<IOrganism> ORGANISM;
	@CapabilityInject(IGeneTemplate.class)
	public static Capability<IGeneTemplate> GENE_TEMPLATE;

	public Genetics() {
		GeneticsAPI.apiInstance = ApiInstance.INSTANCE;
		//FMLJavaModLoadingContext.get().getModEventBus().addListener(this::preInit);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupCommon);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}

	//public void preInit(FMLCommonSetupEvent event) {
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void registerBlocks(RegistryEvent.Register<Block> event) {
		CapabilityManager.INSTANCE.register(IOrganism.class, new NullStorage<>(), () -> GeneticHelper.EMPTY);
		CapabilityManager.INSTANCE.register(IGeneTemplate.class, new NullStorage<>(), () -> new IGeneTemplate() {
			@Override
			public Optional<IAllele> getAllele() {
				return Optional.empty();
			}

			@Override
			public Optional<IChromosomeType> getType() {
				return Optional.empty();
			}

			@Override
			public Optional<IIndividualRoot> getRoot() {
				return Optional.empty();
			}

			@Override
			public void setAllele(@Nullable IChromosomeType type, @Nullable IAllele allele) {
			}
		});

		PluginManager.create();

		PluginManager.initPlugins();
	}

	public void setupCommon(FMLCommonSetupEvent event) {
		for (IRootDefinition definition : GeneticsAPI.apiInstance.getRoots().values()) {
			if (!definition.isRootPresent()) {
				continue;
			}
			definition.get().getComponentContainer().onStage(DefaultStage.SETUP);
		}
	}

	public void loadComplete(FMLLoadCompleteEvent event) {
		for (IRootDefinition definition : GeneticsAPI.apiInstance.getRoots().values()) {
			if (!definition.isRootPresent()) {
				continue;
			}
			definition.get().getComponentContainer().onStage(DefaultStage.COMPLETION);
		}
		GeneticSaveHandler.setWriteFormat(SaveFormat.BINARY);
	}

	public class NullStorage<T> implements Capability.IStorage<T> {
		@Nullable
		public INBT writeNBT(Capability<T> capability, T instance, Direction side) {
			/* compiled code */
			return null;
		}

		@Override
		public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {
			/* compiled code */
		}
	}
}

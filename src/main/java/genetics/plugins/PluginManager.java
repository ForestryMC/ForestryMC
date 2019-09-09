package genetics.plugins;

import com.google.common.collect.ImmutableSortedMap;

import java.util.Comparator;
import java.util.Map;
import java.util.function.Consumer;

import net.minecraftforge.eventbus.api.EventPriority;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import genetics.api.GeneticPlugin;
import genetics.api.GeneticsAPI;
import genetics.api.IGeneticPlugin;
import genetics.api.alleles.Allele;

import genetics.ApiInstance;
import genetics.GeneticFactory;
import genetics.alleles.AlleleRegistry;
import genetics.classification.ClassificationRegistry;
import genetics.root.IndividualRootBuilder;
import genetics.root.RootManager;

public class PluginManager {
	private static final Comparator<IGeneticPlugin> PLUGIN_COMPARATOR = (firstPlugin, secondPlugin) -> {
		EventPriority first = firstPlugin.getClass().getAnnotation(GeneticPlugin.class).priority();
		EventPriority second = secondPlugin.getClass().getAnnotation(GeneticPlugin.class).priority();
		if (first.equals(second)) {
			return firstPlugin.hashCode() - secondPlugin.hashCode();
		}
		return first.ordinal() > second.ordinal() ? 1 : -1;
	};
	private static ImmutableSortedMap<IGeneticPlugin, ModContainer> plugins;

	private PluginManager() {
	}

	public static void create() {
		ImmutableSortedMap.Builder<IGeneticPlugin, ModContainer> builder = new ImmutableSortedMap.Builder<>(PLUGIN_COMPARATOR);
		builder.putAll(PluginUtil.getPlugins());
		plugins = builder.build();
		for (IGeneticPlugin plugin : plugins.keySet()) {
			FMLJavaModLoadingContext.get().getModEventBus().register(plugin);
		}
	}

	public static void initPlugins() {
		//
		ClassificationRegistry classificationRegistry = new ClassificationRegistry();
		ApiInstance.INSTANCE.setClassificationRegistry(classificationRegistry);
		handlePlugins(p -> p.registerClassifications(classificationRegistry));
		//
		RootManager rootManager = new RootManager();
		handlePlugins(p -> p.registerListeners(rootManager));
		//register all alleles
		AlleleRegistry alleleRegistry = new AlleleRegistry();
		alleleRegistry.registerAllele(Allele.EMPTY);
		ApiInstance.INSTANCE.setAlleleRegistry(alleleRegistry);
		handlePlugins(p -> p.registerAlleles(alleleRegistry));
		rootManager.getListeners().values().forEach(listener -> listener.registerAlleles(alleleRegistry));
		//
		handlePlugins(p -> p.createRoot(rootManager, GeneticFactory.INSTANCE));
		handlePlugins(p -> p.initRoots(rootManager));
		Map<String, IndividualRootBuilder> rootBuilders = rootManager.getRootBuilders();
		for (IndividualRootBuilder builder : rootBuilders.values()) {
			builder.create(rootManager.getListeners(builder.uid));
		}
		handlePlugins(p -> p.onFinishRegistration(rootManager, GeneticsAPI.apiInstance));
	}

	private static void handlePlugins(Consumer<IGeneticPlugin> pluginConsumer) {
		ModContainer oldContainer = ModLoadingContext.get().getActiveContainer();
		plugins.forEach((plugin, container) -> {
			ModLoadingContext.get().setActiveContainer(container, FMLJavaModLoadingContext.get());
			pluginConsumer.accept(plugin);
		});
		ModLoadingContext.get().setActiveContainer(oldContainer, FMLJavaModLoadingContext.get());
	}
}

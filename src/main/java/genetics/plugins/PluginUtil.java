package genetics.plugins;

import genetics.api.GeneticPlugin;
import genetics.api.IGeneticPlugin;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

import java.util.*;
import java.util.function.Function;

public class PluginUtil {
    private static final Logger LOGGER = LogManager.getLogger();

    private PluginUtil() {

    }

    public static Map<IGeneticPlugin, ModContainer> getPlugins() {
        Type annotationType = Type.getType(GeneticPlugin.class);
        List<ModFileScanData> allScanData = ModList.get().getAllScanData();
        Function<String, Optional<? extends ModContainer>> getContainer = (modId) -> ModList.get().getModContainerById(
                modId);
        List<String> pluginClassNames = new ArrayList<>();
        for (ModFileScanData scanData : allScanData) {
            Set<ModFileScanData.AnnotationData> annotations = scanData.getAnnotations();
            for (ModFileScanData.AnnotationData a : annotations) {
                if (Objects.equals(a.getAnnotationType(), annotationType)) {
                    String memberName = a.getMemberName();
                    pluginClassNames.add(memberName);
                }
            }
        }
        Map<IGeneticPlugin, ModContainer> instances = new HashMap<>();
        Set<String> loadedClasses = new HashSet<>();
        for (String className : pluginClassNames) {
            if (loadedClasses.contains(className)) {
                continue;
            }
            try {
                Class<?> asmClass = Class.forName(className);
                Class<? extends IGeneticPlugin> asmInstanceClass = asmClass.asSubclass(IGeneticPlugin.class);
                IGeneticPlugin instance = asmInstanceClass.newInstance();
                GeneticPlugin plugin = asmInstanceClass.getAnnotation(GeneticPlugin.class);
                if (plugin != null) {
                    Optional<? extends ModContainer> modContainer = getContainer.apply(plugin.modId());
                    modContainer.ifPresent(container -> instances.put(instance, container));
                    loadedClasses.add(className);
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | LinkageError e) {
                LOGGER.error("Failed to load: {}", className, e);
            }
        }
        return instances;
    }
}
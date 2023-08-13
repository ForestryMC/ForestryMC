package forestry.modules;

import forestry.api.modules.ForestryModule;
import forestry.api.modules.IForestryModule;
import forestry.modules.features.FeatureProvider;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.util.*;

public class ForestryPluginUtil {
    private ForestryPluginUtil() {

    }

    public static Map<String, List<IForestryModule>> getForestryModules() {
        Map<String, List<IForestryModule>> modules = new LinkedHashMap<>();

        for (String name : annotated(Type.getType(ForestryModule.class))) {
            try {
                IForestryModule module = Class.forName(name).asSubclass(IForestryModule.class).getConstructor().newInstance();
                ForestryModule info = module.getClass().getAnnotation(ForestryModule.class);
                modules.computeIfAbsent(info.containerID(), k -> new ArrayList<>()).add(module);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Failed to load " + name, e);
            }
        }

        return modules;
    }

    public static void loadFeatureProviders() {
        for (String provider : annotated(Type.getType(FeatureProvider.class))) {
            try {
                Class.forName(provider);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Failed to load " + provider, e);
            }
        }
    }

    private static Set<String> annotated(Type annotationType) {
        // Stable order
        Set<String> names = new HashSet<>();

        for (ModFileScanData scanData : ModList.get().getAllScanData()) {
            Set<ModFileScanData.AnnotationData> annotationData = scanData.getAnnotations();

            for (ModFileScanData.AnnotationData data : annotationData) {
                if (!data.annotationType().equals(annotationType)) {
                    continue;
                }

                names.add(data.memberName());
            }
        }

        return names;
    }
}

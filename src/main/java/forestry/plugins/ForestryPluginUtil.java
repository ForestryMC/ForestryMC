package forestry.plugins;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraftforge.fml.common.discovery.ASMDataTable;

import forestry.core.utils.Log;

public class ForestryPluginUtil {
	private ForestryPluginUtil() {

	}

	public static List<IForestryPlugin> getForestryPlugins(@Nonnull ASMDataTable asmDataTable) {
		return getInstances(asmDataTable, ForestryPlugin.class, IForestryPlugin.class);
	}

	private static <T> List<T> getInstances(@Nonnull ASMDataTable asmDataTable, Class annotationClass, Class<T> instanceClass) {
		String annotationClassName = annotationClass.getCanonicalName();
		Set<ASMDataTable.ASMData> asmDatas = asmDataTable.getAll(annotationClassName);
		List<T> instances = new ArrayList<>();
		for (ASMDataTable.ASMData asmData : asmDatas) {
			try {
				Class<?> asmClass = Class.forName(asmData.getClassName());
				Class<? extends T> asmInstanceClass = asmClass.asSubclass(instanceClass);
				T instance = asmInstanceClass.newInstance();
				instances.add(instance);
			} catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
				Log.error("Failed to load: {}", asmData.getClassName(), e);
			}
		}
		return instances;
	}
}

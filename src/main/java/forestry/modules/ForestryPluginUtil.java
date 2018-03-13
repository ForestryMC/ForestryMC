package forestry.modules;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

import net.minecraftforge.fml.common.discovery.ASMDataTable;

import forestry.api.modules.ForestryModule;
import forestry.api.modules.IForestryModule;
import forestry.core.utils.Log;

public class ForestryPluginUtil {
	private ForestryPluginUtil() {

	}

	public static Map<String, List<IForestryModule>> getForestryModules(ASMDataTable asmDataTable) {
		List<IForestryModule> instances = getInstances(asmDataTable, ForestryModule.class, IForestryModule.class);
		Map<String, List<IForestryModule>> modules = new LinkedHashMap<>();
		for (IForestryModule module : instances) {
			ForestryModule info = module.getClass().getAnnotation(ForestryModule.class);
			modules.computeIfAbsent(info.containerID(), k -> new ArrayList<>()).add(module);
		}
		return modules;
	}


	public static String getComment(IForestryModule module) {
		ForestryModule info = module.getClass().getAnnotation(ForestryModule.class);

		String comment = I18n.translateToLocal(info.unlocalizedDescription());
		Set<ResourceLocation> dependencies = module.getDependencyUids();
		if (!dependencies.isEmpty()) {
			Iterator<ResourceLocation> iDependencies = dependencies.iterator();

			StringBuilder builder = new StringBuilder(comment);
			builder.append("\n");
			builder.append("Dependencies: [ ");
			builder.append(iDependencies.next());
			while (iDependencies.hasNext()) {
				ResourceLocation uid = iDependencies.next();
				builder.append(", ").append(uid.toString());
			}
			builder.append(" ]");
			comment = builder.toString();
		}
		return comment;
	}

	private static <T> List<T> getInstances(ASMDataTable asmDataTable, Class annotationClass, Class<T> instanceClass) {
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

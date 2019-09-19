//package forestry.modules.features;
//
//import javax.annotation.Nullable;
//import java.util.function.Supplier;
//
//import net.minecraftforge.event.RegistryEvent;
//import net.minecraftforge.registries.IForgeRegistryEntry;
//
//import forestry.core.config.Constants;
//
//public class FeatureFluid implements IFluidFeature {
//	private final String moduleID;
//	private final String identifier;
//
//	public FeatureFluid(String moduleID, String identifier) {
//		this.moduleID = moduleID;
//		this.identifier = identifier;
//	}
//
//	@Override
//	public void setFluid(FluidType fluid) {
//
//	}
//
//	@Nullable
//	@Override
//	public FluidType getFluid() {
//		return null;
//	}
//
//	@Override
//	public boolean hasFluid() {
//		return false;
//	}
//
//	@Override
//	public String getIdentifier() {
//		return null;
//	}
//
//	@Override
//	public Supplier<FluidType> getConstructor() {
//		return null;
//	}
//
//	@Override
//	public FeatureType getType() {
//		return FeatureType.FLUID;
//	}
//
//	@Override
//	public String getModId() {
//		return Constants.MOD_ID;
//	}
//
//	@Override
//	public String getModuleId() {
//		return moduleID;
//	}
//
//	@Override
//	public <T extends IForgeRegistryEntry<T>> void register(RegistryEvent.Register<T> event) {
//
//	}
//}

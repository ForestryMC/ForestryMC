package forestry.api.registration;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

import net.minecraftforge.registries.IForgeRegistryEntry;

import net.minecraftforge.fml.RegistryObject;

import forestry.core.utils.FieldsAreNonnullByDefault;

@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
public class WrappedRegistryObject<T extends IForgeRegistryEntry<? super T>> implements Supplier<T>, INamedEntry {

	protected final RegistryObject<T> registryObject;

	protected WrappedRegistryObject(RegistryObject<T> registryObject) {
		this.registryObject = registryObject;
	}

	@Nonnull
	@Override
	public T get() {
		return registryObject.get();
	}

	@Override
	public String getInternalRegistryName() {
		return registryObject.getId().getPath();
	}
}

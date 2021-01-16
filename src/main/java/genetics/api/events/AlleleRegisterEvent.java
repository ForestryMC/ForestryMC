package genetics.api.events;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

import genetics.api.GeneticsAPI;
import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleRegistry;

public class AlleleRegisterEvent extends Event /*implements IContextSetter*/ {

	private final IAlleleRegistry alleleRegistry;
	private final IForgeRegistry<IAllele> registry;

	public AlleleRegisterEvent() {
		this.alleleRegistry = GeneticsAPI.apiInstance.getAlleleRegistry();
		this.registry = RegistryManager.ACTIVE.getRegistry(IAllele.class);
	}

	public IAlleleRegistry getAlleleRegistry() {
		return alleleRegistry;
	}

	public IForgeRegistry<IAllele> getRegistry() {
		return registry;
	}
}

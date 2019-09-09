package forestry.core.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import java.util.Optional;

import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.functions.ILootFunction;

import genetics.api.GeneticsAPI;
import genetics.api.alleles.IAllele;
import genetics.api.individual.IIndividual;
import genetics.api.organism.IOrganismType;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.IRootDefinition;

//TODO - loot tables now different
public class SetSpeciesNBT implements ILootFunction {
	private final String speciesUid;

	public SetSpeciesNBT(String speciesUid) {
		this.speciesUid = speciesUid;
	}

	@Override
	public ItemStack apply(ItemStack stack, LootContext context) {
		IRootDefinition<IIndividualRoot<IIndividual>> definition = GeneticsAPI.apiInstance.getRootHelper().getSpeciesRoot(stack);
		if (definition.isRootPresent()) {
			IIndividualRoot<IIndividual> speciesRoot = definition.get();
			Optional<IOrganismType> speciesType = speciesRoot.getTypes().getType(stack);
			if (speciesType.isPresent()) {
				IAllele[] template = speciesRoot.getTemplates().getTemplate(speciesUid);
				if (template.length > 0) {
					IIndividual individual = speciesRoot.templateAsIndividual(template);
					return speciesRoot.getTypes().createStack(individual, speciesType.get());
				}
			}
		}
		return stack;
	}

	public static class Serializer extends ILootFunction.Serializer<SetSpeciesNBT> {
		public Serializer() {
			super(new ResourceLocation("set_species_nbt"), SetSpeciesNBT.class);
		}

		@Override
		public void serialize(JsonObject object, SetSpeciesNBT functionClazz, JsonSerializationContext serializationContext) {
			object.addProperty("speciesUid", functionClazz.speciesUid);
		}

		@Override
		public SetSpeciesNBT deserialize(JsonObject object, JsonDeserializationContext deserializationContext) {
			String speciesUid = JSONUtils.getString(object, "speciesUid");
			return new SetSpeciesNBT(speciesUid);
		}
	}
}
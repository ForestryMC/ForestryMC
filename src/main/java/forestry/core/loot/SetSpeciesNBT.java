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

	private SetSpeciesNBT(String speciesUid) {
		this.speciesUid = speciesUid;
	}

	@Override
	public ItemStack apply(ItemStack stack, LootContext context) {
		IRootDefinition<IIndividualRoot<IIndividual>> definition = GeneticsAPI.apiInstance.getRootHelper().getSpeciesRoot(stack);
		return definition.map((root) -> {
			Optional<IOrganismType> speciesType = root.getType(stack);
			return speciesType.map((type) -> {
				IAllele[] template = root.getTemplate(speciesUid);
				if (template.length > 0) {
					IIndividual individual = root.templateAsIndividual(template);
					return root.createStack(individual, speciesType.get());
				}
				return stack;
			}).orElse(stack);
		}).orElse(stack);
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
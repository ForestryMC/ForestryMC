package forestry.core.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import java.util.Optional;

import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import genetics.api.alleles.IAllele;
import genetics.api.individual.IIndividual;
import genetics.api.individual.ISpeciesDefinition;
import genetics.api.organism.IOrganismType;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.IRootDefinition;

import genetics.utils.RootUtils;

public class OrganismFunction extends LootFunction {
	private final ResourceLocation speciesUid;

	private OrganismFunction(ILootCondition[] conditions, ResourceLocation speciesUid) {
		super(conditions);
		this.speciesUid = speciesUid;
	}

	public static LootFunction.Builder fromDefinition(ISpeciesDefinition definition) {
		return fromUID(definition.getSpecies().getRegistryName());
	}

	public static LootFunction.Builder fromUID(ResourceLocation speciesUid) {
		return builder((conditions) -> new OrganismFunction(conditions, speciesUid));
	}

	@Override
	protected ItemStack doApply(ItemStack stack, LootContext lootContext) {
		IRootDefinition<IIndividualRoot<IIndividual>> definition = RootUtils.getRoot(stack);
		return definition.map((root) -> {
			Optional<IOrganismType> speciesType = root.getType(stack);
			return speciesType.map((type) -> {
				IAllele[] template = root.getTemplate(speciesUid.toString());
				if (template.length > 0) {
					IIndividual individual = root.templateAsIndividual(template);
					return root.createStack(individual, speciesType.get());
				}
				return stack;
			}).orElse(stack);
		}).orElse(stack);
	}

	public static class Serializer extends LootFunction.Serializer<OrganismFunction> {
		public Serializer() {
			super(new ResourceLocation("set_species_nbt"), OrganismFunction.class);
		}

		@Override
		public void serialize(JsonObject object, OrganismFunction functionClazz, JsonSerializationContext serializationContext) {
			object.addProperty("speciesUid", functionClazz.speciesUid.toString());
		}

		@Override
		public OrganismFunction deserialize(JsonObject object, JsonDeserializationContext jsonDeserializationContext, ILootCondition[] conditions) {
			String speciesUid = JSONUtils.getString(object, "speciesUid");
			return new OrganismFunction(conditions, new ResourceLocation(speciesUid));
		}
	}
}
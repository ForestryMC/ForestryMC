package forestry.core.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import java.util.Optional;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;

import genetics.api.alleles.IAllele;
import genetics.api.individual.IIndividual;
import genetics.api.individual.ISpeciesDefinition;
import genetics.api.organism.IOrganismType;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.IRootDefinition;
import genetics.utils.RootUtils;

/**
 * Loot function to add genetic information, an organism, to the item stack.
 */
public class OrganismFunction extends LootItemConditionalFunction {
	public static LootItemFunctionType type;

	private final ResourceLocation speciesUid;

	private OrganismFunction(LootItemCondition[] conditions, ResourceLocation speciesUid) {
		super(conditions);
		this.speciesUid = speciesUid;
	}

	public static LootItemConditionalFunction.Builder<?> fromDefinition(ISpeciesDefinition<?> definition) {
		return fromUID(definition.getSpecies().getRegistryName());
	}

	public static LootItemConditionalFunction.Builder<?> fromUID(ResourceLocation speciesUid) {
		return simpleBuilder((conditions) -> new OrganismFunction(conditions, speciesUid));
	}

	@Override
	protected ItemStack run(ItemStack stack, LootContext lootContext) {
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

	@Override
	public LootItemFunctionType getType() {
		return type;
	}

	public static class Serializer extends LootItemConditionalFunction.Serializer<OrganismFunction> {

		@Override
		public void serialize(JsonObject object, OrganismFunction function, JsonSerializationContext context) {
			super.serialize(object, function, context);
			object.addProperty("speciesUid", function.speciesUid.toString());
		}

		@Override
		public OrganismFunction deserialize(JsonObject object, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] conditions) {
			String speciesUid = GsonHelper.getAsString(object, "speciesUid");
			return new OrganismFunction(conditions, new ResourceLocation(speciesUid));
		}
	}
}
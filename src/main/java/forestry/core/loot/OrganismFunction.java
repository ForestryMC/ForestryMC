package forestry.core.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import genetics.api.alleles.IAllele;
import genetics.api.individual.IIndividual;
import genetics.api.individual.ISpeciesDefinition;
import genetics.api.organism.IOrganismType;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.IRootDefinition;
import genetics.utils.RootUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import java.util.Optional;

public class OrganismFunction extends LootFunction {
    public static LootFunctionType type;

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

    @Override
    public LootFunctionType getFunctionType() {
        return type;
    }

    public static class Serializer extends LootFunction.Serializer<OrganismFunction> {
        @Override
        public void serialize(JsonObject object, OrganismFunction function, JsonSerializationContext context) {
            super.serialize(object, function, context);
            object.addProperty("speciesUid", function.speciesUid.toString());
        }

        @Override
        public OrganismFunction deserialize(
                JsonObject object,
                JsonDeserializationContext jsonDeserializationContext,
                ILootCondition[] conditions
        ) {
            String speciesUid = JSONUtils.getString(object, "speciesUid");
            return new OrganismFunction(conditions, new ResourceLocation(speciesUid));
        }
    }
}
package forestry.core.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.ISpeciesRoot;
import forestry.api.genetics.ISpeciesType;

public class SetSpeciesNBT extends LootFunction {
	private final String speciesUid;

	public SetSpeciesNBT(LootCondition[] conditionsIn, String speciesUid) {
		super(conditionsIn);
		this.speciesUid = speciesUid;
	}

	@Override
	public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
		ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(stack);
		if (speciesRoot != null) {
			ISpeciesType speciesType = speciesRoot.getType(stack);
			if (speciesType != null) {
				IAllele[] template = speciesRoot.getTemplate(speciesUid);
				if (template != null) {
					IIndividual individual = speciesRoot.templateAsIndividual(template);
					return speciesRoot.getMemberStack(individual, speciesType);
				}
			}
		}
		return stack;
	}

	public static class Serializer extends LootFunction.Serializer<SetSpeciesNBT> {
		public Serializer() {
			super(new ResourceLocation("set_species_nbt"), SetSpeciesNBT.class);
		}

		@Override
		public void serialize(JsonObject object, SetSpeciesNBT functionClazz, JsonSerializationContext serializationContext) {
			object.addProperty("speciesUid", functionClazz.speciesUid);
		}

		@Override
		public SetSpeciesNBT deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootCondition[] conditionsIn) {
			String speciesUid = JsonUtils.getString(object, "speciesUid");
			return new SetSpeciesNBT(conditionsIn, speciesUid);
		}
	}
}
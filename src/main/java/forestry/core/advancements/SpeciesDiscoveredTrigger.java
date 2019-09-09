package forestry.core.advancements;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import genetics.api.GeneticsAPI;
import genetics.api.alleles.IAllele;

import forestry.core.config.Constants;

public class SpeciesDiscoveredTrigger implements ICriterionTrigger<SpeciesDiscoveredTrigger.Instance> {
	private static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "species_discovered");

	public static final SpeciesDiscoveredTrigger INSTANCE = new SpeciesDiscoveredTrigger();

	private final Map<PlayerAdvancements, Listeners> listeners = Maps.newHashMap();

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	public void addListener(PlayerAdvancements advancements, ICriterionTrigger.Listener<Instance> listener) {
		Listeners listeners = this.listeners.get(advancements);

		if (listeners == null) {
			listeners = new Listeners(advancements);
			this.listeners.put(advancements, listeners);
		}

		listeners.add(listener);
	}

	public void removeListener(PlayerAdvancements advancements, ICriterionTrigger.Listener<Instance> listener) {
		Listeners listeners = this.listeners.get(advancements);

		if (listeners != null) {
			listeners.remove(listener);

			if (listeners.isEmpty()) {
				this.listeners.remove(advancements);
			}
		}
	}

	@Override
	public void removeAllListeners(PlayerAdvancements advancements) {
		this.listeners.remove(advancements);
	}

	@Override
	public Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
		String uid = JSONUtils.getString(json, "uid");
		Optional<IAllele> allele = GeneticsAPI.apiInstance.getAlleleRegistry().getAllele(uid);

		if (!allele.isPresent()) {
			throw new JsonSyntaxException("Unknown allele '" + uid + "'");
		} else {
			return new Instance(allele.get());
		}
	}

	public void trigger(ServerPlayerEntity player, IAllele allele) {
		Listeners listeners = this.listeners.get(player.getAdvancements());

		if (listeners != null) {
			listeners.trigger(allele);
		}
	}

	public static class Instance extends CriterionInstance {
		private final IAllele allele;

		public Instance(IAllele allele) {
			super(ID);
			this.allele = allele;
		}

		public boolean test(IAllele allele) {
			return this.allele == allele;
		}
	}

	private static class Listeners {
		private final PlayerAdvancements playerAdvancements;
		private final Set<Listener<Instance>> listeners = Sets.newHashSet();

		public Listeners(PlayerAdvancements playerAdvancementsIn) {
			this.playerAdvancements = playerAdvancementsIn;
		}

		public boolean isEmpty() {
			return this.listeners.isEmpty();
		}

		public void add(ICriterionTrigger.Listener<Instance> listener) {
			this.listeners.add(listener);
		}

		public void remove(ICriterionTrigger.Listener<Instance> listener) {
			this.listeners.remove(listener);
		}

		public void trigger(IAllele allele) {
			List<Listener<Instance>> list = null;

			for (ICriterionTrigger.Listener<Instance> listener : this.listeners) {
				Instance instance = listener.getCriterionInstance();
				if (instance.test(allele)) {
					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(listener);
				}
			}

			if (list != null) {
				for (ICriterionTrigger.Listener<Instance> listener : list) {
					listener.grantCriterion(this.playerAdvancements);
				}
			}
		}
	}
}

package forestry.apiculture;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import forestry.api.genetics.alyzer.IAlleleDisplayHelper;
import forestry.api.genetics.alyzer.IAlyzerDisplayProvider;
import forestry.apiculture.genetics.IGeneticTooltipProvider;

import genetics.api.individual.IIndividual;
import genetics.api.organism.IOrganismType;

public class DisplayHelper implements IAlleleDisplayHelper {
	private final Map<String, PriorityQueue<OrderedPair<IGeneticTooltipProvider<?>>>> tooltips = new HashMap<>();
	private final Map<String, PriorityQueue<OrderedPair<IAlyzerDisplayProvider<?>>>> alyzers = new HashMap<>();

	@Nullable
	private static DisplayHelper instance;

	public static DisplayHelper getInstance() {
		if (instance == null) {
			instance = new DisplayHelper();
		}
		return instance;
	}

	@Override
	public void addTooltip(IGeneticTooltipProvider<?> provider, String rootUID, int orderingInfo) {
		tooltips.computeIfAbsent(rootUID, (root) -> new PriorityQueue<>()).add(new OrderedPair<>(provider, orderingInfo, null));
	}

	@Override
	public void addTooltip(IGeneticTooltipProvider<?> provider, String rootUID, int orderingInfo, Predicate<IOrganismType> typeFilter) {
		tooltips.computeIfAbsent(rootUID, (root) -> new PriorityQueue<>()).add(new OrderedPair<>(provider, orderingInfo, typeFilter));
	}

	@Override
	public void addAlyzer(IAlyzerDisplayProvider<?> provider, String rootUID, int orderingInfo) {
		alyzers.computeIfAbsent(rootUID, (root) -> new PriorityQueue<>()).add(new OrderedPair<>(provider, orderingInfo, null));
	}

	@Override
	public void addAlyzer(IAlyzerDisplayProvider<?> provider, String rootUID, int orderingInfo, Predicate<IOrganismType> typeFilter) {
		alyzers.computeIfAbsent(rootUID, (root) -> new PriorityQueue<>()).add(new OrderedPair<>(provider, orderingInfo, typeFilter));
	}

	public <I extends IIndividual> Collection<IGeneticTooltipProvider<I>> getTooltips(String rootUID, IOrganismType type) {
		if (!tooltips.containsKey(rootUID)) {
			return Collections.emptyList();
		}
		return tooltips.get(rootUID).stream()
				.filter((value) -> value.hasValue(type))
				.map((value) -> (IGeneticTooltipProvider<I>) value.value)
				.collect(Collectors.toList());
	}

	public <I extends IIndividual> Collection<IAlyzerDisplayProvider<I>> getAlyzers(String rootUID, IOrganismType type) {
		if (!alyzers.containsKey(rootUID)) {
			return Collections.emptyList();
		}
		return alyzers.get(rootUID).stream()
				.filter((value) -> value.hasValue(type))
				.map((value) -> (IAlyzerDisplayProvider<I>) value.value)
				.collect(Collectors.toList());
	}

	private static class OrderedPair<T> implements Comparable<OrderedPair<T>> {
		private final T value;
		private final int info;
		@Nullable
		private final Predicate<IOrganismType> filter;

		private OrderedPair(T value, int info, @Nullable Predicate<IOrganismType> filter) {
			this.value = value;
			this.info = info;
			this.filter = filter;
		}

		public boolean hasValue(IOrganismType type) {
			return filter == null || filter.test(type);
		}

		@Override
		public int compareTo(OrderedPair<T> o) {
			return info - o.info;
		}
	}
}

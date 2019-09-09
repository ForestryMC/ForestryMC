package genetics.api.root;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import net.minecraft.item.ItemStack;

import genetics.api.GeneticsAPI;
import genetics.api.IGeneticFactory;
import genetics.api.alleles.IAllele;
import genetics.api.individual.IGenome;
import genetics.api.individual.IIndividual;
import genetics.api.individual.IKaryotype;
import genetics.api.organism.IOrganismType;
import genetics.api.organism.IOrganismTypes;
import genetics.api.root.components.ComponentKey;
import genetics.api.root.components.ComponentKeys;
import genetics.api.root.components.IRootComponent;
import genetics.api.root.components.IRootComponentContainer;
import genetics.api.root.translator.IIndividualTranslator;

import genetics.individual.RootDefinition;
import genetics.root.RootComponentContainer;

/**
 * Abstract implementation of the {@link IIndividualRoot} interface.
 *
 * @param <I> The type of the individual that this root provides.
 */
public abstract class IndividualRoot<I extends IIndividual> implements IIndividualRoot<I> {
	protected final IRootDefinition<? extends IIndividualRoot<I>> definition;
	protected final IOrganismTypes<I> types;
	protected final ITemplateContainer<I> templates;
	protected final IKaryotype karyotype;
	protected final String uid;
	private ImmutableList<I> individualTemplates;
	private I defaultMember;
	private final IRootComponentContainer<I> components;
	@Nullable
	private IDisplayHelper<I> displayHelper;

	public IndividualRoot(IRootContext<I> context) {
		this.uid = context.getKaryotype().getUID();
		this.definition = (IRootDefinition<? extends IIndividualRoot<I>>) context.getDefinition();
		//noinspection unchecked
		((RootDefinition<IIndividualRoot<I>>) this.definition).setRoot(this);
		this.karyotype = context.getKaryotype();
		this.components = new RootComponentContainer<>(context.createComponents(this), context.getComponentListeners(), context.getListeners());
		this.types = components.get(ComponentKeys.TYPES);
		this.templates = components.get(ComponentKeys.TEMPLATES);
		createDefault();
	}

	protected void createDefault() {
		this.defaultMember = create(karyotype.getDefaultGenome());
		ImmutableList.Builder<I> templateBuilder = new ImmutableList.Builder<>();
		for (IAllele[] template : templates.getTemplates()) {
			templateBuilder.add(templateAsIndividual(template));
		}
		this.individualTemplates = templateBuilder.build();
	}

	@Override
	public final String getUID() {
		return uid;
	}

	@Override
	public I templateAsIndividual(IAllele[] templateActive, @Nullable IAllele[] templateInactive) {
		IGenome genome = karyotype.templateAsGenome(templateActive, templateInactive);
		return create(genome);
	}

	@Override
	public I getDefaultMember() {
		return defaultMember;
	}

	@Override
	public List<I> getIndividualTemplates() {
		return individualTemplates;
	}

	@Override
	public Optional<I> create(String templateIdentifier) {
		IAllele[] template = templates.getTemplate(templateIdentifier);
		if (template.length == 0) {
			return Optional.empty();
		}
		return Optional.of(create(karyotype.templateAsGenome(template)));
	}

	@Override
	public ItemStack createStack(IAllele allele, IOrganismType type) {
		Optional<I> optional = create(allele.getRegistryName().toString());
		return optional.map(i -> types.createStack(i, type)).orElse(ItemStack.EMPTY);
	}

	@Override
	public boolean isMember(ItemStack stack) {
		return types.getType(stack).isPresent();
	}

	@Override
	public ITemplateContainer getTemplates() {
		return templates;
	}

	@Override
	public IKaryotype getKaryotype() {
		return karyotype;
	}

	@Override
	@SuppressWarnings("unchecked")
	public IIndividualTranslator<I> getTranslator() {
		Optional<IIndividualTranslator> translator = getComponentSafe(ComponentKeys.TRANSLATORS);
		if (!translator.isPresent()) {
			throw new IllegalStateException(String.format("No translator component was added to the root with the uid '%s'.", getUID()));
		}
		return (IIndividualTranslator<I>) translator.get();
	}

	@Override
	public IOrganismTypes<I> getTypes() {
		return types;
	}

	@Override
	public IRootDefinition<? extends IIndividualRoot<I>> getDefinition() {
		return definition;
	}

	@Override
	public boolean hasComponent(ComponentKey key) {
		return components.has(key);
	}

	@Override
	public <C extends IRootComponent<I>> Optional<C> getComponentSafe(ComponentKey key) {
		return components.getSafe(key);
	}

	@Override
	public <C extends IRootComponent<I>> C getComponent(ComponentKey key) {
		return components.get(key);
	}

	@Override
	public IRootComponentContainer<I> getComponentContainer() {
		return components;
	}

	@Override
	@SuppressWarnings("unchecked")
	public IDisplayHelper getDisplayHelper() {
		if (displayHelper == null) {
			IGeneticFactory geneticFactory = GeneticsAPI.apiInstance.getGeneticFactory();
			displayHelper = geneticFactory.createDisplayHelper(this);
		}
		return displayHelper;
	}
}

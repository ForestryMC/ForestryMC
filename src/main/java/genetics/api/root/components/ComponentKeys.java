package genetics.api.root.components;

import genetics.api.mutation.IMutationContainer;
import genetics.api.organism.IOrganismTypes;
import genetics.api.root.ITemplateContainer;
import genetics.api.root.translator.IIndividualTranslator;

/**
 * Every default component key that the genetic api provides.
 */
public class ComponentKeys {
	/* Components that are added automatically at the creation of the IIndividualRootBuilder. */
	public static final ComponentKey<ITemplateContainer> TEMPLATES = ComponentKey.create("templates", ITemplateContainer.class);
	public static final ComponentKey<IOrganismTypes> TYPES = ComponentKey.create("types", IOrganismTypes.class, DefaultStage.SETUP);
	/* Components that are optional. */
	public static final ComponentKey<IIndividualTranslator> TRANSLATORS = ComponentKey.create("translators", IIndividualTranslator.class, DefaultStage.SETUP);
	public static final ComponentKey<IMutationContainer> MUTATIONS = ComponentKey.create("mutations", IMutationContainer.class);

	private ComponentKeys() {
	}
}

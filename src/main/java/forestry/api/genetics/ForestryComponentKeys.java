package forestry.api.genetics;

import genetics.api.root.components.ComponentKey;
import genetics.api.root.components.DefaultStage;

public class ForestryComponentKeys {
	public static final ComponentKey<IResearchHandler> RESEARCH = ComponentKey.create("research", IResearchHandler.class, DefaultStage.SETUP);

	private ForestryComponentKeys() {
	}
}

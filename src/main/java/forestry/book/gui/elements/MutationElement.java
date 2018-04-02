package forestry.book.gui.elements;

import java.util.Collection;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import forestry.api.genetics.IMutation;
import forestry.api.genetics.ISpeciesRoot;
import forestry.api.gui.GuiElementAlignment;
import forestry.core.config.Constants;
import forestry.core.gui.Drawable;
import forestry.core.gui.elements.layouts.ElementGroup;

public class MutationElement extends SelectionElement {
	private static final ResourceLocation BOOK_TEXTURE = new ResourceLocation(Constants.MOD_ID, Constants.TEXTURE_PATH_GUI + "/atlas.png");
	private static final Drawable SLOT = new Drawable(BOOK_TEXTURE, 0, 223, 18, 18);
	private static final Drawable MUTATION_PLUS = new Drawable(BOOK_TEXTURE, 0, 241, 15, 15);
	private static final Drawable MUTATION_ARROW = new Drawable(BOOK_TEXTURE, 15, 241, 18, 15);

	private final IMutation[] mutations;
	private final ElementGroup selected;

	public MutationElement(int xPos, int yPos, IMutation[] mutations, boolean withTitle) {
		super(xPos, yPos, 108, 20 + (withTitle ? 12 : 0) + (mutations.length > 0 ? 16 : 0), mutations.length > 1);

		this.mutations = mutations;

		setAlign(GuiElementAlignment.TOP_CENTER);

		int gridStartY = 2;
		//Title
		if (withTitle) {
			text(TextFormatting.DARK_GRAY + "Bee Breeding", GuiElementAlignment.TOP_CENTER);
			gridStartY = 12;
		}

		selected = panel(0, gridStartY, width, height);

		drawable(0, gridStartY, SLOT);
		drawable(21, 2 + gridStartY, MUTATION_PLUS);
		drawable(39, gridStartY, SLOT);
		drawable(84, gridStartY, SLOT);

		updateIndex(0);
	}

	@Override
	protected void updateIndex(int index) {
		this.index = index;
		selected.clear();

		IMutation mutation = mutations[index];
		ISpeciesRoot root = mutation.getRoot();
		//
		Collection<String> conditions = mutation.getSpecialConditions();
		String conditionText;
		if (!conditions.isEmpty()) {
			conditionText = String.format("[%.0f%%]", mutation.getBaseChance());
		} else {
			conditionText = String.format("%.0f%%", mutation.getBaseChance());
		}
		selected.text(58, conditionText, GuiElementAlignment.TOP_LEFT, 0).addTooltip(conditions);
		selected.drawable(62, 6, MUTATION_ARROW).addTooltip(conditions);
		//
		selected.item(1, 1, root.getMemberStack(mutation.getAllele0(), root.getTypeForMutation(0)));
		selected.item(40, 1, root.getMemberStack(mutation.getAllele1(), root.getTypeForMutation(1)));
		selected.item(85, 1, root.getMemberStack(mutation.getTemplate(), root.getTypeForMutation(2)));

		if (text != null) {
			text.clear();
			text.text(TextFormatting.BLACK.toString() + (index + 1) + "/" + mutations.length, GuiElementAlignment.BOTTOM_CENTER, 0).setYPosition(2);
		}
		if (leftButton != null) {
			leftButton.setEnabled(index > 0);
		}
		if (rightButton != null) {
			rightButton.setEnabled(index < mutations.length - 1);
		}
	}
}

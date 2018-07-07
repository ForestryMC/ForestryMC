package forestry.book.gui.elements;

import java.util.Collection;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.genetics.IMutation;
import forestry.api.genetics.ISpeciesRoot;
import forestry.api.gui.GuiConstants;
import forestry.api.gui.GuiElementAlignment;
import forestry.book.gui.GuiForesterBook;
import forestry.core.gui.Drawable;

@SideOnly(Side.CLIENT)
public class MutationElement extends SelectionElement<IMutation> {
	private static final Drawable SLOT = new Drawable(GuiForesterBook.TEXTURE, 0, 223, 18, 18);
	private static final Drawable MUTATION_PLUS = new Drawable(GuiForesterBook.TEXTURE, 0, 241, 15, 15);
	private static final Drawable MUTATION_ARROW = new Drawable(GuiForesterBook.TEXTURE, 15, 241, 18, 15);

	public MutationElement(int xPos, int yPos, IMutation[] mutations) {
		super(xPos, yPos, 108, 20, mutations, 2);

		setAlign(GuiElementAlignment.TOP_CENTER);

		drawable(0, 2, SLOT);
		drawable(21, 4, MUTATION_PLUS);
		drawable(39, 2, SLOT);
		drawable(84, 2, SLOT);
		add(selectedElement);
		setIndex(0);
	}

	@Override
	protected void onIndexUpdate(int index, IMutation mutation) {
		ISpeciesRoot root = mutation.getRoot();
		//
		Collection<String> conditions = mutation.getSpecialConditions();
		String conditionText;
		if (!conditions.isEmpty()) {
			conditionText = String.format("[%.0f%%]", mutation.getBaseChance());
		} else {
			conditionText = String.format("%.0f%%", mutation.getBaseChance());
		}
		selectedElement.label(conditionText, 58, 0, -1, 12, GuiElementAlignment.TOP_LEFT, GuiConstants.BLACK_STYLE).addTooltip(conditions);
		selectedElement.drawable(62, 6, MUTATION_ARROW).addTooltip(conditions);
		//
		selectedElement.item(1, 1, root.getMemberStack(mutation.getAllele0(), root.getTypeForMutation(0)));
		selectedElement.item(40, 1, root.getMemberStack(mutation.getAllele1(), root.getTypeForMutation(1)));
		selectedElement.item(85, 1, root.getMemberStack(mutation.getTemplate(), root.getTypeForMutation(2)));
	}
}

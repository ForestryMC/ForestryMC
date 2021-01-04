package forestry.book.gui.elements;

import forestry.api.genetics.IForestrySpeciesRoot;
import forestry.book.gui.GuiForesterBook;
import forestry.core.gui.Drawable;
import forestry.core.gui.elements.lib.GuiConstants;
import forestry.core.gui.elements.lib.GuiElementAlignment;
import genetics.api.mutation.IMutation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collection;

@OnlyIn(Dist.CLIENT)
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
        IForestrySpeciesRoot root = (IForestrySpeciesRoot) mutation.getRoot();
        Collection<ITextComponent> conditions = mutation.getSpecialConditions();
        String conditionText;
        if (!conditions.isEmpty()) {
            conditionText = String.format("[%.0f%%]", mutation.getBaseChance());
        } else {
            conditionText = String.format("%.0f%%", mutation.getBaseChance());
        }

        selectedElement.label(new StringTextComponent(conditionText))
                       .setStyle(GuiConstants.BLACK_STYLE)
                       .setFitText(true)
                       .addTooltip(conditions)
                       .setXPosition(58);
        selectedElement.drawable(62, 6, MUTATION_ARROW).addTooltip(conditions);
        //
        selectedElement.item(1, 1, root.createStack(mutation.getFirstParent(), root.getTypeForMutation(0)));
        selectedElement.item(40, 1, root.createStack(mutation.getSecondParent(), root.getTypeForMutation(1)));
        selectedElement.item(85, 1, root.createStack(mutation.getResultingSpecies(), root.getTypeForMutation(2)));
    }
}

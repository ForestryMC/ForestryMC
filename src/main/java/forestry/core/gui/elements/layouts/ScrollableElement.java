package forestry.core.gui.elements.layouts;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IAlleleTolerance;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IMutation;
import forestry.api.gui.GuiElementAlignment;
import forestry.api.gui.IElementGenetic;
import forestry.api.gui.IElementGroup;
import forestry.api.gui.IElementLayout;
import forestry.api.gui.IGuiElement;
import forestry.core.gui.elements.GuiElementFactory;
import forestry.core.gui.widgets.IScrollable;
import forestry.core.render.ColourProperties;
import forestry.core.translation.Translator;

import org.lwjgl.opengl.GL11;

public class ScrollableElement extends VerticalLayout implements IScrollable, IElementGenetic {
	private final List<IGuiElement> visibleElements = new ArrayList<>();
	private final int sizeY;
	private int elementOffset;
	private float scrollPercentage;
	private float step;

	public ScrollableElement(int xPos, int yPos, int width, int height) {
		super(xPos, yPos, width);
		this.sizeY = height;
	}

	public void updateVisibleElements(int offset) {
		visibleElements.clear();
		int height = 0;

		int widgetEnd = yPos + sizeY;
		for (int i = 0; i < elements.size(); i++) {
			IGuiElement element = elements.get(i);
			if (i < offset) {
				height += element.getHeight();
				continue;
			}
			elementOffset = -height;
			int elementHeight = element.getY() + element.getHeight() + elementOffset;
			if (elementHeight > widgetEnd) {
				continue;
			}
			visibleElements.add(element);
		}
	}

	public int getInvisibleElementCount() {
		step = (12 * 0.5F);
		return (int) ((getHeight() - sizeY) / (step));
	}

	@Override
	public void drawElement(int mouseX, int mouseY) {
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution res = new ScaledResolution(mc);
		double scaleWidth = mc.displayWidth / res.getScaledWidth_double();
		double scaleHeight = mc.displayHeight / res.getScaledHeight_double();
		GL11.glScissor((int) ((getAbsoluteX()) * scaleWidth), (int) (mc.displayHeight - ((getAbsoluteY() + sizeY) * scaleHeight)), (int) (width * scaleWidth), (int) (sizeY * scaleHeight));
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.0F, -((int) scrollPercentage), 0.0F);
		for (IGuiElement element : elements) {
			element.draw(mouseX, mouseY);
		}
		GlStateManager.popMatrix();
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}

	@Override
	public List<String> getTooltip(int mouseX, int mouseY) {
		mouseX -= getX();
		mouseY -= getY();
		for (IGuiElement element : visibleElements) {
			if (element.isMouseOver()) {
				List<String> toolTip = element.getTooltip(mouseX, mouseY);
				if (!toolTip.isEmpty()) {
					return toolTip;
				}
			}
		}
		return Collections.emptyList();
	}

	@Override
	public void addFertilityInfo(String chromosomeName, IAlleleInteger fertilityAllele, int texOffset) {
		addRow(chromosomeName, GuiElementFactory.INSTANCE.createFertilityInfo(fertilityAllele, texOffset));
	}

	@Override
	public void addToleranceInfo(String chromosomeName, IAlleleTolerance toleranceAllele, IAlleleSpecies species, String text) {
		addRow(chromosomeName, text, species.isDominant());
		addRow("  " + Translator.translateToLocal("for.gui.tolerance"), GuiElementFactory.INSTANCE.createToleranceInfo(toleranceAllele));
	}

	@Override
	public void addMutation(int x, int y, int width, int height, IMutation mutation, IAllele species, IBreedingTracker breedingTracker) {
		IGuiElement element = GuiElementFactory.INSTANCE.createMutation(x, y, width, height, mutation, species, breedingTracker);
		if (element == null) {
			return;
		}
		add(element);
	}

	@Override
	public void addMutationResultant(int x, int y, int width, int height, IMutation mutation, IBreedingTracker breedingTracker) {
		IGuiElement element = GuiElementFactory.INSTANCE.createMutationResultant(x, y, width, height, mutation, breedingTracker);
		if (element == null) {
			return;
		}
		add(element);
	}

	@Override
	public void addRow(String firstText, String secondText, boolean dominant) {
		addRow(firstText, secondText, ColourProperties.INSTANCE.get("gui.screen"), GuiElementFactory.INSTANCE.getColorCoding(dominant));
	}

	@Override
	public final void addAlleleRow(String chromosomeName, IIndividual individual, IChromosomeType chromosome, boolean active) {
		addAlleleRow(chromosomeName, IAllele::getAlleleName, individual, chromosome, active);
	}

	@Override
	public void addRow(String firstText, String secondText, int firstColor, int secondColor) {
		IElementLayout first = addSplitText(width, firstText, GuiElementAlignment.TOP_LEFT, firstColor);
		IElementLayout second = addSplitText(width, secondText, GuiElementAlignment.TOP_LEFT, secondColor);
		addRow(first, second);
	}

	private IElementLayout addSplitText(int width, String text, GuiElementAlignment alignment, int color) {
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		IElementLayout vertical = new VerticalLayout(width);
		for (String splitText : fontRenderer.listFormattedStringToWidth(text, 70)) {
			vertical.text(splitText, alignment, color);
		}
		return vertical;
	}

	private void addRow(String chromosomeName, IGuiElement right) {
		int center = width / 2;
		IGuiElement first = addSplitText(center, chromosomeName, GuiElementAlignment.TOP_LEFT, ColourProperties.INSTANCE.get("gui.screen"));
		addRow(first, right);
	}

	private void addRow(IGuiElement first, IGuiElement second) {
		int center = width / 2;
		IElementGroup panel = new PaneLayout(width, 0);
		if (first.getHeight() > second.getHeight()) {
			first.setAlign(GuiElementAlignment.MIDDLE_LEFT);
		} else if (second.getHeight() > first.getHeight()) {
			second.setAlign(GuiElementAlignment.MIDDLE_LEFT);
		}
		panel.add(first);
		panel.add(second);
		second.setXPosition(center);
		add(panel);
	}

	@SuppressWarnings("unchecked")
	@Override
	public final <A extends IAllele> void addAlleleRow(String chromosomeName, Function<A, String> toString, IIndividual individual, IChromosomeType chromosome, boolean active) {
		A allele;
		if (active) {
			allele = (A) individual.getGenome().getActiveAllele(chromosome);
		} else {
			allele = (A) individual.getGenome().getInactiveAllele(chromosome);
		}
		addRow(chromosomeName, toString.apply(allele), allele.isDominant());
	}

	@Override
	public void onScroll(int value) {
		scrollPercentage = (value * step);
	}

	@Override
	public void clear() {
		remove(Lists.newArrayList(elements));
	}

	@Override
	public boolean isFocused(int mouseX, int mouseY) {
		return isMouseOver(mouseX, mouseY);
	}

	/*@Override
	public int getHeight() {
		return sizeY;
	}*/
}

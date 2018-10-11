package forestry.book.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.book.IBookCategory;
import forestry.api.book.IForesterBook;
import forestry.book.gui.buttons.GuiButtonBookCategory;
import forestry.core.config.Constants;
import forestry.core.gui.Drawable;
import forestry.core.utils.Translator;

@SideOnly(Side.CLIENT)
public class GuiForestryBookCategories extends GuiForesterBook {
	public static final Drawable LOGO = new Drawable(new ResourceLocation(Constants.MOD_ID, "textures/gui/almanac/logo.png"), 0, 0, 256, 58, 256, 58);

	public GuiForestryBookCategories(IForesterBook book) {
		super(book);
	}

	@Override
	public void initGui() {
		super.initGui();
		int x = 0;
		int y = 0;
		for (IBookCategory category : book.getCategories()) {
			if (category.getEntries().isEmpty()) {
				continue;
			}
			buttonList.add(new GuiButtonBookCategory(y * 3 + x, guiLeft + LEFT_PAGE_START_X + x * 36, guiTop + 25 + y * 36, category));
			x++;
			if (x == 3) {
				y++;
				x = 0;
			}
		}
	}

	@Override
	protected boolean hasButtons() {
		return false;
	}

	@Override
	protected void drawText() {
		drawCenteredString(fontRenderer, TextFormatting.UNDERLINE + Translator.translateToLocal("for.gui.book.about.title"), guiLeft + RIGHT_PAGE_START_X + 52, guiTop + PAGE_START_Y, 0xD3D3D3);
		String about = Translator.translateToLocal("for.gui.book.about");
		fontRenderer.drawSplitString(about, guiLeft + RIGHT_PAGE_START_X, guiTop + LEFT_PAGE_START_Y, 108, 0);
		fontRenderer.drawString(Translator.translateToLocal("for.gui.book.about.author"), guiLeft + RIGHT_PAGE_START_X, guiTop + LEFT_PAGE_START_Y + fontRenderer.getWordWrappedHeight(about, 108), 0);
		LOGO.draw(guiLeft + RIGHT_PAGE_START_X, guiTop + LEFT_PAGE_START_Y + 110, 108, 24);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button instanceof GuiButtonBookCategory) {
			GuiButtonBookCategory buttonCategory = (GuiButtonBookCategory) button;
			mc.displayGuiScreen(new GuiForestryBookEntries(book, buttonCategory.category));
		}
	}

	@Override
	protected String getTitle() {
		return Translator.translateToLocal("for.gui.book.categories");
	}
}

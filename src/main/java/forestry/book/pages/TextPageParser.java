package forestry.book.pages;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.oredict.OreDictionary;

import net.minecraftforge.fml.common.registry.ForgeRegistries;

import forestry.api.book.IBookEntry;
import forestry.api.book.IBookPage;
import forestry.api.book.IBookPageFactory;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IMutation;
import forestry.api.genetics.ISpeciesRoot;
import forestry.api.gui.GuiElementAlignment;
import forestry.api.gui.IElementGroup;
import forestry.api.gui.IElementLayout;
import forestry.api.gui.IGuiElement;
import forestry.api.multiblock.IMultiblockBlueprint;
import forestry.api.multiblock.MultiblockBlueprints;
import forestry.api.recipes.ICarpenterRecipe;
import forestry.api.recipes.IDescriptiveRecipe;
import forestry.book.gui.elements.PageLinkIndex;
import forestry.core.config.Constants;
import forestry.core.gui.Drawable;
import forestry.core.gui.elements.DrawableElement;
import forestry.core.gui.elements.GuiElement;
import forestry.core.gui.elements.GuiElementFactory;
import forestry.core.gui.elements.IngredientElement;
import forestry.core.gui.elements.TankElement;
import forestry.core.gui.elements.TextElement;
import forestry.core.gui.elements.layouts.AbstractElementLayout;
import forestry.core.gui.elements.layouts.ElementGroup;
import forestry.core.gui.elements.layouts.PaneLayout;
import forestry.core.gui.elements.layouts.VerticalLayout;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Translator;
import forestry.factory.recipes.CarpenterRecipeManager;

public class TextPageParser implements IBookPageFactory {
	public static final TextPageParser INSTANCE = new TextPageParser();

	private TextPageParser() {
	}

	@Override
	public Collection<IBookPage> load(IBookEntry entry) {
		String text = entry.getLocalizedPages();
		if (text.isEmpty()) {
			return Collections.emptySet();
		}
		text = text.replaceAll("<BR>", "\n");
		text = text.replaceAll("<BR/>", "\n");
		text = text.replaceAll("<p>", "\n<6>\n");
		text = text.replaceAll("<p/>", "\n<6>\n");
		text = text.replaceAll("<PAGE/>", "<3>");
		text = text.replaceAll("<PAGE>", "<3>");
		text = text.replaceAll("<CENTER>", "<4>");
		text = text.replaceAll("<CENTER/>", "<4>");
		text = text.replaceAll("<INDEX>", "<9>");
		List<Image> images = new ArrayList<>();
		List<IRecipe> recipes = new ArrayList<>();
		List<IMultiblockBlueprint> blueprints = new ArrayList<>();
		List<IAlleleSpecies> mutations = new ArrayList<>();
		List<ItemStack> carpenterRecipes = new ArrayList<>();
		List<PageLinkIndex.PageLink> links = new ArrayList<>();

		int linkIndex = 0;
		//Parse all crafting recipes and replace the old position of it with <0>
		String[] recipeArray = text.split("<crafting>");
		for (int i = 1; i < recipeArray.length; i++) {
			String rawText = recipeArray[i];
			int endIndex = rawText.indexOf("</crafting>");
			if (endIndex < 0) {
				continue;
			}
			String rawRecipe = rawText.substring(0, endIndex);
			IRecipe recipe = ForgeRegistries.RECIPES.getValue(new ResourceLocation(rawRecipe));
			if (recipe != null) {
				recipes.add(recipe);
				text = text.replaceFirst(rawRecipe, "<0>\n");
			} else {
				text = text.replaceFirst(rawRecipe, "\n");
			}
		}
		text = text.replaceAll("<crafting>", "");
		text = text.replaceAll("</crafting>", "");
		//Parse all images and replace the old position of it with <1>
		String[] imageArray = text.split("<img>");
		for (int i = 1; i < imageArray.length; i++) {
			String rawText = imageArray[i];
			int endIndex = rawText.indexOf("</img>");
			if (endIndex < 0) {
				continue;
			}
			String rawImage = rawText.substring(0, endIndex);
			Image image = Image.parse(rawImage);
			if (image != null) {
				images.add(image);
				text = text.replaceFirst(rawImage, "<1>\n");
			} else {
				text = text.replaceFirst(rawImage, "\n");
			}
		}
		text = text.replaceAll("<img>", "");
		text = text.replaceAll("</img>", "");

		//Parse all images and replace the old position of it with <5>
		String[] blueprintArray = text.split("<blueprint>");
		for (int i = 1; i < blueprintArray.length; i++) {
			String rawText = blueprintArray[i];
			int endIndex = rawText.indexOf("</blueprint>");
			if (endIndex < 0) {
				continue;
			}
			String rawBlueprint = rawText.substring(0, endIndex);
			IMultiblockBlueprint blueprint = MultiblockBlueprints.blueprints.get(rawBlueprint);
			if (blueprint != null) {
				blueprints.add(blueprint);
				text = text.replaceFirst("<blueprint>" + rawBlueprint + "</blueprint>", "<5>");
			} else {
				text = text.replaceFirst("<blueprint>" + rawBlueprint + "</blueprint>", "\n");
			}
		}

		//Parse all mutation and replace the old position of it with <7>
		String[] mutationArray = text.split("<mutation>");
		for (int i = 1; i < mutationArray.length; i++) {
			String rawText = mutationArray[i];
			int endIndex = rawText.indexOf("</mutation>");
			if (endIndex < 0) {
				continue;
			}
			String rawSpecies = rawText.substring(0, endIndex);
			IAllele allele = AlleleManager.alleleRegistry.getAllele(rawSpecies);
			if (allele instanceof IAlleleSpecies) {
				mutations.add((IAlleleSpecies) allele);
				text = text.replaceFirst(rawSpecies, "<7>\n");
			} else {
				text = text.replaceFirst(rawSpecies, "\n");
			}
		}
		text = text.replaceAll("<mutation>", "");
		text = text.replaceAll("</mutation>", "");

		//Parse all carpenter recipes and replace the old position of it with <8>
		String[] carpenterArray = text.split("<carpenter>");
		for (int i = 1; i < carpenterArray.length; i++) {
			String rawText = carpenterArray[i];
			int endIndex = rawText.indexOf("</carpenter>");
			if (endIndex < 0) {
				continue;
			}
			String rawItem = rawText.substring(0, endIndex);
			ItemStack stack = ItemStackUtil.parseItemStackString(rawItem, OreDictionary.WILDCARD_VALUE);
			if (stack != null) {
				carpenterRecipes.add(stack);
				text = text.replaceFirst(rawItem, "<8>\n");
			} else {
				text = text.replaceFirst(rawItem, "\n");
			}
		}
		text = text.replaceAll("<carpenter>", "");
		text = text.replaceAll("</carpenter>", "");

		//Parse all links and replace the old position of it with <10>
		String[] linkArray = text.split("<pagelink>");
		for (int i = 1; i < linkArray.length; i++) {
			String rawText = linkArray[i];
			int endIndex = rawText.indexOf("</pagelink>");
			if (endIndex < 0) {
				continue;
			}
			String rawImage = rawText.substring(0, endIndex);
			Image image = Image.parse(rawImage);
			if (image != null) {
				images.add(image);
				text = text.replaceFirst(rawImage, "<10>\n");
			} else {
				text = text.replaceFirst(rawImage, "\n");
			}
		}
		text = text.replaceAll("<pagelink>", "");
		text = text.replaceAll("</pagelink>", "");

		PageBuilder builder = new PageBuilder();
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		boolean unicode = fontRenderer.getUnicodeFlag();
		fontRenderer.setUnicodeFlag(true);

		List<String> lines = fontRenderer.listFormattedStringToWidth(text, 108);
		fontRenderer.setUnicodeFlag(unicode);
		for (String line : lines) {
			if (line.contains("<0>")) {
				String[] t = line.split("<0>");
				if (t.length > 0 && fontRenderer.getStringWidth(t[0]) > 0) {
					builder.add(t[0]);
				}
				IRecipe recipe = recipes.remove(0);
				builder.add(recipe);
				if (t.length > 1 && fontRenderer.getStringWidth(t[1]) > 0) {
					builder.add(t[1]);
				}
				continue;
			} else if (line.contains("<1>")) {
				String[] t = line.split("<1>");
				if (t.length > 0 && fontRenderer.getStringWidth(t[0]) > 0) {
					builder.add(t[0]);
				}
				Image image = images.remove(0);
				builder.add(image);
				if (t.length > 1 && fontRenderer.getStringWidth(t[1]) > 0) {
					builder.add(t[1]);
				}
				continue;
			} else if (line.contains("<3>")) {
				String[] t = line.split("<3>");
				if (t.length > 0 && fontRenderer.getStringWidth(t[0]) > 0) {
					builder.add(t[0]);
				}
				builder.page(true, 0);
				if (t.length > 1 && fontRenderer.getStringWidth(t[1]) > 0) {
					builder.add(t[1]);
				}
				continue;
			} else if (line.contains("<4>")) {
				builder.add(line.replace("<4>", ""), true);
				continue;
			} else if (line.contains("<5>")) {
				String[] t = line.split("<5>");
				if (t.length > 0 && fontRenderer.getStringWidth(t[0]) > 0) {
					builder.add(t[0]);
				}
				IMultiblockBlueprint blueprint = blueprints.remove(0);
				builder.add(blueprint);
				if (t.length > 1 && fontRenderer.getStringWidth(t[1]) > 0) {
					builder.add(t[1]);
				}
				continue;
			}else if(line.contains("<7>")){
				String[] t = line.split("<7>");
				if (t.length > 0 && fontRenderer.getStringWidth(t[0]) > 0) {
					builder.add(t[0]);
				}
				IAlleleSpecies species = mutations.remove(0);
				builder.add(species);
				if (t.length > 1 && fontRenderer.getStringWidth(t[1]) > 0) {
					builder.add(t[1]);
				}
				continue;
			} else if(line.contains("<8>")){
				String[] t = line.split("<8>");
				if (t.length > 0 && fontRenderer.getStringWidth(t[0]) > 0) {
					builder.add(t[0]);
				}
				ItemStack stack = carpenterRecipes.remove(0);
				builder.addCarpenter(stack);
				if (t.length > 1 && fontRenderer.getStringWidth(t[1]) > 0) {
					builder.add(t[1]);
				}
				continue;
			} else if(line.contains("<10>")){
				String[] t = line.split("<10>");
				if (t.length > 0 && fontRenderer.getStringWidth(t[0]) > 0) {
					builder.add(t[0]);
				}
				PageLinkIndex.PageLink link = links.get(linkIndex);
				linkIndex++;
				link.pageIndex = builder.pages.size();
				if (t.length > 1 && fontRenderer.getStringWidth(t[1]) > 0) {
					builder.add(t[1]);
				}
			} else if (line.endsWith("\n")) {
				builder.empty();
				continue;
			} else if (line.endsWith("<6>")) {
				builder.paragraph();
				continue;
			}
			builder.add(line);
		}
		return builder.getPages();
	}

	private static class Image {
		Drawable drawable;
		int width;
		int height;

		public Image(Drawable drawable, int width, int height) {
			this.drawable = drawable;
			this.width = width;
			this.height = height;
		}

		@Nullable
		private static Image parse(String text) {
			String[] segments = text.split(";");
			if (segments.length != 9) {
				return null;
			}
			try {
				ResourceLocation location = new ResourceLocation(segments[0]);
				int u = Integer.parseInt(segments[1]);
				int v = Integer.parseInt(segments[2]);
				int uWeight = Integer.parseInt(segments[3]);
				int vHeight = Integer.parseInt(segments[4]);
				int texWeight = Integer.parseInt(segments[5]);
				int texHeight = Integer.parseInt(segments[6]);
				int width = Integer.parseInt(segments[7]);
				int height = Integer.parseInt(segments[8]);
				return new Image(new Drawable(location, u, v, uWeight, vHeight, texWeight, texHeight), width, height);
			} catch (Exception ignored) {
			}
			return null;
		}
	}

	private static class PageBuilder {
		/* Constants */
		private static final ResourceLocation BOOK_TEXTURE = new ResourceLocation(Constants.MOD_ID, Constants.TEXTURE_PATH_GUI + "/atlas.png");
		private static final ResourceLocation BOOK_CRAFTING_TEXTURE = new ResourceLocation(Constants.MOD_ID, Constants.TEXTURE_PATH_GUI + "/atlas_crafting.png");
		private static final Drawable CRAFTING_GRID = new Drawable(BOOK_TEXTURE, 158, 181, 98, 58);
		private static final Drawable SLOT = new Drawable(BOOK_TEXTURE, 0, 223, 18, 18);
		private static final Drawable MUTATION_PLUS = new Drawable(BOOK_TEXTURE, 0, 241, 15, 15);
		private static final Drawable MUTATION_ARROW = new Drawable(BOOK_TEXTURE, 15, 241, 18, 15);
		private static final Drawable CARPENTER_BACKGROUND = new Drawable(BOOK_CRAFTING_TEXTURE, 0, 0, 108, 60);
		private static final Drawable CARPENTER_TANK_OVERLAY = new Drawable(BOOK_CRAFTING_TEXTURE, 109, 1, 16, 58);

		private static final int PAGE_HEIGHT = 155;

		private List<IBookPage> pages = new ArrayList<>();
		@Nullable
		private IElementGroup currentPage;
		@Nullable
		private ContentType previousElement = null;

		public void empty() {
			if (isPageEmpty()) {
				return;
			}
			previousElement = null;
			IElementGroup page = page(9);
			page.add(new GuiElement(108, 9));
		}

		public void paragraph() {
			if (isPageEmpty()) {
				return;
			}
			previousElement = null;
			int height = (int) (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT * 0.66D);
			IElementGroup page = page(height);
			page.add(new GuiElement(108, height));
		}

		public void add(Image image) {
			previousElement = ContentType.IMAGE;
			IElementGroup page = page(image.height);
			IGuiElement element = new DrawableElement(0, 0, image.width, image.height, image.drawable);
			element.setAlign(GuiElementAlignment.TOP_CENTER);
			page.add(element);
		}

		public void add(IAlleleSpecies species){
			if(previousElement != ContentType.MUTATION){
				page(12).text(TextFormatting.DARK_GRAY + "Bee Breeding", GuiElementAlignment.TOP_CENTER);
			}
			previousElement = ContentType.MUTATION;
			ISpeciesRoot root = species.getRoot();
			for(IMutation mutation : root.getResultantMutations(species)) {
				addBookMutation( mutation, SLOT, MUTATION_PLUS, MUTATION_ARROW);
			}
		}

		private void addBookMutation(IMutation mutation, Drawable slot, Drawable plus, Drawable arrow) {
			IElementGroup page = page(18);
			ISpeciesRoot root = mutation.getRoot();
			AbstractElementLayout mutationElement = GuiElementFactory.INSTANCE.createVertical(0, 0, 108);
			mutationElement.horizontal(2);
			//
			AbstractElementLayout background = mutationElement.horizontal(0, 0, 18).setDistance(3);
			background.drawable(slot);
			background.drawable(0, 2, plus);
			background.drawable(slot);
			ElementGroup conditionArrow = background.panel(24, 18);
			Collection<String> conditions = mutation.getSpecialConditions();
			String text;
			if (!conditions.isEmpty()) {
				text = String.format("[%.0f%%]", mutation.getBaseChance());
			} else {
				text = String.format("%.0f%%", mutation.getBaseChance());
			}
			conditionArrow.text(text, GuiElementAlignment.TOP_CENTER, 0);
			conditionArrow.addTooltip(conditions);
			conditionArrow.drawable(3, 6, arrow);
			background.drawable(slot);
			//
			IElementLayout foreground = mutationElement.horizontal(2).setDistance(23);
			foreground.item(1, -17, root.getMemberStack(mutation.getAllele0(), root.getTypeForMutation(0)));
			foreground.item(1, -17, root.getMemberStack(mutation.getAllele1(), root.getTypeForMutation(1)));
			foreground.item(10, -17, root.getMemberStack(mutation.getTemplate(), root.getTypeForMutation(2)));
			page.add(mutationElement);
		}

		public void add(IMultiblockBlueprint blueprint) {
			previousElement = ContentType.BLUEPRINT;
			IElementGroup page = page(100);
			//page.add(new MultiblockElement(0, 0, blueprint));
		}

		public void add(IRecipe recipe) {
			boolean firstRecipe = previousElement != ContentType.RECIPE;
			NonNullList<Ingredient> ingredients = recipe.getIngredients();
			IElementGroup page = page(firstRecipe ? 72 : 60);
			PaneLayout panel = new PaneLayout(98, firstRecipe ? 72 : 60);
			int gridStartY = 0;
			if(firstRecipe) {
				panel.text(TextFormatting.DARK_GRAY + Translator.translateToLocal("for.gui.book.element.crafting"), GuiElementAlignment.TOP_CENTER);
				gridStartY = 12;
			}
			panel.drawable(0, gridStartY, CRAFTING_GRID);
			panel.item(81, gridStartY + 21, recipe.getRecipeOutput());
			for (int x = 0; x < 3; x++) {
				for (int y = 0; y < 3; y++) {
					int index = y * 3 + x;
					if (ingredients.size() <= index) {
						continue;
					}
					Ingredient ingredient = ingredients.get(index);
					panel.add(new IngredientElement(1 + x * 20, gridStartY + 1 + y * 20, ingredient));
				}
			}
			panel.setAlign(GuiElementAlignment.TOP_CENTER);
			page.add(panel);
			previousElement = ContentType.RECIPE;
		}

		public void addCarpenter(ItemStack resolute){
			for(ICarpenterRecipe recipe : CarpenterRecipeManager.getRecipes(resolute)) {
				addCarpenter(recipe);
				//ResourceLocation BOOK_TEXTURE = new ResourceLocation(Constants.MOD_ID, Constants.TEXTURE_PATH_GUI + "/atlas_crafting.png");
				//GuiElementLayout vertical = elementManager.vertical(RIGHT_PAGE_START_X, PAGE_START_Y, 108);
				//vertical.text(0, 9, "Carpenter", GuiElementAlignment.CENTER, 0);
				//vertical.drawable(new Drawable(BOOK_TEXTURE, 0, 0, 108, 60));
			}
		}

		private void addCarpenter(ICarpenterRecipe recipe){
			boolean firstRecipe = previousElement != ContentType.CARPENTER;
			ElementGroup panel = GuiElementFactory.INSTANCE.createPanel(0, 0, 108, firstRecipe ? 72 : 60);
			int gridStartY = 0;
			if(firstRecipe){
				panel.text(TextFormatting.DARK_GRAY + "Carpenter", GuiElementAlignment.TOP_CENTER);
				gridStartY = 12;
			}
			panel.drawable(0, gridStartY, CARPENTER_BACKGROUND);
			panel.add(new TankElement(91, gridStartY + 1, null, () -> new FluidTankInfo(recipe.getFluidResource(), Constants.PROCESSOR_TANK_CAPACITY), CARPENTER_TANK_OVERLAY));
			IDescriptiveRecipe gridRecipe = recipe.getCraftingGridRecipe();
			NonNullList<NonNullList<ItemStack>> ingredients = gridRecipe.getRawIngredients();
			for (int x = 0; x < 3; x++) {
				for (int y = 0; y < 3; y++) {
					int index = y * 3 + x;
					if(index >= ingredients.size()){
						continue;
					}
					NonNullList<ItemStack> items = ingredients.get(index);
					panel.add(new IngredientElement(1 + x * 19, gridStartY + 3 + y * 19, Ingredient.fromStacks(items.toArray(new ItemStack[items.size()]))));
				}
			}
			panel.item(71, gridStartY + 41, gridRecipe.getOutput());
			previousElement = ContentType.CARPENTER;
			add(panel);
		}

		public void add(String line) {
			add(line, false);
		}

		public void add(String line, boolean center) {
			add(new TextElement(-1, 9, line, GuiElementAlignment.TOP_LEFT, 0, true), center);
		}

		private boolean isPageEmpty() {
			return currentPage != null && currentPage.getElements().isEmpty();
		}

		private void add(IGuiElement element){
			add(element, false);
		}

		private void add(IGuiElement element, boolean center){
			IElementGroup helper = page(element.getHeight());
			if(center){
				element.setAlign(GuiElementAlignment.TOP_CENTER);
			}
			helper.add(element);
		}

		private IElementGroup page(int height) {
			return page(false, height);
		}

		private IElementGroup page(boolean forced, int elementHeight) {
			if (currentPage == null) {
				currentPage = createNewPage();
			} else {
				int height = PAGE_HEIGHT;
				if (pages.size() % 2 == 1) {
					//The left page is always smaller because of the title
					height -= 12;
				}
				if (forced || currentPage.getHeight() + elementHeight > height) {
					currentPage = createNewPage();
				}
			}
			return currentPage;
		}

		private IElementGroup createNewPage() {
			IElementLayout page = new VerticalLayout(108);
			pages.add(new BookPageElements(page));
			return page;
		}

		public List<IBookPage> getPages() {
			return pages;
		}
	}

	private enum ContentType{
		IMAGE, RECIPE, CARPENTER, FABRICATOR, TEXT, BLUEPRINT, MUTATION
	}
}

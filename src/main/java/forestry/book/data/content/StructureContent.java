package forestry.book.data.content;

import javax.annotation.Nullable;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import forestry.api.book.BookContent;
import forestry.api.gui.IElementGroup;
import forestry.api.gui.IGuiElement;
import forestry.api.gui.IGuiElementFactory;
import forestry.book.BookLoader;
import forestry.book.data.BlockData;
import forestry.book.data.StructureData;
import forestry.book.gui.elements.MultiblockElement;

public class StructureContent extends BookContent {
	private String structureFile;
	private transient StructureData structureData;

	@Nullable
	@Override
	public Class getDataClass() {
		return null;
	}

	@Override
	public void onDeserialization() {
		if(structureFile == null || structureFile.isEmpty()) {
			return;
		}

		ResourceLocation location = BookLoader.getResourceLocation(structureFile);

		if(location != null && BookLoader.resourceExists(location)) {
			IResource resource = BookLoader.getResource(location);
			structureData = BookLoader.GSON.fromJson(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8), StructureData.class);
		}
	}

	@Override
	public boolean addElements(IElementGroup group, IGuiElementFactory factory, @Nullable BookContent previous, @Nullable IGuiElement previousElement) {
		if(structureFile == null){
			return false;
		}

		int offset = 0;
		int structureSizeX = group.getWidth();
		int structureSizeY = 155 - 10;

		int[] size = structureData.size;
		BlockData[] structure = structureData.structure;

		if(size != null && size.length == 3 && structure != null && structure.length > 0) {
			boolean showButtons = size[1] > 1;
			if(showButtons) {
				//structureSizeX -= GuiArrow.ArrowType.REFRESH.w;
			}
			MultiblockElement elementStructure = new MultiblockElement(offset, 0, structureSizeX, structureSizeY, size, structure);
			group.add(elementStructure);

			/*if(showButtons) {
				int col = book.appearance.structureButtonColor;
				int colHover = book.appearance.structureButtonColorHovered;
				int colToggled = book.appearance.structureButtonColorToggled;

				int midY = y + structureSizeY / 2 - (GuiArrow.ArrowType.UP.h + GuiArrow.ArrowType.DOWN.h) / 2;

				int dx = (GuiArrow.ArrowType.REFRESH.w - GuiArrow.ArrowType.UP.w) / 2;

				//list.add(new ElementArrow(ElementStructure.BUTTON_ID_LAYER_UP, elementStructure, structureSizeX + offset + dx, midY, GuiArrow.ArrowType.UP, col, colHover));
				//midY += GuiArrow.ArrowType.UP.h + 2;
				//list.add(new ElementArrow(ElementStructure.BUTTON_ID_LAYER_DOWN, elementStructure, structureSizeX + offset + dx, midY, GuiArrow.ArrowType.DOWN, col, colHover));

				list.add(new ElementAnimationToggle(ElementStructure.BUTTON_ID_ANIMATE, elementStructure, GuiBook.PAGE_WIDTH - GuiArrow.ArrowType.REFRESH.w, 0, GuiArrow.ArrowType.REFRESH, col, colHover, colToggled));
			}*/
		}
		return true;
	}
}

/*******************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2013-2014 Slime Knights (mDiyo, fuj1n, Sunstrike, progwml6, pillbox, alexbegt)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Any alternate licenses are noted where appropriate.
 ******************************************************************************/
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
import forestry.book.BookRegistry;
import forestry.book.data.structure.BlockData;
import forestry.book.data.structure.StructureData;
import forestry.book.gui.elements.MultiblockElement;
import forestry.core.utils.ResourceUtil;

public class StructureContent extends BookContent {
	@Nullable
	private String structureFile = null;
	private transient StructureData structureData;

	@Nullable
	@Override
	public Class getDataClass() {
		return null;
	}

	@Override
	public void onDeserialization() {
		if (structureFile == null || structureFile.isEmpty()) {
			return;
		}

		ResourceLocation location = BookRegistry.getResourceLocation(structureFile);

		if (location != null && ResourceUtil.resourceExists(location)) {
			IResource resource = ResourceUtil.getResource(location);
			structureData = BookRegistry.GSON.fromJson(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8), StructureData.class);
		}
	}

	@Override
	public boolean addElements(IElementGroup page, IGuiElementFactory factory, @Nullable BookContent previous, @Nullable IGuiElement previousElement, int pageHeight) {
		if (structureFile == null) {
			return false;
		}

		int offset = 0;
		int structureSizeX = page.getWidth();
		int structureSizeY = 155 - 10;

		int[] size = structureData.size;
		BlockData[] structure = structureData.structure;

		if (size.length == 3 && structure.length > 0) {
			boolean showButtons = size[1] > 1;
			if (showButtons) {
				//structureSizeX -= GuiArrow.ArrowType.REFRESH.w;
			}
			MultiblockElement elementStructure = new MultiblockElement(offset, 0, structureSizeX, structureSizeY, size, structure);
			page.add(elementStructure);

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

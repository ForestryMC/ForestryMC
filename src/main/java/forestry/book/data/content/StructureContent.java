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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

import net.minecraft.resources.IResource;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.book.BookContent;
import forestry.api.gui.IElementGroup;
import forestry.api.gui.IGuiElement;
import forestry.api.gui.IGuiElementFactory;
import forestry.book.BookLoader;
import forestry.book.data.structure.BlockData;
import forestry.book.data.structure.StructureData;
import forestry.book.gui.elements.MultiblockElement;
import forestry.core.utils.Log;

/**
 * A book content that displays a multiblock structure.
 */
@OnlyIn(Dist.CLIENT)
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

		IResource resource = BookLoader.getResource(structureFile);
		if (resource == null) {
			return;
		}
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
			structureData = BookLoader.GSON.fromJson(reader, StructureData.class);
		} catch (IOException e) {
			Log.error("Failed to load structure file {}.{}", structureFile, e);
		} finally {
			IOUtils.closeQuietly(resource);
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
			MultiblockElement elementStructure = new MultiblockElement(offset, 0, structureSizeX, structureSizeY, size, structure);
			page.add(elementStructure);
		}
		return true;
	}
}

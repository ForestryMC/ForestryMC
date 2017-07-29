/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.climate;

import java.util.Collection;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IClimateTable {
	
	IClimateTable addValueEntry(String textKey, String value);

	IClimateTable addCenteredEntry(String textKey);
	
	IClimateTable addEmptyEntry();
	
	IClimateTable addEntry(IClimateTableEntry entry);

	int getHeight();

	int getLineWidth();
	
	Collection<IClimateTableEntry> getEntrys();
	
}

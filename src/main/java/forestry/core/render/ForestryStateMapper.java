package forestry.core.render;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

public abstract class ForestryStateMapper implements IStateMapper {

	protected Map<IBlockState, ModelResourceLocation> mapStateModelLocations = Maps.newLinkedHashMap();

	@SuppressWarnings("unchecked")
	public String getPropertyString(Map<IProperty<?>, Comparable<?>> map) {
		StringBuilder stringbuilder = new StringBuilder();

		for (Entry<IProperty<?>, Comparable<?>> entry : map.entrySet()) {
			if (stringbuilder.length() != 0) {
				stringbuilder.append(",");
			}

			IProperty iproperty = entry.getKey();
			Comparable comparable = entry.getValue();
			stringbuilder.append(iproperty.getName());
			stringbuilder.append("=");
			stringbuilder.append(iproperty.getName(comparable));
		}

		if (stringbuilder.length() == 0) {
			stringbuilder.append("normal");
		}

		return stringbuilder.toString();
	}
	
}

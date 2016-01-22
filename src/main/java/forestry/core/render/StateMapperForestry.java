package forestry.core.render;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;

import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.block.statemap.IStateMapper;

public abstract class StateMapperForestry implements IStateMapper {

	protected Map mapStateModelLocations = Maps.newLinkedHashMap();

	public String getPropertyString(Map p_178131_1_) {
		StringBuilder stringbuilder = new StringBuilder();
		Iterator iterator = p_178131_1_.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry entry = (Entry) iterator.next();

			if (stringbuilder.length() != 0) {
				stringbuilder.append(",");
			}

			IProperty iproperty = (IProperty) entry.getKey();
			Comparable comparable = (Comparable) entry.getValue();
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

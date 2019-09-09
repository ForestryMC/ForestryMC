package forestry.core.render;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.state.IProperty;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

//import net.minecraft.client.renderer.block.statemap.IStateMapper;
//TODO - flatten to avoid statemapper?
@OnlyIn(Dist.CLIENT)
public abstract class ForestryStateMapper {//implements IStateMapper {

	protected final Map<BlockState, ModelResourceLocation> mapStateModelLocations = Maps.newLinkedHashMap();

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

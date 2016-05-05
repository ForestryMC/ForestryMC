package forestry.core.render;

import com.google.common.collect.Maps;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.blocks.IBlockType;
import forestry.core.blocks.IMachinePropertiesTesr;

@SideOnly(Side.CLIENT)
public class MachineStateMapper<T extends Enum<T> & IBlockType & IStringSerializable> extends ForestryStateMapper {

	private final Class<T> machinePropertiesClass;
	private final PropertyEnum<T> META;
	private final PropertyEnum<EnumFacing> FACE;
	
	public MachineStateMapper(Class<T> machinePropertiesClass, PropertyEnum<T> META, PropertyEnum<EnumFacing> FACE) {
		this.machinePropertiesClass = machinePropertiesClass;
		this.META = META;
		this.FACE = FACE;
	}

	@Override
	public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block block) {
		for (T type : machinePropertiesClass.getEnumConstants()) {
			if (type.getMachineProperties() instanceof IMachinePropertiesTesr) {
				continue;
			}
			for (EnumFacing facing : EnumFacing.values()) {
				if (facing == EnumFacing.DOWN || facing == EnumFacing.UP) {
					continue;
				}
				IBlockState state = block.getDefaultState().withProperty(META, type).withProperty(FACE, facing);
				LinkedHashMap<IProperty<?>, Comparable<?>> linkedhashmap = Maps.newLinkedHashMap(state.getProperties());
				ResourceLocation blockLocation = Block.REGISTRY.getNameForObject(block);
				String s = String.format("%s:%s", blockLocation.getResourceDomain(), blockLocation.getResourcePath() + "_" + META.getName((T) linkedhashmap.remove(META)));
				mapStateModelLocations.put(state, new ModelResourceLocation(s, getPropertyString(linkedhashmap)));
			}
		}
		return this.mapStateModelLocations;
	}

}

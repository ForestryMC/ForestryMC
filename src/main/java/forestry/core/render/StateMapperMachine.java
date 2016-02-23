package forestry.core.render;

import java.util.LinkedHashMap;
import java.util.Map;
import com.google.common.collect.Maps;

import forestry.core.blocks.IBlockType;
import forestry.core.blocks.IMachinePropertiesTesr;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class StateMapperMachine<C extends Enum<C> & IBlockType & IStringSerializable> extends StateMapperForestry {

	private final Class<C> clazz;
	private final PropertyEnum<C> META;
	private final PropertyEnum<EnumFacing> FACE;
	
	public StateMapperMachine(Class<C> clazz, PropertyEnum<C> META, PropertyEnum<EnumFacing> FACE) {
		this.clazz = clazz;
		this.META = META;
		this.FACE = FACE;
	}

	@Override
	public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block block) {
		for (C definition : clazz.getEnumConstants()) {
			if (definition instanceof IMachinePropertiesTesr)
				continue;
			for (EnumFacing facing : EnumFacing.values()) {
				if (facing == EnumFacing.DOWN || facing == EnumFacing.UP)
					continue;
				IBlockState state = block.getDefaultState().withProperty(META, definition).withProperty(FACE, facing);
				LinkedHashMap linkedhashmap = Maps.newLinkedHashMap(state.getProperties());
				ResourceLocation RL = Block.blockRegistry.getNameForObject(block);
				String s = String.format("%s:%s", RL.getResourceDomain(), RL.getResourcePath() + "_" + META.getName((C) linkedhashmap.remove(META)));
				mapStateModelLocations.put(state, new ModelResourceLocation(s, getPropertyString(linkedhashmap)));
			}
		}
		return this.mapStateModelLocations;
	}

}

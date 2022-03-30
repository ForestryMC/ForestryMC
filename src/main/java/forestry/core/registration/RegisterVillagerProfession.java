package forestry.core.registration;

import com.google.common.collect.ImmutableSet;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.village.poi.PoiType;

public class RegisterVillagerProfession {
	public static VillagerProfession create(ResourceLocation name, PoiType poi, SoundEvent sound) {
		return new VillagerProfession(name.toString(), poi, ImmutableSet.<Item>builder().build(), ImmutableSet.<Block>builder().build(), sound);
	}
}

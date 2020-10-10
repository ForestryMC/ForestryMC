package forestry.api.registration;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.village.PointOfInterestType;

public class RegisterVillagerProfession {
    public static VillagerProfession create(
            ResourceLocation name,
            PointOfInterestType poi,
            SoundEvent sound
    ) {
        return new VillagerProfession(
                name.toString(),
                poi,
                ImmutableSet.<Item>builder().build(),
                ImmutableSet.<Block>builder().build(),
                sound
        );
    }
}

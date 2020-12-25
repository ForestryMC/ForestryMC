package forestry.apiculture.features;

import forestry.api.apiculture.genetics.EnumBeeType;
import forestry.api.core.ItemGroups;
import forestry.apiculture.ModuleApiculture;
import forestry.apiculture.items.*;
import forestry.core.items.ItemForestry;
import forestry.core.items.ItemOverlay;
import forestry.core.items.ItemScoop;
import forestry.modules.features.*;
import net.minecraft.inventory.EquipmentSlotType;

@FeatureProvider
public class ApicultureItems {
    // BEES
    public static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleApiculture.class);

    public static final FeatureItem<ItemBeeGE> BEE_QUEEN = REGISTRY.item(
            () -> new ItemBeeGE(EnumBeeType.QUEEN),
            "bee_queen_ge"
    );
    public static final FeatureItem<ItemBeeGE> BEE_DRONE = REGISTRY.item(
            () -> new ItemBeeGE(EnumBeeType.DRONE),
            "bee_drone_ge"
    );
    public static final FeatureItem<ItemBeeGE> BEE_PRINCESS = REGISTRY.item(
            () -> new ItemBeeGE(EnumBeeType.PRINCESS),
            "bee_princess_ge"
    );
    public static final FeatureItem<ItemBeeGE> BEE_LARVAE = REGISTRY.item(
            () -> new ItemBeeGE(EnumBeeType.LARVAE),
            "bee_larvae_ge"
    );

    public static final FeatureItem<ItemHabitatLocator> HABITAT_LOCATOR = REGISTRY.item(
            ItemHabitatLocator::new,
            "habitat_locator"
    );
    public static final FeatureItem<ItemImprinter> IMPRINTER = REGISTRY.item(ItemImprinter::new, "imprinter");

    public static final FeatureItemGroup<ItemMinecartBeehousing, ItemMinecartBeehousing.Type> MINECART_BEEHOUSING = REGISTRY
            .itemGroup(ItemMinecartBeehousing::new, "cart", ItemMinecartBeehousing.Type.values());

    // COMB FRAMES
    public static final FeatureItem<ItemHiveFrame> FRAME_UNTREATED = REGISTRY.item(
            () -> new ItemHiveFrame(80, 0.9f),
            "frame_untreated"
    );
    public static final FeatureItem<ItemHiveFrame> FRAME_IMPREGNATED = REGISTRY.item(
            () -> new ItemHiveFrame(240, 0.4f),
            "frame_impregnated"
    );
    public static final FeatureItem<ItemHiveFrame> FRAME_PROVEN = REGISTRY.item(
            () -> new ItemHiveFrame(720, 0.3f),
            "frame_proven"
    );

    // BEE RESOURCES
    public static final FeatureItemGroup<ItemOverlay, EnumHoneyDrop> HONEY_DROPS = REGISTRY.itemGroup((type) -> new ItemOverlay(
            ItemGroups.tabApiculture,
            type
    ), "honey_drop", EnumHoneyDrop.values());
    public static final FeatureItemGroup<ItemPropolis, EnumPropolis> PROPOLIS = REGISTRY.itemGroup(
            ItemPropolis::new,
            "propolis",
            EnumPropolis.values()
    );
    public static final FeatureItem<ItemForestry> HONEYDEW = REGISTRY.item(
            () -> new ItemForestry(ItemGroups.tabApiculture),
            "honeydew"
    );

    public static final FeatureItem<ItemForestry> ROYAL_JELLY = REGISTRY.item(
            () -> new ItemForestry(ItemGroups.tabApiculture),
            "royal_jelly"
    );

    public static final FeatureItem<ItemWaxCast> WAX_CAST = REGISTRY.item(ItemWaxCast::new, "wax_cast");
    public static final FeatureItemGroup<ItemPollenCluster, EnumPollenCluster> POLLEN_CLUSTER = REGISTRY.itemGroup(
            ItemPollenCluster::new,
            "pollen_cluster",
            EnumPollenCluster.VALUES
    );
    public static final FeatureItemGroup<ItemHoneyComb, EnumHoneyComb> BEE_COMBS = REGISTRY.itemGroup(
            ItemHoneyComb::new,
            "bee_comb",
            EnumHoneyComb.VALUES
    );
    //		OreDictionary.registerOre(OreDictUtil.BEE_COMB, beeComb.getWildcard());
    //TODO - tags

    // APIARIST'S CLOTHES
    public static final FeatureItem<ItemArmorApiarist> APIARIST_HELMET = REGISTRY.item(() -> new ItemArmorApiarist(
            EquipmentSlotType.HEAD), "apiarist_helmet");
    public static final FeatureItem<ItemArmorApiarist> APIARIST_CHEST = REGISTRY.item(() -> new ItemArmorApiarist(
            EquipmentSlotType.CHEST), "apiarist_chest");
    public static final FeatureItem<ItemArmorApiarist> APIARIST_LEGS = REGISTRY.item(() -> new ItemArmorApiarist(
            EquipmentSlotType.LEGS), "apiarist_legs");
    public static final FeatureItem<ItemArmorApiarist> APIARIST_BOOTS = REGISTRY.item(() -> new ItemArmorApiarist(
            EquipmentSlotType.FEET), "apiarist_boots");

    // TOOLS
    public static final FeatureItem<ItemScoop> SCOOP = REGISTRY.item(ItemScoop::new, "scoop");
    //TODO - harvest stuff
    public static final FeatureItem<ItemSmoker> SMOKER = REGISTRY.item(ItemSmoker::new, "smoker");

    private ApicultureItems() {
    }
}

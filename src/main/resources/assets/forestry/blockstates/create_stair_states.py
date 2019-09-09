forestry_wood_type = ["LARCH",
                      "TEAK",
                      "ACACIA_FORESTRY",
                      "LIME",
                      "CHESTNUT",
                      "WENGE",
                      "BAOBAB",
                      "SEQUOIA",
                      "KAPOK",
                      "EBONY",
                      "MAHOGANY",
                      "BALSA",
                      "WILLOW",
                      "WALNUT",
                      "GREENHEART",
                      "CHERRY",
                      "MAHOE",
                      "POPLAR",
                      "PALM",
                      "PAPAYA",
                      "PINE",
                      "PLUM",
                      "MAPLE",
                      "CITRUS",
                      "GIGANTEUM",
                      "IPE",
                      "PADAUK",
                      "COCOBOLO",
                      "ZEBRAWOOD"]

for l in forestry_wood_type:
    ll = l.lower()
    formatted_json = """{
    "variants": {
        "facing=east,half=bottom,shape=straight":  { "model": "forestry:block/arboriculture/stairs/%s" },
        "facing=west,half=bottom,shape=straight":  { "model": "forestry:block/arboriculture/stairs/%s", "y": 180, "uvlock": true },
        "facing=south,half=bottom,shape=straight": { "model": "forestry:block/arboriculture/stairs/%s", "y": 90, "uvlock": true },
        "facing=north,half=bottom,shape=straight": { "model": "forestry:block/arboriculture/stairs/%s", "y": 270, "uvlock": true },
        "facing=east,half=bottom,shape=outer_right":  { "model": "forestry:block/arboriculture/stairs/%s_outer" },
        "facing=west,half=bottom,shape=outer_right":  { "model": "forestry:block/arboriculture/stairs/%s_outer", "y": 180, "uvlock": true },
        "facing=south,half=bottom,shape=outer_right": { "model": "forestry:block/arboriculture/stairs/%s_outer", "y": 90, "uvlock": true },
        "facing=north,half=bottom,shape=outer_right": { "model": "forestry:block/arboriculture/stairs/%s_outer", "y": 270, "uvlock": true },
        "facing=east,half=bottom,shape=outer_left":  { "model": "forestry:block/arboriculture/stairs/%s_outer", "y": 270, "uvlock": true },
        "facing=west,half=bottom,shape=outer_left":  { "model": "forestry:block/arboriculture/stairs/%s_outer", "y": 90, "uvlock": true },
        "facing=south,half=bottom,shape=outer_left": { "model": "forestry:block/arboriculture/stairs/%s_outer" },
        "facing=north,half=bottom,shape=outer_left": { "model": "forestry:block/arboriculture/stairs/%s_outer", "y": 180, "uvlock": true },
        "facing=east,half=bottom,shape=inner_right":  { "model": "forestry:block/arboriculture/stairs/%s_inner" },
        "facing=west,half=bottom,shape=inner_right":  { "model": "forestry:block/arboriculture/stairs/%s_inner", "y": 180, "uvlock": true },
        "facing=south,half=bottom,shape=inner_right": { "model": "forestry:block/arboriculture/stairs/%s_inner", "y": 90, "uvlock": true },
        "facing=north,half=bottom,shape=inner_right": { "model": "forestry:block/arboriculture/stairs/%s_inner", "y": 270, "uvlock": true },
        "facing=east,half=bottom,shape=inner_left":  { "model": "forestry:block/arboriculture/stairs/%s_inner", "y": 270, "uvlock": true },
        "facing=west,half=bottom,shape=inner_left":  { "model": "forestry:block/arboriculture/stairs/%s_inner", "y": 90, "uvlock": true },
        "facing=south,half=bottom,shape=inner_left": { "model": "forestry:block/arboriculture/stairs/%s_inner" },
        "facing=north,half=bottom,shape=inner_left": { "model": "forestry:block/arboriculture/stairs/%s_inner", "y": 180, "uvlock": true },
        "facing=east,half=top,shape=straight":  { "model": "forestry:block/arboriculture/stairs/%s", "x": 180, "uvlock": true },
        "facing=west,half=top,shape=straight":  { "model": "forestry:block/arboriculture/stairs/%s", "x": 180, "y": 180, "uvlock": true },
        "facing=south,half=top,shape=straight": { "model": "forestry:block/arboriculture/stairs/%s", "x": 180, "y": 90, "uvlock": true },
        "facing=north,half=top,shape=straight": { "model": "forestry:block/arboriculture/stairs/%s", "x": 180, "y": 270, "uvlock": true },
        "facing=east,half=top,shape=outer_right":  { "model": "forestry:block/arboriculture/stairs/%s_outer", "x": 180, "y": 90, "uvlock": true },
        "facing=west,half=top,shape=outer_right":  { "model": "forestry:block/arboriculture/stairs/%s_outer", "x": 180, "y": 270, "uvlock": true },
        "facing=south,half=top,shape=outer_right": { "model": "forestry:block/arboriculture/stairs/%s_outer", "x": 180, "y": 180, "uvlock": true },
        "facing=north,half=top,shape=outer_right": { "model": "forestry:block/arboriculture/stairs/%s_outer", "x": 180, "uvlock": true },
        "facing=east,half=top,shape=outer_left":  { "model": "forestry:block/arboriculture/stairs/%s_outer", "x": 180, "uvlock": true },
        "facing=west,half=top,shape=outer_left":  { "model": "forestry:block/arboriculture/stairs/%s_outer", "x": 180, "y": 180, "uvlock": true },
        "facing=south,half=top,shape=outer_left": { "model": "forestry:block/arboriculture/stairs/%s_outer", "x": 180, "y": 90, "uvlock": true },
        "facing=north,half=top,shape=outer_left": { "model": "forestry:block/arboriculture/stairs/%s_outer", "x": 180, "y": 270, "uvlock": true },
        "facing=east,half=top,shape=inner_right":  { "model": "forestry:block/arboriculture/stairs/%s_inner", "x": 180, "y": 90, "uvlock": true },
        "facing=west,half=top,shape=inner_right":  { "model": "forestry:block/arboriculture/stairs/%s_inner", "x": 180, "y": 270, "uvlock": true },
        "facing=south,half=top,shape=inner_right": { "model": "forestry:block/arboriculture/stairs/%s_inner", "x": 180, "y": 180, "uvlock": true },
        "facing=north,half=top,shape=inner_right": { "model": "forestry:block/arboriculture/stairs/%s_inner", "x": 180, "uvlock": true },
        "facing=east,half=top,shape=inner_left":  { "model": "forestry:block/arboriculture/stairs/%s_inner", "x": 180, "uvlock": true },
        "facing=west,half=top,shape=inner_left":  { "model": "forestry:block/arboriculture/stairs/%s_inner", "x": 180, "y": 180, "uvlock": true },
        "facing=south,half=top,shape=inner_left": { "model": "forestry:block/arboriculture/stairs/%s_inner", "x": 180, "y": 90, "uvlock": true },
        "facing=north,half=top,shape=inner_left": { "model": "forestry:block/arboriculture/stairs/%s_inner", "x": 180, "y": 270, "uvlock": true }
    }
}

""".replace("%s", ll)

    with open(ll + "_stairs.json", "w") as f:
        f.write(formatted_json)

    with open(ll + "_fireproof_stairs.json", "w") as f:
        f.write(formatted_json)

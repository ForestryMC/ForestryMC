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

# TODO override texture here.
for l in forestry_wood_type:
    ll = l.lower()
    formatted_json = """{
  "variants": {
    "facing=east,half=lower,hinge=left,open=false":  { "model": "forestry:arboriculture/door_bottom" },
    "facing=south,half=lower,hinge=left,open=false": { "model": "forestry:arboriculture/door_bottom", "y": 90 },
    "facing=west,half=lower,hinge=left,open=false":  { "model": "forestry:arboriculture/door_bottom", "y": 180 },
    "facing=north,half=lower,hinge=left,open=false": { "model": "forestry:arboriculture/door_bottom", "y": 270 },
    "facing=east,half=lower,hinge=right,open=false":  { "model": "forestry:arboriculture/door_bottom_rh" },
    "facing=south,half=lower,hinge=right,open=false": { "model": "forestry:arboriculture/door_bottom_rh", "y": 90 },
    "facing=west,half=lower,hinge=right,open=false":  { "model": "forestry:arboriculture/door_bottom_rh", "y": 180 },
    "facing=north,half=lower,hinge=right,open=false": { "model": "forestry:arboriculture/door_bottom_rh", "y": 270 },
    "facing=east,half=lower,hinge=left,open=true":  { "model": "forestry:arboriculture/door_bottom_rh", "y": 90 },
    "facing=south,half=lower,hinge=left,open=true": { "model": "forestry:arboriculture/door_bottom_rh", "y": 180 },
    "facing=west,half=lower,hinge=left,open=true":  { "model": "forestry:arboriculture/door_bottom_rh", "y": 270 },
    "facing=north,half=lower,hinge=left,open=true": { "model": "forestry:arboriculture/door_bottom_rh" },
    "facing=east,half=lower,hinge=right,open=true":  { "model": "forestry:arboriculture/door_bottom", "y": 270 },
    "facing=south,half=lower,hinge=right,open=true": { "model": "forestry:arboriculture/door_bottom" },
    "facing=west,half=lower,hinge=right,open=true":  { "model": "forestry:arboriculture/door_bottom", "y": 90 },
    "facing=north,half=lower,hinge=right,open=true": { "model": "forestry:arboriculture/door_bottom", "y": 180 },
    "facing=east,half=upper,hinge=left,open=false":  { "model": "forestry:arboriculture/door_top" },
    "facing=south,half=upper,hinge=left,open=false": { "model": "forestry:arboriculture/door_top", "y": 90 },
    "facing=west,half=upper,hinge=left,open=false":  { "model": "forestry:arboriculture/door_top", "y": 180 },
    "facing=north,half=upper,hinge=left,open=false": { "model": "forestry:arboriculture/door_top", "y": 270 },
    "facing=east,half=upper,hinge=right,open=false":  { "model": "forestry:arboriculture/door_top_rh" },
    "facing=south,half=upper,hinge=right,open=false": { "model": "forestry:arboriculture/door_top_rh", "y": 90 },
    "facing=west,half=upper,hinge=right,open=false":  { "model": "forestry:arboriculture/door_top_rh", "y": 180 },
    "facing=north,half=upper,hinge=right,open=false": { "model": "forestry:arboriculture/door_top_rh", "y": 270 },
    "facing=east,half=upper,hinge=left,open=true":  { "model": "forestry:arboriculture/door_top_rh", "y": 90 },
    "facing=south,half=upper,hinge=left,open=true": { "model": "forestry:arboriculture/door_top_rh", "y": 180 },
    "facing=west,half=upper,hinge=left,open=true":  { "model": "forestry:arboriculture/door_top_rh", "y": 270 },
    "facing=north,half=upper,hinge=left,open=true": { "model": "forestry:arboriculture/door_top_rh" },
    "facing=east,half=upper,hinge=right,open=true":  { "model": "forestry:arboriculture/door_top", "y": 270 },
    "facing=south,half=upper,hinge=right,open=true": { "model": "forestry:arboriculture/door_top" },
    "facing=west,half=upper,hinge=right,open=true":  { "model": "forestry:arboriculture/door_top", "y": 90 },
    "facing=north,half=upper,hinge=right,open=true": { "model": "forestry:arboriculture/door_top", "y": 180 }
  }
}
""".replace("%s", ll)

    with open(ll + "_door.json", "w") as f:
        f.write(formatted_json)

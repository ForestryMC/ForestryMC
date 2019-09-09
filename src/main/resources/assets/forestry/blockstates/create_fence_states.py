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
    "multipart": [
      { 
        "apply": {
          "model": "forestry:arboriculture/fence_post",
          "textures": {
            "particle": "forestry:block/wood/%s_planks",
            "texture": "forestry:block/wood/%s_planks"
          },
          "uvlock": true
        }
      },
      {
        "apply": {
          "model": "forestry:arboriculture/fence_side",
          "uvlock": true,
          "textures": { "texture": "forestry:block/wood/%s_planks" }
        },
        "when": { "north": "true" }
      },
      {
        "apply": {
          "model": "forestry:arboriculture/fence_side",
          "uvlock": true,
          "textures": { "texture": "forestry:block/wood/%s_planks" },
		  "y": 90
		  
        },
        "when": { "east": "true" }
      },
      {
        "apply": {
          "model": "forestry:arboriculture/fence_side",
          "uvlock": true,
          "textures": { "texture": "forestry:block/wood/%s_planks" },
		  "y": 180
        },
        "when": { "south": "true" }
      },
      {
        "apply": {
          "model": "forestry:arboriculture/fence_side",
          "uvlock": true,
          "textures": { "texture": "forestry:block/wood/%s_planks" },
		  "y": 270
        },
        "when": { "west": "true" }
      }
    ]
}
""".replace("%s", ll)

    with open(ll + "_fence.json", "w") as f:
        f.write(formatted_json)

    with open(ll + "_fireproof_fence.json", "w") as f:
        f.write(formatted_json)

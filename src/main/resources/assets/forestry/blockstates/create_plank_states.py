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
		"":  { "model": "forestry:block/arboriculture/planks",
		        "textures": {
		            "all": "forestry:/block/wood/%s_planks"
		        }
		    }
    }
}""".replace("%s", ll)

    with open(ll + "_planks.json", "w") as f:
        f.write(formatted_json)

    with open(ll + "_fireproof_planks.json", "w") as f:
        f.write(formatted_json)

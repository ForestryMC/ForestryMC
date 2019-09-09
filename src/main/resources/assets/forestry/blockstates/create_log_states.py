forestry_logs = ["LARCH",
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

for l in forestry_logs:
    ll = l.lower()
    formatted_json = """{
  "variants": {
    "axis=y":  {     "textures": {
            "side": "forestry:block/wood/%s_bark",
            "end": "forestry:block/wood/%s_heart"
          },
          "model": "forestry:block/arboriculture/log" },
    "axis=z":   {     "textures": {
            "side": "forestry:block/wood/%s_bark",
            "end": "forestry:block/wood/%s_heart"
          },
          "model": "forestry:block/arboriculture/log", "x": 90 },
    "axis=x":   {     "textures": {
            "side": "forestry:block/wood/%s_bark",
            "end": "forestry:block/wood/%s_heart"
          },
          "model": "forestry:block/arboriculture/log", "x": 90, "y": 90 }
  }
}"""
    with open(ll + "_log.json", "w") as f:
        f.write(formatted_json)

    with open(ll + "_fireproof_log.json", "w") as f:
        f.write(formatted_json)

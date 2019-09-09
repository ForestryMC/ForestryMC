tubes = ["copper", "tin", "bronze", "iron", "gold", "diamond", "obsidian", "blaze", "rubber", "emerald", "apatite",
         "lapis", "ender", "orchid"]
for c in tubes:
    with open("electron_tube_" + c + ".json", "w") as f:
        f.write("""{
    "parent": "item/generated",
    "textures": {
        "layer0": "forestry:item/thermionic_tubes.0",
		"layer1": "forestry:item/thermionic_tubes.1"
    }
}"""
                )

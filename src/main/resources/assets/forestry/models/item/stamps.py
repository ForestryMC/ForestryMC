stamp = ["1n", "2n", "5n", "10n", "20n", "50n", "100n"]
for c in stamp:
    with open("stamp_" + c + ".json", "w") as f:
        f.write("""{
    "parent": "item/generated",
    "textures": {
        "layer0": "forestry:item/stamps.0",
		"layer1": "forestry:item/stamps.1"
    }
}"""
                )

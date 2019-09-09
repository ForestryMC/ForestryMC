combs = ["honey", "cocoa", "simmering", "stringy", "frozen", "dripping", "silky", "parched", "mysterious", "irradiated",
         "powdery", "reddened", "darkened", "omega", "wheaten", "mossy", "mellow"]
for c in combs:
    with open("block_bee_comb_" + c + ".json", "w") as f:
        f.write("""{
    "variants": {
        "": { "model": "forestry:block/block_bee_combs" }
	}
}"""
                )

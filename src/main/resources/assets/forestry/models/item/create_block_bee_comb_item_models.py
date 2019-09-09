combs = ["honey", "cocoa", "simmering", "stringy", "frozen", "dripping", "silky", "parched", "mysterious", "irradiated",
         "powdery", "reddened", "darkened", "omega", "wheaten", "mossy", "mellow"]
for c in combs:
    with open("block_bee_comb_" + c + ".json", "w") as f:
        f.write("""{
    "parent": "forestry:block/block_bee_combs",
    "display": {
        "thirdperson": {
            "rotation": [ 10, -45, 170 ],
            "translation": [ 0, 1.5, -2.75 ],
            "scale": [ 0.375, 0.375, 0.375 ]
        }
    }
}"""
                )

renderer = ["bee_chest", "tree_chest", "engine_peat", "engine_biogas", "engine_clockwork", "analyzer", "escritoire",
            "bottler", "carpenter", "centrifuge", "moistener", "squeezer", "still", "rainmaker"]
for h in renderer:
    with open(h + ".json", "w") as f:
        f.write("""{
    "variants": {
        "": { "model": "forestry:block/%s" }
    }
}""" % h)

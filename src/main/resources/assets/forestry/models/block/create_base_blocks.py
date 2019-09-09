renderer = ["bee_chest", "tree_chest", "engine_peat", "engine_biogas", "engine_clockwork", "analyzer", "escritoire",
            "bottler", "carpenter", "centrifuge", "moistener", "squeezer", "still", "rainmaker"]
particles = ["api_chest.0", "arb_chest.0", "engine_peat.0", "engine_biogas.0", "engine_clockwork.0", "analyzer.0",
             "escritoire.0", "bottler.0", "carpenter.0", "centrifuge.0", "moistener.0", "squeezer.0", "still.0",
             "rainmaker.0"]
for h in renderer:
    with open(h + ".json", "w") as f:
        f.write("""{
    "textures": {
        "particle": "forestry:block/%s"
    }
}""" % particles[renderer.index(h)])

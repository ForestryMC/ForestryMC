renderer = ["bee_chest", "tree_chest", "engine_peat", "engine_biogas", "engine_clockwork", "analyzer", "escritoire",
            "bottler", "carpenter", "centrifuge", "moistener", "squeezer", "still", "rainmaker"]
particles = ["api_chest.0", "arb_chest.0", "engine_peat.0", "engine_biogas.0", "engine_clockwork.0", "analyzer.0",
             "escritoire.0", "bottler.0", "carpenter.0", "centrifuge.0", "moistener.0", "squeezer.0", "still.0",
             "rainmaker.0"]
for h in renderer:
    with open(h + ".json", "w") as f:
        f.write("""{
    "parent": "builtin/entity",
    "textures": {
        "particle": "forestry:block/%s"
    },
    "display": {
        "gui": {
            "rotation": [ 30, 45, 0 ],
            "translation": [ 0, 0, 0],
            "scale":[ 0.625, 0.625, 0.625 ]
        },
        "ground": {
            "rotation": [ 0, 0, 0 ],
            "translation": [ 0, 3, 0],
            "scale":[ 0.25, 0.25, 0.25 ]
        },
        "head": {
            "rotation": [ 0, 180, 0 ],
            "translation": [ 0, 0, 0],
            "scale":[ 1, 1, 1]
        },
        "fixed": {
            "rotation": [ 0, 180, 0 ],
            "translation": [ 0, 0, 0],
            "scale":[ 0.5, 0.5, 0.5 ]
        },
        "thirdperson_righthand": {
            "rotation": [ 75, 315, 0 ],
            "translation": [ 0, 2.5, 0],
            "scale": [ 0.375, 0.375, 0.375 ]
        },
        "firstperson_righthand": {
            "rotation": [ 0, 315, 0 ],
            "translation": [ 0, 0, 0],
            "scale": [ 0.4, 0.4, 0.4 ]
        }
    }
}""" % particles[renderer.index(h)])

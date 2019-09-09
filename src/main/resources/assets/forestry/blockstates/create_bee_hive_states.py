hives = ["forest", "meadows", "desert", "jungle", "end", "snow", "swamp", "swarm"]
for h in hives:
    with open("beehive_" + h + ".json", "w") as f:
        f.write("""{
    "variants": {
        "": { "model": "forestry:block/beehives/%s" }
	}
}""" % h)

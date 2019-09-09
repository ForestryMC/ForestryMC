import os

for f in os.listdir("beecombs"):
    cName = f.replace(".json", "")
    os.rename("beecombs/" + f, "bee_comb_" + cName + ".json")

import os

for f in os.listdir("beehives"):
    cName = f.replace(".json", "")
    os.rename("beehives/" + f, "beehive_" + cName + ".json")

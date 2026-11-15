#!/usr/bin/env python3
"""Maakt van de gebouwde mod-jars modpacks die de CurseForge-app kan importeren.
Draai vanuit de projectmap, nadat beide versies gebouwd zijn:  python3 tools/maak_modpacks.py

Een modpack is een zip met een manifest.json (welke Minecraft-versie en welke modloader) en een
map overrides/mods met de jars. Importeren in CurseForge maakt er een nieuw profiel van, met de
goede versies al ingesteld.

- Fabric (Minecraft 26.2): de mod en Fabric API, die de mod nodig heeft.
- Forge (Minecraft 1.20.1): alleen de mod; die heeft verder niets nodig.

De zips komen in build/modpacks/.
"""
import glob, json, os, sys, urllib.request, zipfile

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
UIT = os.path.join(ROOT, "build/modpacks")
FABRIC_MAVEN = "https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api"

def eigenschappen(pad):
    """Leest een gradle.properties als woordenboek."""
    uit = {}
    with open(pad) as f:
        for regel in f:
            regel = regel.strip()
            if regel and not regel.startswith("#") and "=" in regel:
                sleutel, waarde = regel.split("=", 1)
                uit[sleutel.strip()] = waarde.strip()
    return uit

def mod_jar(map_):
    """De ene mod-jar in een build/libs-map (geen -sources of -dev)."""
    jars = [j for j in glob.glob(os.path.join(ROOT, map_, "build/libs/*.jar"))
            if not j.endswith(("-sources.jar", "-dev.jar"))]
    if len(jars) != 1:
        sys.exit(f"Verwacht precies één jar in {map_}/build/libs, gevonden: {jars or 'geen'}. Eerst bouwen?")
    return jars[0]

def haal_fabric_api(versie):
    """Downloadt Fabric API (Apache-2.0, mag meegeleverd worden) van de Maven-server van Fabric."""
    naam = f"fabric-api-{versie}.jar"
    doel = os.path.join(UIT, "download", naam)
    if not os.path.exists(doel):
        os.makedirs(os.path.dirname(doel), exist_ok=True)
        urllib.request.urlretrieve(f"{FABRIC_MAVEN}/{versie}/{naam}", doel)
    return doel

def modpack(bestand, naam, versie, minecraft, modloader, jars):
    manifest = {
        "minecraft": {"version": minecraft, "modLoaders": [{"id": modloader, "primary": True}]},
        "manifestType": "minecraftModpack",
        "manifestVersion": 1,
        "name": naam,
        "version": versie,
        "author": "De makers van de Sinterklaas-mod",
        # Geen mods van CurseForge zelf: alles zit in overrides/mods.
        "files": [],
        "overrides": "overrides",
    }
    pad = os.path.join(UIT, bestand)
    with zipfile.ZipFile(pad, "w", zipfile.ZIP_DEFLATED) as z:
        z.writestr("manifest.json", json.dumps(manifest, indent=2) + "\n")
        for jar in jars:
            z.write(jar, "overrides/mods/" + os.path.basename(jar))
    print(f"  {os.path.relpath(pad, ROOT)}: Minecraft {minecraft}, {modloader}, {len(jars)} jar(s)")

if __name__ == "__main__":
    hoofd = eigenschappen(os.path.join(ROOT, "gradle.properties"))
    forge = eigenschappen(os.path.join(ROOT, "forge-1.20.1/gradle.properties"))
    versie = hoofd["mod_version"]
    os.makedirs(UIT, exist_ok=True)

    modpack(f"sinterklaas-modpack-{versie}+fabric-mc{hoofd['minecraft_version']}.zip",
            f"Sinterklaas (Fabric {hoofd['minecraft_version']})", versie,
            hoofd["minecraft_version"], f"fabric-{hoofd['loader_version']}",
            [mod_jar("fabric"), haal_fabric_api(hoofd["fabric_api_version"])])
    modpack(f"sinterklaas-modpack-{versie}+forge-mc{forge['minecraft_version']}.zip",
            f"Sinterklaas (Forge {forge['minecraft_version']})", versie,
            forge["minecraft_version"], f"forge-{forge['forge_version']}",
            [mod_jar("forge-1.20.1")])
    print("klaar")

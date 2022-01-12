# Origin Orbs Mod

## Setup

To add an orb:
  1) In config ``orbs_config.json`` add a line for an item name.
  2) To change names of items make a datapack with ``assets/origin_orbs/lang`` in it. See [Minecraft Resourcepack](https://minecraft.fandom.com/wiki/Resource_Pack) for information about creating resourcepacks.
  3) Create a resourcepack with ``assets/origin_orgs/models/item`` in it and name the model files the same as what you put in the config. E.g if in the config you put ``orb`` then the file name would be ``orb.json``. See [Minecraft Resourcepack](https://minecraft.fandom.com/wiki/Resource_Pack) for information about creating resourcepacks.
  4) To use the orb, you need to define an Origin Power to it, which you can specify in ``data/origin_orbs/powers/``. Look at ``orb.json`` for an example on how to code the power. See [Minecraft Datapack](https://minecraft.fandom.com/wiki/Data_Pack) and [Origin Docs](https://origins.readthedocs.io/en/latest/) for help on creating your custom orb power.
  5) Reference the Orb power in your origin: ``origin_orbs:<orb_name>``. E.g ``origin_orbs:orb``.

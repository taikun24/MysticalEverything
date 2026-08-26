# Changelog

このブランチ (NeoForge 1.21.1) の changelog は 1.0.4 から記録しています。

## 1.0.4
- Port the JSON based custom item filter from the Forge branch (`useCustomFilter` / `filterWhitelist` / `filterJson`)
  - Filter types: `and` / `or` / `not`, `itemId`, `tag`, `nbt_has_key`, `nbt_string`, `nbt_int`, `nbt_has_enchantment`, `nbt_damage_less_than`, `nbt_has_any`
  - On 1.21 the `nbt_*` types read data components instead of raw NBT
- The item filter is applied to both the Compression Catalyst crafting recipe and the Infusion Altar
- `disableNBT` can now be switched per item with a filter (`useNBTFilter` / `nbtFilterWhitelist` / `nbtFilterJson`)
- Support the Mekanism:More Machine Planting Station / Planting Factory (`mekmm`)
- Fix crop contents being read as empty on a dedicated server when another mod's machine asks for them without a registry provider
- See the wiki for how to write a filter

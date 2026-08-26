# Changelog

## 1.0.4
- Support the Mekanism:More Machine Planting Station / Planting Factory (`mekmm`)
- The item filter is now applied to the Compression Catalyst crafting recipe as well
- `disableNBT` can now be switched per item with a filter (`useNBTFilter` / `nbtFilterWhitelist` / `nbtFilterJson`)
- See the wiki for how to write a filter

## 1.0.3
- Add a JSON based custom item filter (`useCustomFilter` / `filterWhitelist` / `filterJson`)
- Filter types: `and` / `or` / `not`, `itemId`, `tag`, `nbt_has_key`, `nbt_string`, `nbt_int`, `nbt_has_enchantment`, `nbt_damage_less_than`, `nbt_has_any`
- An invalid `filterJson` is now logged and ignored instead of crashing
- `filterJson` is re-read when the config changes

## 1.0.2
- Support the Astral Mekanism & Energistics greenhouse
- Fix Botany Pots crop id being read from a tag that was never written
- Internal code cleanup

## 1.0.1
- Fix Server Crash
- Support Botany Pots

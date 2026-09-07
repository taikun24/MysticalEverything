# Changelog

このブランチ (NeoForge 1.21.1) の changelog は 1.0.4 から記録しています。

## Unreleased
- The default `filterJson` no longer restricts crops to ores; it now allows every item (`{"type": "and", "filters": []}`)
  - The ore filter was only ever a sample value, and it only took effect once `useCustomFilter` was turned on -- turning that switch on used to silently restrict crops to ores
  - Existing configs are untouched. Set `filterJson` yourself if you want the old behaviour
- Config comments now say which switch activates each filter

## 1.0.4
- Port the JSON based custom item filter from the Forge branch (`useCustomFilter` / `filterWhitelist` / `filterJson`)
  - Filter types: `and` / `or` / `not`, `itemId`, `tag`, `nbt_has_key`, `nbt_string`, `nbt_int`, `nbt_has_enchantment`, `nbt_damage_less_than`, `nbt_has_any`
  - On 1.21 the `nbt_*` types read data components instead of raw NBT
- The item filter is applied to both the Compression Catalyst crafting recipe and the Infusion Altar
- `disableNBT` can now be switched per item with a filter (`useNBTFilter` / `nbtFilterWhitelist` / `nbtFilterJson`)
- Support the Mekanism:More Machine Planting Station / Planting Factory (`mekmm`)
- Fix crop contents being read as empty on a dedicated server when another mod's machine asks for them without a registry provider
- See the wiki for how to write a filter
- Cache the crop contents resolved from NBT, instead of rebuilding an `ItemStack` on every frame and every tick
- Reuse the Infusion Altar recipe, the Botany Pots crop and the Planting Station output while the contents stay the same
- `CropResource` is now immutable and built through `CropResource.of(...)`, with the display name and hash memoized
- Drop a per-call `INFO` log from the Botany Pots hook

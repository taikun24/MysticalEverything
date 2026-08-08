package jp.main.taikun.mysticaleverything.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class FilterBuilder {
    private static final FilterBuilder INSTANCE = new FilterBuilder();
    public static FilterBuilder getInstance() {
        return INSTANCE;
    }
    private FilterBuilder() { }

    public IFilter parse(JsonObject object) {
        IFilter filter = null;
        if (!object.has("type")) throw new IllegalArgumentException("Filter must have a \"type\": " + object);
        String type = object.get("type").getAsString();

        switch (type) {
            case "and":
            case "or":
            case "not":
                JsonArray filters = object.getAsJsonArray("filters");
                IFilter[] filterArray = new IFilter[filters.size()];
                for (int i = 0; i < filters.size(); i++) {
                    filterArray[i] = this.parse(filters.get(i).getAsJsonObject());
                }
                filter = switch (type) {
                    case "and" -> new FilterLogical.And(filterArray);
                    case "or" -> new FilterLogical.Or(filterArray);
                    case "not" -> {
                        if (filterArray.length != 1)
                            throw new IllegalArgumentException("Not filter must have exactly one filter");
                        yield new FilterLogical.Not(filterArray[0]);
                    }
                    default -> filter;
                };
                break;

            case "itemId":
                String itemId = object.get("itemId").getAsString();
                filter = new FilterItemId(itemId);
                break;

            // --- ここから追加：NBTフィルター群 ---
            case "nbt_has_key":
                filter = new FilterNBT.HasKey(object.get("key").getAsString());
                break;

            case "nbt_string":
                filter = new FilterNBT.StringEquals(
                        object.get("key").getAsString(),
                        object.get("value").getAsString()
                );
                break;

            case "nbt_int":
                filter = new FilterNBT.IntEquals(
                        object.get("key").getAsString(),
                        object.get("value").getAsInt()
                );
                break;

            case "nbt_has_enchantment":
                filter = new FilterNBT.HasEnchantment(object.get("enchantmentId").getAsString());
                break;

            case "nbt_damage_less_than":
                filter = new FilterNBT.DamageLessThan(object.get("maxDamage").getAsInt());
                break;

            case "nbt_has_any":
                filter = new FilterHasNBT();
                break;

            case "tag":
                filter = new FilterTag(object.get("tag").getAsString());
                break;
        }

        if (filter == null) throw new IllegalArgumentException("Filter Type Not Found: " + type);
        return filter;
    }
}
package jp.main.taikun.mysticaleverything.config;

import net.minecraft.world.item.ItemStack;

public abstract class FilterLogical implements IFilter {
    private final IFilter[] filters;
    public FilterLogical(IFilter[] filters) {
        this.filters = filters;
    }
    public boolean filter(ItemStack itemStack) {
        boolean[] conditions = new boolean[this.filters.length];
        for (int filterIndex = 0; filterIndex < this.filters.length; filterIndex++) {
            conditions[filterIndex] = this.filters[filterIndex].filter(itemStack);
        }
        return this.whatToDo(conditions);
    }
    protected abstract boolean whatToDo(boolean[] results);
    public static class Or extends FilterLogical {
        public Or(IFilter[] filters) {super(filters);}

        @Override
        protected boolean whatToDo(boolean[] results) {
            for (boolean result : results) {
                if (result) return true;
            }
            return false;
        }
    }
    public static class And extends FilterLogical {
        public And(IFilter[] filters) {super(filters);}

        @Override
        protected boolean whatToDo(boolean[] results) {
            for (boolean result : results) {
                if (!result) return false;
            }
            return true;
        }
    }
    public static class Not extends FilterLogical {
        public Not(IFilter filter) {super(new IFilter[]{filter});}

        @Override
        protected boolean whatToDo(boolean[] results) {
            return !results[0];
        }
    }
}

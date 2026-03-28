package io.github.raverbury.aggroindicator.config;

import com.google.gson.annotations.Expose;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ClientConfigValue {
    public static void pruneAll()
    {
        BaseConfigValue.pruneAll();
    }

    private abstract static class BaseConfigValue {
        private static final List<WeakReference<BaseConfigValue>> bcvs =
                new ArrayList<>();

        protected BaseConfigValue() {
            bcvs.add(new WeakReference<>(this));
        }

        public static void pruneAll() {
            for (int i = bcvs.size() - 1; i >= 0; i--) {
                WeakReference<BaseConfigValue> wr = bcvs.get(i);
                BaseConfigValue cv = wr.get();
                if (cv != null) {
                    cv.prune();
                }
            }
            bcvs.clear();
        }

        protected abstract void prune();
    }

    public static class ConfigValue<T> extends BaseConfigValue {
        @Expose
        private T value;

        @Expose(deserialize = false)
        private String comment;
        @Expose(deserialize = false)
        private T defaultValue;

        public ConfigValue(String comment, T defaultValue) {
            this.comment = comment;
            this.value = this.defaultValue = defaultValue;
        }

        public T getValue() {
            return value;
        }

        public final T getDefaultValue() {
            return defaultValue;
        }

        @Override
        protected void prune() {
            comment = null;
        }
    }

    public static class RangeConfigValue extends ConfigValue<Double> {
        @Expose(deserialize = false)
        private Double maxValue;
        @Expose(deserialize = false)
        private Double minValue;

        public RangeConfigValue(String comment, double defaultValue,
                                double minValue, double maxValue) {
            super(comment, defaultValue);
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        @Override
        public Double getValue() {
            return Math.clamp(super.getValue(), minValue, maxValue);
        }
    }
}
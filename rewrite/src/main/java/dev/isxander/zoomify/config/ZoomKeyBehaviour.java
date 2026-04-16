package dev.isxander.zoomify.config;

public enum ZoomKeyBehaviour {
    HOLD,
    TOGGLE;

    public static ZoomKeyBehaviour fromString(String value, ZoomKeyBehaviour fallback) {
        if (value == null) {
            return fallback;
        }
        for (ZoomKeyBehaviour behaviour : values()) {
            if (behaviour.name().equalsIgnoreCase(value)) {
                return behaviour;
            }
        }
        return fallback;
    }
}

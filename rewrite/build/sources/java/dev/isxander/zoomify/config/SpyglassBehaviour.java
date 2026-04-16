package dev.isxander.zoomify.config;

public enum SpyglassBehaviour {
    COMBINE,
    OVERRIDE,
    ONLY_ZOOM_WHILE_HOLDING,
    ONLY_ZOOM_WHILE_CARRYING;

    public static SpyglassBehaviour fromString(String value, SpyglassBehaviour fallback) {
        if (value == null) {
            return fallback;
        }
        for (SpyglassBehaviour behaviour : values()) {
            if (behaviour.name().equalsIgnoreCase(value)) {
                return behaviour;
            }
        }
        return fallback;
    }
}

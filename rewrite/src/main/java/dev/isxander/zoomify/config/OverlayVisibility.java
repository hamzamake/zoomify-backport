package dev.isxander.zoomify.config;

public enum OverlayVisibility {
    NEVER,
    HOLDING,
    CARRYING,
    ALWAYS;

    public static OverlayVisibility fromString(String value, OverlayVisibility fallback) {
        if (value == null) {
            return fallback;
        }
        for (OverlayVisibility visibility : values()) {
            if (visibility.name().equalsIgnoreCase(value)) {
                return visibility;
            }
        }
        return fallback;
    }
}

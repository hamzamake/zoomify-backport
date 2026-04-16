package dev.isxander.zoomify.config;

public enum SoundBehaviour {
    NEVER,
    ALWAYS,
    ONLY_SPYGLASS,
    WITH_OVERLAY;

    public static SoundBehaviour fromString(String value, SoundBehaviour fallback) {
        if (value == null) {
            return fallback;
        }
        for (SoundBehaviour behaviour : values()) {
            if (behaviour.name().equalsIgnoreCase(value)) {
                return behaviour;
            }
        }
        return fallback;
    }
}

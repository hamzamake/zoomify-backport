package dev.isxander.zoomify.zoom;

public enum TransitionType {
    INSTANT {
        @Override
        public double apply(double t) {
            return t;
        }
    },
    LINEAR {
        @Override
        public double apply(double t) {
            return t;
        }
    },
    EASE_IN_SINE {
        @Override
        public double apply(double t) {
            return 1.0D - Math.cos((t * Math.PI) / 2.0D);
        }

        @Override
        public double inverse(double x) {
            return Math.acos(-(x - 1.0D)) * 2.0D / Math.PI;
        }
    },
    EASE_OUT_SINE {
        @Override
        public double apply(double t) {
            return Math.sin((t * Math.PI) / 2.0D);
        }

        @Override
        public double inverse(double x) {
            return Math.asin(x) * 2.0D / Math.PI;
        }
    },
    EASE_IN_OUT_SINE {
        @Override
        public double apply(double t) {
            return -(Math.cos(Math.PI * t) - 1.0D) / 2.0D;
        }
    },
    EASE_IN_QUAD {
        @Override
        public double apply(double t) {
            return t * t;
        }

        @Override
        public double inverse(double x) {
            return Math.sqrt(x);
        }
    },
    EASE_OUT_QUAD {
        @Override
        public double apply(double t) {
            return 1.0D - (1.0D - t) * (1.0D - t);
        }

        @Override
        public double inverse(double x) {
            return -(Math.sqrt(-(x - 1.0D)) - 1.0D);
        }
    },
    EASE_IN_OUT_QUAD {
        @Override
        public double apply(double t) {
            if (t < 0.5D) {
                return 2.0D * t * t;
            }
            return 1.0D - Math.pow(-2.0D * t + 2.0D, 2.0D) / 2.0D;
        }
    },
    EASE_IN_CUBIC {
        @Override
        public double apply(double t) {
            return Math.pow(t, 3.0D);
        }

        @Override
        public double inverse(double x) {
            return Math.pow(x, 1.0D / 3.0D);
        }
    },
    EASE_OUT_CUBIC {
        @Override
        public double apply(double t) {
            return 1.0D - Math.pow(1.0D - t, 3.0D);
        }

        @Override
        public double inverse(double x) {
            return -Math.pow(-x + 1.0D, 1.0D / 3.0D) + 1.0D;
        }
    },
    EASE_IN_OUT_CUBIC {
        @Override
        public double apply(double t) {
            if (t < 0.5D) {
                return 4.0D * t * t * t;
            }
            return 1.0D - Math.pow(-2.0D * t + 2.0D, 3.0D) / 2.0D;
        }
    },
    EASE_IN_EXP {
        private final double cLog2_1023 = log(1023.0D, 2.0D);

        @Override
        public double apply(double t) {
            if (t == 0.0D) {
                return 0.0D;
            }
            if (t == 1.0D) {
                return 1.0D;
            }
            return Math.pow(2.0D, 10.0D * t - cLog2_1023) - 1.0D / 1023.0D;
        }

        @Override
        public double inverse(double x) {
            if (x == 0.0D) {
                return 0.0D;
            }
            if (x == 1.0D) {
                return 1.0D;
            }
            return Math.log(1023.0D * x + 1.0D) / (10.0D * Math.log(2.0D));
        }
    },
    EASE_OUT_EXP {
        private final double cLog2_1023 = log(1023.0D, 2.0D);
        private final double c10Ln2 = 10.0D * Math.log(2.0D);
        private final double cLn1023 = Math.log(1023.0D);

        @Override
        public double apply(double t) {
            if (t == 0.0D) {
                return 0.0D;
            }
            if (t == 1.0D) {
                return 1.0D;
            }
            return 1.0D - Math.pow(2.0D, 10.0D - cLog2_1023 - 10.0D * t) + 1.0D / 1023.0D;
        }

        @Override
        public double inverse(double x) {
            if (x == 0.0D) {
                return 0.0D;
            }
            if (x == 1.0D) {
                return 1.0D;
            }
            return -((Math.log(-((1023.0D * x - 1024.0D) / 1023.0D)) - c10Ln2 + cLn1023) / c10Ln2);
        }
    },
    EASE_IN_OUT_EXP {
        private final double cLog2_1023 = log(1023.0D, 2.0D);

        @Override
        public double apply(double t) {
            if (t == 0.0D) {
                return 0.0D;
            }
            if (t == 1.0D) {
                return 1.0D;
            }
            if (t < 0.5D) {
                return Math.pow(2.0D, 20.0D * t - cLog2_1023) - 1.0D / 1023.0D;
            }
            return 1.0D - Math.pow(2.0D, 10.0D - cLog2_1023 - 10.0D * t) + 1.0D / 1023.0D;
        }
    };

    public abstract double apply(double t);

    public double inverse(double x) {
        throw new UnsupportedOperationException();
    }

    public boolean hasInverse() {
        try {
            inverse(0.0D);
            return true;
        } catch (UnsupportedOperationException ignored) {
            return false;
        }
    }

    public TransitionType opposite() {
        switch (this) {
            case INSTANT:
                return INSTANT;
            case LINEAR:
                return LINEAR;
            case EASE_IN_SINE:
                return EASE_OUT_SINE;
            case EASE_OUT_SINE:
                return EASE_IN_SINE;
            case EASE_IN_OUT_SINE:
                return EASE_IN_OUT_SINE;
            case EASE_IN_QUAD:
                return EASE_OUT_QUAD;
            case EASE_OUT_QUAD:
                return EASE_IN_QUAD;
            case EASE_IN_OUT_QUAD:
                return EASE_IN_OUT_QUAD;
            case EASE_IN_CUBIC:
                return EASE_OUT_CUBIC;
            case EASE_OUT_CUBIC:
                return EASE_IN_CUBIC;
            case EASE_IN_OUT_CUBIC:
                return EASE_IN_OUT_CUBIC;
            case EASE_IN_EXP:
                return EASE_OUT_EXP;
            case EASE_OUT_EXP:
                return EASE_IN_EXP;
            case EASE_IN_OUT_EXP:
            default:
                return EASE_IN_OUT_EXP;
        }
    }

    public static TransitionType fromString(String value, TransitionType fallback) {
        if (value == null) {
            return fallback;
        }
        for (TransitionType type : values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return fallback;
    }

    private static double log(double value, double base) {
        return Math.log(value) / Math.log(base);
    }
}

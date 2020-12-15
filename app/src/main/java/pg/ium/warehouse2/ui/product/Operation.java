package pg.ium.warehouse2.ui.product;

public enum Operation {
    ADD {
        @Override
        public String toString() {
            return "+";
        }
    },
    SUBTRACT {
        @Override
        public String toString() {
            return "-";
        }
    }
}



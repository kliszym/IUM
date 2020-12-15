package pg.ium.warehouse2.ui.login;

public enum Role {
    WORKER, MASTER;

    public static Role set(boolean master) {
        if(master)
            return MASTER;
        else
            return WORKER;
    }
}

package business.artifacts.entity;

public class NoRegistryFoundOnDatabase extends Exception {
    public NoRegistryFoundOnDatabase() {
        super("No Registry Found");
    }

    public NoRegistryFoundOnDatabase(Throwable cause) {
        super("No Registry Found", cause);
    }

    public NoRegistryFoundOnDatabase(String s) {
        super(s);
    }
}

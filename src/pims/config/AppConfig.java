package pims.config;

public final class AppConfig {
    private AppConfig() {
    }

    public static final String DB_URL = "jdbc:mysql://localhost:3306/pims_db?useSSL=false&serverTimezone=UTC";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "root";
}

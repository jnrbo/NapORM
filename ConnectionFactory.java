
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * Created by junior - Oct / 2016.
 */

public class ConnectionFactory {

    private static Connection connection;
    private static Properties properties;
    private static final String URL = "jdbc:mysql://localhost/NOMEDOBANCO";
    private static final String USER = "root";
    private static final String PASSWORD = "senha";

    public static Connection connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();

            properties = new Properties();
            properties.setProperty("user", USER);
            properties.setProperty("password", PASSWORD);
            properties.setProperty("useSSL", "false");
            properties.setProperty("autoReconnect", "true");
            connection = DriverManager.getConnection(URL, properties);

            return connection;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao abrir Driver " + e);
        }
    }
}

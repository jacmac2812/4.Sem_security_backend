/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.dbcp2.BasicDataSource;

public class ConnectionFactory {

    private static BasicDataSource dataSource;

    private ConnectionFactory() {
    }

    public static Connection getConnection() throws SQLException {
        boolean isDeployed = (System.getenv("DEPLOYED") != null);
        if (isDeployed) {
            String user = System.getenv("USER");
            String pw = System.getenv("PW");
            String connection_str = System.getenv("CONNECTION_STR");
            String driver = "com.mysql.cj.jdbc.Driver";
            if (dataSource == null) {
                dataSource = new BasicDataSource();
                dataSource.setUrl(connection_str);
                dataSource.setDriverClassName(driver);
                dataSource.setUsername(user);
                dataSource.setPassword(pw);
            }

        } else {
            if (dataSource == null) {
                dataSource = new BasicDataSource();
                dataSource.setUrl("jdbc:mysql://localhost:3306/security");
                dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
                dataSource.setUsername("dev");
                dataSource.setPassword("ax2");
            }
        }
        return dataSource.getConnection();
    }
}

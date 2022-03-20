package ru.itis.orm;

import org.springframework.beans.factory.annotation.Autowired;
import ru.itis.Services.QueryServices;
import ru.itis.Services.ScanDB;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public class EntityManagerFactory {

    @Autowired
    private ScanDB db;
    @Autowired
    private QueryServices queryServices;

    private Connection connection;

    private final Map<Long, EntityManager> entityManagerMap = new HashMap<>();

    private CharSequence url;


    public EntityManagerFactory() {
        this.connection = getConnection();
    }

    public EntityManagerFactory(CharSequence url) {
        this.url = url;
    }

    private Connection getConnection() {
        return db.getConnection();
    }

    public EntityManager getEntityManager() {
        Long id = Thread.currentThread().getId();
        EntityManager em = entityManagerMap.get(id);
        if (em == null) {
            em = new EntityManagerImpl(db, queryServices);
        }
        return em;
    }

    public void closeEntityManager() {
        Long id = Thread.currentThread().getId();
        entityManagerMap.remove(id);
    }

    public void destroy() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            System.out.println("DbWork destroyed");
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    public CharSequence getUrl() {
        return url;
    }
}

package ru.itis.orm;

import ru.itis.Services.QueryServices;
import ru.itis.Services.ScanDB;
import ru.itis.orm.annotations.Column;
import ru.itis.orm.annotations.Entity;
import ru.itis.orm.exceptions.NoEntityException;
import ru.itis.orm.exceptions.NotFoundIdException;
import ru.itis.orm.exceptions.NullFieldException;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EntityManagerImpl implements EntityManager {


    private final ScanDB scanDB;

    private final QueryServices queryServices;

    public EntityManagerImpl(ScanDB scanDB, QueryServices queryServices) {
        this.scanDB = scanDB;
        this.queryServices = queryServices;
    }

    @Override
    public void persist(Object var1) throws Exception {
        if (!isEntity(var1))
            throw new NoEntityException("The " + var1.getClass() + "is not Entity !!!");

        String tableName = var1.getClass().getAnnotation(Entity.class).name();

        Map<Field, Object> f = getFieldsMap(var1, tableName);

        if (f == null)
            throw new NullFieldException();

        String sql = queryServices.getInsertQuery(f, tableName);

        scanDB.runQuery(sql);

        System.out.println(sql);
    }


    @Override
    public <T> T merge(T var1) throws Exception {
        if (!isEntity(var1))
            throw new NoEntityException("The " + var1.getClass() + "is not Entity !!!");

        String tableName = var1.getClass().getAnnotation(Entity.class).name();
        Long id = queryServices.getIdEntity(var1);
        if (!hasId(tableName, id))
            throw new NotFoundIdException();

        Map<Field, Object> f = getFieldsMap(var1, tableName);
        if (f == null)
            throw new NullFieldException();

        String sql = queryServices.getUpdateQuery(f, tableName, var1);
        scanDB.runQuery(sql);
        System.out.println(sql);

        return var1;
    }

    @Override
    public void remove(Object var1) throws Exception {
        if (!isEntity(var1))
            throw new NoEntityException("The " + var1.getClass() + "is not Entity !!!");

        String tableName = var1.getClass().getAnnotation(Entity.class).name();
        Long id = queryServices.getIdEntity(var1);
        if (!hasId(tableName, id))
            throw new NotFoundIdException();

        String sql = queryServices.getRemoveQuery(tableName, id);
        scanDB.runQuery(sql);

    }

    @Override
    public <T> T find(Class<T> var1, Object var2) throws Exception {
        if (!var1.isAnnotationPresent(Entity.class))
            throw new NoEntityException("The " + var1 + "is not Entity !!!");
        String tableName = var1.getAnnotation(Entity.class).name();
        Long id = (Long) var2;
        if (hasId(tableName, id)) {
            String sql = queryServices.getSelectQuery(tableName, id);
            return getFieldsValue(sql, var1);
        }
        return null;
    }

    private <T> T getFieldsValue(String sql, Class<T> var1) {

        ResultSet rs = scanDB.runExecuteQuery(sql);
        T result = null;

        try {
            result = var1.newInstance();
            while (rs.next()) {
                for (Field declaredField : result.getClass().getDeclaredFields()) {
                    declaredField.setAccessible(true);
                    String name = declaredField.getAnnotation(Column.class).name();
                    declaredField.set(result, getTypeField(declaredField, rs.getString(name)));
                }
            }
        } catch (InstantiationException | IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Map<Field, Object> getFieldsMap(Object var, String nameTable) {
        Map<Field, Object> nameAndValueFields = new HashMap<>();
        if (scanDB.tables.containsKey(nameTable)) {
            Field[] nameFields = var.getClass().getDeclaredFields();
            List<Object> fieldsValues = queryServices.getValueFields(var);
            for (int i = 0; i < fieldsValues.size(); i++) {
                nameAndValueFields.put(nameFields[i], fieldsValues.get(i));
            }
        }
        return nameAndValueFields;
    }

    private Object getTypeField(Field field, String value) {
        if (Long.class.equals(field.getType())) {
            return Long.parseLong(value);
        } else if (Double.class.equals(field.getType())) {
            return Double.parseDouble(value);
        }
        return value;
    }

    private boolean isEntity(Object var) {
        return var.getClass().isAnnotationPresent(Entity.class);
    }


    private boolean hasId(String tableName, Long id) {
        if (id != null) {
            String r = scanDB.existsQuery(tableName, id);
            return r.equals("t");
        }
        return false;
    }
}

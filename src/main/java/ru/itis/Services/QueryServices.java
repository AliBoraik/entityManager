package ru.itis.Services;

import org.springframework.stereotype.Component;
import ru.itis.orm.annotations.Column;
import ru.itis.orm.exceptions.NullFieldException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Component
public class QueryServices {
    public String getInsertQuery(Map<Field, Object> f, String nameTable) {

        String sql = "INSERT INTO " + nameTable + " ";

        StringBuilder names = new StringBuilder();
        StringBuilder values = new StringBuilder();
        Set<Field> keys = f.keySet();

        int length = keys.size();
        names.append("(");
        values.append("VALUES (");
        for (Field name : keys) {
            names.append(name.getAnnotation(Column.class).name());

            values.append(getValueField(f.get(name)));
            if (length != 1) {
                names.append(",");
                values.append(",");
                length--;
            }
        }
        names.append(")");
        values.append("); ");

        sql += names + " " + values;
        return sql;
    }

    private Object getValueField(Object f) {
        if (String.class.equals(f.getClass())) {
            return "'" + f + "'";
        }
        return f;
    }

    public String getSelectQuery(String nameTable, Long id) {
        return "SELECT * FROM " + nameTable + " WHERE id = " + id;
    }

    public String getRemoveQuery(String tableName, Long id) {
        return "DELETE FROM " + tableName + " where id = " + id;
    }

    public <T> String getUpdateQuery(Map<Field, Object> f, String nameTable, T var1) {
        if (f.size() != 0) {

            String sql = "UPDATE " + nameTable + " SET ";

            StringBuilder names = new StringBuilder();
            Set<Field> keys = f.keySet();

            int length = keys.size();

            for (Field name : keys) {
                names.append(name.getAnnotation(Column.class).name())
                        .append(" = ")
                        .append(getValueField(f.get(name)));
                if (length != 1) {
                    names.append(" , ");
                    length--;
                }
            }
            sql += names + " WHERE id = " + getIdEntity(var1);
            return sql;
        } else {
            System.out.println("Exception...");
            return null;
        }
    }

    public Long getIdEntity(Object var) {
        try {
            return (Long) var.getClass().getMethod("getId").invoke(var);

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            System.out.println("Exception getId ...");
        }
        return null;
    }


    public List<Object> getValueFields(Object var) {
        List<Object> fieldsValues = new ArrayList<>();
        try {
            for (Field declaredField : var.getClass().getDeclaredFields()) {
                declaredField.setAccessible(true);
                Object v = declaredField.get(var);
                if (v == null) throw new NullFieldException();
                fieldsValues.add(v);
            }
        } catch (IllegalAccessException | NullFieldException e) {
            System.out.println("Exception NullFieldException ");
        }
        return fieldsValues;
    }

}

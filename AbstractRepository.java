
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by junior - Set / 2018.
 */

public abstract class AbstractRepository<ENTITY extends AbstractEntity> {

    public void save(ENTITY entity) {
        try {
            String query = "INSERT INTO " + entity.getClass().getSimpleName().toLowerCase() + " (" + getAttributesName(entity) + ") VALUES (" + getAttributesAlias(entity) + ");";
            PreparedStatement statement = connection.prepareStatement(query);
            setValues(statement, getAttributeValues(entity));
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public abstract ArrayList<ENTITY> list();

    protected ResultSet listResultSet() {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + getEntityName());
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected ResultSet listResultSetWhereDate(String date) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + getEntityName() + " WHERE DATE_FORMAT(`date`, '%Y-%m') = ?");
            setValues(statement, new Object[] {date});
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public abstract ENTITY findById(Integer id);

    protected ResultSet findResultSetById(Integer id) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + getEntityName() + " WHERE id = " + id);
            ResultSet result = statement.executeQuery();
            result.next();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getAttributesName(ENTITY entity) {
        String fields = "";
        try {
            for (Field field : getAllFields(entity.getClass())) {
                field.setAccessible(true);
                if (AbstractEntity.class.isAssignableFrom(field.getType())) {
                    fields += field.getName() + "_id, ";
                } else {
                    fields += charUppercaseToUnderlineChar(field.getName()) + ", ";
                }
            }
            return fields.substring(0, fields.length() - 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet executeQuery(String query) {
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            return statement.executeQuery();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getAttributesAlias(ENTITY entity) {
        String values = "";
        int size = getAllFields(entity.getClass()).size();
        for (int i = 0; i < size; i++) {
            values += "?, ";
        }
        return values.substring(0, values.length() - 2);
    }

    private Object[] getAttributeValues(ENTITY entity) {
        ArrayList<Object> values = new ArrayList<>();
        try {
            for (Field field : getAllFields(entity.getClass())) {
                field.setAccessible(true);
                if (AbstractEntity.class.isAssignableFrom(field.getType())) {
                    values.add(((AbstractEntity)field.get(entity)).getId());
                } else {
                    values.add(field.get(entity));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values.toArray();
    }

    private void setValues(PreparedStatement statement, Object[] entries) {
        int size = entries.length;
        try {
            for (int i = 0; i < size; i++) {
                Object value = entries[i];
                int index = (i + 1);

                if (value instanceof Integer) {
                    statement.setInt(index, (int)value);
                } else if (value instanceof Date) {
                    statement.setDate(index, new java.sql.Date(((Date)value).getTime()));
                } else if (value instanceof BigDecimal) {
                    statement.setBigDecimal(index, (BigDecimal)value);
                } else if (value instanceof Boolean) {
                    statement.setBoolean(index, (Boolean)value);
                } else if (value instanceof AbstractEntity) {
                    statement.setInt(index, ((AbstractEntity)value).getId());
                } else {
                    statement.setString(index, (String)value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String charUppercaseToUnderlineChar(String text) {
        String converted = "";
        for (char c : text.toCharArray()) {
            if (Character.isUpperCase(c)) {
                converted += "_" + c;
            } else {
                converted += c;
            }
        }
        return converted.toLowerCase();
    }

    private String getEntityName() {
        ParameterizedType type = (ParameterizedType)getClass().getGenericSuperclass();
        String simpleName = ((type.getActualTypeArguments()[0])).getTypeName();

        String[] name = simpleName.split(Pattern.quote("."));
        return name[name.length - 1].toLowerCase();
    }

    public static ArrayList<Field> getAllFields(Class<?> type) {
        ArrayList<Field> fields = new ArrayList<>();

        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            fields.addAll(getAllFields(type.getSuperclass()));
        }
        return fields;
    }

    private Connection connection = ConnectionFactory.connect();

}

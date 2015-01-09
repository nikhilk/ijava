// ChartTableCommand.java
//

package ijava.extensions.charting;

import java.beans.*;
import java.lang.reflect.*;
import java.util.*;
import ijava.data.*;
import ijava.extensibility.*;

/**
 * Handles the %_chartTable command used to retrieve chart data to charted.
 */
public final class ChartTableCommand implements Command {

  private static final HashMap<Class<?>, String> SupportedTypes;

  private final Shell _shell;

  static {
    SupportedTypes = new HashMap<Class<?>, String>();
    ChartTableCommand.SupportedTypes.put(Boolean.class, "boolean");
    ChartTableCommand.SupportedTypes.put(Byte.class, "number");
    ChartTableCommand.SupportedTypes.put(Double.class, "number");
    ChartTableCommand.SupportedTypes.put(Float.class, "number");
    ChartTableCommand.SupportedTypes.put(Integer.class, "number");
    ChartTableCommand.SupportedTypes.put(Long.class, "number");
    ChartTableCommand.SupportedTypes.put(Short.class, "number");
    ChartTableCommand.SupportedTypes.put(Character.class, "string");
    ChartTableCommand.SupportedTypes.put(String.class, "string");

    // TODO: Support for Dates
  }

  public ChartTableCommand(Shell shell) {
    _shell = shell;
  }

  private List<?> getData(String name) throws EvaluationError {
    Object value = _shell.getVariable(name);
    if (value == null) {
      throw new EvaluationError("The name '" + name + "' doesn't exist or is null.");
    }

    if (!List.class.isAssignableFrom(value.getClass())) {
      throw new EvaluationError("The name '" + name + "' refers to non-list data.");
    }

    return (List<?>)value;
  }

  @Override
  public Object evaluate(String arguments, String data, long evaluationID,
                         Map<String, Object> metadata) throws Exception {
    List<?> items = getData(arguments);

    List<Map<String, Object>> columns = null;
    List<Map<String, Object>> rows = null;

    if (!items.isEmpty()) {
      TableGenerator generator;

      Object sampleItem = items.get(0);
      Class<?> itemClass = sampleItem.getClass();
      if (ChartTableCommand.SupportedTypes.containsKey(itemClass)) {
        generator = new ScalarTableGenerator();
      }
      else if (Map.class.isAssignableFrom(itemClass)) {
        generator = new KeyValueTableGenerator();
      }
      else {
        generator = new ObjectTableGenerator();
      }

      columns = generator.generateHeader(sampleItem);
      rows = new ArrayList<Map<String, Object>>();

      for (Object item: items) {
        rows.add(generator.generateRow(item));
      }
    }

    Map<String, Object> chartTable = new HashMap<String, Object>();
    chartTable.put("cols", columns);
    chartTable.put("rows", rows);

    return new Data(chartTable);
  }


  private static abstract class TableGenerator {

    protected Map<String, Object> createCell(Object value) {
      Map<String, Object> cell = new HashMap<String, Object>();
      cell.put("v", value);

      return cell;
    }

    protected Map<String, Object> createColumn(String name, Class<?> type) {
      Map<String, Object> column = new HashMap<String, Object>();
      column.put("id", name);
      column.put("label", name);
      column.put("type", ChartTableCommand.SupportedTypes.get(type));

      return column;
    }

    protected Map<String, Object> createRow(List<Map<String, Object>> cells) {
      Map<String, Object> row = new HashMap<String, Object>();
      row.put("c", cells);

      return row;
    }

    public abstract List<Map<String, Object>> generateHeader(Object item);

    public abstract Map<String, Object> generateRow(Object item);
  }

  private static final class ScalarTableGenerator extends TableGenerator {

    @Override
    public List<Map<String, Object>> generateHeader(Object item) {
      List<Map<String, Object>> columns = new ArrayList<Map<String, Object>>();
      columns.add(createColumn("item", item.getClass()));

      return columns;
    }

    @Override
    public Map<String, Object> generateRow(Object item) {
      List<Map<String, Object>> cells = new ArrayList<Map<String, Object>>();
      cells.add(createCell(item));

      return createRow(cells);
    }
  }

  private static final class KeyValueTableGenerator extends TableGenerator {

    private List<String> _keys;

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> generateHeader(Object item) {
      List<Map<String, Object>> columns = new ArrayList<Map<String, Object>>();
      _keys = new ArrayList<String>();

      Map<String, Object> data = (Map<String, Object>)item;
      for (Map.Entry<String, Object> entry: data.entrySet()) {
        String key = entry.getKey();
        Object value = entry.getValue();

        if ((value != null) && ChartTableCommand.SupportedTypes.containsKey(value.getClass())) {
          _keys.add(key);
          columns.add(createColumn(key, value.getClass()));
        }
      }

      return columns;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> generateRow(Object item) {
      Map<String, Object> data = (Map<String, Object>)item;

      List<Map<String, Object>> cells = new ArrayList<Map<String, Object>>();
      for (String key: _keys) {
        cells.add(createCell(data.get(key)));
      }

      return createRow(cells);
    }
  }

  private static final class ObjectTableGenerator extends TableGenerator {

    private List<Field> _fields;
    private List<Method> _getters;

    @Override
    public List<Map<String, Object>> generateHeader(Object item) {
      List<Map<String, Object>> columns = new ArrayList<Map<String, Object>>();

      _fields = new ArrayList<Field>();
      _getters = new ArrayList<Method>();

      try {
        Class<?> itemClass = item.getClass();

        Field[] allFields = itemClass.getFields();
        for (Field f: allFields) {
          if ((f.getModifiers() & Modifier.STATIC) != 0) {
            continue;
          }
          if (!ChartTableCommand.SupportedTypes.containsKey(f.getType())) {
            continue;
          }

          if ((f.getModifiers() & Modifier.STATIC) == 0) {
            _fields.add(f);
            columns.add(createColumn(f.getName(), f.getType()));
          }
        }

        BeanInfo beanInfo = Introspector.getBeanInfo(itemClass);
        for (PropertyDescriptor pd: beanInfo.getPropertyDescriptors()) {
          String name = pd.getName();
          if (name.equals("class")) {
            continue;
          }

          Object value = null;
          try {
            value = pd.getReadMethod().invoke(item);
          }
          catch (Exception e) {
          }
          if ((value == null) || !ChartTableCommand.SupportedTypes.containsKey(value.getClass())) {
            continue;
          }

          _getters.add(pd.getReadMethod());
          columns.add(createColumn(name, value.getClass()));
        }
      }
      catch (IntrospectionException e) {
      }

      return columns;
    }

    @Override
    public Map<String, Object> generateRow(Object item) {
      List<Map<String, Object>> cells = new ArrayList<Map<String, Object>>();
      for (Field f: _fields) {
        Object value = null;
        try {
          value = f.get(item);
        }
        catch (Exception e) {
        }

        cells.add(createCell(value));
      }

      for (Method getter: _getters) {
        Object value = null;
        try {
          value = getter.invoke(item);
        }
        catch (Exception e) {
        }

        cells.add(createCell(value));
      }

      return createRow(cells);
    }
  }
}

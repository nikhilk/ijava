// Table.java
//

package ijava.data;

import java.beans.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * Represents tabular data produced from a list or map.
 */
@SuppressWarnings("rawtypes")
public final class Table {

  private static final Set<Class<?>> ScalarTypes;

  private final List _list;
  private final Map _map;

  private int _limit;
  private boolean _showTotal;

  static {
    ScalarTypes = new HashSet<Class<?>>();
    Table.ScalarTypes.add(Boolean.class);
    Table.ScalarTypes.add(Byte.class);
    Table.ScalarTypes.add(Character.class);
    Table.ScalarTypes.add(Double.class);
    Table.ScalarTypes.add(Float.class);
    Table.ScalarTypes.add(Integer.class);
    Table.ScalarTypes.add(Long.class);
    Table.ScalarTypes.add(Short.class);
    Table.ScalarTypes.add(String.class);
  }

  /**
   * Initializes a Table object with data represented by a list.
   * @param list the data to render in a table.
   */
  public Table(List list) {
    _list = list;
    _map = null;
  }

  /**
   * Initializes a Table object with data represented by a map.
   * @param map the data to render in a table.
   */
  public Table(Map map) {
    _map = map;
    _list = null;
  }

  /**
   * Limits the number of items to render.
   * @param limit the maximum number of items to render in the table.
   * @return the modified Table object.
   */
  public Table addLimit(int limit) {
    return addLimit(limit, /* showTotal */ false);
  }

  /**
   * Limits the number of items to render.
   * @param limit the maximum number of items to render in the table.
   * @param showTotal whether to display the total number of items.
   * @return the modified Table object.
   */
  public Table addLimit(int limit, boolean showTotal) {
    _limit = limit;
    _showTotal = showTotal;
    return this;
  }

  /**
   * Helper to determine if an object should be considered as a scalar value or not.
   * @param value the value to check.
   * @return true if the value is of scalar type, false otherwise.
   */
  private static boolean isScalar(Object value) {
    return (value == null) || Table.ScalarTypes.contains(value.getClass());
  }

  /**
   * Renders the set of items into an HTML table.
   * @return the markup representing the table.
   */
  private String renderItems(Iterable items, int totalCount, TableGenerator generator) {
    StringBuilder sb = new StringBuilder();
    sb.append("<table>");
    sb.append("<tr>");
    generator.generateHeader(sb);
    sb.append("</tr>");

    int count = 0;
    for (Object item: items) {
      if ((_limit > 0) && (count == _limit)) {
        break;
      }

      sb.append("<tr>");
      generator.generateRow(sb, item);
      sb.append("</tr>");

      count++;
    }

    sb.append("</table>");

    if (_showTotal && (_limit > 0) && (totalCount > _limit)) {
      sb.append("<br />");
      sb.append("<span>");
      sb.append(String.format("Rendered %d of %d items.", _limit, totalCount));
      sb.append("</span>");
    }

    return sb.toString();
  }

  /**
   * Renders the list data into an HTML table.
   * @return the markup representing the table.
   */
  private String renderList() {
    TableGenerator generator;

    Object sampleItem = _list.get(0);
    if (Table.isScalar(sampleItem)) {
      generator = new ScalarTableGenerator(sampleItem);
    }
    else {
      generator = new RecordTableGenerator(sampleItem);
    }

    return renderItems(_list, _list.size(), generator);
  }

  /**
   * Renders the map data into an HTML table.
   * @return the markup representing the table.
   */
  private String renderMap() {
    TableGenerator valueGenerator;

    Set items = _map.entrySet();
    Object sampleValue = ((Map.Entry)items.iterator().next()).getValue();

    if (Table.isScalar(sampleValue)) {
      valueGenerator = new ScalarTableGenerator(sampleValue);
    }
    else {
      valueGenerator = new RecordTableGenerator(sampleValue);
    }

    return renderItems(items, items.size(), new KeyValueTableGenerator(valueGenerator));
  }

  /**
   * Generates a mime representation of this object.
   * @return a text/html representation of this object.
   */
  public Map<String, String> toMimeRepresentation() {
    String markup;
    if (((_list == null) || (_list.size() == 0)) &&
        ((_map == null) || (_map.size() == 0))) {
      markup = "<span>Empty list.</span>";
    }
    else {
      markup = (_list != null) ? renderList() : renderMap();
    }

    HashMap<String, String> representations = new HashMap<String, String>();
    representations.put("text/html", markup);

    return representations;
  }


  private interface TableGenerator {

    public void generateHeader(StringBuilder sb);

    public void generateRow(StringBuilder sb, Object item);
  }

  private static final class ScalarTableGenerator implements TableGenerator {

    private final String _type;

    public ScalarTableGenerator(Object sampleItem) {
      String type = sampleItem.getClass().getSimpleName();
      if (type.length() == 0) {
        type = "items";
      }

      _type = type;
    }

    @Override
    public void generateHeader(StringBuilder sb) {
      sb.append("<th>");
      sb.append(_type);
      sb.append("</th>");
    }

    @Override
    public void generateRow(StringBuilder sb, Object item) {
      sb.append("<td>");
      if (item == null) {
        sb.append("&nbsp;");
      }
      else {
        sb.append(item);
      }
      sb.append("</td>");
    }
  }

  private static final class RecordTableGenerator implements TableGenerator {

    private final List<Field> _fields;
    private final List<String> _properties;
    private final List<Method> _getters;

    public RecordTableGenerator(Object sampleItem) {
      _fields = new ArrayList<Field>();
      _properties = new ArrayList<String>();
      _getters = new ArrayList<Method>();

      try {
        Class<?> itemClass = sampleItem.getClass();

        Field[] allFields = itemClass.getFields();
        for (Field f: allFields) {
          if ((f.getModifiers() & Modifier.STATIC) == 0) {
            _fields.add(f);
          }
        }

        BeanInfo beanInfo = Introspector.getBeanInfo(itemClass);
        for (PropertyDescriptor pd: beanInfo.getPropertyDescriptors()) {
          String name = pd.getName();
          if (name.equals("class")) {
            continue;
          }

          _properties.add(pd.getName());
          _getters.add(pd.getReadMethod());
        }
      }
      catch (IntrospectionException e) {
      }
    }

    @Override
    public void generateHeader(StringBuilder sb) {
      if (_fields.size() != 0) {
        for (Field f: _fields) {
          sb.append("<th>");
          sb.append(f.getName());
          sb.append("</th>");
        }
      }

      if (_properties.size() != 0) {
        for (String p: _properties) {
          sb.append("<th>");
          sb.append(p);
          sb.append("</th>");
        }
      }
    }

    @Override
    public void generateRow(StringBuilder sb, Object item) {
      if (_fields.size() != 0) {
        for (Field f: _fields) {
          sb.append("<td>");
          try {
            sb.append(f.get(item));
          }
          catch (Exception e) {
            sb.append("&nbsp;");
          }
          sb.append("</td>");
        }
      }

      if (_properties.size() != 0) {
        for (Method getter: _getters) {
          sb.append("<td>");
          try {
            sb.append(getter.invoke(item));
          }
          catch (Exception e) {
            sb.append("&nbsp;");
          }
          sb.append("</td>");
        }
      }
    }
  }

  private static final class KeyValueTableGenerator implements TableGenerator {

    private final TableGenerator _valueTableGenerator;

    public KeyValueTableGenerator(TableGenerator valueTableGenerator) {
      _valueTableGenerator = valueTableGenerator;
    }

    @Override
    public void generateHeader(StringBuilder sb) {
      sb.append("<th></th>");
      _valueTableGenerator.generateHeader(sb);
    }

    @Override
    public void generateRow(StringBuilder sb, Object item) {
      Map.Entry entry = (Map.Entry)item;

      sb.append("<td>");
      sb.append(entry.getKey());
      sb.append("</td>");

      _valueTableGenerator.generateRow(sb, entry.getValue());
    }
  }
}

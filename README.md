# csv4j
---
**Generate csv file with columns defined in runtime!**

CSV4j is a **dynamic**, lightweight Java library to generate and write Java POJO as CSV

It is a dynamic library that can be used to generate columns that are defined in runtime.
The map is the key here. It can be configured to convert its keys as columns.

## Quick Start
---

First, we have a simple class
~~~
public class CJModel {
  
    @CJName("CSV ID")
    private Integer id;

    @CJIgnore
    private String name;

    @CJName("CSV Values")
    private List<String> values;

    @CJMap
    private Map<Integer, String> map;

    public CJModel(Integer id, String name, List<String> values, Map<Integer, String> map) {
        this.id = id;
        this.name = name;
        this.values = values;
        this.map = map;
    }
}
~~~

The `CJMap` annotation flags the map as a dynamic map, and its keys will be set as columns.
<br>
Then we create an instance of CJModel
~~~
Map<Integer, String> map = new HashMap<>();
map.put(1, "One");
map.put(2, "Two");
map.put(3, "Three");

List<CJModel> cjModels = Arrays.asList(
        new CJModel(435, "CJ Model", Arrays.asList("A", "B"), map)
);
~~~
Finally we initialize the CJWriter. The easiest way to generate the csv from the CJWriter is by calling `csv()` to generate csv as string and `writeCSV()` to write the csv directly to `Writer` object.
~~~
CJWriter<CJModel> cjWriter = new CJWriter<CJModel>(CJModel.class);

// CSV as string
String csv = cjWriter.csv(cjModels);

// Write csv directly to a writer
Writer writer = new FileWriter("data.csv");
cjWriter.writeCSV(writer, cjModels);
~~~
The result is


| CSV ID | CSV Values | 1      | 2    | 3     | 4    | 
|--------|------------|--------|------|-------|------| 
| 435    | [A;B]      | One    | Two  | Three | null | 
| 54     | [C;D]      | OneOne | null | null  | Four |


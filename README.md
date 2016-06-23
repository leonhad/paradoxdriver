# Project #
This project aims to create a pure Paradox Java Driver by using the JDBC 4 technology.

To use it, install the driver class `com.googlecode.paradox.Driver` and use the JDBC String `jdbc:paradox:/dir/to/files`.

## Example ##

```
    Class.forName("com.googlecode.paradox.Driver");
    java.sql.Connection conn = DriverManager.getConnection("jdbc:paradox:./db");
```

# Downloads #
If you using Maven, you can use the dependencies below:

```
<dependency>
    <groupId>com.googlecode.paradoxdriver</groupId>
    <artifactId>paradoxdriver</artifactId>
    <version>1.2</version>
</dependency>
```

If you just want the files, you can grab them here:

  * [paradoxdriver-1.2.jar](http://search.maven.org/remotecontent?filepath=com/googlecode/paradoxdriver/paradoxdriver/1.2/paradoxdriver-1.2.jar)
  * [paradoxdriver-1.2-javadoc.jar](http://search.maven.org/remotecontent?filepath=com/googlecode/paradoxdriver/paradoxdriver/1.2/paradoxdriver-1.2-javadoc.jar)
  * [paradoxdriver-1.2-sources.jar](http://search.maven.org/remotecontent?filepath=com/googlecode/paradoxdriver/paradoxdriver/1.2/paradoxdriver-1.2-sources.jar)

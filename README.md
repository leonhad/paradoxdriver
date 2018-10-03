# Project #
This project aims to create a pure Paradox Java Driver by using the JDBC 4 technology.

To use it, install the driver class `com.googlecode.paradox.Driver` and use the JDBC String `jdbc:paradox:/dir/to/files`.

## Example ##

```
    Class.forName("com.googlecode.paradox.Driver");
    java.sql.Connection conn = DriverManager.getConnection("jdbc:paradox:./db");
```

# Downloads #
If you're using Maven, you can use the dependencies below:

```
<dependency>
    <groupId>com.googlecode.paradoxdriver</groupId>
    <artifactId>paradoxdriver</artifactId>
    <version>1.4.0</version>
</dependency>
```

If you're using Gradle, add the following line to your dependencies section:

    compile 'com.googlecode.paradoxdriver:paradoxdriver:1.4.0'

If you just want the files, you can grab the [latest release](https://github.com/leonhad/paradoxdriver/releases/latest).


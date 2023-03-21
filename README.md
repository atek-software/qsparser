![GitHub Workflow Status](https://img.shields.io/github/actions/workflow/status/llalexandru00/qsparser/maven-publish.yml?logo=github)
![GitHub](https://img.shields.io/github/license/llalexandru00/qsparser)
 [![SonarCloud Coverage](https://sonarcloud.io/api/project_badges/measure?project=llalexandru00_qsparser&metric=coverage)](https://sonarcloud.io/component_measures/metric/coverage/list?id=llalexandru00_qsparser)
 [![SonarCloud Maintainability](https://sonarcloud.io/api/project_badges/measure?project=llalexandru00_qsparser&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=llalexandru00_qsparser)
 [![SonarCloud Bugs](https://sonarcloud.io/api/project_badges/measure?project=llalexandru00_qsparser&metric=reliability_rating)](https://sonarcloud.io/component_measures/metric/reliability_rating/list?id=llalexandru00_qsparser)
 [![SonarCloud Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=llalexandru00_qsparser&metric=security_rating)](https://sonarcloud.io/component_measures/metric/security_rating/list?id=llalexandru00_qsparser)
 
QsParser is a [query-string](https://en.wikipedia.org/wiki/Query_string) parsing library for Java. Its goal is to make Java back-end applications able to parse query strings generated on a JavaScript client with [qs](https://github.com/ljharb/qs). This way, one can close the gap between the JS front-end data structures and the Java back-end representations.

## Parser
QsParser's core feature is the parsing of query-strings into internal nested representations (dictionaries, arrays, strings, etc.). The implementation is fully compatible with how [qs](https://github.com/ljharb/qs) is parsing query-strings into JS objects.

```java
QueryStringParser parser = new QueryStringParser();
Value value = parser.parse("filters[name][:eq]=John&filters[age][:ge]=18&filters[age][:le]=60");
System.out.println(value);
/* {
   filters : {
      name : {
         :eq : John
      }
      age : {
         :ge : 18
         :le : 60
      }
   }
} */
```

### :star: Features
The main value of QsParser is the support for nested data structures encoded as query strings. Therefore, the parser is able to identify nested compound representations (i.e. dictionaries, arrays) and merge them into a single hierarchical data structure.
* Specify the components of an array using square brackets and integer indexes: `date[0]=14&date[1]=10&date[2]=2000`
* Make use of no-index arrays to gather data into a single structure: `part[]="hello "&part[]="world"`
* Use sparse arrays or choose to automatically compact them: `checkbox[0]=yes&checkbox[3]=yes&checkbox[10]=yes`
* Represent the members of dictionaries using square brackets or dots: `person.name=John&person.birth.year=2000`
* Allow nesting of dictionaries and arrays: `person.name=John&person.year[0]=2000&person.year[1].value=2012`
* Supports jQuery parameter strings: `filter%5B0%5D%5B%5D=John`
* Supports multiple encodings: `%8c%a7=%91%e5%8d%e3%95%7b` can parse as `{ 県 : 大阪府 }` 


### :gear: Options
A parser instance can be customized to use certain parsing options:
* Allow dots as component specificator instead of square brackets
* Choose if integer-indexed structures should be parsed as arrays
* Override parsing limits for the array size, representation depth or parameter count
* Customize the charset and the decoder for the query string to be parsed (or use the default UTF-8 URLDecoder)
* Adapt for specific query strings in regard to the leading prefix (?) or the delimiter (&)
* Parse commas inside values and generate arrays

## Builder
QsParser has a custom set of structures to represent a query string. A vital functionality when it comes down to the generation of query strings is the QS Builder. This ensure that a fully compatible query string is generated based on an input representation.

```java
QueryStringBuilder builder = new QueryStringBuilder();
StringValue address = StringValue.get("address");
StringValue city = StringValue.get("city");
StringValue ny = StringValue.get("New York");
String queryString = builder.stringify(new DictValue().append(address, new DictValue().append(city, ny)));
System.out.println(queryString);
/* address%5Bcity%5D=New+York */
```

### :star: Features
The QS Builder is able to stringify any structure represented through the provided model. Therefore, the builder is able to generate a string construct which can be parsed back into a structure equal to the initial input. 
* Specify the format in which the arrays should convert: `names=John&names=Jack` or `names[0]=John&names[1]=Jack` or `names[]=John&names[]=Jack`
* Compress arrays and use comma separator: `names=John,Jack`
* Represent the members of dictionaries using square brackets or dots: `person.name=John&person.birth.year=2000`
* Allow nesting of dictionaries and arrays: `person.name=John&person.year[0]=2000&person.year[1].value=2012`
* Encode query string and support custom encodings: `filter%5B0%5D%5B%5D=John`

## Download
Download from the [GitHub Packages](https://github.com/llalexandru00/qsparser/packages/) section or depend through Maven or Gradle. 
```xml
<dependency>
  <groupId>ro.atek</groupId>
  <artifactId>qsparser</artifactId>
  <version>LATEST</version>
</dependency>
```
:warning: The packages are not uploaded to Maven Central yet.

:heavy_exclamation_mark: The packages are uploaded to [GitHub Packages](https://github.com/llalexandru00/qsparser/packages/), so you may need to add the required repository or use [jitpack](https://jitpack.io).
```xml
<repository>
   <id>github</id>
   <name>GitHub Packages</name>
   <url>https://maven.pkg.github.com/llalexandru00/qsparser</url>
</repository>
```

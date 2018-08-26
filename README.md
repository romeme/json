# json

JSON with List<String> and Map<String, String> interfaces

Usages:

```
 List<Integer> integers =
                     Json.Array.string("[1,2,3,4,5]")
                             .stream()
                             .map(Integer::parseInt)
                             .collect(Collectors.toList());

```

```
class Person {
    private final String name;
    private final int age;

    private Person(Map<String, String> map) {
        this(map.get("name"), Integer.parseInt(map.get("age")));
    }

    private Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}

String input = "{"name":"Roman", "age":"28"}";//age is a string
Person person =
        Optional.of(Json.Object.string(input))
                .map(map -> new Person(map.get("name"), Integer.parseInt(map.get("age"))))
                .get();
//or
person = Optional.of(Json.Object.string(input))
        .map(Person::new)
        .get();

```
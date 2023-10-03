# wheels

## Modules

- [x] [tinyspring](tinyspring/README.md)
- [x] [tinybatis](tinybatis/README.md)
- [x] [tinynetty](tinynetty/README.md)
- [x] [tinyrpc](tinyrpc/README.md)
- [x] [tinyweb](tinyweb/README.md)

## Requirements

- JDK 21

## Build

```sh
mvn clean package
```

## Some Useful Tips

Replace all `tinyspring` to `tinybatis` in all java files.

```sh
find . -type f -name "*.java" -exec sed -i 's/tinyspring/tinybatis/g' {} +
```
# Java Core

Dự án thực hành các kiến thức Java core: OOP, Collections, Generics, Streams, Concurrency, Exception Handling, I/O, Date/Time.

## Yêu cầu

- JDK 21
- Maven 3.8+

## Cấu trúc dự án

```
src/main/java/com/javacore/
├── Main.java              # Entry point
├── oop/                   # Kế thừa, đa hình, trừu tượng hóa
├── collections/           # List, Map, Set...
├── generics/               # Generic type
├── streams/                # Stream API
├── concurrency/             # Thread, đồng bộ hóa
├── exception/               # Xử lý ngoại lệ
├── io/                       # Đọc/ghi file (NIO)
└── datetime/                 # java.time API

src/test/java/com/javacore/   # Unit test (JUnit 5)
```

## Chạy dự án

Build và chạy test:

```bash
mvn test
```

Chạy chương trình chính:

```bash
mvn exec:java
```

Chạy một class demo cụ thể (ví dụ `StreamDemo`):

```bash
mvn exec:java -Dexec.mainClass="com.javacore.streams.StreamDemo"
```

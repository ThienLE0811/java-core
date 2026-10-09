# Java Core

Dự án thực hành Java core theo lộ trình của [dev.java/learn](https://dev.java/learn/).

## Yêu cầu

- JDK 21
- Maven 3.8+

## Quy ước

Mỗi topic là một package `tNN_<tên_topic>` nằm trực tiếp dưới `com.javacore`.
Số thứ tự `NN` phản ánh thứ tự học, nên IDE sắp xếp theo tên là ra đúng lộ trình.

Mỗi package có `package-info.java` liệt kê checklist nội dung cần thực hành
và mục tương ứng trên dev.java. Đọc file đó trước khi bắt đầu một topic mới.

## Lộ trình

```
src/main/java/com/javacore/
├── Main.java
│
│   # Language Basics
├── t01_syntax_variables_types/                             ✅
├── t02_scope_operators_control_flow/                       ✅
├── t03_string_array_methods/                               ✅
│
│   # Classes, Objects, Inheritance
├── t04_class_object_constructor_encapsulation/             ✅
├── t05_inheritance_abstraction_interface_polymorphism/     ✅
├── t06_final_enum_record_nested_class_object_lifecycle/    ✅
│
│   # Core language nâng cao
├── t07_numbers_strings_text_blocks/
├── t08_generics/
├── t09_exceptions/
├── t10_sealed_classes_pattern_matching/
├── t11_annotations/
├── t12_packages_modules/
│
│   # Functional programming
├── t13_lambda_functional_interfaces/
├── t14_collections_framework/
├── t15_stream_api_collectors/
├── t16_imperative_to_functional/
│
│   # Standard library APIs
├── t17_date_time_api/
├── t18_regular_expressions/
├── t19_io_nio_files/
├── t20_serialization/
│
│   # Concurrency & Runtime
├── t21_threads_synchronization_executors/
├── t22_virtual_threads_concurrency_utils/
├── t23_reflection_api/
└── t24_jdbc/

src/test/java/com/javacore/     # Unit test (JUnit 5), mirror cấu trúc package trên
```

Chú thích: `✅` = đã có bài thực hành.

## Chạy dự án

Build và chạy test:

```bash
mvn test
```

Chạy `Main`:

```bash
mvn exec:java
```

Chạy một class demo cụ thể — dùng `java -cp`, **không** dùng `exec:java`.
Lý do: `pom.xml` đang hardcode `<mainClass>com.javacore.Main</mainClass>`, nên
`-Dexec.mainClass` bị ghi đè và luôn chạy `Main`:

```bash
mvn -q compile
java -cp target/classes com.javacore.t05_inheritance_abstraction_interface_polymorphism.PolymorphismDemo
```

Trên Windows, nếu output tiếng Việt bị lỗi font, bật UTF-8 cho console:

```powershell
chcp 65001
java -Dstdout.encoding=UTF-8 -cp target\classes com.javacore.t05_inheritance_abstraction_interface_polymorphism.PolymorphismDemo
```

Khi học thì gọn nhất là bấm Run trên từng class trong IDE (IntelliJ / VS Code).

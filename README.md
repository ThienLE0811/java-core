# Java Core

Dự án thực hành Java core bám theo [dev.java/learn](https://dev.java/learn/).

## Yêu cầu

- JDK 21
- Maven 3.8+

## Quy ước đặt tên

Cây package **mirror đúng đường dẫn URL** của dev.java, chỉ đổi `-` thành `_`
cho hợp lệ với Java:

| URL dev.java | Package |
|---|---|
| `/learn/language/constructs/basics/` | `com.javacore.language.constructs.basics` |
| `/learn/api/date-time-regex/regex/` | `com.javacore.api.date_time_regex.regex` |
| `/learn/language/fp/refactoring-to-functional-style/` | `com.javacore.language.fp.refactoring_to_functional_style` |

Không có số thứ tự trong tên package, vì **dev.java không đánh số module**.
Thứ tự học được ghi trong Javadoc của từng `package-info.java` (`Bước N/41`)
và trong bảng dưới đây.

Package cấp nhóm (`language`, `language/fp`, …) chỉ để phân cấp, không chứa code.
Package lá chứa bài thực hành + `package-info.java` với link tài liệu gốc và
checklist nội dung.

## Lộ trình — 41 tutorial

### 1. Your First Steps in Java &nbsp;·&nbsp; `first_steps`

| # | Package con | Tutorial |
|---|---|---|
| 1 | `first_java_code` | Your first Java code |
| 2 | `using_ides` | Using IDEs |
| 3 | `evolution` | Java Platform Evolution |
| 4 | `using_preview` | Using JDK Preview Features |

### 2. Getting to Know the Language &nbsp;·&nbsp; `language`

**`language/constructs`** — Language Basics

| # | Package con | Tutorial |
|---|---|---|
| 5 | `basics` | Java Language Basics |
| 6 | `numbers_strings` | Numbers and Strings |

**`language/oop`** — Object Oriented Programming

| # | Package con | Tutorial |
|---|---|---|
| 7 | `classes` | Objects, Classes, Interfaces, Packages, and Inheritance |
| 8 | `classes_objects` | Classes and Objects |
| 9 | `records` | Using Records to Model Immutable Data |
| 10 | `inheritance` | Inheritance |
| 11 | `interfaces` | Interfaces |
| 12 | `packages` | Packages |

**`language/fp`** — Generics, Lambda, and Pattern Matching

| # | Package con | Tutorial |
|---|---|---|
| 13 | `generics` | Generics |
| 14 | `lambdas` | Lambda Expressions |
| 15 | `pattern_matching` | Using Pattern Matching |
| 16 | `refactoring_to_functional_style` | Refactoring from the Imperative to the Functional Style |

**`language/annotations_exceptions`** — Annotations and Exceptions

| # | Package con | Tutorial |
|---|---|---|
| 17 | `annotations` | Annotations |
| 18 | `exceptions` | Exceptions |

### 3. Mastering the API &nbsp;·&nbsp; `api`

**`api/collections_and_streams`** — Collections and Streams

| # | Package con | Tutorial |
|---|---|---|
| 19 | `collections_framework` | The Collections Framework |
| 20 | `streams` | The Stream API |

**`api/io`** — Managing Out-of-Memory Data

| # | Package con | Tutorial |
|---|---|---|
| 21 | `java_io` | The Java I/O API |
| 22 | `modernio` | Common I/O Tasks in Modern Java |
| 23 | `ffm` | The Foreign Function and Memory API |

**`api/date_time_regex`** — Managing Dates and Regular Expressions

| # | Package con | Tutorial |
|---|---|---|
| 24 | `date_time` | The Date Time API |
| 25 | `regex` | Regular Expressions |

**`api/reflection_method_handles`** — Reflection and Method Handles

| # | Package con | Tutorial |
|---|---|---|
| 26 | `introduction_to_java_reflection` | Introduction to Java Reflection |
| 27 | `reflection` | The Reflection API |
| 28 | `introduction_to_method_handles` | Introduction to Method Handles |

**`api/virtual_threads`** — Virtual Threads (tutorial lá, không có package con)

| # | Package | Tutorial |
|---|---|---|
| 29 | `api/virtual_threads` | Virtual Threads |

### 4. Organizing your Application &nbsp;·&nbsp; `organizing`

| # | Package con | Tutorial |
|---|---|---|
| 30 | `modules` | Modules |
| 31 | `jlink` | Creating Runtime and Application Images with JLink |

### 5. Getting to know the JVM &nbsp;·&nbsp; `jvm`

| # | Package con | Tutorial |
|---|---|---|
| 32 | `core_tools` | The Core JDK Tools |
| 33 | `monitoring_troubleshooting` | JFR, Monitoring and Troubleshooting |
| 34 | `security_gc` | Security and Garbage Collection |
| 35 | `other_tools` | Other Tools |

### 6. Fundamentals of Security using JDK Libraries &nbsp;·&nbsp; `security`

| # | Package con | Tutorial |
|---|---|---|
| 36 | `intro` | Introduction to Java Encryption/Decryption |
| 37 | `digital_signature` | Fundamentals of Digital Signatures and Certificates in Java |
| 38 | `monitor` | Monitoring Java Application Security with JDK tools and JFR Events |
| 39 | `app_integrity_tools` | Leveraging JDK Tools and Updates to Help Safeguard Java Applications |

### 7. Validate Foundational Knowledge &nbsp;·&nbsp; `validate`

| # | Package con | Tutorial |
|---|---|---|
| 40 | `java_cert_overview` | Getting Started with Java Certification |
| 41 | `debugging` | Debugging in Java |

### Phần cố ý bỏ qua

**Developing Rich Client Applications with JavaFX** (`/learn/javafx/`) — không liên
quan tới Java backend.

## Chạy dự án

```bash
mvn test          # build và chạy test
mvn exec:java     # chạy Main (mặc định)
```

Chạy riêng một class đang học — override `exec.mainClass`:

```bash
mvn -q exec:java -Dexec.mainClass="com.javacore.first_steps.first_java_code.HelloWorldDemo"
```

(`exec.mainClass` là property trong `pom.xml`, mặc định trỏ `com.javacore.Main`;
truyền `-Dexec.mainClass=...` sẽ ghi đè property này cho lần chạy đó.)

Trên Windows, nếu output tiếng Việt bị lỗi font, bật UTF-8 cho console trước:

```powershell
chcp 65001
mvn -q exec:java "-Dexec.mainClass=com.javacore.first_steps.first_java_code.HelloWorldDemo" "-Dstdout.encoding=UTF-8"
```

Khi học thì gọn nhất vẫn là bấm Run trên từng class trong IDE — không cần nhớ lệnh gì.

## Phiên bản JDK

Project build bằng **JDK 25** (`maven.compiler.source/target` trong `pom.xml`,
và `JAVA_HOME` của máy). Chọn JDK 25 vì dev.java viết tutorial theo JDK mới
nhất (hiện là 27), nên các ví dụ dùng cú pháp mới — ví dụ `IO.println(...)`
(chính thức từ JDK 25) — build và chạy được trực tiếp qua `mvn`/nút Run trong
IDE mà không cần thiết lập riêng.

Lưu ý: đây là lựa chọn riêng cho repo học tập này. Project Spring Boot thực tế
sau này nhiều khả năng vẫn dùng JDK LTS (21 hoặc 17), không phải 25.

## Lịch sử

Cấu trúc cũ (tự đặt tên, không bám dev.java) lưu ở commit `be6e899`. Lấy lại:

```bash
git checkout be6e899 -- src/main/java/com/javacore/t05_inheritance_abstraction_interface_polymorphism
```

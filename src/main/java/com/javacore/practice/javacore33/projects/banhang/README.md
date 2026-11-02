# Bài 33 — Hệ thống bán hàng

Dự án cuối của lộ trình Java Core: quản lý sản phẩm, khách hàng, đặt hàng (trừ tồn kho),
hủy đơn (hoàn tồn kho) và báo cáo doanh thu / sản phẩm bán chạy.

Java thuần, không framework, chạy in-memory. Đặc tả đầy đủ ở [SPEC.md](SPEC.md) — mỗi quy
tắc được đánh số `R1`…`R21` và được tham chiếu lại trong comment của code và tên test.

## Cấu trúc

```
banhang/
├─ Store.java              nơi giữ state + toàn bộ nghiệp vụ
├─ BanHangDemo.java        dữ liệu mẫu + in báo cáo
├─ model/
│  ├─ Product.java         tồn kho, giamTon/hoanTon
│  ├─ Customer.java        record
│  ├─ OrderLine.java       record, chụp tên + giá tại thời điểm đặt
│  ├─ Order.java           vòng đời đơn, ủy quyền OrderStatus (bài 12)
│  └─ OutOfStockException.java
└─ report/SalesReport.java doanh thu, top bán chạy
```

## Chạy

```bash
mvn compile
mvn exec:java -Dexec.mainClass=com.javacore.practice.javacore33.projects.banhang.BanHangDemo
mvn test -Dtest='StoreTest,SalesReportTest'
```

Console Windows cần `chcp 65001` để hiển thị đúng tiếng Việt.

## Những quyết định thiết kế và lý do

### 1. `OrderLine` chụp lại tên và giá, không trỏ sang `Product` (R11)

Nếu dòng đơn hàng giữ tham chiếu `Product`, chỉ cần đổi giá hôm nay là doanh thu tháng
trước đổi theo — sai **mà không một exception nào được ném ra**. Snapshot làm đơn hàng trở
thành bản ghi lịch sử bất biến: sản phẩm sau đó đổi tên, đổi giá, thậm chí bị xóa thì báo
cáo cũ vẫn đúng và vẫn đọc được tên.

Đây cũng là cách mọi hệ thống bán hàng thật lưu dòng đơn hàng. Cái giá phải trả là dữ liệu
lặp lại giữa `Product` và `OrderLine` — đổi lại là tính đúng đắn của mọi báo cáo về sau.

### 2. Đặt hàng là thao tác nguyên tử, kiểm tra hai lượt (R3)

```
Lượt 1: duyệt cả giỏ, chỉ kiểm tra → mọi lỗi ném ở đây, kho còn nguyên
Lượt 2: tới đây chắc chắn đủ hàng → mới trừ
```

Phương án ngược lại — trừ dần rồi hoàn ngược khi gặp lỗi — nhìn gọn hơn nhưng đoạn hoàn
ngược hầu như không bao giờ chạy trong thực tế, nên đó đúng là chỗ bug nằm im mà không ai
phát hiện. Cách hai lượt không có gì để rollback, nên không có gì để rollback sai.

Hệ quả kiểm chứng được: giỏ 3 dòng mà dòng thứ 3 thiếu hàng thì **không dòng nào bị trừ** và
không đơn nào được tạo.

### 3. Hủy đơn: đổi trạng thái trước, hoàn kho sau (R10)

```java
don.updateStatus(OrderStatus.CANCELLED);   // ném nếu đã hủy / đã ship / đã giao
for (OrderLine dong : don.getDongHang()) { ... hoanTon ... }
```

Đảo hai dòng này là có bug "hủy hai lần hoàn kho hai lần" — và nguy hiểm ở chỗ **test vẫn
xanh** nếu chỉ `assertThrows`: exception vẫn được ném đúng loại, chỉ có tồn kho là đã phình
lên. Vì vậy test `huyDon_haiLan_lanHaiNemLoiVaKhoKhongPhinh` assert cả hai vế.

### 4. Luật chuyển trạng thái nằm ở `OrderStatus`, không ở `Store` (R6, R7)

`Store` không có một dòng `if (status == ...)` nào. Quy tắc "không hủy được đơn đã giao" là
hệ quả trực tiếp của bảng `canTransitionTo` viết từ bài 12, và `Order.updateStatus` tự ném
`InvalidStatusTransitionException`. Luật sống ở đúng một chỗ nên không thể lệch nhau giữa
các lời gọi.

### 5. Tồn kho đóng trong `Product`, không có setter (R2)

`Product` chỉ phơi ra `giamTon(n)` / `hoanTon(n)` / `duTon(n)`, và `giamTon` tự ném
`OutOfStockException` thay vì kẹp về 0 — tồn kho âm là lỗi logic, giấu nó đi bằng `Math.max`
chỉ làm sai ở chỗ khác và khó truy hơn.

**Điểm yếu còn lại, ghi ra để không tự lừa mình:** `Store.themSanPham(Product)` nhận object
từ ngoài và `timSanPham` trả thẳng `Product` ra, nên trên lý thuyết người gọi vẫn có thể
`store.timSanPham("SP01").giamTon(5)`, đi vòng qua khóa của `Store`. Đóng chặt được bằng
cách cho `Store` tự tạo `Product` và chỉ trả ra bản read-only; chưa làm vì phạm vi bài tập,
nhưng đây là rò rỉ đóng gói thật chứ không phải chuyện lý thuyết.

### 6. `synchronized` khóa thô trên `Store` thay vì khóa từng `Product` (R19)

`datHang` động tới nhiều sản phẩm cùng lúc, nên khóa mảnh theo từng `Product` sẽ cần lock
ordering để tránh deadlock (đúng bài học bài 34). Với một hệ in-memory, đánh đổi throughput
lấy sự đơn giản và tính đúng đắn là xứng đáng. Khi nào `Store` chuyển sang Spring Data JPA,
khóa này sẽ được thay bằng `@Transactional` cộng khóa ở tầng database, không phải bằng lock
mảnh ở tầng Java.

### 7. Tiền dùng `long` (VND), không dùng `double` (R21)

`double` không biểu diễn chính xác số thập phân, nên cộng dồn doanh thu qua nhiều đơn sẽ
lệch dần một cách im lặng. VND không có đơn vị nhỏ hơn 1 đồng nên `long` vừa khít, không sai
số, và test so sánh bằng `==` được. Giới hạn: không dùng được cho tiền tệ có phần lẻ — khi
đó phải chuyển sang `BigDecimal` (cũng là kiểu sẽ map vào cột `NUMERIC` khi lên JPA).

### 8. Doanh thu chỉ tính đơn `DELIVERED` (R13)

Tiền chỉ được ghi nhận khi hàng đã đến tay khách; đơn đang xử lý còn có thể bị hủy. Đánh
đổi: demo phải đẩy đơn qua đủ `PENDING → CONFIRMED → SHIPPED → DELIVERED` mới thấy doanh thu
khác 0. Phương án thay thế là tính mọi đơn chưa `CANCELLED`, nhưng khi đó con số phải được
gọi đúng tên là *giá trị đặt hàng*, không phải doanh thu.

### 9. Top bán chạy có tie-break tới tận `productId` (R14)

Xếp theo số lượng giảm dần, hòa thì theo doanh thu giảm dần, vẫn hòa thì theo `productId`
tăng dần. Thiếu tie-break thì thứ tự phụ thuộc thứ tự duyệt map, và test sẽ lúc xanh lúc đỏ
mà không ai hiểu vì sao.

### 10. `Store` không biết gì về console

Không một dòng `System.out` nào trong nghiệp vụ; mọi in ấn nằm ở `BanHangDemo`. Nhờ vậy khi
lên Spring Boot, thêm `OrderController` lên trên mà không phải sửa một dòng nào trong
`Store` — nó vốn đã đúng hình dáng của một service layer.

## Test

20 test, mỗi test bám vào một quy tắc trong `SPEC.md`. Nguyên tắc chung: **assert cả hai vế**
— exception đúng loại, *và* state không bị đổi sai.

| Test | Quy tắc |
|---|---|
| `huyDon_haiLan_lanHaiNemLoiVaKhoKhongPhinh` | R8 — hủy hai lần |
| `datHang_vuotTonKho_nemLoiVaKhoKhongDoi` | R1 — đặt vượt tồn kho |
| `haiThreadCungDatMonHangCuoiCung_chiMotThreadThanhCong` | R20 — hai thread tranh món cuối |
| `datHang_motDongThieuHang_khongDongNaoBiTru` | R3 — tính nguyên tử |
| `capNhatSanPham_doiGia_khongLamDoiDonDaDat` | R11, R18 — snapshot giá |
| `baoCao_khongDoiKhiSanPhamBiSuaGiaHoacBiXoa` | R15 — báo cáo đọc từ `OrderLine` |

Test đồng thời lặp **500 vòng**: race chỉ thỉnh thoảng mới lộ, chạy một lần thì ngay cả bản
không đồng bộ cũng có thể xanh. Hai thread xuất phát cùng lúc bằng `CountDownLatch` thay vì
hy vọng chúng tình cờ gặp nhau.

## Hướng phát triển

Khi lên Spring Boot, ánh xạ gần như cơ học:

| Bài 33 | Spring Boot |
|---|---|
| `Store.datHang(...)` | `OrderService.datHang(...)` |
| `Map<String, Product>` | `ProductRepository` (Spring Data JPA) |
| `synchronized` | `@Transactional` + khóa ở tầng DB |
| `OutOfStockException` | `@ExceptionHandler` → HTTP 409 |
| `BanHangDemo.main()` | `OrderController` + `@RequestBody` |

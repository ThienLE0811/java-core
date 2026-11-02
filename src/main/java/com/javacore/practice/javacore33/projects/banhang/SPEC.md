# Bài 33 - Hệ thống bán hàng (SPEC)

## 1. Phạm vi

Hệ thống bán hàng in-memory, chạy trong một tiến trình: quản lý sản phẩm, khách hàng, đặt
hàng (trừ tồn kho), hủy đơn (hoàn tồn kho) và báo cáo doanh thu / sản phẩm bán chạy.

Cố tình **không** làm: thanh toán, vận chuyển, khuyến mãi, tài khoản / phân quyền, lưu trữ
xuống file hay database, và không expose REST API. Đầu vào là tham số method, đầu ra là giá
trị trả về hoặc exception.

## 2. Entity

| Loại | Trường | Bất biến |
|---|---|---|
| `Product` | `id`, `ten`, `gia`, `tonKho` | `gia >= 0`, `tonKho >= 0` tại mọi thời điểm; `id` bất biến |
| `Customer` | `id`, `ten`, `email` | `id` bất biến; `ten`, `email` không rỗng |
| `OrderLine` | `productId`, `tenSanPham`, `donGia`, `soLuong` | `soLuong > 0`; immutable sau khi tạo |
| `Order` | `id`, `customerId`, `List<OrderLine>`, `status`, `thoiGianDat` | danh sách dòng hàng không rỗng và không đổi sau khi tạo; chỉ `status` thay đổi |

`OrderStatus` **dùng lại** `..javacore33.oop.donhang.OrderStatus` (bài 12), kể cả
`canTransitionTo` và `isFinal`. Không định nghĩa enum trạng thái mới.

`Store` **không phải entity**: là nơi giữ toàn bộ state (danh mục sản phẩm, khách hàng, đơn
hàng) và là chỗ duy nhất chứa nghiệp vụ. Mọi thay đổi state đi qua `Store`.

`SalesReport` chỉ đọc, không đổi state.

## 3. Nghiệp vụ

| Thao tác | Vào | Ra | Tác dụng phụ lên state |
|---|---|---|---|
| `themSanPham` | `Product` | — | thêm vào danh mục |
| `capNhatSanPham` | `id`, `ten`, `gia` | — | đổi tên/giá; **không** đổi tồn kho |
| `nhapKho` | `id`, `soLuong` | — | `tonKho += soLuong` |
| `xoaSanPham` | `id` | — | xóa khỏi danh mục |
| `timSanPham` / `danhSachSanPham` | `id` / — | `Product` / `List<Product>` | không |
| `themKhachHang` | `Customer` | — | thêm vào danh bạ |
| `datHang` | `customerId`, `Map<productId, soLuong>` | `Order` (`PENDING`) | trừ tồn kho mọi sản phẩm trong giỏ; thêm đơn mới |
| `capNhatTrangThai` | `orderId`, `OrderStatus` | — | đổi trạng thái đơn (uỷ quyền cho `canTransitionTo`) |
| `huyDon` | `orderId` | — | đơn sang `CANCELLED`; **hoàn lại tồn kho** mọi dòng hàng |
| `doanhThu` | — | tổng tiền | không |
| `topSanPhamBanChay` | `n` | `List` n sản phẩm | không |

## 4. Quy tắc

### Tồn kho

- **R1** — `tonKho` không bao giờ âm. Mọi thao tác làm nó âm đều bị từ chối bằng exception,
  không bao giờ bị kẹp về 0.
- **R2** — Chỉ `Store` được đổi tồn kho. `Product` không có setter tồn kho, chỉ có
  `giamTon(n)` / `hoanTon(n)` tự kiểm tra R1.
- **R3** — `datHang` là **nguyên tử**: kiểm tra đủ tồn kho cho *toàn bộ* giỏ hàng trước, chỉ
  khi tất cả đều đủ mới trừ. Giỏ 3 dòng mà dòng thứ 3 thiếu hàng thì **không dòng nào bị
  trừ**, và không có đơn nào được tạo.
- **R4** — Giỏ hàng rỗng, hoặc có dòng `soLuong <= 0` → từ chối, không tạo đơn.
- **R5** — `nhapKho` chỉ nhận `soLuong > 0`. Giảm tồn kho bằng tay không nằm trong phạm vi
  bài này; tồn kho chỉ giảm qua `datHang`.

### Vòng đời đơn hàng

- **R6** — Đơn mới luôn ở `PENDING`. Mọi chuyển trạng thái đi qua `OrderStatus.canTransitionTo`;
  không nơi nào tự viết lại luật chuyển trạng thái.
- **R7** — Chỉ hủy được đơn `PENDING` hoặc `CONFIRMED` (hệ quả trực tiếp của bảng chuyển
  trạng thái bài 12). Đơn `SHIPPED` đã rời kho và đơn `DELIVERED` đã giao thì **không hủy
  được**.
- **R8** — Hủy đơn đã `CANCELLED` → lỗi, **không** idempotent. *Lý do: hủy hai lần hầu như
  luôn là bug của người gọi (double-click, retry sai chỗ); im lặng bỏ qua sẽ che bug đi. Bù
  lại người gọi phải bắt exception nếu muốn retry an toàn.*
- **R9** — Hủy đơn hoàn lại **đúng** số lượng của từng dòng hàng, **đúng một lần**. Vì R7 chỉ
  cho hủy khi hàng chưa rời kho, mọi lần hủy hợp lệ đều hoàn kho — không có trường hợp hủy
  mà không hoàn.
- **R10** — Kiểm tra trạng thái và hoàn kho phải nằm trong **cùng một vùng đồng bộ**. Nếu
  tách ra, hai thread cùng hủy một đơn có thể hoàn kho hai lần.

### Tiền và báo cáo

- **R11** — `OrderLine` **chụp lại** (`snapshot`) `tenSanPham` và `donGia` tại thời điểm đặt,
  không trỏ sang `Product`. *Lý do: nếu trỏ sang `Product`, đổi giá hôm nay sẽ làm đổi doanh
  thu của tháng trước mà không lỗi nào báo. Đây cũng là cách mọi hệ thống bán hàng thật lưu
  dòng đơn hàng.*
- **R12** — Thành tiền một dòng = `donGia * soLuong`. Tổng tiền đơn = tổng các dòng.
- **R21** — Tiền biểu diễn bằng `long`, đơn vị đồng (VND), không dùng `double`. *Lý do:
  `double` không biểu diễn chính xác số thập phân nên cộng dồn doanh thu sẽ lệch một cách im
  lặng. VND không có đơn vị nhỏ hơn 1 đồng nên `long` vừa khít và so sánh được bằng `==`.
  Đánh đổi: không dùng được cho tiền tệ có phần lẻ — khi đó phải chuyển sang `BigDecimal`.*
- **R13** — Doanh thu chỉ tính đơn ở trạng thái `DELIVERED`. *Lý do: tiền chỉ được ghi nhận
  khi hàng đã đến tay khách; đơn đang xử lý có thể bị hủy. Đánh đổi: demo phải đẩy đơn qua đủ
  `PENDING → CONFIRMED → SHIPPED → DELIVERED` mới thấy doanh thu khác 0.*
- **R14** — `topSanPhamBanChay` dùng **cùng tập đơn** với R13 (chỉ `DELIVERED`), xếp theo
  tổng số lượng bán giảm dần. Hòa thì xếp theo doanh thu giảm dần, vẫn hòa thì theo `productId`
  tăng dần. *Lý do tie-break: không có nó thì thứ tự phụ thuộc thứ tự duyệt map và test sẽ
  lúc xanh lúc đỏ.*
- **R15** — Báo cáo đọc số liệu từ `OrderLine`, không từ `Product`. Nhờ R11, sản phẩm bị sửa
  giá hay bị xóa thì báo cáo cũ vẫn đúng và vẫn đọc được tên.

### Danh mục

- **R16** — `id` sản phẩm và khách hàng là duy nhất. Thêm trùng `id` → lỗi.
- **R17** — Không xóa được sản phẩm đang nằm trong **đơn còn có thể bị hủy** (`PENDING`,
  `CONFIRMED`). *Lý do: hủy đơn cần chỗ để hoàn kho (R9); xóa mất sản phẩm thì không hoàn vào
  đâu được.* Đơn `SHIPPED` theo R7 không hủy được nữa nên không chặn xóa. Sản phẩm chỉ xuất
  hiện trong đơn `SHIPPED` / `DELIVERED` / `CANCELLED` thì xóa được, vì R11 đã giữ đủ dữ liệu
  cho báo cáo.
- **R18** — `capNhatSanPham` đổi giá **không** ảnh hưởng đơn đã đặt (hệ quả của R11).

### Đồng thời

- **R19** — `Store` là thread-safe. Mọi method đổi state đều `synchronized` trên chính
  `Store`. *Lý do chọn khóa thô thay vì khóa từng `Product`: `datHang` động tới nhiều sản
  phẩm cùng lúc, khóa mảnh sẽ cần lock ordering để tránh deadlock — phức tạp không đáng với
  một hệ in-memory. Đánh đổi: throughput thấp, chấp nhận được.*
- **R20** — Hai thread cùng đặt món hàng cuối cùng: đúng một thread thành công, thread kia
  nhận `OutOfStockException`, tồn kho cuối bằng 0 — không âm, không còn dư.

## 5. Lỗi

| Tình huống | Exception |
|---|---|
| Tham số `null` | `NullPointerException` (qua `Objects.requireNonNull`) |
| `soLuong <= 0`, giá âm, giỏ rỗng, tên/`id` rỗng (R4, R5) | `IllegalArgumentException` |
| Không tìm thấy `id` sản phẩm / khách hàng / đơn | `NoSuchElementException` |
| Thêm trùng `id` (R16) | `IllegalStateException` |
| Không đủ tồn kho (R1, R3) | `OutOfStockException` (mới, kế thừa `IllegalStateException`) |
| Chuyển trạng thái sai, hủy đơn đã giao / đã hủy (R7, R8) | `InvalidStatusTransitionException` (dùng lại bài 12) |
| Xóa sản phẩm còn trong đơn chưa kết thúc (R17) | `IllegalStateException` |

Mọi exception được ném **trước** khi state thay đổi: thất bại thì state giữ nguyên như trước
khi gọi.

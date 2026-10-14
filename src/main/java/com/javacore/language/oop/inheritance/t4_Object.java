package com.javacore.language.oop.inheritance;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Object as a Superclass
 * <p>
 * 0. Vị trí bài này
 * - Trang 4/5 của section Inheritance. t1 mục 1 đã nói Object nằm ở đỉnh
 * mọi cây kế thừa; trang này mở ra xem CỤ THỂ mình được thừa hưởng
 * những gì, và cái nào thì phải tự viết lại.
 * - Một câu cho cả bài: mọi class đều có sẵn sáu method này, nhưng bản
 * mặc định của equals và hashCode so sánh theo DANH TÍNH object chứ
 * không theo dữ liệu - và nếu chỉ sửa một trong hai thì chương trình
 * hỏng một cách IM LẶNG (mục 4).
 * - Mọi kết quả dán trong ghi chú này đều chạy thật trên JDK 25 của repo.
 * <p>
 * ======================================================================
 * TRANG GỐC - OBJECT AS A SUPERCLASS (mục 1 đến 7)
 * ======================================================================
 * <p>
 * 1. SÁU METHOD KẾ THỪA TỪ OBJECT
 * - protected Object clone() throws CloneNotSupportedException
 * - public boolean equals(Object obj)
 * - protected void finalize() throws Throwable      (bỏ dần, mục 7)
 * - public final Class getClass()                   (final, mục 5)
 * - public int hashCode()
 * - public String toString()
 * - Ngoài ra còn nhóm đồng bộ hóa notify(), notifyAll(), wait() - trang
 * gốc nói rõ là không bàn ở bài này. Xem mục 11.
 * - Chỉ cần nhớ ba cái đầu tiên cần quan tâm hằng ngày: toString, equals,
 * hashCode. Ba cái còn lại gần như không bao giờ tự viết.
 * <p>
 * 2. toString - method nên ghi đè đầu tiên
 * - Nguyên văn: "You should ALWAYS CONSIDER overriding the toString()
 * method in your classes".
 * - Bản mặc định là tên class, dấu @, rồi hashCode dạng hex. Đã chạy:
 * toString mặc định : Tran@1dbd16a6
 * identityHashCode  : 1dbd16a6
 * Hai dòng đó khớp nhau - chứng minh phần sau dấu @ chính là hashCode
 * mặc định in ra hệ 16, KHÔNG phải địa chỉ bộ nhớ như nhiều người nghĩ.
 * - Vì sao nên ghi đè: toString được gọi ngầm ở rất nhiều nơi - khi nối
 * chuỗi, khi println, khi log, khi debugger hiển thị. Một dòng log ghi
 * DonHang@6d06d69c là một dòng log vô dụng.
 * <p>
 * 3. equals - mặc định là so sánh DANH TÍNH
 * - Bản mặc định của Object dùng đúng toán tử == : hai tham chiếu có trỏ
 * tới CÙNG MỘT object hay không. Đã chạy:
 * new Tran().equals(new Tran()) -&gt; false
 * - Muốn so sánh theo DỮ LIỆU thì phải tự ghi đè.
 * - Nhớ lại t2 mục 7: phải là equals(Object), không phải equals(TenClass).
 * Luôn viết annotation Override để compiler canh giúp.
 * - Và câu luật quan trọng nhất của cả trang: "If you override equals(),
 * you MUST override hashCode() as well".
 * <p>
 * 4. hashCode - vì sao bắt buộc đi kèm equals
 * - Hợp đồng: "By definition, if two objects are EQUAL, their HASH CODE
 * must also be EQUAL". Chiều ngược lại không bắt buộc - hai object khác
 * nhau được phép trùng hashCode (đụng độ).
 * - Bản mặc định sinh số theo danh tính từng object, nên hai object cùng
 * dữ liệu cho hai số khác nhau. Ghi đè equals mà quên hashCode là phá
 * vỡ hợp đồng ngay lập tức.
 * - Đã chạy thật, class chỉ ghi đè equals:
 * c1.equals(c2)        : true
 * hashCode bằng nhau?  : false
 * HashSet size (2 obj) : 2        &lt;- tưởng 1
 * HashMap.get(c2)      : null     &lt;- vừa put bằng c1 xong
 * List.contains(c2)    : true     &lt;- CHỖ NÀY VẪN ĐÚNG
 * - Dòng cuối chính là lý do bug này sống lâu: List không băm, nó chỉ gọi
 * equals nên vẫn ra đúng. Chỉ các collection BĂM mới hỏng. Code chạy
 * đúng ở chỗ này, sai ở chỗ kia, không exception, không log.
 * - Sửa lại cho đủ cả hai thì mọi thứ về đúng chỗ:
 * HashSet size (2 obj) : 1
 * HashMap.get(d2)      : giá trị
 * - Cách viết gọn và an toàn: Objects.hash(field1, field2) cho hashCode,
 * và pattern matching cho equals:
 * o instanceof DuCa d &amp;&amp; ma.equals(d.ma)
 * <p>
 * 5. getClass - final, không ai ghi đè được
 * - "You cannot override getClass()". Đã chạy thật:
 * error: getClass() in A cannot override getClass() in Object
 * overridden method is final
 * - Lý do nó phải final: đây là câu trả lời cho "object này THẬT RA là
 * gì". Cho phép ghi đè thì object nói dối được về chính nó, và toàn bộ
 * cơ chế phản chiếu lẫn ép kiểu mất chỗ dựa.
 * - Trả về một đối tượng Class mang siêu dữ liệu. Đã chạy:
 * getSimpleName : DuCa
 * getSuperclass : class java.lang.Object
 * "x".getClass().getInterfaces() -&gt; [Serializable, Comparable,
 * CharSequence, Constable, ConstantDesc]
 * - Lưu ý: getClass() luôn trả về kiểu THẬT, kể cả khi biến khai báo kiểu
 * cha - đúng tinh thần đa hình ở t3.
 * <p>
 * 6. clone - sao chép NÔNG, và đầy cạm bẫy
 * - Muốn clone được thì class phải implement Cloneable. Thiếu nó thì
 * super.clone() ném exception. Đã chạy:
 * java.lang.CloneNotSupportedException: QuenCloneable
 * - Cloneable là một interface RỖNG, không có method nào. Nó chỉ là một
 * cái nhãn báo cho Object.clone() biết "class này cho phép sao chép".
 * Một thiết kế rất cũ và bị chê nhiều.
 * - Bản của Object sao chép NÔNG: copy từng field một. Field nguyên thủy
 * thì được bản riêng, field tham chiếu thì hai object DÙNG CHUNG. Đã
 * chạy thật:
 * gốc.so = 1       &lt;- sửa bản sao không ảnh hưởng
 * gốc.ds = [a, b]  &lt;- SỬA BẢN SAO LÀM ĐỔI CẢ BẢN GỐC
 * cùng một list? true
 * - Đúng y vấn đề bất biến NÔNG đã gặp ở bước 9 mục 7 với record. Muốn
 * sao chép sâu thì phải tự copy từng field tham chiếu trong clone().
 * - Thực tế: hầu như không ai dùng clone() nữa. Thay bằng constructor
 * sao chép, static factory copyOf, hoặc record với method tạo bản mới.
 * Biết clone để ĐỌC được code cũ là đủ.
 * <p>
 * 7. finalize - đã chết, đừng đụng tới
 * - "As of Java SE 9 the finalize() method had been DEPRECATED, and as of
 * Java SE 18, DEPRECATED FOR REMOVAL". Ghi đè nó bị "strongly
 * discouraged".
 * - Đã thử ghi đè trên JDK 25, javac cảnh báo:
 * warning: [removal] finalize() in Object has been deprecated and
 * marked for removal
 * - Vì sao bỏ: không ai bảo đảm nó được gọi, không bảo đảm gọi lúc nào,
 * và nó làm chậm thu gom rác. Dọn tài nguyên mà trông chờ vào nó là
 * thả nổi.
 * - Thay bằng AutoCloseable và try-with-resources - đóng tài nguyên tại
 * chỗ, đúng lúc, nhìn thấy được trong code.
 * <p>
 * ======================================================================
 * NGOÀI TRANG GỐC - NHỮNG CHỖ THỰC SỰ GÂY LỖI
 * ======================================================================
 * <p>
 * 8. equals PHẢI ĐỐI XỨNG - và kế thừa rất dễ phá luật này
 * - Hợp đồng đầy đủ của equals có năm điều: phản xạ, ĐỐI XỨNG, bắc cầu,
 * nhất quán, và x.equals(null) luôn false.
 * - Đối xứng nghĩa là a.equals(b) phải bằng b.equals(a). Nghe hiển nhiên,
 * nhưng chỉ cần một class con thêm field là vỡ. Đã chạy thật với Diem
 * (so x) và DiemMau extends Diem (so x và màu):
 * p.equals(cp) : true
 * cp.equals(p) : false
 * đối xứng?    : false
 * - Hậu quả nhìn thấy được ngay:
 * List.of(p).contains(cp)  : false
 * List.of(cp).contains(p)  : true
 * Cùng một cặp object, đảo thứ tự cho hai câu trả lời trái ngược.
 * - Cách tránh: dùng getClass() thay cho instanceof nếu muốn hai class
 * khác nhau không bao giờ bằng nhau; hoặc để class là final; hoặc dùng
 * record, vốn đã final sẵn. Đây là lý do rất thực tế để ưu tiên record
 * cho các kiểu mang dữ liệu.
 * - Nguyên tắc ngắn gọn: đừng mở rộng một class đã ghi đè equals rồi
 * thêm field vào phần so sánh. Hãy chứa nó làm field thay vì kế thừa.
 * <p>
 * 9. BỘ CÔNG CỤ java.util.Objects
 * - Objects.equals(a, b) an toàn với null hai phía. Đã chạy:
 * Objects.equals(null, null) -&gt; true
 * - Objects.hash(a, b, c) gói việc tính hashCode nhiều field thành một
 * dòng. Objects.hashCode(x) an toàn với null.
 * - Objects.requireNonNull(x, "thông điệp") dùng để kiểm tra đối số ngay
 * đầu constructor - ném NullPointerException có thông điệp rõ ràng,
 * thay vì để nó nổ ở một nơi xa hơn nhiều.
 * - Objects.toString(x, "mặc định") tránh in ra chữ null.
 * <p>
 * 10. hashCode KHÔNG PHẢI ĐỊA CHỈ BỘ NHỚ
 * - Rất nhiều tài liệu cũ nói vậy, nhưng sai. Nó là một con số do JVM
 * sinh và GHI NHỚ cho object đó; bộ thu gom rác di chuyển object trong
 * bộ nhớ mà hashCode vẫn giữ nguyên.
 * - Hệ quả thực dụng, giống hệt bài ordinal của enum và hashCode của
 * record: ĐỪNG lưu hashCode ra file hay database. Chạy lại chương
 * trình là số khác.
 * <p>
 * 11. wait / notify - biết để tránh
 * - Ba method này phải gọi khi đang giữ khóa của chính object đó, tức là
 * bên trong khối synchronized. Gọi bên ngoài thì nổ ngay. Đã chạy:
 * java.lang.IllegalMonitorStateException: current thread is not owner
 * - Code hiện đại gần như không dùng tới chúng nữa. Dùng các công cụ
 * trong java.util.concurrent - chúng an toàn hơn và khó dùng sai hơn
 * nhiều. Phần này sẽ gặp lại ở section Concurrency.
 * <p>
 * 12. GÓC NHÌN PRODUCTION
 * - Với kiểu chỉ mang dữ liệu: dùng record. Có sẵn equals, hashCode,
 * toString đúng chuẩn, không quên, không lệch khi thêm field. Đã chạy:
 * record R(int x) {} -&gt; R[x=1], equals = true.
 * - Với JPA entity thì NGƯỢC LẠI, phải rất cẩn thận. Entity có vòng đời:
 * trước khi lưu thì id còn null, sau khi lưu mới có id. Lấy toàn bộ
 * field ra so sánh sẽ làm object "đổi danh tính" ngay giữa một phiên
 * làm việc, và Set đang chứa nó sẽ không tìm lại được. Thực hành phổ
 * biến là so sánh theo id nghiệp vụ, và hashCode trả về một hằng số
 * hoặc giá trị không đổi theo vòng đời.
 * - Hibernate còn trả về đối tượng ĐẠI DIỆN (proxy) là một class con tự
 * sinh. Viết equals bằng getClass() sẽ coi proxy khác với entity thật.
 * Chỗ này phải dùng instanceof, hoặc dùng hàm hỗ trợ của Hibernate.
 * Đây là ngoại lệ của lời khuyên ở mục 8 - không có luật nào đúng cho
 * mọi hoàn cảnh.
 * - toString của entity hay DTO: đừng in mật khẩu, token, số thẻ. Log là
 * nơi rò rỉ dữ liệu phổ biến nhất mà không ai để ý.
 * - Lombok: annotation Data sinh equals và hashCode từ MỌI field, kể cả
 * quan hệ lazy của JPA - gọi toString một phát là kéo nguyên cây dữ
 * liệu, hoặc nổ LazyInitializationException. Dùng thì phải biết nó
 * sinh ra cái gì.
 */
public class t4_Object {

    /**
     * Ghi đè equals mà QUÊN hashCode - để thấy bug im lặng.
     */
    public static final class ChiEquals {
        private final String ma;

        public ChiEquals(String ma) {
            this.ma = ma;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof ChiEquals c && ma.equals(c.ma);
        }
    }

    /**
     * Bản viết đủ cả ba method - đây là mẫu để dùng lại.
     */
    public static final class DuCa {
        private final String ma;

        public DuCa(String ma) {
            this.ma = Objects.requireNonNull(ma, "ma khong duoc null");
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof DuCa d && ma.equals(d.ma);
        }

        @Override
        public int hashCode() {
            return Objects.hash(ma);
        }

        @Override
        public String toString() {
            return "DuCa[ma=" + ma + "]";
        }
    }

    void main() {
        IO.println("--- ghi de equals, QUEN hashCode ---");
        ChiEquals c1 = new ChiEquals("A");
        ChiEquals c2 = new ChiEquals("A");
        Map<ChiEquals, String> map1 = new HashMap<>();
        map1.put(c1, "gia tri");

        IO.println("c1.equals(c2)      : " + c1.equals(c2));
        IO.println("HashSet size       : " + new HashSet<>(List.of(c1, c2)).size());
        IO.println("HashMap.get(c2)    : " + map1.get(c2));
        IO.println("List.contains(c2)  : " + List.of(c1).contains(c2));

        IO.println("--- ghi de DU CA HAI ---");
        DuCa d1 = new DuCa("A");
        DuCa d2 = new DuCa("A");
        Map<DuCa, String> map2 = new HashMap<>();
        map2.put(d1, "gia tri");

        IO.println("toString           : " + d1);
        IO.println("HashSet size       : " + new HashSet<>(List.of(d1, d2)).size());
        IO.println("HashMap.get(d2)    : " + map2.get(d2));

        // Dong "List.contains" o tren van tra ve true du class bi loi. Do la
        // ly do bug nay song lau: no chi hong o collection BAM.

        // TODO 1 (muc 2): tao mot class khong ghi de toString roi in no ra.
        //   In them Integer.toHexString(System.identityHashCode(obj)) va so
        //   sanh voi phan sau dau @. Tu ket luan con so do la gi.

        // TODO 2 (muc 3, 4): tu viet lai ChiEquals cho DU hashCode, chay lai
        //   bon dong tren va doi chieu. Sau do thu nguoc lai: ghi de hashCode
        //   nhung KHONG ghi de equals - doan xem HashMap.get se ra gi.

        // TODO 3 (muc 4): viet mot class co ba field, ghi de equals bang
        //   pattern matching va hashCode bang Objects.hash. Them mot field
        //   thu tu nhung CO Y quen cap nhat ca hai method - nghi xem trieu
        //   chung se xuat hien o dau trong mot ung dung that.

        // TODO 4 (muc 5): in getClass().getSimpleName(), getSuperclass() va
        //   getInterfaces() cua mot object. Thu ghi de getClass() de doc loi
        //   "overridden method is final".

        // TODO 5 (muc 6): viet class implements Cloneable co mot field List,
        //   clone no roi sua list cua ban sao. In ca hai de thay chung dung
        //   chung list. Sau do bo chu implements Cloneable di de bat
        //   CloneNotSupportedException.

        // TODO 6 (muc 7): thu ghi de finalize() va doc canh bao [removal] cua
        //   javac. Sau do viet lai bang AutoCloseable va try-with-resources,
        //   in ra de thay close() duoc goi dung luc nao.

        // TODO 7 (muc 8 - bai quan trong nhat ngoai trang goc): viet class
        //   Diem(x) ghi de equals bang instanceof, roi DiemMau extends Diem
        //   them field mau. In p.equals(cp) va cp.equals(p). Sau do thu
        //   List.of(p).contains(cp) va List.of(cp).contains(p). Doi instanceof
        //   thanh getClass() va chay lai.

        // TODO 8 (muc 12): doi ca class DuCa o tren thanh mot record. Dem xem
        //   xoa duoc bao nhieu dong ma hanh vi khong doi.
    }
}

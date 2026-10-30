package com.javacore.practice.javacore33.streams.optional;

import java.util.List;
import java.util.Optional;

/**
 * Bài 28 - Tầng service: nơi Optional được "tiêu thụ". Quy tắc chia việc giữa 3 tầng:
 * <ul>
 *   <li>Repository: TRẢ Optional (có thể không tìm thấy).</li>
 *   <li>Service: BÓC Optional - quyết định thiếu dữ liệu thì ném lỗi hay lấy giá trị mặc định.</li>
 *   <li>Controller: nhận về giá trị thật hoặc exception, không còn phải ngó Optional.</li>
 * </ul>
 */
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    // Constructor injection - đúng kiểu Spring sẽ tiêm cho bạn khi lên project thật.
    public UserService(UserRepository userRepository, AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    /**
     * orElseThrow: dùng khi KHÔNG tìm thấy là lỗi nghiệp vụ. Đây là cách bóc Optional
     * phổ biến nhất ở service layer của Spring Boot.
     */
    public User layBatBuoc(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    /**
     * map + orElse: không tìm thấy là chuyện bình thường, có sẵn giá trị thay thế rẻ tiền.
     * "Khách" là hằng chuỗi, tạo ra không tốn gì -> orElse là đúng, không cần orElseGet.
     */
    public String tenHienThi(String email) {
        return userRepository.findByEmail(email)
                .map(User::name)
                .orElse("Khách");
    }

    /**
     * map trên trường có thể null: phone null thì map() tự cho ra Optional rỗng,
     * KHÔNG ném NPE. Đây là lý do không cần if (u.phone() != null) ở đây.
     */
    public Optional<String> soDienThoai(String email) {
        return userRepository.findByEmail(email).map(User::phone);
    }

    /**
     * flatMap: hàm bên trong ĐÃ trả Optional rồi. Nếu dùng map ở đây, kết quả sẽ là
     * Optional&lt;Optional&lt;Address&gt;&gt; - compile vẫn qua nhưng gần như chắc chắn sai ý định.
     */
    public Optional<String> thanhPho(String email) {
        return userRepository.findByEmail(email)
                .flatMap(user -> addressRepository.findByUserId(user.id()))
                .map(Address::city);
    }

    /**
     * filter: thêm điều kiện nghiệp vụ lên giá trị đang có. Không thoả thì Optional
     * chuyển thành rỗng, và cả chuỗi phía sau tự động đi nhánh "không có".
     */
    public Optional<User> timNguoiDungDangHoatDong(String email) {
        return userRepository.findByEmail(email).filter(User::active);
    }

    /**
     * Chuỗi đầy đủ: tìm -> lọc còn hoạt động -> lấy địa chỉ -> lấy thành phố -> mặc định.
     * Đọc từ trên xuống như một câu văn, không có tầng if nào lồng nhau.
     */
    public String thanhPhoCuaNguoiDungDangHoatDong(String email) {
        return userRepository.findByEmail(email)
                .filter(User::active)
                .flatMap(user -> addressRepository.findByUserId(user.id()))
                .map(Address::city)
                .orElse("Chưa rõ");
    }

    /**
     * Optional.stream(): bóc Optional ngay trong stream, rỗng thì tự biến mất.
     * Gọn hơn hẳn .filter(Optional::isPresent).map(Optional::get).
     */
    public List<String> thanhPhoCuaTatCa() {
        return userRepository.findAll().stream()
                .flatMap(user -> addressRepository.findByUserId(user.id()).stream())
                .map(Address::city)
                .distinct()
                .toList();
    }

    /**
     * THAM SỐ nhận String chứ không nhận Optional&lt;String&gt;. Muốn tham số không bắt buộc
     * thì nạp chồng hàm hoặc nhận null có ghi chú, đừng bắt người gọi phải gói Optional.of(...).
     */
    public List<User> locTheoTrangThai(Boolean dangHoatDong) {
        return userRepository.findAll().stream()
                .filter(u -> dangHoatDong == null || u.active() == dangHoatDong)
                .toList();
    }
}

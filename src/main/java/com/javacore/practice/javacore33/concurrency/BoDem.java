package com.javacore.practice.javacore33.concurrency;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Bài 34 - năm cách đếm, một cách sai và bốn cách đúng.
 *
 * <p>Mỗi cách giữ một biến đếm riêng để chạy cả năm trong cùng một lần đo mà không
 * ảnh hưởng lẫn nhau.
 */
public class BoDem {

    // Cách 1: sai. giaTri++ là ba lệnh - đọc, cộng, ghi - và hai thread có thể
    // đọc cùng một giá trị cũ rồi cùng ghi đè lên nhau.
    private int giaTri;

    // Cách 2: vẫn sai. volatile bảo đảm thread sau đọc được giá trị thread trước
    // vừa ghi (visibility), nhưng không làm cho ba lệnh trên thành một lệnh
    // không chia cắt được (atomicity). Để đây để chạy ra và thấy nó sai.
    private volatile int giaTriVolatile;

    private int giaTriDongBo;

    private final AtomicInteger giaTriAtomic = new AtomicInteger();

    private final ReentrantLock khoa = new ReentrantLock();
    private int giaTriLock;

    private final LongAdder giaTriAdder = new LongAdder();

    public void tang() {
        giaTri++;
    }

    public int getGiaTri() {
        return giaTri;
    }

    public void tangVolatile() {
        giaTriVolatile++;
    }

    public int getGiaTriVolatile() {
        return giaTriVolatile;
    }

    // synchronized trên method = khoá trên chính object BoDem này.
    // Getter cũng phải synchronized: không chỉ để chống ghi đè, mà để thread
    // đọc nhìn thấy mọi thay đổi các thread khác đã ghi xong.
    public synchronized void tangDongBo() {
        giaTriDongBo++;
    }

    public synchronized int getGiaTriDongBo() {
        return giaTriDongBo;
    }

    // incrementAndGet() là một lệnh CAS của CPU - không khoá, không thread nào
    // phải chờ. Đổi lại chỉ làm được trên đúng một biến.
    public void tangAtomic() {
        giaTriAtomic.incrementAndGet();
    }

    public int getGiaTriAtomic() {
        return giaTriAtomic.get();
    }

    // unlock() BẮT BUỘC nằm trong finally. Nếu đoạn giữa ném exception mà không
    // có finally, khoá không bao giờ được trả - mọi thread sau đó treo vĩnh viễn.
    // Đây là cái giá của ReentrantLock so với synchronized (vốn tự nhả khoá).
    public void tangLock() {
        khoa.lock();
        try {
            giaTriLock++;
        } finally {
            khoa.unlock();
        }
    }

    public int getGiaTriLock() {
        khoa.lock();
        try {
            return giaTriLock;
        } finally {
            khoa.unlock();
        }
    }

    // Không nằm trong đề bài. Thêm vào vì đây mới là thứ dùng thật khi cần đếm
    // dưới tải cao: mỗi thread cộng vào ô riêng, chỉ cộng dồn lại lúc đọc
    // sum(). Nhanh hơn AtomicInteger khi đông thread, chậm hơn khi ít thread.
    public void tangAdder() {
        giaTriAdder.increment();
    }

    public long getGiaTriAdder() {
        return giaTriAdder.sum();
    }
}

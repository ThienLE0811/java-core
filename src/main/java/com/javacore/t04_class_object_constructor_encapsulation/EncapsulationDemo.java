package com.javacore.t04_class_object_constructor_encapsulation;

public class EncapsulationDemo {

    public static void main(String[] args) {
        BankAccount account = new BankAccount("An", 100_000);

        // Không thể truy cập trực tiếp account.balance từ bên ngoài vì field là private
        // (dòng dưới đây sẽ KHÔNG compile nếu bỏ comment):
        // account.balance = -999999;

        // Muốn đọc/ghi phải đi qua method công khai (getter/setter, hoặc method nghiệp vụ như deposit/withdraw)
        System.out.println("Số dư ban đầu: " + account.getBalance());

        account.deposit(50_000);
        System.out.println("Số dư sau khi nạp 50.000: " + account.getBalance());

        // setter/method nghiệp vụ có thể validate trước khi thay đổi field -> bảo vệ tính hợp lệ của dữ liệu
        account.withdraw(1_000_000); // rút vượt số dư -> bị từ chối, field không bị thay đổi sai
        System.out.println("Số dư sau khi rút 1.000.000 (thất bại vì không đủ tiền): " + account.getBalance());

        account.withdraw(30_000);
        System.out.println("Số dư sau khi rút 30.000: " + account.getBalance());

        // accountNumber là final, không có setter -> chỉ gán được 1 lần trong constructor, không đổi được sau đó
        System.out.println("Số tài khoản (chỉ đọc): " + account.getAccountNumber());

        // So sánh với object literal trong JS: JS object thường để field public tự do (dù có # private field
        // từ ES2022), Java khuyến khích mặc định private + expose có kiểm soát qua method để giữ invariant
        // (ví dụ: số dư không bao giờ âm) dù code có bị sửa từ đâu đi nữa.
    }

    static class BankAccount {
        // Field private: chỉ code bên trong class này mới truy cập trực tiếp được -> đây là cốt lõi của encapsulation
        private final String accountNumber;
        private String owner;
        private long balance;

        BankAccount(String owner, long initialBalance) {
            if (initialBalance < 0) {
                throw new IllegalArgumentException("Số dư ban đầu không được âm");
            }
            this.accountNumber = "ACC-" + System.nanoTime();
            this.owner = owner;
            this.balance = initialBalance;
        }

        // Getter: cho phép đọc field từ bên ngoài nhưng không cho sửa trực tiếp
        long getBalance() {
            return balance;
        }

        String getAccountNumber() {
            return accountNumber;
        }

        String getOwner() {
            return owner;
        }

        // Setter có validate: không cho gán owner rỗng, khác với việc gán thẳng field public vô điều kiện
        void setOwner(String owner) {
            if (owner == null || owner.isBlank()) {
                throw new IllegalArgumentException("Tên chủ tài khoản không được rỗng");
            }
            this.owner = owner;
        }

        // Method nghiệp vụ thay vì setBalance() lộ liễu: gói logic + validate vào 1 chỗ,
        // đảm bảo balance luôn ở trạng thái hợp lệ (không âm) dù gọi từ đâu
        void deposit(long amount) {
            if (amount <= 0) {
                throw new IllegalArgumentException("Số tiền nạp phải dương");
            }
            this.balance += amount;
        }

        void withdraw(long amount) {
            if (amount <= 0) {
                throw new IllegalArgumentException("Số tiền rút phải dương");
            }
            if (amount > this.balance) {
                System.out.println("Từ chối rút " + amount + ": vượt quá số dư hiện có (" + this.balance + ")");
                return;
            }
            this.balance -= amount;
        }
    }
}

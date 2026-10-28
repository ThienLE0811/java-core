package com.javacore.practice.javacore33.collections.queue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test toàn diện cho {@link HospitalTriageQueue} bài 20.
 */
class HospitalTriageQueueTest {

    private HospitalTriageQueue triage;

    @BeforeEach
    void setUp() {
        triage = new HospitalTriageQueue();
    }

    @Nested
    @DisplayName("1. Ràng buộc dữ liệu đầu vào (Validation)")
    class ValidationTest {

        @Test
        void admitTenNullHoacTrangSeNemException() {
            assertThrows(IllegalArgumentException.class, () -> triage.admit(null, Priority.HIGH));
            assertThrows(IllegalArgumentException.class, () -> triage.admit("", Priority.HIGH));
            assertThrows(IllegalArgumentException.class, () -> triage.admit("   ", Priority.HIGH));
        }

        @Test
        void admitPriorityNullSeNemException() {
            assertThrows(NullPointerException.class, () -> triage.admit("Nguyễn Văn A", null));
        }

        @Test
        void patientRecordTuValidate() {
            assertThrows(IllegalArgumentException.class, () -> new Patient("A", Priority.LOW, -1));
        }
    }

    @Nested
    @DisplayName("2. Trạng thái rỗng ban đầu")
    class EmptyStateTest {

        @Test
        void hangDoiMoiKhoiTaoRong() {
            assertTrue(triage.isEmpty());
            assertEquals(0, triage.size());
            assertEquals(Optional.empty(), triage.nextPatient());
            assertEquals(Optional.empty(), triage.peekNext());
            assertTrue(triage.getWaitingPatients().isEmpty());
        }
    }

    @Nested
    @DisplayName("3. Quy tắc ưu tiên theo mức độ khẩn cấp")
    class PriorityRulesTest {

        @Test
        void mucDoUuTienCaoHonLuonDuocKhamTruoc() {
            // Nhập từ nhẹ nhất tới nguy kịch nhất
            triage.admit("Bệnh nhân Nhẹ", Priority.LOW);
            triage.admit("Bệnh nhân Trung bình", Priority.MEDIUM);
            triage.admit("Bệnh nhân Khẩn cấp", Priority.HIGH);
            triage.admit("Bệnh nhân Nguy kịch", Priority.EMERGENCY);

            assertEquals(4, triage.size());

            // Thứ tự khám phải đảo ngược lại: EMERGENCY -> HIGH -> MEDIUM -> LOW
            assertEquals("Bệnh nhân Nguy kịch", triage.nextPatient().orElseThrow().name());
            assertEquals("Bệnh nhân Khẩn cấp", triage.nextPatient().orElseThrow().name());
            assertEquals("Bệnh nhân Trung bình", triage.nextPatient().orElseThrow().name());
            assertEquals("Bệnh nhân Nhẹ", triage.nextPatient().orElseThrow().name());

            assertTrue(triage.isEmpty());
        }
    }

    @Nested
    @DisplayName("4. Tiêu chí phụ (Tie-Breaker): Cùng mức ưu tiên thì ai đến trước khám trước")
    class TieBreakerTest {

        @Test
        void cungMucUuTienBaoDamThuTuFIFO() {
            Patient p1 = triage.admit("Bệnh nhân 1", Priority.HIGH);
            Patient p2 = triage.admit("Bệnh nhân 2", Priority.HIGH);
            Patient p3 = triage.admit("Bệnh nhân 3", Priority.HIGH);
            Patient p4 = triage.admit("Bệnh nhân 4", Priority.HIGH);

            assertEquals(1, p1.arrivalOrder());
            assertEquals(2, p2.arrivalOrder());
            assertEquals(3, p3.arrivalOrder());
            assertEquals(4, p4.arrivalOrder());

            assertEquals("Bệnh nhân 1", triage.nextPatient().orElseThrow().name());
            assertEquals("Bệnh nhân 2", triage.nextPatient().orElseThrow().name());
            assertEquals("Bệnh nhân 3", triage.nextPatient().orElseThrow().name());
            assertEquals("Bệnh nhân 4", triage.nextPatient().orElseThrow().name());
        }
    }

    @Nested
    @DisplayName("5. Tình huống kết hợp xen kẽ nhiều bệnh nhân")
    class ComplexScenarioTest {

        @Test
        void thuTuGoiKhamChinhXacTuyetDoiVoiNhieuBenhNhanXenKe() {
            triage.admit("An", Priority.MEDIUM);      // #1
            triage.admit("Bình", Priority.HIGH);      // #2
            triage.admit("Cường", Priority.LOW);      // #3
            triage.admit("Dũng", Priority.HIGH);      // #4
            triage.admit("Em", Priority.EMERGENCY);   // #5
            triage.admit("Phương", Priority.MEDIUM);  // #6

            assertEquals(6, triage.size());

            // 1. Em (EMERGENCY)
            assertEquals("Em", triage.nextPatient().orElseThrow().name());
            // 2. Bình (HIGH, #2 đến trước Dũng #4)
            assertEquals("Bình", triage.nextPatient().orElseThrow().name());
            // 3. Dũng (HIGH, #4)
            assertEquals("Dũng", triage.nextPatient().orElseThrow().name());
            // 4. An (MEDIUM, #1 đến trước Phương #6)
            assertEquals("An", triage.nextPatient().orElseThrow().name());
            // 5. Phương (MEDIUM, #6)
            assertEquals("Phương", triage.nextPatient().orElseThrow().name());
            // 6. Cường (LOW, #3)
            assertEquals("Cường", triage.nextPatient().orElseThrow().name());

            assertTrue(triage.isEmpty());
        }

        @Test
        void peekNextKhongXoaPhanTuKhoiHangDoi() {
            triage.admit("An", Priority.MEDIUM);
            triage.admit("Bình", Priority.EMERGENCY);

            assertEquals("Bình", triage.peekNext().orElseThrow().name());
            assertEquals(2, triage.size()); // size không đổi

            assertEquals("Bình", triage.nextPatient().orElseThrow().name());
            assertEquals(1, triage.size());
        }

        @Test
        void getWaitingPatientsTraVeDanhSachDungThuTuTriageMaKhongXoaQueue() {
            triage.admit("An", Priority.LOW);
            triage.admit("Bình", Priority.HIGH);
            triage.admit("Cường", Priority.EMERGENCY);

            List<Patient> waiting = triage.getWaitingPatients();
            assertEquals(3, waiting.size());
            assertEquals("Cường", waiting.get(0).name());
            assertEquals("Bình", waiting.get(1).name());
            assertEquals("An", waiting.get(2).name());

            // Queue gốc vẫn nguyên vẹn
            assertEquals(3, triage.size());
            assertFalse(triage.isEmpty());
        }
    }
}

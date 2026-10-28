package com.javacore.practice.javacore33.collections.map;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test toàn diện cho {@link WordCounter} bài 18.
 */
class WordCounterTest {

    @Nested
    @DisplayName("1. Tách từ và chuẩn hóa")
    class ExtractWordsTest {

        @Test
        void textNullHoacRongTraVeDanhSachRong() {
            assertTrue(WordCounter.extractWords(null).isEmpty());
            assertTrue(WordCounter.extractWords("").isEmpty());
            assertTrue(WordCounter.extractWords("   \t  \n  ").isEmpty());
        }

        @Test
        void tachTuLoaiBoDauCauVaChuanHoaChuThuong() {
            String text = "Xin chào, tôi học Java! Java là ngôn ngữ tuyệt vời; đúng không?";
            List<String> words = WordCounter.extractWords(text);

            assertEquals(List.of("xin", "chào", "tôi", "học", "java", "java", "là", "ngôn", "ngữ", "tuyệt", "vời", "đúng", "không"),
                    words);
        }

        @Test
        void hoTroTiengVietCoDauVaSo() {
            String text = "Lớp Java 21 có 30 học viên. Học, học nữa, học mãi!";
            List<String> words = WordCounter.extractWords(text);

            assertEquals(List.of("lớp", "java", "21", "có", "30", "học", "viên", "học", "học", "nữa", "học", "mãi"),
                    words);
        }
    }

    @Nested
    @DisplayName("2. Đếm tần suất từ")
    class CountFrequenciesTest {

        @Test
        void textNullHoacRongTraVeMapRong() {
            assertTrue(WordCounter.countWithMerge(null).isEmpty());
            assertTrue(WordCounter.countWithMerge("   ").isEmpty());
            assertTrue(WordCounter.countWithGetOrDefault(null).isEmpty());
            assertTrue(WordCounter.countWithGetOrDefault("   ").isEmpty());
        }

        @Test
        void countWithMergeVaGetOrDefaultChoCungKetQua() {
            String text = "Java Spring Boot Java Docker Kubernetes Java Spring";

            Map<String, Integer> mergeResult = WordCounter.countWithMerge(text);
            Map<String, Integer> getOrDefaultResult = WordCounter.countWithGetOrDefault(text);

            assertEquals(mergeResult, getOrDefaultResult);
            assertEquals(3, mergeResult.get("java"));
            assertEquals(2, mergeResult.get("spring"));
            assertEquals(1, mergeResult.get("boot"));
            assertEquals(1, mergeResult.get("docker"));
            assertEquals(1, mergeResult.get("kubernetes"));
        }

        @Test
        void khongPhanBietHoaThuong() {
            String text = "JAVA java Java jAvA";
            Map<String, Integer> result = WordCounter.countWithMerge(text);

            assertEquals(1, result.size());
            assertEquals(4, result.get("java"));
        }
    }

    @Nested
    @DisplayName("3. Lấy Top N từ phổ biến nhất")
    class TopWordsTest {

        private final Map<String, Integer> sampleFrequencies = Map.of(
                "java", 10,
                "spring", 8,
                "backend", 5,
                "api", 5,
                "database", 3,
                "docker", 1
        );

        @Test
        void layTop5TuPhoBienSapXepGiamDanVaHoaDiemAtoZ() {
            List<WordCounter.WordCount> top5 = WordCounter.topWords(sampleFrequencies, 5);

            assertEquals(5, top5.size());
            // java (10), spring (8), api (5) trước backend (5) vì 'a' < 'b', database (3)
            assertEquals(new WordCounter.WordCount("java", 10), top5.get(0));
            assertEquals(new WordCounter.WordCount("spring", 8), top5.get(1));
            assertEquals(new WordCounter.WordCount("api", 5), top5.get(2));
            assertEquals(new WordCounter.WordCount("backend", 5), top5.get(3));
            assertEquals(new WordCounter.WordCount("database", 3), top5.get(4));
        }

        @Test
        void khiNYeuCauLonHonSoTuHienCo() {
            Map<String, Integer> small = Map.of("a", 1, "b", 2);
            List<WordCounter.WordCount> result = WordCounter.topWords(small, 10);

            assertEquals(2, result.size());
            assertEquals(new WordCounter.WordCount("b", 2), result.get(0));
            assertEquals(new WordCounter.WordCount("a", 1), result.get(1));
        }

        @Test
        void thamSoNKhongHopLeHoacMapRong() {
            assertTrue(WordCounter.topWords(sampleFrequencies, 0).isEmpty());
            assertTrue(WordCounter.topWords(sampleFrequencies, -5).isEmpty());
            assertTrue(WordCounter.topWords(null, 5).isEmpty());
            assertTrue(WordCounter.topWords(Collections.emptyMap(), 5).isEmpty());
        }
    }

    @Nested
    @DisplayName("4. Các từ chỉ xuất hiện 1 lần")
    class WordsAppearingOnceTest {

        @Test
        void locDungCacTuCoTanSuatBang1VaSapXepAtoZ() {
            Map<String, Integer> frequencies = Map.of(
                    "java", 3,
                    "rust", 1,
                    "go", 1,
                    "python", 2,
                    "c", 1
            );

            List<String> once = WordCounter.wordsAppearingOnce(frequencies);

            assertEquals(List.of("c", "go", "rust"), once);
        }

        @Test
        void khiKhongCoTuNaoXuatHienMotLan() {
            Map<String, Integer> frequencies = Map.of(
                    "java", 2,
                    "spring", 4
            );

            assertTrue(WordCounter.wordsAppearingOnce(frequencies).isEmpty());
        }

        @Test
        void mapNullHoacRong() {
            assertTrue(WordCounter.wordsAppearingOnce(null).isEmpty());
            assertTrue(WordCounter.wordsAppearingOnce(Collections.emptyMap()).isEmpty());
        }
    }

    @Nested
    @DisplayName("5. Record WordCount validation")
    class WordCountRecordTest {

        @Test
        void taoHopLe() {
            WordCounter.WordCount wc = new WordCounter.WordCount("test", 5);
            assertEquals("test", wc.word());
            assertEquals(5, wc.count());
        }

        @Test
        void wordNullNemNullPointerException() {
            assertThrows(NullPointerException.class, () -> new WordCounter.WordCount(null, 1));
        }

        @Test
        void countAmNemIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> new WordCounter.WordCount("test", -1));
        }
    }
}

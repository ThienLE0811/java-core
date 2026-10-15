/**
 * Refactoring from the Imperative to the Functional Style
 *
 * <p>Bước 16/41 trong lộ trình dev.java/learn.
 *
 * <p>Tài liệu: <a href="https://dev.java/learn/language/fp/refactoring-to-functional-style/">https://dev.java/learn/language/fp/refactoring-to-functional-style/</a>
 *
 * <p>Nội dung 5 bài trong series của Venkat Subramaniam:
 * <ul>
 *   <li>{@link com.javacore.language.fp.refactoring_to_functional_style.t1_SimpleLoops}: for() → IntStream.range() / rangeClosed()</li>
 *   <li>{@link com.javacore.language.fp.refactoring_to_functional_style.t2_LoopsWithSteps}: for(...i = i + step) → iterate(), break → takeWhile()</li>
 *   <li>{@link com.javacore.language.fp.refactoring_to_functional_style.t3_ForEachWithIf}: foreach(...) { if(...) } → forEach(), filter()</li>
 *   <li>{@link com.javacore.language.fp.refactoring_to_functional_style.t4_IterationWithTransformation}: foreach(...) { ...transform... } → map(), filter().map()</li>
 *   <li>{@link com.javacore.language.fp.refactoring_to_functional_style.t5_ConvertingToStreams}: BufferedReader loop → Files.lines() Stream</li>
 * </ul>
 */
package com.javacore.language.fp.refactoring_to_functional_style;

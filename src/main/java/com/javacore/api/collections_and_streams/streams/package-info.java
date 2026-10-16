/**
 * The Stream API
 *
 * <p>Bước 20/41 trong lộ trình dev.java/learn.
 *
 * <p>Tài liệu: <a href="https://dev.java/learn/api/collections-and-streams/streams/">https://dev.java/learn/api/collections-and-streams/streams/</a>
 *
 * <p>Section gồm 12 trang gốc, mỗi trang một file {@code tN_*.java}:
 * <ol>
 *   <li>{@code t1_MapFilterReduce} - map-filter-reduce, lazy, pipeline, specialized stream</li>
 *   <li>{@code t2_IntermediateOperations} - map, filter, flatMap, mapMulti, distinct, sorted,
 *       limit, skip, takeWhile, dropWhile, concat, peek</li>
 *   <li>{@code t3_CreatingStreams} - of, Arrays.stream, generate, iterate, range, random,
 *       chars, Files.lines, splitAsStream, builder</li>
 *   <li>{@code t4_ReducingAStream} - reduce, tính kết hợp, phần tử đơn vị, ba overload</li>
 *   <li>{@code t5_TerminalOperations} - count, forEach, toList, min, max, findFirst,
 *       anyMatch, short-circuit</li>
 *   <li>{@code t6_StreamCharacteristics} - Spliterator, ORDERED, DISTINCT, NONNULL,
 *       SORTED, SIZED, SUBSIZED</li>
 *   <li>{@code t7_UsingCollectors} - toList, joining, groupingBy, partitioningBy, toMap,
 *       downstream collector</li>
 *   <li>{@code t8_CustomCollectors} - Collector.of, supplier/accumulator/combiner/finisher,
 *       collectingAndThen, teeing</li>
 *   <li>{@code t9_CollectorInterface} - tự cài interface Collector, ba đặc tính</li>
 *   <li>{@code t10_Optionals} - of, ofNullable, orElse, orElseGet, map, Optional.stream, luật dùng</li>
 *   <li>{@code t11_ParallelStreams} - Fork/Join, data locality, chẻ nguồn, chi phí, khi nào nên dùng</li>
 *   <li>{@code t12_GathererApi} - Stream.gather, integrator, finisher, fold, scan,
 *       mapConcurrent, windowFixed, windowSliding (JDK 24+)</li>
 * </ol>
 */
package com.javacore.api.collections_and_streams.streams;

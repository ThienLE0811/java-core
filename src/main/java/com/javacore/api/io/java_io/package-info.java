/**
 * The Java I/O API
 *
 * <p>Bước 21/41 trong lộ trình dev.java/learn.
 *
 * <p>Tài liệu: <a href="https://dev.java/learn/api/io/java-io/">https://dev.java/learn/api/io/java-io/</a>
 *
 * <p>Section gồm 18 trang gốc chia 4 nhóm, mỗi trang một file {@code tN_*.java}:
 *
 * <p><b>Mở đầu</b>
 * <ol>
 *   <li>{@code t1_MainIoConcepts} - io so với nio so với nio2, vì sao File cũ bị thay bằng Path</li>
 * </ol>
 *
 * <p><b>File System Basics</b> - NIO.2, làm việc với hệ thống file
 * <ol start="2">
 *   <li>{@code t2_AccessingResourcesUsingPaths} - Path thay cho File</li>
 *   <li>{@code t3_WorkingWithPaths} - resolve, relativize, normalize, so sánh path</li>
 *   <li>{@code t4_AccessingTheFileSystem} - FileSystem, FileSystems, provider</li>
 *   <li>{@code t5_ManipulatingFilesAndDirectories} - copy, move, delete, CopyOption</li>
 *   <li>{@code t6_Links} - symbolic link và hard link</li>
 *   <li>{@code t7_FileAttributes} - metadata, BasicFileAttributes, quyền POSIX</li>
 *   <li>{@code t8_CreatingAndReadingDirectories} - tạo thư mục, file tạm</li>
 *   <li>{@code t9_ListingDirectoryContent} - DirectoryStream, Files.list</li>
 *   <li>{@code t10_WalkingTheFileTree} - Files.walk, FileVisitor, bắt buộc đóng stream</li>
 *   <li>{@code t11_WatchingDirectoryChanges} - WatchService</li>
 * </ol>
 *
 * <p><b>File Operations Basics</b> - java.io, các lớp stream
 * <ol start="12">
 *   <li>{@code t12_ResourcesAndExceptions} - try-with-resources, IOException</li>
 *   <li>{@code t13_SmallFiles} - Files.readAllBytes, readString, write</li>
 *   <li>{@code t14_BufferedTextFiles} - Reader, Writer, buffered, charset</li>
 *   <li>{@code t15_BinaryFiles} - InputStream, OutputStream, DataInput/DataOutput</li>
 *   <li>{@code t16_DecoratingIoStreams} - decorator pattern trong thiết kế java.io</li>
 *   <li>{@code t17_InMemoryIoStreams} - ByteArrayInputStream, StringWriter</li>
 * </ol>
 *
 * <p><b>Tổng hợp</b>
 * <ol start="18">
 *   <li>{@code t18_PuttingItAllTogether} - bài tập gộp: đọc và xử lý file thật</li>
 * </ol>
 */
package com.javacore.api.io.java_io;

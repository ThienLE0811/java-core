package com.javacore.language.constructs.basics;

/**
 * Creating Primitive Type Variables in Your Programs
 * <p>
 * 8 kiểu nguyên thủy của Java, kèm kích thước, giá trị mặc định, phạm vi:
 * <p>
 * - byte    8-bit    mặc định 0          phạm vi -128 đến 127
 * - short   16-bit   mặc định 0          phạm vi -32,768 đến 32,767
 * - int     32-bit   mặc định 0          phạm vi -2^31 đến 2^31-1
 * - long    64-bit   mặc định 0L         phạm vi -2^63 đến 2^63-1
 * - float   32-bit   mặc định 0.0f       xem Java Language Specification
 * - double  64-bit   mặc định 0.0d       xem Java Language Specification
 * - boolean (không xác định)  mặc định false   true/false
 * - char    16-bit   mặc định '\u0000'   phạm vi 0 đến 65,535
 * <p>
 * Quy tắc literal:
 * - long literal cần hậu tố L/l, thiếu sẽ lỗi compile nếu số vượt phạm vi int.
 * - float literal cần hậu tố F/f, thiếu thì mặc định là double, gán vào float sẽ lỗi compile.
 * - số hex: tiền tố 0x (ví dụ 0x1A).
 * - số binary: tiền tố 0b (ví dụ 0b1010).
 * - dấu gạch dưới _ giữa các chữ số cho dễ đọc (Java 7+), không đặt ở đầu/cuối số hay sát dấu thập phân.
 * <p>
 * * - char literal: dấu nháy đơn, ví dụ 'A'.
 * * - String literal: dấu nháy kép, ví dụ "Hello".
 * * - escape sequence: \b \t \n \f \r \" \' \\.
 * * - unicode: \u0108, hỗ trợ UTF-16 cho char và String.
 * * - null: literal đặc biệt, chỉ gán cho reference type, không gán cho kiểu nguyên thủy.
 */
public class t2_PrimitiveType {
}

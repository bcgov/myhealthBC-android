/*
 * QR Code generator library (Java)
 *
 * Copyright (c) Project Nayuki. (MIT License)
 * https://www.nayuki.io/page/qr-code-generator-library
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * - The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 * - The Software is provided "as is", without warranty of any kind, express or
 *   implied, including but not limited to the warranties of merchantability,
 *   fitness for a particular purpose and noninfringement. In no event shall the
 *   authors or copyright holders be liable for any claim, damages or other
 *   liability, whether in an action of contract, tort or otherwise, arising from,
 *   out of or in connection with the Software or the use or other dealings in the
 *   Software.
 */

package ca.bc.gov.bchealth.ui.mycards.qrgen;

/**
 * A segment of character/binary/control data in a QR Code symbol.
 * Instances of this class are immutable.
 * <p>The mid-level way to create a segment is to take the payload data and call a
 * static factory function such as {@link QrSegment#makeNumeric(CharSequence)}. The low-level
 * way to create a segment is to custom-make the bit buffer and call the {@link
 * QrSegment#QrSegment(Mode, int, BitBuffer) constructor} with appropriate values.</p>
 * <p>This segment class imposes no length restrictions, but QR Codes have restrictions.
 * Even in the most favorable conditions, a QR Code can only hold 7089 characters of data.
 * Any segment longer than this is meaningless for the purpose of generating QR Codes.
 * This class can represent kanji mode segments, but provides no help in encoding them
 * - see {@link QrSegmentAdvanced} for full kanji support.</p>
 */
public final class QrSegment {

    /*---- Static factory functions (mid level) ----*/

    // The set of all legal characters in alphanumeric mode, where
    // each character value maps to the index in the string.
    static final String ALPHANUMERIC_CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ $%*+-./:";
    // Describes precisely all strings that are encodable in numeric mode.
    private static final java.util.regex.Pattern NUMERIC_REGEX = java.util.regex.Pattern.compile("[0-9]*");
    // Describes precisely all strings that are encodable in alphanumeric mode.
    private static final java.util.regex.Pattern ALPHANUMERIC_REGEX = java.util.regex.Pattern.compile("[A-Z0-9 $%*+./:-]*");
    /**
     * The mode indicator of this segment. Not {@code null}.
     */
    public final ca.bc.gov.bchealth.ui.mycards.qrgen.QrSegment.Mode mode;
    /**
     * The length of this segment's unencoded data. Measured in characters for
     * numeric/alphanumeric/kanji mode, bytes for byte mode, and 0 for ECI mode.
     * Always zero or positive. Not the same as the data's bit length.
     */
    public final int numChars;
    // The data bits of this segment. Not null. Accessed through getData().
    final BitBuffer data;


    /**
     * Constructs a QR Code segment with the specified attributes and data.
     * The character count (numCh) must agree with the mode and the bit buffer length,
     * but the constraint isn't checked. The specified bit buffer is cloned and stored.
     *
     * @param md    the mode (not {@code null})
     * @param numCh the data length in characters or bytes, which is non-negative
     * @param data  the data bits (not {@code null})
     * @throws NullPointerException     if the mode or data is {@code null}
     * @throws IllegalArgumentException if the character count is negative
     */
    public QrSegment(ca.bc.gov.bchealth.ui.mycards.qrgen.QrSegment.Mode md, int numCh, BitBuffer data) {
        mode = java.util.Objects.requireNonNull(md);
        java.util.Objects.requireNonNull(data);
        if (numCh < 0)
            throw new IllegalArgumentException("Invalid value");
        numChars = numCh;
        this.data = data.clone();  // Make defensive copy
    }



    /*---- Instance fields ----*/

    /**
     * Returns a segment representing the specified binary data
     * encoded in byte mode. All input byte arrays are acceptable.
     * <p>Any text string can be converted to UTF-8 bytes ({@code
     * s.getBytes(StandardCharsets.UTF_8)}) and encoded as a byte mode segment.</p>
     *
     * @param data the binary data (not {@code null})
     * @return a segment (not {@code null}) containing the data
     * @throws NullPointerException if the array is {@code null}
     */
    public static ca.bc.gov.bchealth.ui.mycards.qrgen.QrSegment makeBytes(byte[] data) {
        java.util.Objects.requireNonNull(data);
        BitBuffer bb = new BitBuffer();
        for (byte b : data)
            bb.appendBits(b & 0xFF, 8);
        return new ca.bc.gov.bchealth.ui.mycards.qrgen.QrSegment(ca.bc.gov.bchealth.ui.mycards.qrgen.QrSegment.Mode.BYTE, data.length, bb);
    }

    /**
     * Returns a segment representing the specified string of decimal digits encoded in numeric mode.
     *
     * @param digits the text (not {@code null}), with only digits from 0 to 9 allowed
     * @return a segment (not {@code null}) containing the text
     * @throws NullPointerException     if the string is {@code null}
     * @throws IllegalArgumentException if the string contains non-digit characters
     */
    public static ca.bc.gov.bchealth.ui.mycards.qrgen.QrSegment makeNumeric(CharSequence digits) {
        java.util.Objects.requireNonNull(digits);
        if (!isNumeric(digits))
            throw new IllegalArgumentException("String contains non-numeric characters");

        BitBuffer bb = new BitBuffer();
        for (int i = 0; i < digits.length(); ) {  // Consume up to 3 digits per iteration
            int n = Math.min(digits.length() - i, 3);
            bb.appendBits(Integer.parseInt(digits.subSequence(i, i + n).toString()), n * 3 + 1);
            i += n;
        }
        return new ca.bc.gov.bchealth.ui.mycards.qrgen.QrSegment(ca.bc.gov.bchealth.ui.mycards.qrgen.QrSegment.Mode.NUMERIC, digits.length(), bb);
    }

    /**
     * Returns a segment representing the specified text string encoded in alphanumeric mode.
     * The characters allowed are: 0 to 9, A to Z (uppercase only), space,
     * dollar, percent, asterisk, plus, hyphen, period, slash, colon.
     *
     * @param text the text (not {@code null}), with only certain characters allowed
     * @return a segment (not {@code null}) containing the text
     * @throws NullPointerException     if the string is {@code null}
     * @throws IllegalArgumentException if the string contains non-encodable characters
     */
    public static ca.bc.gov.bchealth.ui.mycards.qrgen.QrSegment makeAlphanumeric(CharSequence text) {
        java.util.Objects.requireNonNull(text);
        if (!isAlphanumeric(text))
            throw new IllegalArgumentException("String contains unencodable characters in alphanumeric mode");

        BitBuffer bb = new BitBuffer();
        int i;
        for (i = 0; i <= text.length() - 2; i += 2) {  // Process groups of 2
            int temp = ALPHANUMERIC_CHARSET.indexOf(text.charAt(i)) * 45;
            temp += ALPHANUMERIC_CHARSET.indexOf(text.charAt(i + 1));
            bb.appendBits(temp, 11);
        }
        if (i < text.length())  // 1 character remaining
            bb.appendBits(ALPHANUMERIC_CHARSET.indexOf(text.charAt(i)), 6);
        return new ca.bc.gov.bchealth.ui.mycards.qrgen.QrSegment(ca.bc.gov.bchealth.ui.mycards.qrgen.QrSegment.Mode.ALPHANUMERIC, text.length(), bb);
    }


    /*---- Constructor (low level) ----*/

    /**
     * Returns a list of zero or more segments to represent the specified Unicode text string.
     * The result may use various segment modes and switch modes to optimize the length of the bit stream.
     *
     * @param text the text to be encoded, which can be any Unicode string
     * @return a new mutable list (not {@code null}) of segments (not {@code null}) containing the text
     * @throws NullPointerException if the text is {@code null}
     */
	/*public static java.util.List<QrSegment> makeSegments(CharSequence text) {
		java.util.Objects.requireNonNull(text);

		// Select the most efficient segment encoding automatically
		java.util.List<QrSegment> result = new java.util.ArrayList<>();
		if (text.equals(""));  // Leave result empty
		else if (isNumeric(text))
			result.add(makeNumeric(text));
		else if (isAlphanumeric(text))
			result.add(makeAlphanumeric(text));
		else
			result.add(makeBytes(text.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8)));
		return result;
	}*/
    public static java.util.List<QrSegment> makeSegments(CharSequence text) throws Exception {
        java.util.Objects.requireNonNull(text);

        // Select the most efficient segment encoding automatically
        java.util.List<QrSegment> result = new java.util.ArrayList<>();
        if (text.toString().contains("shc:/"))
            result.add(makeBytes("shc:/".getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        else
            throw new Exception("QR Specs not matching!");

        result.add(makeNumeric(text.subSequence(5, text.length())));

        return result;
    }


    /*---- Methods ----*/

    /**
     * Returns a segment representing an Extended Channel Interpretation
     * (ECI) designator with the specified assignment value.
     *
     * @param assignVal the ECI assignment number (see the AIM ECI specification)
     * @return a segment (not {@code null}) containing the data
     * @throws IllegalArgumentException if the value is outside the range [0, 10<sup>6</sup>)
     */
    public static ca.bc.gov.bchealth.ui.mycards.qrgen.QrSegment makeEci(int assignVal) {
        BitBuffer bb = new BitBuffer();
        if (assignVal < 0)
            throw new IllegalArgumentException("ECI assignment value out of range");
        else if (assignVal < (1 << 7))
            bb.appendBits(assignVal, 8);
        else if (assignVal < (1 << 14)) {
            bb.appendBits(2, 2);
            bb.appendBits(assignVal, 14);
        } else if (assignVal < 1_000_000) {
            bb.appendBits(6, 3);
            bb.appendBits(assignVal, 21);
        } else
            throw new IllegalArgumentException("ECI assignment value out of range");
        return new ca.bc.gov.bchealth.ui.mycards.qrgen.QrSegment(ca.bc.gov.bchealth.ui.mycards.qrgen.QrSegment.Mode.ECI, 0, bb);
    }

    /**
     * Tests whether the specified string can be encoded as a segment in numeric mode.
     * A string is encodable iff each character is in the range 0 to 9.
     *
     * @param text the string to test for encodability (not {@code null})
     * @return {@code true} iff each character is in the range 0 to 9.
     * @throws NullPointerException if the string is {@code null}
     * @see #makeNumeric(CharSequence)
     */
    public static boolean isNumeric(CharSequence text) {
        return NUMERIC_REGEX.matcher(text).matches();
    }


    /*---- Constants ----*/

    /**
     * Tests whether the specified string can be encoded as a segment in alphanumeric mode.
     * A string is encodable iff each character is in the following set: 0 to 9, A to Z
     * (uppercase only), space, dollar, percent, asterisk, plus, hyphen, period, slash, colon.
     *
     * @param text the string to test for encodability (not {@code null})
     * @return {@code true} iff each character is in the alphanumeric mode character set
     * @throws NullPointerException if the string is {@code null}
     * @see #makeAlphanumeric(CharSequence)
     */
    public static boolean isAlphanumeric(CharSequence text) {
        return ALPHANUMERIC_REGEX.matcher(text).matches();
    }

    // Calculates the number of bits needed to encode the given segments at the given version.
    // Returns a non-negative number if successful. Otherwise returns -1 if a segment has too
    // many characters to fit its length field, or the total bits exceeds Integer.MAX_VALUE.
    static int getTotalBits(java.util.List<QrSegment> segs, int version) {
        java.util.Objects.requireNonNull(segs);
        long result = 0;
        for (ca.bc.gov.bchealth.ui.mycards.qrgen.QrSegment seg : segs) {
            java.util.Objects.requireNonNull(seg);
            int ccbits = seg.mode.numCharCountBits(version);
            if (seg.numChars >= (1 << ccbits))
                return -1;  // The segment's length doesn't fit the field's bit width
            result += 4L + ccbits + seg.data.bitLength();
            if (result > Integer.MAX_VALUE)
                return -1;  // The sum will overflow an int type
        }
        return (int) result;
    }

    /**
     * Returns the data bits of this segment.
     *
     * @return a new copy of the data bits (not {@code null})
     */
    public BitBuffer getData() {
        return data.clone();  // Make defensive copy
    }



    /*---- Public helper enumeration ----*/

    /**
     * Describes how a segment's data bits are interpreted.
     */
    public enum Mode {

        /*-- Constants --*/

        NUMERIC(0x1, 10, 12, 14),
        ALPHANUMERIC(0x2, 9, 11, 13),
        BYTE(0x4, 8, 16, 16),
        KANJI(0x8, 8, 10, 12),
        ECI(0x7, 0, 0, 0);


        /*-- Fields --*/

        // The mode indicator bits, which is a uint4 value (range 0 to 15).
        final int modeBits;

        // Number of character count bits for three different version ranges.
        private final int[] numBitsCharCount;


        /*-- Constructor --*/

        Mode(int mode, int... ccbits) {
            modeBits = mode;
            numBitsCharCount = ccbits;
        }


        /*-- Method --*/

        // Returns the bit width of the character count field for a segment in this mode
        // in a QR Code at the given version number. The result is in the range [0, 16].
        int numCharCountBits(int ver) {
            assert QrCode.MIN_VERSION <= ver && ver <= QrCode.MAX_VERSION;
            return numBitsCharCount[(ver + 7) / 17];
        }

    }

}

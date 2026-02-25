package com.maksim;

public class Bitset {
    private final int SEGMENT_SIZE = Long.SIZE;
    private final int SEGMENT_POW_2 = Integer.numberOfTrailingZeros(SEGMENT_SIZE);
    private final int prefixSize;
    private final int bitCount;
    private final int size;
    private final long[] bitset;

    public Bitset(int bitCount) {
        if (bitCount <= 0)
            throw new IllegalArgumentException("Bit count of bitset constructor must be positive integer");
        this.bitCount = bitCount;
        this.size = (bitCount + SEGMENT_SIZE - 1) >>> SEGMENT_POW_2;
        this.prefixSize = (SEGMENT_SIZE - (bitCount & (SEGMENT_SIZE - 1))) & (SEGMENT_SIZE - 1);
        this.bitset = new long[size];
    }

    public void set(final int index, final int bit) {
        if (index < 0 || index >= bitCount)
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for length " + (bitCount - 1));

        int segmentIndex = size - 1 - (index >>> SEGMENT_POW_2);
        int bitIndex = index & (SEGMENT_SIZE - 1);

        if ((bit & 1) == 1) {
            bitset[segmentIndex] |= 1L << bitIndex;
        } else {
            bitset[segmentIndex] &= ~(1L << bitIndex);
        }
    }

    public int get(final int index) {
        if (index < 0 || index >= bitCount)
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for length " + (bitCount - 1));

        int segmentIndex = size - 1 - (index >>> SEGMENT_POW_2);
        int bitIndex = index & (SEGMENT_SIZE - 1);
        return (int) (bitset[segmentIndex] >>> bitIndex) & 1;
    }


    public int popCount() {
        int n = 0;
        for (int i = 0; i < size; i++) {
            n += Long.bitCount(bitset[i]);
        }
        return n;
    }

    public int clz() {
        int n = -prefixSize;
        for (int i = 0; i < size; i++) {
            if (bitset[i] == 0) {
                n += SEGMENT_SIZE;
            } else {
                n += Long.numberOfLeadingZeros(bitset[i]);
                break;
            }
        }
        return n;
    }

    public int ctz() {
        int n = 0;
        for (int i = size - 1; i >= 0; i--) {
            if (bitset[i] == 0) {
                n += SEGMENT_SIZE;
            } else {
                n += Long.numberOfTrailingZeros(bitset[i]);
                break;
            }
        }
        return n;
    }

    public int findFirst() {
        return ctz();
    }

    public int findLast() {
        return bitCount - clz() - 1;
    }

    public int findNext(final int index) {
        if (index < 0 || index >= bitCount)
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for length " + (bitCount - 1));

        int segmentIndex = size - 1 - (index >>> SEGMENT_POW_2);
        int bitIndex = index & (SEGMENT_SIZE - 1);
        long tmp = bitset[segmentIndex] >>> (bitIndex + 1);
        if (tmp != 0) {
            System.out.println(Long.toBinaryString(tmp));
            return index + Long.numberOfTrailingZeros(tmp) + 1;
        }
        int result = bitCount;
        for (int i = segmentIndex - 1; i >= 0; i--) {
            if (bitset[i] != 0) {
                result = SEGMENT_SIZE * segmentIndex + Long.numberOfTrailingZeros(bitset[i]);
                break;
            }
        }
        return result;
    }


    public void notAssign() {
        bitset[0] = ~(bitset[0] << prefixSize) >>> prefixSize;
        for (int i = 1; i < size; i++) {
            bitset[i] = ~bitset[i];
        }
    }

    public void xorAssign(final Bitset bs) {
        int in0 = size - 1;
        int in1 = bs.size - 1;
        for (; in0 >= 0 && in1 >= 0; in0--, in1--) {
            bitset[in0] ^= bs.bitset[in1];
        }
    }

    public void andAssign(final Bitset bs) {
        int in0 = size - 1;
        int in1 = bs.size - 1;
        for (; in0 >= 0 && in1 >= 0; in0--, in1--) {
            bitset[in0] &= bs.bitset[in1];
        }
        for (; in0 >= 0; in0--) {
            bitset[in0] = 0L;
        }
    }

    public void orAssign(final Bitset bs) {
        int in0 = size - 1;
        int in1 = bs.size - 1;
        for (; in0 >= 0 && in1 >= 0; in0--, in1--) {
            bitset[in0] |= bs.bitset[in1];
        }
    }

//    public void leftShiftAssign(final int shift) {
//        for (int i = 0; i < size; i++) {
//            bitset[i] <<= shift;
//        }
//    }
//
//    public void rightShiftAssign(final int shift) {
//        for (int i = 0; i < size; i++) {
//            bitset[i] >>>= shift;
//        }
//    }

    public int getSize() {
        return bitCount;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = SEGMENT_SIZE - prefixSize - 1; i >= 0; i--) {
            sb.append((bitset[0] >> i) & 1);
        }
        for (int i = 1; i < size; i++) {
            for (int j = SEGMENT_SIZE - 1; j >= 0; j--) {
                sb.append((bitset[i] >> j) & 1);
            }
        }
        sb.append("]");
        return sb.toString();
    }

}

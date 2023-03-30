package com.example.demo;

public class LSFR {
    //x^38 + x^6 + x^5 + x + 1
    private long currentState;

    private int n = 38;
    private int xorBit1 = 6;
    private int xorBit2 = 5;
    private int xorBit3 = 1;

    public LSFR(long currentState) {
        this.currentState = currentState;
    }

    public long getShiftBit(int n) {
        return (currentState >> (n-1)) & 1;
    }

    public int shift() {
        StringBuilder keyByte = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            keyByte.append(getShiftBit(n));
            long xor1 = getShiftBit(n) ^ getShiftBit(xorBit1);
            long xor2 = xor1 ^ getShiftBit(xorBit2);
            long xor3 = xor2 ^ getShiftBit(xorBit3);
            currentState = (currentState << 1) + xor3;
        }
        return Integer.parseInt(keyByte.toString(), 2);
    }
}

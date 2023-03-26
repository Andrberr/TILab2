package com.example.demo;

public class Register {
    //x^38 + x^6 + x^5 + x + 1
    long state;

    public Register(long state) {
        this.state = state;
    }

    public int shift() {
        StringBuilder keyByte = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            keyByte.append(getBit(state, 38));
            long xor1 = getBit(state, 6) ^ getBit(state, 38);
            long xor2 = xor1 ^ getBit(state, 5);
            long xor3 = xor2 ^ getBit(state, 1);
            state = (state << 1) + xor3;
        }
        return Integer.parseInt(keyByte.toString(), 2);
    }

    public long getBit(long state, int n) {
        return (state >> (n-1)) & 1;
    }
}

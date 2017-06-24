package virtualmachine;

import java.util.Arrays;

/*

MIT License

Copyright (c) 2017 Paul Olsen

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.



 */
public class Memory {
    private int[] memory;

    public Memory(int capacity) {
        memory = new int[capacity];
    }

    public void set(int address, int value) {
        check(address);
        memory[address] = value;
    }

    public int get(int address) {
        check(address);
        return memory[address];
    }

    public void check(int address) {
        if(address < 0 || address >= memory.length) {
            System.out.println("Illegal Memory Access: " + address + " for memory of capacity " + memory.length);
            System.exit(0);
        }
    }

    public int capacity() {
        return memory.length;
    }

    public int[] from(int start) {
        return Arrays.copyOfRange(memory, start, memory.length);
    }
}

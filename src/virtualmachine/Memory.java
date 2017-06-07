package virtualmachine;

import java.util.Arrays;

/**
 * Created by po917265 on 6/7/17.
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

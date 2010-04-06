package model;

class Util {
    static int BIG_NUM = 1000000;

    static void printIndent(int x) {
        for (int i = 0; i < x; ++i)
            System.out.print(" ");
    }

    static void printlnWithIndent(int x, String str) {
        printIndent(x);
        System.out.println(str);
    }
}

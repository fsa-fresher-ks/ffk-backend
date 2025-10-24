package fsa.fresher.pos;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Main {
    static class NumberCalc {
        public boolean isEven(int number) {
            return number % 2 == 0;
        }

    }

    public static void main(String[] args) throws FileNotFoundException {
//        List<Integer> list = List.of(1, 2, 3, 4, 5);
//        list.add(4);
//        NumberCalc calc = new NumberCalc();
//        Stream<Integer> stream = list.stream()
//                .filter(calc::isEven);
//        List<Integer> list2 = stream.toList();
//        List<Integer> list3 = stream.toList();
        BufferedReader br = new BufferedReader(new FileReader("abc.txt"));
    }
}

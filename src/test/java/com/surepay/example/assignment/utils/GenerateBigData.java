package com.surepay.example.assignment.utils;

import java.io.FileWriter;
import java.io.IOException;

/**
 * This is standalone utility to generate a fully-valid csv file with N rows,
 * and is not itself a unit test.
 */
public class GenerateBigData {
    
    /**
     * Useage: java com.surepay.example.assignment.utils.GenerateBigData <output file> <number of data rows>
     * Example: java com.surepay.example.assignment.utils.GenerateBigData output.csv 10
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        final String filename = args[0];
        final int rows = Integer.valueOf(args[1]);
        try (FileWriter fw = new FileWriter(filename)) {
            fw.write("Reference,AccountNumber,Description,Start Balance,Mutation,End Balance\n");
            for (int i = 0; i < rows; i++) {
                fw.write(String.join(",", new String[] {
                    Integer.toString(i),
                    "blah",
                    "Bob Loblaw",
                    "5",
                    "-3",
                    "2",
                }) + "\n");

            }
        }
    }
}

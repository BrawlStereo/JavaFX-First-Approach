package com.example.invoice.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class FolioGenerator {
    private static final AtomicInteger counter = new AtomicInteger(1);

    public static String nextFolio() {
        String year = DateTimeFormatter.ofPattern("yyyy").format(LocalDate.now());
        int num = counter.getAndIncrement();
        return String.format("INV-%s-%04d", year, num);
    }
}

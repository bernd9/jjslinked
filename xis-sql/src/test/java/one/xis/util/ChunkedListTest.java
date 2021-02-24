package one.xis.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChunkedListTest {

    @Test
    void nextChunk() {
        ChunkedList<Integer> chunkedList = new ChunkedList<>(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        var chunk = chunkedList.removeChunk(2);

        assertThat(chunk).hasSize(2);
        assertThat(chunkedList).hasSize(8);

        chunk = chunkedList.removeChunk(100);
        assertThat(chunk).hasSize(8);
        assertThat(chunkedList).isEmpty();
    }

    @Test
    void speed() {
        ChunkedList<Integer> chunkedList = new ChunkedList<>();
        for (int i = 0; i < 50000; i++) {
            chunkedList.add(i);
        }
        long t0 = System.currentTimeMillis();
        while (chunkedList.isEmpty()) {
            chunkedList.removeChunk(50);
        }
        long t1 = System.currentTimeMillis();
        System.out.println(t1 - t0);
    }
}
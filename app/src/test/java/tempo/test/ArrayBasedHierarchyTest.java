package tempo.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ArrayBasedHierarchyTest {
    @Test
    void testArrayBasedHierarchy_throwsWhenNodeIdsAndDepthsLengthsDiffer() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ArrayBasedHierarchy(new int[] { 1, 2 }, new int[] { 0 }));

        assertEquals("nodeIds and depths must have the same length", exception.getMessage());
    }

    @Test
    void testArrayBasedHierarchy_sizeReturnsNumberOfNodes() {
        Hierarchy hierarchy = new ArrayBasedHierarchy(new int[] { 10, 20, 30 }, new int[] { 0, 1, 2 });

        assertEquals(3, hierarchy.size());
    }

    @Test
    void testArrayBasedHierarchy_nodeIdReturnsCorrectValue() {
        Hierarchy hierarchy = new ArrayBasedHierarchy(new int[] { 10, 20, 30 }, new int[] { 0, 1, 2 });

        assertEquals(20, hierarchy.nodeId(1));
    }

    @Test
    void testArrayBasedHierarchy_depthReturnsCorrectValue() {
        Hierarchy hierarchy = new ArrayBasedHierarchy(new int[] { 10, 20, 30 }, new int[] { 0, 1, 2 });

        assertEquals(2, hierarchy.depth(2));
    }

    @Test
    void testArrayBasedHierarchy_formatStringReturnsExpectedRepresentation() {
        Hierarchy hierarchy = new ArrayBasedHierarchy(new int[] { 10, 20, 30 }, new int[] { 0, 1, 2 });

        assertEquals("[10:0, 20:1, 30:2]", hierarchy.formatString());
    }
}

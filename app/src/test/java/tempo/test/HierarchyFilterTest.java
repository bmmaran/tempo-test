package tempo.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.IntPredicate;

import org.junit.jupiter.api.Test;

public class HierarchyFilterTest {
    private static Hierarchy hierarchyOf(int[] nodeIds, int[] depths) {
        return new ArrayBasedHierarchy(nodeIds, depths);
    }

    private static void assertFilteredHierarchyEquals(
            int[] nodeIds,
            int[] depths,
            IntPredicate predicate,
            int[] expectedNodeIds,
            int[] expectedDepths) {
        Hierarchy actual = HierarchyFilter.filter(hierarchyOf(nodeIds, depths), predicate);
        Hierarchy expected = hierarchyOf(expectedNodeIds, expectedDepths);
        assertEquals(expected.formatString(), actual.formatString());
    }

    @Test
    void testFilter_basicTree_keepsOnlyMatchingBranches() {
        assertFilteredHierarchyEquals(
                new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 },
                new int[] { 0, 1, 2, 3, 1, 0, 1, 0, 1, 1, 2 },
                nodeId -> nodeId % 3 != 0,
                new int[] { 1, 2, 5, 8, 10, 11 },
                new int[] { 0, 1, 1, 0, 1, 2 });
    }

    @Test
    void testFilter_emptyHierarchy_emptyHierarchy() {
        assertFilteredHierarchyEquals(
                new int[] {},
                new int[] {},
                nodeId -> true,
                new int[] {},
                new int[] {});
    }

    @Test
    void testFilter_allNodesIncluded_sameHierarchy() {
        assertFilteredHierarchyEquals(
                new int[] { 1, 2, 3, 4, 5 },
                new int[] { 0, 1, 1, 2, 2 },
                nodeId -> true,
                new int[] { 1, 2, 3, 4, 5 },
                new int[] { 0, 1, 1, 2, 2 });
    }

    @Test
    void testFilter_allNodesExcluded_emptyHierarchy() {
        assertFilteredHierarchyEquals(
                new int[] { 1, 2, 3, 4, 5 },
                new int[] { 0, 1, 1, 2, 2 },
                nodeId -> false,
                new int[] {},
                new int[] {});
    }

    @Test
    void testFilter_singleNodeIncluded_sameNode() {
        assertFilteredHierarchyEquals(
                new int[] { 42 },
                new int[] { 0 },
                nodeId -> nodeId == 42,
                new int[] { 42 },
                new int[] { 0 });
    }

    @Test
    void testFilter_singleNodeExcluded_emptyHierarchy() {
        assertFilteredHierarchyEquals(
                new int[] { 42 },
                new int[] { 0 },
                nodeId -> nodeId != 42,
                new int[] {},
                new int[] {});
    }

    @Test
    void testFilter_rootExcluded_descendantsRemoved() {
        assertFilteredHierarchyEquals(
                new int[] { 1, 2, 3, 4, 5 },
                new int[] { 0, 1, 1, 2, 2 },
                nodeId -> nodeId != 1,
                new int[] {},
                new int[] {});
    }

    @Test
    void testFilter_internalNodeExcluded_subtreeRemoved() {
        assertFilteredHierarchyEquals(
                new int[] { 1, 2, 3, 4, 5 },
                new int[] { 0, 1, 1, 2, 2 },
                nodeId -> nodeId != 3,
                new int[] { 1, 2 },
                new int[] { 0, 1 });
    }

    @Test
    void testFilter_matchingSiblingsPreserved_afterFilteredBranch() {
        assertFilteredHierarchyEquals(
                new int[] { 1, 2, 3, 4, 5, 6 },
                new int[] { 0, 1, 1, 2, 2, 2 },
                nodeId -> nodeId != 2,
                new int[] { 1, 3, 4, 5, 6 },
                new int[] { 0, 1, 2, 2, 2 });
    }

    @Test
    void testFilter_leafNodesRemoved_keepsInternalNodes() {
        assertFilteredHierarchyEquals(
                new int[] { 1, 2, 3, 4, 5, 6, 7 },
                new int[] { 0, 1, 1, 2, 2, 2, 3 },
                nodeId -> nodeId != 4 && nodeId != 5 && nodeId != 6 && nodeId != 7,
                new int[] { 1, 2, 3 },
                new int[] { 0, 1, 1 });
    }

    @Test
    void testFilter_lastNodeRemoved_keepsEarlierNodes() {
        assertFilteredHierarchyEquals(
                new int[] { 1, 2, 3, 4 },
                new int[] { 0, 1, 1, 2 },
                nodeId -> nodeId != 4,
                new int[] { 1, 2, 3 },
                new int[] { 0, 1, 1 });
    }

    @Test
    void testFilter_singleRootRemoved_removesOnlyThatTree() {
        assertFilteredHierarchyEquals(
                new int[] { 1, 2, 3, 4, 10, 11, 12, 13, 20, 21 },
                new int[] { 0, 1, 1, 2, 0, 1, 1, 2, 0, 1 },
                nodeId -> nodeId != 10,
                new int[] { 1, 2, 3, 4, 20, 21 },
                new int[] { 0, 1, 1, 2, 0, 1 });
    }
}

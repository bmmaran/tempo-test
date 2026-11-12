package tempo.test;

import java.util.Arrays;

/**
 * A node is present in the filtered hierarchy iff its node ID passes the predicate and all of its ancestors pass it as
 * well.
 */
public class HierarchyFilter {
    private HierarchyFilter() {
        // Prevent instantiation; to be used as a static utility class.
    }

    public static Hierarchy filter(Hierarchy hierarchy, java.util.function.IntPredicate nodeIdPredicate) {
        int[] filteredNodeIds = new int[hierarchy.size()];
        int[] filteredDepths = new int[hierarchy.size()];
        int filteredSize = 0;

        for (int i = 0; i < hierarchy.size(); i++) {
            int nodeId = hierarchy.nodeId(i);
            if (nodeIdPredicate.test(nodeId)) {
                filteredNodeIds[filteredSize] = nodeId;
                filteredDepths[filteredSize] = hierarchy.depth(i);
                filteredSize++;
            } else {
                // If the node is filtered out, remove it and its children by skipping over them
                // in the hierarchy. The children will have a greater depth than the current node.
                int currentDepth = hierarchy.depth(i);
                while (i + 1 < hierarchy.size() && hierarchy.depth(i + 1) > currentDepth) {
                    i++;
                }
            }
        }

        return new ArrayBasedHierarchy(
            Arrays.copyOf(filteredNodeIds, filteredSize),
            Arrays.copyOf(filteredDepths, filteredSize)
        );
    }
}

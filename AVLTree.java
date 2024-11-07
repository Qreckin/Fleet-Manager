public class AVLTree{

    private AVLNode root; // Root of the tree
    public static class AVLNode{
        private AVLNode left; // Left node
        private AVLNode right; // Right node

        private AVLNode parent; // Parent node

        private int height; // Height of the node (distance from this node to leaf), initially 0

        public ParkingLot p; // The parking lot associated with the node

        AVLNode(ParkingLot p){
            this.p = p;
            height = 0; // Leaf node's distance to itself is 0
        }
    }

    AVLTree(){
        root = null;
    }

    public String count(int capacity){
        // Counts the number of trucks in parking lots with capacityConstraint greater than capacity
        return Integer.toString(findSum(root, capacity));
    }

    private int findSum(AVLNode node, int capacity){
        if (node == null) // Base case, null node has 0 trucks
            return 0;

        int impact = 0; // Impact of the current node on summation
        if (node.p.getCapacityConstraint() > capacity) // If its capacityConstraint is greater, set impact to it
            impact = node.p.getTruckCount();
        // Otherwise, impact stays 0

        // Return the sum of findSum(left subtree) + findSum(right subtree) + impact
        return impact + findSum(node.left, capacity) + findSum(node.right, capacity);
    }


    public AVLNode findSuccessor(AVLNode node){
        // Case-1 If the node has a right node, we can easily find the successor
        // Go right once then, go to left until reaching null
        if (node.right != null){
            node = node.right;
            while (node.left != null)
                node = node.left;
            return node;
        }

        //Case-2 If node's right node is null, then its successor is somewhere above
        // We will go upwards following the parent if current node is to the right of its parent
        AVLNode current = node;
        while (current.parent != null && current == current.parent.right){
            current = current.parent;
        }

        return current.parent;
    }

    public AVLNode findSmallerThan(int capacity){
        // Finds the node that contains the parking lot with greatest capacity that is smaller than or equal to given capacity
        return findSmallerThan(root, capacity, null);
    }

    private AVLNode findSmallerThan(AVLNode node, int capacity, AVLNode best){
        if (node == null) // Base case, return the best candidate if node is null
            return best;

        // Case-1 If current node's parking lot is smaller than or equal to given capacity, we might find our best
        if (node.p.getCapacityConstraint() <= capacity){
            best = node; // Update best
            return findSmallerThan(node.right, capacity, best); // Go to right for further exploration
        }
        // Case-2 If current node's parking lot is greater than to given capacity, we shall go to left
        return findSmallerThan(node.left, capacity, best);
    }

    public AVLNode findGreaterThan(int capacity){
        // Finds the node that contains the parking lot with smallest capacity that is greater than or equal to given capacity
        return findGreaterThan(root, capacity, null);
    }

    private AVLNode findGreaterThan(AVLNode node, int capacity, AVLNode best){
        if (node == null) // Base case, return the best candidate if node is null
            return best;

        // Case-1 If the current node's parking lot is greater than or equal to given capacity, we might find our best
        if (node.p.getCapacityConstraint() >= capacity){
            best = node; // Update best
            return findGreaterThan(node.left, capacity, best); // Go to left for further exploration
        }
        // Case-2 If the current node's parking lot is smaller than given capacity, we shall go right
        return findGreaterThan(node.right, capacity, best);
    }

    public void delete(int capacity){
        // Delete the specified node with parking lot with given capacity (if exists) then set the root to the new tree
        root = delete(root, capacity);
    }

    public AVLNode delete(AVLNode node, int capacity){

        if (node == null) // If node is null we could not find a parking lot with this capacity
            return null;


        if (capacity < node.p.getCapacityConstraint()) {
            // Potential node we will delete is to the left from the node
            // Perform the deletion on left subtree and assign the root of it to node.left
            node.left = delete(node.left, capacity);
        }
        else if (capacity > node.p.getCapacityConstraint()) {
            // Potential node we will delete is to the right from the node
            // Perform the deletion on right subtree and assign the root of it to node.right
            node.right = delete(node.right, capacity);
        }
        else{
            // We have found the node with the specified capacity

            // Case-1 not both of node's children exists (at least one of them is null)
            if (node.left == null || node.right == null){
                AVLNode existentNode; // The node preferably not the null one
                if (node.left != null)
                    existentNode = node.left;
                else
                    existentNode = node.right;

                // Case-1-a both of the children of the node are null
                if (existentNode == null){
                    // This means both of node's left and node's right are null
                    // So, removal is easy
                    // We return null, but if this node has a parent, since we recursively delete nodes, parent's children will be assigned to null
                    return null;
                }
                // Case-1-b one of them is null
                else{
                    // One of them is null and the other is not null

                    // Set not null one's parent to the deleted node's parent
                    existentNode.parent = node.parent;


                    // Update parent's children if parent is not null
                    if (node.parent != null){
                        if (node.parent.left == node){
                            node.parent.left = existentNode;
                        }
                        else{
                            node.parent.right = existentNode;
                        }
                    }
                    node = existentNode; // Now, instead of node, we have its not null child
                }
            }
            // Case2- none of node's children are null
            else{
                AVLNode successor = findSuccessor(node); // Find the smallest valued node that is greater than node's value

                // Swap node with its successor
                node.p = successor.p;

                // We copied the parking lot of successor to the node but now there are duplicate parking lots
                // So we must recursively delete successor node
                node.right = delete(node.right, successor.p.getCapacityConstraint());
            }
        }

        updateHeight(node); // Update the height of the node
        return balance(node); // Balance the node

    }


    public void insert(ParkingLot p){
        // Insert the specified node with parking lot with given capacity (if is not duplicate) then set the root to the new tree
        root = insert(root, p, null);
    }



    public AVLNode insert(AVLNode node, ParkingLot p, AVLNode parent){
        if (node == null){
            // If node is null, parent is where we want to add this parking lot below
            AVLNode newNode = new AVLNode(p); // Create the parking lot with a node
            newNode.parent = parent; // Set its parent to parent node (Actually this might be unnecessary due to recursive insert)
            return newNode;
        }

        if (p.compareTo(node.p) < 0) {
            node.left = insert(node.left, p, node);
        } else if (p.compareTo(node.p) > 0) {
            node.right = insert(node.right, p, node);
        } else {
            return node; // Insertion of duplicate values is not allowed
        }

        updateHeight(node); // Since we inserted a new node to an avl tree, heights are changed
        return balance(node); // Since we inserted a new node to an avl tree, balance factors might have changed
    }

    private AVLNode balance(AVLNode node){
        int bf = getBalanceFactor(node); // Left node height - right node height

        if (bf > 1 && getBalanceFactor(node.left) >= 0){
            // Left-Left case, inserted node is in the left subtree of node's left node
            node = rightRotate(node);
        }
        if (bf < -1 && getBalanceFactor(node.left) <= 0){
            // Right-Right case, inserted node is in the right subtree of node's right node
            node = leftRotate(node);
        }
        if (bf > 1 && getBalanceFactor(node.left) < 0){
            // Left-Right case, inserted node is in the right subtree of node's left node

            node.left = leftRotate(node.left); // First, perform left rotation on node's left
            node = rightRotate(node); // Second, perform right rotation on the node itself
        }
        if (bf < -1 && getBalanceFactor(node.right) > 0){
            // Right-Left case, inserted node is in the left subtree of node's right node

            node.right = rightRotate(node.right); // First, perform right rotation on node's right
            node = rightRotate(node); // Second, perform left rotation on the node itself
        }

        return node; // If we are here, node is balanced so just return it.
    }


    private AVLNode rightRotate(AVLNode imbalancedNode){
        AVLNode newNode = imbalancedNode.left; // New node is the node that will take the place of imbalanced node
        imbalancedNode.left = newNode.right; // Step 1-) change imbalanced node's left to newNode's right

        // Since we are storing parents, we must check after assignments
        if (newNode.right != null) {
            // If newNode's right is not null, set its parent to imbalanced node since it is now left to it
            newNode.right.parent = imbalancedNode;
        }

        newNode.right = imbalancedNode; // Since we are doing right rotation, imbalanced node will be to the right
        newNode.parent = imbalancedNode.parent; // newNode has taken the place of imbalancedNode, so its parent should change
        imbalancedNode.parent = newNode; // Necessary since we are using parent attribute

        // Now, the rotation has been completed but old imbalanced node's parent's child is newNode and we must state it
        if (newNode.parent != null) {
            if (newNode.parent.left == imbalancedNode) { // If imbalanced node was previously to the left of its parent
                newNode.parent.left = newNode;
            } else {
                newNode.parent.right = newNode;
            }
        }

        updateHeight(imbalancedNode); // Update height of initially imbalanced node
        updateHeight(newNode); // Update height of the newNode

        return newNode; // Return newNode since it has taken the place of the imbalanced node
    }

    private AVLNode leftRotate(AVLNode imbalancedNode){
        AVLNode newNode = imbalancedNode.right; // New node is the node that will take the place of imbalanced node
        imbalancedNode.right = newNode.left; // Step 1-) change imbalanced node's right to newNode's left

        // Since we are storing parents, we must check after assignments
        if (newNode.left != null) {
            // If newNode's left is not null, set its parent to imbalanced node since it is now right to it
            newNode.left.parent = imbalancedNode;
        }

        newNode.left = imbalancedNode; // Since we are doing left rotation, imbalanced node will be to the left
        newNode.parent = imbalancedNode.parent; // newNode has taken the place of imbalancedNode, so its parent should change
        imbalancedNode.parent = newNode; // Necessary since we are using parent attribute

        // Now, the rotation has been completed but old imbalanced node's parent's child is newNode and we must state it
        if (newNode.parent != null) {
            if (newNode.parent.left == imbalancedNode) { // If imbalanced node was previously to the left of its parent
                newNode.parent.left = newNode;
            } else {
                newNode.parent.right = newNode;
            }
        }

        updateHeight(imbalancedNode); // Update height of initially imbalanced node
        updateHeight(newNode); // Update height of the newNode

        return newNode; // Return newNode since it has taken the place of the imbalanced node
    }

    private void updateHeight(AVLNode node){
        // This is valid since every node below a node gets updated first, then we go up
        // So, any node's height can be calculated as the biggest one of its right and left child's height + 1
        node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));
    }

    private int getHeight(AVLNode node){
        if (node == null) // Convention, since leaf nodes' heights are 0, nulls' must be -1
            return -1;
        return node.height; // Otherwise just return the height
    }


    private int getBalanceFactor(AVLNode node){
        if (node == null) // Null node's are not imbalanced
            return 0;
        return getHeight(node.left) - getHeight(node.right); // Otherwise, leftHeight - rightHeight
    }


}

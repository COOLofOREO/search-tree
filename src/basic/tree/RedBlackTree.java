package basic.tree;

import date.Date;

/**
 * 红黑树
 */
public class RedBlackTree {

    private Node root;

    enum Color{
        RED,BLACK
    }

    private static class Node{
        int key;
        Date value;
        Node left;
        Node right;
        Node parent;
        Color color=Color.RED;

        /**
         * 是否是左孩子
         * @return
         */
        boolean isLeftChild(){
            return parent!=null && parent.left==this;
        }

        /**
         * 获得叔叔节点
         * @return
         */
        Node uncle(){
            if(parent==null||parent.parent==null) return null;
            if(parent.isLeftChild()) return parent.parent.right;
            else return parent.parent.left;
        }

        /**
         * 获得兄弟节点
         * @return
         */
        Node sibling(){
            if(parent==null) return null;
            if(this.isLeftChild()) return parent.right;
            else return parent.left;
        }

        public Node(int key, Date value) {
            this.key = key;
            this.value = value;
        }

        public Date getValue(){
            return this.value;
        }

        public void setValue(Date value){
            this.value=value;
        }
    }

    private boolean isRed(Node node){
        return node!=null && node.color==Color.RED;
    }

    private boolean isBlack(Node node){
        return node==null || node.color==Color.BLACK;
    }

    /**
     * 左旋
     * @param node
     */
    private void leftRotate(Node node){
        Node parent=node.parent;
        Node left=node.left;
        Node right=node.right;
        if(left!=null) left.parent=node;
        right.left=node;
        right.parent=parent;
        node.right=left;
        node.parent=right;
        if(parent==null) root=right;
        else if(parent.right==node) parent.right=right;
        else parent.left=right;
    }

    /**
     * 右旋
     * @param node
     */
    private void rightRotate(Node node){
        Node parent=node.parent;
        Node left=node.left;
        Node right=node.right;
        if(right!=null) right.parent=node;
        left.right=node;
        left.parent=parent;
        node.left=right;
        node.parent=left;
        if(parent==null) root=left;
        else if(parent.left==node) parent.left=left;
        else parent.right=left;
    }

    /**
     * 新增或更新
     * @param key
     * @param value
     */
    public void put(int key,Date value){
        Node node=root;
        Node parent=null;
        while (node!=null){
            parent=node;
            if(key<node.key) node=node.left;
            else if(key>node.key) node=node.left;
            else {
                //更新
                node.value=value;
                return;
            }
        }
        Node inserted=new Node(key,value);
        if(parent==null) root=inserted;
        else if(key< parent.key){
            parent.left=inserted;
            inserted.parent=parent;
        }else {
            parent.right=inserted;
            inserted.parent=parent;
        }
        fixRedRed(inserted);
    }

    /**
     * 调整节点颜色
     * @param node
     */
    private void fixRedRed(Node node){
        //case 1:插入节点是根节点，直接变黑即可
        if(node==root){
            node.color=Color.BLACK;
            return;
        }
        //case 2:插入节点的父节点是黑色，无需调整
        if(isBlack(node.parent)) return;
        /*
        case 3:插入节点的父节点是红色，叔节点是红色
        将父亲和叔叔变黑，祖父变红，对祖父做递归处理
        */
        Node parent=node.parent;
        Node uncle=node.uncle();
        Node grandparent=node.parent.parent;
        if(isRed(uncle)){
            parent.color=Color.BLACK;
            uncle.color=Color.BLACK;
            grandparent.color=Color.RED;
            fixRedRed(grandparent);
            return;
        }
        //case 4:插入节点的父节点是红色，叔节点是黑色
        if(parent.isLeftChild()&&node.isLeftChild()){
            //LL
            parent.color=Color.BLACK;
            grandparent.color=Color.RED;
            rightRotate(grandparent);
        }else if(parent.isLeftChild()){
            //LR
            leftRotate(parent);
            node.color=Color.BLACK;
            grandparent.color=Color.RED;
            rightRotate(grandparent);
        }else if(node.isLeftChild()){
            //RL
            rightRotate(parent);
            node.color=Color.BLACK;
            grandparent.color=Color.RED;
            leftRotate(grandparent);
        }else {
            //RR
            parent.color=Color.BLACK;
            grandparent.color= Color.RED;
            leftRotate(grandparent);
        }
    }

    private void fixDoubleBlack(Node node){
        if(node==root) return;
        Node parent=node.parent;
        Node sibling = node.sibling();
        if(isRed(sibling)){
            //被调整节点的兄弟是红色
            if(node.isLeftChild()){
                leftRotate(parent);
            }else rightRotate(parent);
            parent.color=Color.RED;
            sibling.color=Color.BLACK;
            fixDoubleBlack(node);
            return;
        }
        //兄弟节点是黑色
        if(sibling!=null){
            if(isBlack(sibling.left)&&isBlack(sibling.right)){
                //兄弟节点是黑色,两个侄子也是黑色
                sibling.color=Color.RED;
                if(isRed(parent)) parent.color=Color.BLACK;
                else fixDoubleBlack(parent);
            }else {
                //兄弟节点是黑色,侄子有红色的
                if(sibling.isLeftChild()&&isRed(sibling.left)){
                    //LL
                    rightRotate(parent);
                    sibling.left.color=Color.BLACK;
                    sibling.color=parent.color;
                }else if(sibling.isLeftChild()&&isRed(sibling.right)){
                    //LR
                    sibling.right.color=parent.color;
                    leftRotate(sibling);
                    rightRotate(parent);
                }else if(!sibling.isLeftChild()&&isRed(sibling.left)){
                    //RL
                    sibling.left.color=parent.color;
                    rightRotate(sibling);
                    leftRotate(parent);
                }else {
                    ///RR
                    leftRotate(parent);
                    sibling.right.color=Color.BLACK;
                    sibling.color=parent.color;
                }
                parent.color=Color.BLACK;
            }
        }else {
            fixDoubleBlack(parent);
        }

    }

    /**
     * 删除
     * @param key
     */
    public void remove(int key){
        Node deleted = find(key);
        if(deleted==null) return;
        doRemove(deleted);
    }

    private void doRemove(Node deleted){
        Node replace = findReplace(deleted);
        Node parent=deleted.parent;
        if(replace==null){
            //没有孩子
            if(deleted==root){
                //删除的是根节点
                root=null;
            }else {
                if(isBlack(deleted)){
                    //复杂处理
                    fixDoubleBlack(deleted);
                }else {
                    //红色叶子节点，无需任何处理
                }
                //删除的不是根节点
                if(deleted.isLeftChild()){
                    parent.left=null;
                }else {
                    parent.right=null;
                }
                deleted.parent=null;
            }
            return;
        }
        if(deleted.left==null||deleted.right==null){
            //有一个孩子
            if(deleted==root){
                //删除的是根节点
                root.key= replace.key;
                root.value= replace.value;
                root.left=null;
                root.right=null;
            }else {
                //删除的不是根节点
                if(deleted.isLeftChild()){
                    parent.left=replace;
                }else {
                    parent.right=replace;
                }
                replace.parent=parent;
                //垃圾回收
                deleted.left=deleted.right=deleted.parent=null;
                if(isBlack(deleted)&&isBlack(replace)){
                    //复杂处理
                    fixDoubleBlack(replace);
                }else {
                    //case 2
                    replace.color=Color.BLACK;
                }
            }
            return;
        }
        //有两个孩子
        exchangeValue(deleted,replace);
        doRemove(replace);
    }

    /**
     * 交换两个节点的值
     * @param a
     * @param b
     */
    private void exchangeValue(Node a,Node b){
        int t=a.key;
        a.key=b.key;
        b.key=t;
        Date v=a.getValue();
        a.setValue(b.getValue());
        b.setValue(v);
    }

    /**
     * 查找节点
     * @param key
     * @return
     */
    private Node find(int key){
        Node node=root;
        while (node!=null){
            if(key<node.key) node=node.left;
            else if(key>node.key) node=node.right;
            else return node;
        }
        return null;
    }

    /**
     * 查找当前节点的后继节点
     * @param deleted
     * @return
     */
    private Node findReplace(Node deleted){
        if(deleted.left==null&&deleted.right==null) return null;
        if(deleted.left==null) return deleted.right;
        if(deleted.right==null) return deleted.left;
        Node right=deleted.right;
        while (right.left!=null){
            right=root.left;
        }
        return right;
    }
}

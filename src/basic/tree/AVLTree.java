package basic.tree;

import date.Date;

/**
 * AVL树
 */
public class AVLTree {

    private AVLNode root;

    static class AVLNode{
        int key;
        Date date;
        AVLNode left;
        AVLNode right;
        int height=1;

        public AVLNode(int key) {
            this.key = key;
        }

        public AVLNode(int key, Date date) {
            this.key = key;
            this.date = date;
        }

        public AVLNode(int key, Date date, AVLNode left, AVLNode right) {
            this.key = key;
            this.date = date;
            this.left = left;
            this.right = right;
        }
    }

    /**
     * 返回节点高度
     * @param node
     * @return
     */
    private int height(AVLNode node){
        return node==null?0:node.height;
    }

    /**
     * 更新节点高度
     * @param node
     */
    private void updateHeight(AVLNode node){
        node.height=Integer.max(height(node.left),height(node.right))+1;
    }

    /**
     * 返回节点平衡因子
     * @param node
     * @return
     */
    private int balanceFactor(AVLNode node){
        return height(node.left)-height(node.right);
    }

    /**
     * 左旋
     * @param node
     * @return
     */
    private AVLNode leftRotate(AVLNode node){
        AVLNode right=node.right;
        node.right=right.left;
        right.left=node;
        //更新节点高度
        updateHeight(node);
        updateHeight(right);
        return right;
    }

    /**
     * 右旋
     * @param node
     * @return
     */
    private AVLNode rightRotate(AVLNode node){
        AVLNode left=node.left;
        node.left=left.right;
        left.right=node;
        //更新节点高度
        updateHeight(node);
        updateHeight(left);
        return left;
    }

    /**
     * 先左旋左子树，再右旋根节点
     * @param node
     * @return
     */
    private AVLNode leftRightRotate(AVLNode node){
        node.left=leftRotate(node.left);
        return rightRotate(node);
    }

    /**
     * 先右旋右子树，再左旋根节点
     * @param node
     * @return
     */
    private AVLNode rightLeftRotate(AVLNode node){
        node.right=rightRotate(node.right);
        return leftRotate(node);
    }

    /**
     * 检查是否失衡
     * 若失衡，则将其平很
     * @param node
     * @return
     */
    private AVLNode balance(AVLNode node){
        if(node==null) return null;
        int bf=balanceFactor(node);
        if(bf>1){
            int leftBf=balanceFactor(node.left);
            if(leftBf>=0){
                //LL
                return rightRotate(node);
            }else{
                //LR
                return leftRightRotate(node);
            }
        } else if (bf<-1) {
            int rightBf=balanceFactor(node.right);
            if(rightBf<=0){
                //RR
                return leftRotate(node);
            }else {
                //RL
                return rightLeftRotate(node);
            }
        }
        return node;
    }

    /**
     * 新增节点
     * @param key
     * @param value
     */
    public void put(int key,Date value){
        root=doPut(root,key,value);
    }

    private AVLNode doPut(AVLNode node,int key,Date value){
        //找到空位，创建新节点
        if(node==null){
            return new AVLNode(key,value);
        }
        //key已存在，更新
        if(key==node.key){
            node.date=value;
            return node;
        }
        //递归查找
        if(key<node.key){
            node.left=doPut(node.left,key,value);
        }else {
            node.right=doPut(node.right,key,value);
        }
        updateHeight(node);
        return balance(node);
    }

    /**
     * 删除节点
     * @param key
     */
    public void remove(int key){
        root=doRemove(root,key);
    }

    private AVLNode doRemove(AVLNode node,int key){
        //node=null
        if (node==null) return null;
        //没有找到key
        if(key< node.key){
            node.left=doRemove(node.left,key);
        }else if(key>node.key){
            node.right=doRemove(node.right,key);
        }else {
            //找到key 1.没有孩子 2，有一个孩子 3.有两个孩子
            if(node.left==null&&node.right==null){
                return null;
            }else if(node.left==null){
                node=node.right;
            }else if(node.right==null){
                node=node.left;
            }else {
                AVLNode s=node.right;
                //求后继节点
                while (s.left!=null) s=s.left;
                s.right=doRemove(node.right,s.key);
                s.left=node.left;
                node=s;
            }
        }
        //更新高度
        updateHeight(node);
        //检查失衡
        return balance(node);
    }
}











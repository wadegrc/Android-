package com.example.fivechess.Utils;


import com.example.fivechess.AI.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenov0 on 2016/3/3.
 */
public class OperationQueue {

    private List<Point> mOperations = new ArrayList<>();

    public void clear() {
        mOperations.clear();
    }

    public void addOperation(Point point) {
        mOperations.add(point);
    }

    public void removeLastOperation(){
        mOperations.remove(mOperations.size() - 1);
    }

    public Point getLastOperation(){
        if (!mOperations.isEmpty()) {
            return mOperations.get(mOperations.size() - 1);
        } else {
            return null;
        }
    }
    public int size(){
        return mOperations.size();
    }
}

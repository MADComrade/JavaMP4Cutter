package com.asklab.interfaces;

public interface CallbackInterface {
    enum DIRECT_RESIZE {
        INCREASED,
        DECREASED
    }

    public void resizeAtom(Integer size, DIRECT_RESIZE direction);
}

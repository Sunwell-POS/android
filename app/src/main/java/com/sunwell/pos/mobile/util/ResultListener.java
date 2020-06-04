package com.sunwell.pos.mobile.util;

import java.util.Map;

public interface ResultListener<T>
{
    public void onResult(Object source, T result) throws Exception;

    public void onError(Object source, int errCode) throws Exception;

    public void onData(Object source, Map<String, Object> data) throws Exception;
}

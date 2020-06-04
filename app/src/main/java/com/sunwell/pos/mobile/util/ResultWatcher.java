package com.sunwell.pos.mobile.util;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by sunwell on 10/19/17.
 */
public abstract class ResultWatcher<T> implements ResultListener<T>, Serializable
{
    public void onResult(Object source, T result) throws Exception
    {
    }

    ;

    public void onError(Object source, int errCode) throws Exception
    {
    }

    ;

    public void onData(Object source, Map<String, Object> data) throws Exception
    {
    }
}

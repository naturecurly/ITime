/*
 * Copyright (C) 2016 Yorkfine Chan <yorkfinechan@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.itime.team.itime.utils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;

import java.util.Map;

/**
 * Created by Xuhui Chen (yorkfine) on 24/03/16.
 *
 *
 * A request for retrieving a T type response body at a given URL that also
 * optionally sends along a JSON body in the request specified.
 *
 * @param <T> JSON type of response expected
 */
public abstract class JsonFormRequest<T> extends Request<T> {

    /** Default charset for JSON request. */
    protected static final String PROTOCOL_CHARSET = "utf-8";

    private Response.Listener<T> mListener;
    private final Map<String ,String> mParams;

    /**
     * Deprecated constructor for a JsonFormRequest which defaults to GET unless {@link #getPostBody()}
     * or {@link #getPostParams()} is overridden (which defaults to POST).
     *
     * @deprecated Use {@link #JsonFormRequest(int, String, Map<String, String>, Response.Listener, Response.ErrorListener)}.
     */
    public JsonFormRequest(String url, Map<String, String> params, Response.Listener<T> listener,
                       Response.ErrorListener errorListener) {
        this(Method.DEPRECATED_GET_OR_POST, url, params, listener, errorListener);
    }

    public JsonFormRequest(int method, String url, Map<String, String> params, Response.Listener<T> listener,
                       Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
        mParams = params;
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        mListener = null;
    }

    @Override
    abstract protected Response<T> parseNetworkResponse(NetworkResponse response);


    @Override
    protected void deliverResponse(T response) {
        if (mListener != null) {
            mListener.onResponse(response);
        }
    }

    /* No override of getPostBodyContentType, getBodyContentType getPostBody, getBody
     * These methods derive from {@link Request}
     */

    /**
     * Returns a Map of parameters to be used for a POST or PUT request.
     */
    protected Map<String, String> getParams() {
        return mParams;
    }
}

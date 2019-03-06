package com.fafa.guest;

public interface Callback<P,R> {

    R call(P p);
}

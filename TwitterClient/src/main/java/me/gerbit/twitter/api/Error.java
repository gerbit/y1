package me.gerbit.twitter.api;

public class Error {

    private final int mErrorCode;

    private final String mErrorMessage;

    Error(final int code, final String msg) {
        mErrorCode = code;
        mErrorMessage = msg;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }
}

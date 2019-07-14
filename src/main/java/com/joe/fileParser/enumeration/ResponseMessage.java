package com.joe.fileParser.enumeration;

public enum  ResponseMessage {
    SAVE_SUCCESS("Data saving successfully."),
    SAVE_FAIL("Data saving failed."),
    DELETE_SUCCESS("Data deletion successful."),
    DELETE_FAIL("Data deletion failed."),
    QUERY_SUCCESS("Data query successful."),
    QUERY_FAIL("Data query failed."),
    UPDATE_SUCCESS("Data updated successfully."),
    UPDATE_FAIL("Data update failed."),
    UPLOAD_FILE_EMPTY("The uploaded file is empty."),
    UPLOAD_FILE_SUCCESS("The uploaded file was successful."),
    UPLOAD_FILE_FAIL("The uploaded file failed."),
    ;

    private String message;

    ResponseMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}

package com.github.lutzblox.query;

/**
 * @author Christopher Lutz
 */
public class QueryRequest {

    private String id;
    private QueryType type;

    public QueryRequest(String id, QueryType type){

        this.id = id;
        this.type = type;
    }

    public String getId(){

        return id;
    }

    public QueryType getType(){

        return type;
    }
}

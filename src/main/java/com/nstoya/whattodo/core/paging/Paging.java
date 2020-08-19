package com.nstoya.whattodo.core.paging;

public class Paging {

    public static final int MAX_PAGE_SIZE = 1000;
    public static final int STANDARD_PAGE_SIZE = 100;

    public static long lastPage(long total, long page, long pageSize){
        if (pageSize == 0L || pageSize > Paging.MAX_PAGE_SIZE){
            pageSize = Paging.STANDARD_PAGE_SIZE;
        }

        long fullPages = total / pageSize;
        long restPage = total % pageSize;
        long lastPage = 0L;
        if(restPage > 0){
            lastPage = fullPages + 1;
        }
        return lastPage;
    }

    public static long nextPage(long total, long page, long pageSize){
        if (page <= 0L){
            page = 1;
        }

        if((page + 1) < lastPage(total, page, pageSize)){
            return page + 1;
        }
        return page;
    }

}

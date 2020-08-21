package com.nstoya.whattodo.core.paging;

import javax.ws.rs.core.UriInfo;

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

        if((page + 1) <= lastPage(total, page, pageSize)){
            return page + 1;
        }
        return 0;
    }

    public static String getLinkHeader(UriInfo uriInfo, String path, long total, long page, int pageSize){
       long nextPage = nextPage(total, page, pageSize);
        String nextPageHeader = nextPage == 0
                ? ""
                : "<" + uriInfo.getBaseUri() + path + "?page=" + nextPage + "&pageSize="
                    + (pageSize != 0
                        ? pageSize
                        : Paging.STANDARD_PAGE_SIZE) + ">; rel=\"next\", " ;
        return  nextPageHeader
                + "<" + uriInfo.getBaseUri() + path + "?page=" + (Paging.lastPage(total, nextPage - 1, pageSize))
                + "&pageSize=" + (pageSize != 0 ? pageSize : Paging.STANDARD_PAGE_SIZE) + ">; rel=\"last\"";

    }

}

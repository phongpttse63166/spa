package swp490.spa.dto.helper;


import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import swp490.spa.dto.support.Paging;
import swp490.spa.dto.support.Response;

import java.util.List;

public class ResponseHelper {
    public static <T> Response<T> ok(T data){
        Response<T> response = new Response<>();
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK.name());
        response.setData(data);
        return response;
    }

    public static <T> Response<T> error(T data){
        Response<T> response = new Response<>();
        response.setCode(HttpStatus.NO_CONTENT.value());
        response.setStatus(HttpStatus.NO_CONTENT.name());
        response.setData(data);
        return response;
    }

    public static <T> Response<List<T>> ok(Page<T> page){
        Response<List<T>> response = new Response<>();
        response.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK.name());
        response.setData(page.getContent());

        Paging paging = new Paging();
        paging.setPage(page.getNumber());
        paging.setItemPerPage(page.getSize());
        paging.setTotalItem(page.getTotalElements());
        paging.setTotalPage(page.getTotalPages());
        response.setPaging(paging);
        return response;
    }
}

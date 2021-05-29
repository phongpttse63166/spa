package swp490.spa.dto.support;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {
    @JsonProperty("code")
    private Integer code;

    @JsonProperty("status")
    private String status;

    @JsonProperty("data")
    private T data;

    @JsonProperty("paging")
    private Paging paging;

    @JsonProperty("errors")
    private Map<String, List<String>> error;

    public Response() {
    }

    public Response(Integer code, String status, T data, Paging paging, Map<String, List<String>> error) {
        this.code = code;
        this.status = status;
        this.data = data;
        this.paging = paging;
        this.error = error;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }

    public Map<String, List<String>> getError() {
        return error;
    }

    public void setError(Map<String, List<String>> error) {
        this.error = error;
    }

}

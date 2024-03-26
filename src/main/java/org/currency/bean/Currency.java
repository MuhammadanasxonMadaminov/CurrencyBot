package org.currency.bean;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Currency {
    private Integer id;
    private String code;

    @SerializedName("CcyNm_UZ")
    private String shortName;

    @SerializedName("Rate")
    private Double rate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }
}

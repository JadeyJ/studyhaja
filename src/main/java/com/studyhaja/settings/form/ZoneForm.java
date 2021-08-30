package com.studyhaja.settings.form;

import com.studyhaja.domain.Zone;
import lombok.Data;

@Data
public class ZoneForm {

    private String zoneName;

    public String getCityName() {
        return zoneName.substring(0, zoneName.indexOf("("));
    }

    public String getLocalNameofCity() {
        return zoneName.substring(zoneName.indexOf("(") + 1, zoneName.indexOf(")"));
    }

    public String getProvinceName() {
        return zoneName.substring(zoneName.indexOf("/") + 1);
    }

    public Zone getZone() {
        return Zone.builder()
                .city(this.getCityName())
                .localNameOfCity(this.getLocalNameofCity())
                .province(this.getProvinceName())
                .build();
    }

}

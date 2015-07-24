package eu.europa.ec.fisheries.uvms.exchange.service.entity;

import eu.europa.ec.fisheries.uvms.exchange.service.constants.ServiceConstants;
import java.io.Serializable;
import javax.persistence.*;

/**
 * The persistent class for the parameter database table.
 *
 */
@Entity
@NamedQuery(name = ServiceConstants.FIND_BY_NAME, query = "SELECT p FROM Parameter p WHERE p.paramName = :key")
public class Parameter implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "param_id")
    private Integer paramId;

    @Column(name = "param_description")
    private String paramDescription;

    @Column(name = "param_name")
    private String paramName;

    @Column(name = "param_value")
    private String paramValue;

    public Parameter() {
    }

    public Integer getParamId() {
        return this.paramId;
    }

    public void setParamId(Integer paramId) {
        this.paramId = paramId;
    }

    public String getParamDescription() {
        return this.paramDescription;
    }

    public void setParamDescription(String paramDescription) {
        this.paramDescription = paramDescription;
    }

    public String getParamName() {
        return this.paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamValue() {
        return this.paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

}

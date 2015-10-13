package eu.europa.ec.fisheries.uvms.exchange.service.entity;

import eu.europa.ec.fisheries.uvms.exchange.service.constants.ExchangeServiceConstants;
import java.io.Serializable;
import javax.persistence.*;

/**
 * The persistent class for the parameter database table.
 *
 */
@Entity
@NamedQueries({
    @NamedQuery(name = ExchangeServiceConstants.FIND_BY_NAME, query = "SELECT p FROM Parameter p WHERE p.paramDescription = :parameterDescription"),
    @NamedQuery(name = ExchangeServiceConstants.LIST_ALL, query = "SELECT p FROM Parameter p")
})
public class Parameter implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "param_id")
    private String paramId;

    @Column(name = "param_description")
    private String paramDescription;

    @Column(name = "param_value")
    private String paramValue;

    public Parameter() {
    }

    public String getParamId() {
        return this.paramId;
    }

    public void setParamId(String paramId) {
        this.paramId = paramId;
    }

    public String getParamDescription() {
        return this.paramDescription;
    }

    public void setParamDescription(String paramDescription) {
        this.paramDescription = paramDescription;
    }

    public String getParamValue() {
        return this.paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

}

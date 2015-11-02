package eu.europa.ec.fisheries.uvms.exchange.service.converter;

import javax.xml.datatype.XMLGregorianCalendar;
import org.dozer.DozerConverter;

public class XMLGC2XMLGC extends DozerConverter<XMLGregorianCalendar, XMLGregorianCalendar> {

    public XMLGC2XMLGC() {
        super(XMLGregorianCalendar.class, XMLGregorianCalendar.class);
    }

    @Override
    public XMLGregorianCalendar convertFrom(XMLGregorianCalendar src, XMLGregorianCalendar dest) {
        return src;
    }

    @Override
    public XMLGregorianCalendar convertTo(XMLGregorianCalendar src, XMLGregorianCalendar dest) {
        return dest;
    }

}

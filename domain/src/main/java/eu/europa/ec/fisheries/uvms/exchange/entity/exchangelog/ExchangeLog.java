/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.uvms.exchange.entity.exchangelog;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;
import eu.europa.ec.fisheries.schema.exchange.v1.LogType;
import eu.europa.ec.fisheries.schema.exchange.v1.TypeRefType;
import eu.europa.ec.fisheries.uvms.exchange.constant.ExchangeConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import static eu.europa.ec.fisheries.uvms.exchange.entity.exchangelog.ExchangeLog.*;
import static eu.europa.ec.fisheries.uvms.exchange.entity.exchangelog.ExchangeLog.LIST_EXCHANGE;

@Entity
@Table(name="log")
@NamedQueries({
  @NamedQuery(name = ExchangeConstants.LOG_BY_GUID, query = "SELECT log FROM ExchangeLog log WHERE log.guid = :guid AND ((:typeRefType = null) OR (log.typeRefType = :typeRefType))"),
  @NamedQuery(name = ExchangeConstants.LOG_BY_TYPE_RANGE_OF_REF_GUIDS, query = "SELECT DISTINCT log FROM ExchangeLog log WHERE log.typeRefGuid IN (:refGuids)"),
  @NamedQuery(name = ExchangeConstants.LOG_BY_TYPE_REF_AND_GUID, query = "SELECT log FROM ExchangeLog log WHERE log.typeRefGuid = :typeRefGuid AND log.typeRefType in (:typeRefTypes)"),
        @NamedQuery(name = LIST_EXCHANGE, query = "SELECT e FROM ExchangeLog e WHERE (e.dateReceived BETWEEN :DATE_RECEIVED_FROM AND :DATE_RECEIVED_TO ) AND " +
                        "(((:GUID is NULL) OR (UPPER(cast(e.guid as string)) LIKE CONCAT('%', UPPER(cast(:GUID as string)), '%'))) OR " +
                        "((:TYPEREFGUID is NULL) OR (UPPER(cast(e.typeRefGuid as string)) LIKE CONCAT('%', UPPER(cast(:TYPEREFGUID as string)), '%'))) OR " +
						"((:ON is NULL) OR (UPPER(cast(e.onValue as string)) LIKE CONCAT('%', UPPER(cast(:ON as string)), '%'))) OR " +
						"((:SENDER_RECEIVER is NULL) OR (UPPER(cast(e.senderReceiver as string)) LIKE CONCAT('%', UPPER(cast(:SENDER_RECEIVER as string)), '%')))) AND " +
                        "((:TYPEREFTYPE is NULL) OR (UPPER(cast(e.typeRefType as string)) LIKE CONCAT('%', UPPER(cast(:TYPEREFTYPE as string)), '%'))) AND " +
                        "((:STATUS is NULL) OR (UPPER(cast(e.status as string)) LIKE CONCAT('%', UPPER(cast(:STATUS as string)), '%'))) AND " +
                        "((:SOURCE is NULL) OR (UPPER(cast(e.source as string)) LIKE CONCAT('%', UPPER(cast(:SOURCE as string)), '%'))) AND " +
                        "((:RECIPIENT is NULL) OR (UPPER(cast(e.recipient as string)) LIKE CONCAT('%', UPPER(cast(:RECIPIENT as string)), '%'))) AND " +
                        "((:ON is NULL) OR (UPPER(cast(e.onValue as string)) LIKE CONCAT('%', UPPER(cast(:ON as string)), '%'))) AND " +
                        "((:DF is NULL) OR (UPPER(cast(e.df as string)) LIKE CONCAT('%', UPPER(cast(:DF as string)), '%'))) AND " +
                        "((:TODT is NULL) OR (UPPER(cast(e.todt as string)) LIKE CONCAT('%', UPPER(cast(:TODT as string)), '%'))) AND " +
                        "((:AD is NULL) OR (UPPER(cast(e.ad as string)) LIKE CONCAT('%', UPPER(cast(:AD as string)), '%'))) AND " +
                        "(e.transferIncoming = :INCOMING OR e.transferIncoming = :OUTGOING)"
        ),
		@NamedQuery(name = COUNT_LIST_EXCHANGE, query = "SELECT count(*) FROM ExchangeLog e WHERE (e.dateReceived BETWEEN :DATE_RECEIVED_FROM AND :DATE_RECEIVED_TO ) AND " +
				"(((:GUID is NULL) OR (UPPER(cast(e.guid as string)) LIKE CONCAT('%', UPPER(cast(:GUID as string)), '%'))) OR " +
				"((:TYPEREFGUID is NULL) OR (UPPER(cast(e.typeRefGuid as string)) LIKE CONCAT('%', UPPER(cast(:TYPEREFGUID as string)), '%'))) OR " +
				"((:ON is NULL) OR (UPPER(cast(e.onValue as string)) LIKE CONCAT('%', UPPER(cast(:ON as string)), '%'))) OR " +
				"((:SENDER_RECEIVER is NULL) OR (UPPER(cast(e.senderReceiver as string)) LIKE CONCAT('%', UPPER(cast(:SENDER_RECEIVER as string)), '%')))) AND " +
				"((:TYPEREFTYPE is NULL) OR (UPPER(cast(e.typeRefType as string)) LIKE CONCAT('%', UPPER(cast(:TYPEREFTYPE as string)), '%'))) AND " +
				"((:STATUS is NULL) OR (UPPER(cast(e.status as string)) LIKE CONCAT('%', UPPER(cast(:STATUS as string)), '%'))) AND " +
				"((:SOURCE is NULL) OR (UPPER(cast(e.source as string)) LIKE CONCAT('%', UPPER(cast(:SOURCE as string)), '%'))) AND " +
				"((:RECIPIENT is NULL) OR (UPPER(cast(e.recipient as string)) LIKE CONCAT('%', UPPER(cast(:RECIPIENT as string)), '%'))) AND " +
				"((:DF is NULL) OR (UPPER(cast(e.df as string)) LIKE CONCAT('%', UPPER(cast(:DF as string)), '%'))) AND " +
				"((:TODT is NULL) OR (UPPER(cast(e.todt as string)) LIKE CONCAT('%', UPPER(cast(:TODT as string)), '%'))) AND " +
				"((:AD is NULL) OR (UPPER(cast(e.ad as string)) LIKE CONCAT('%', UPPER(cast(:AD as string)), '%'))) AND " +
                "(e.transferIncoming = :INCOMING OR e.transferIncoming = :OUTGOING)"
        )
})
@Data
@EqualsAndHashCode(exclude = "statusHistory")
@ToString(exclude = "statusHistory")
public class ExchangeLog implements Serializable {

    public static final String LOG_BY_TYPE_RANGE_OF_REF_GUIDS = "Log.findByRangeOfRefGuids";
    public static final String LIST_EXCHANGE = "exchange.list";
	public static final String COUNT_LIST_EXCHANGE = "exchange.countlist";

    @Id
	@Column(name="log_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name="log_type")
	@Enumerated(EnumType.STRING)
	private LogType type;
	
	@Column(name="log_type_ref_guid")
	private String typeRefGuid;
	
	@Enumerated(EnumType.STRING)
	@Column(name="log_type_ref_type")
	private TypeRefType typeRefType;

	@Column(name = "log_type_ref_message")
	private String typeRefMessage;

	@Column(name = "log_to")
	private String to;

    @Column(name = "log_df")
    private String df;

    @Column(name = "log_todt")
	private String todt;

	@Column(name = "log_on")
	private String onValue;

	@NotNull(message = "The Guid for the log cannot be empty!")
	@Size(max=100)
	@Column(name = "log_guid", unique=true)
	private String guid;

	@Column(name = "log_ad")
	private String ad;
	
	@Column(name = "log_transfer_incoming")
	private Boolean transferIncoming;

	@NotNull
	@Column(name = "log_senderreceiver")
	@Size(max=100)
	private String senderReceiver;

	@NotNull(message = "The dateReceived for the log cannot be empty!")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "log_daterecieved")
	private Date dateReceived;

	@NotNull(message = "The log_status field for the log cannot be empty!")
	@Enumerated(EnumType.STRING)
	@Column(name = "log_status")
	private ExchangeLogStatusTypeType status;

	@NotNull(message = "The log_updattim field for the log cannot be empty!")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "log_updattim")
	private Date updateTime;

	@NotNull(message = "The updatedBy field for the log cannot be empty!")
	@Size(max=100)
	@Column(name = "log_upuser")
	private String updatedBy;

	@OneToMany(mappedBy="log", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	private List<ExchangeLogStatus> statusHistory;

	@Size(max=100)
	@Column(name="log_recipient")
	private String recipient;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="log_fwddate")
	private Date fwdDate;

	@Size(max=100)
	@Column(name="log_fwdrule")
	private String fwdRule;

	@Size(max=100)
	@Column(name="log_source")
	private String source;

	@Size(max=100)
	@Column(name="log_destination")
	private String destination;

	@Size(max=36)
	@Column(name="log_mdc_request_id")
	private String mdcRequestId;

	@Column(name = "log_business_error")
	private String businessError;

	@PrePersist
	public void prepersist() {
		if(StringUtils.isEmpty(guid)){
			setGuid(UUID.randomUUID().toString());
		}
    }

}
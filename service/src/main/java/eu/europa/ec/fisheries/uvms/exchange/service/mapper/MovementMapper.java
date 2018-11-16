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
package eu.europa.ec.fisheries.uvms.exchange.service.mapper;

import java.util.List;
import eu.europa.ec.fisheries.schema.exchange.movement.asset.v1.AssetIdList;
import eu.europa.ec.fisheries.schema.exchange.movement.mobileterminal.v1.IdList;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementBaseType;
import eu.europa.ec.fisheries.uvms.exchange.service.model.IncomingMovement;

public class MovementMapper {

    private MovementMapper() {}
    
    public static IncomingMovement mapMovementBaseTypeToRawMovementType(MovementBaseType movementBaseType) {
        IncomingMovement incomingMovement = new IncomingMovement();
        if (movementBaseType.getAssetId() != null) {
            if (movementBaseType.getAssetId().getAssetType() != null) {
                incomingMovement.setAssetType(movementBaseType.getAssetId().getAssetType().value());
            }
            mapAssetIdList(movementBaseType.getAssetId().getAssetIdList(), incomingMovement);
        }
        if (movementBaseType.getMobileTerminalId() != null) {
            incomingMovement.setMobileTerminalConnectId(movementBaseType.getMobileTerminalId().getConnectId());
            incomingMovement.setMobileTerminalGuid(movementBaseType.getMobileTerminalId().getGuid());
            mapMobileTerminalIdList(movementBaseType.getMobileTerminalId().getMobileTerminalIdList(), incomingMovement);
        }
        if (movementBaseType.getComChannelType() != null) {
            incomingMovement.setComChannelType(movementBaseType.getComChannelType().value());
        }
        if (movementBaseType.getSource() != null) {
            incomingMovement.setMovementSourceType(movementBaseType.getSource().value());
        }
        incomingMovement.setLongitude(movementBaseType.getPosition().getLongitude());
        incomingMovement.setLatitude(movementBaseType.getPosition().getLatitude());
        incomingMovement.setAltitude(movementBaseType.getPosition().getAltitude());
        incomingMovement.setPositionTime(movementBaseType.getPositionTime().toInstant());
        incomingMovement.setStatus(movementBaseType.getStatus());
        incomingMovement.setReportedSpeed(movementBaseType.getReportedSpeed());
        incomingMovement.setReportedCourse(movementBaseType.getReportedCourse());
        if (movementBaseType.getMovementType() != null) {
            incomingMovement.setMovementType(movementBaseType.getMovementType().value());
        }
        if (movementBaseType.getActivity() != null) {
            incomingMovement.setActivityCallback(movementBaseType.getActivity().getCallback());
            incomingMovement.setActivityMessageId(movementBaseType.getActivity().getMessageId());
            if (movementBaseType.getActivity().getMessageType() != null) {
                incomingMovement.setActivityMessageType(movementBaseType.getActivity().getMessageType().value());
            }
        }
        incomingMovement.setAssetName(movementBaseType.getAssetName());
        incomingMovement.setFlagState(movementBaseType.getFlagState());
        incomingMovement.setExternalMarking(movementBaseType.getExternalMarking());
        incomingMovement.setTripNumber(movementBaseType.getTripNumber());
        incomingMovement.setInternalReferenceNumber(movementBaseType.getInternalReferenceNumber());
        return incomingMovement;
    }
    
    public static void mapAssetIdList(List<AssetIdList> inList, IncomingMovement incomingMovement) {
        for (AssetIdList inAssetId : inList) {
            switch (inAssetId.getIdType()) {
                case CFR:
                    incomingMovement.setAssetCFR(inAssetId.getValue());
                    break;
                case ID:
                    incomingMovement.setAssetID(inAssetId.getValue());
                    break;
                case IMO:
                    incomingMovement.setAssetIMO(inAssetId.getValue());
                    break;
                case IRCS:
                    incomingMovement.setAssetIRCS(inAssetId.getValue());
                    break;
                case MMSI:
                    incomingMovement.setAssetMMSI(inAssetId.getValue());
                    break;
                case GUID:
                    incomingMovement.setAssetGuid(inAssetId.getValue());
                    break;
            }
        }
    }

    public static void mapMobileTerminalIdList(List<IdList> inList, IncomingMovement incomingMovement) {
        for (IdList inId : inList) {
            switch (inId.getType()) {
                case DNID:
                    incomingMovement.setMobileTerminalDNID(inId.getValue());
                    break;
                case LES:
                    incomingMovement.setMobileTerminalLES(inId.getValue());
                    break;
                case MEMBER_NUMBER:
                    incomingMovement.setMobileTerminalMemberNumber(inId.getValue());
                    break;
                case SERIAL_NUMBER:
                    incomingMovement.setMobileTerminalSerialNumber(inId.getValue());
                    break;
            }
        }
    }
    
}
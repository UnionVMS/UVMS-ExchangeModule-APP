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

import java.util.ArrayList;
import java.util.List;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementBaseType;
import eu.europa.ec.fisheries.schema.movementrules.asset.v1.AssetId;
import eu.europa.ec.fisheries.schema.movementrules.asset.v1.AssetIdType;
import eu.europa.ec.fisheries.schema.movementrules.asset.v1.AssetType;
import eu.europa.ec.fisheries.schema.movementrules.mobileterminal.v1.IdType;
import eu.europa.ec.fisheries.schema.movementrules.mobileterminal.v1.MobileTerminalType;
import eu.europa.ec.fisheries.schema.movementrules.movement.v1.MovementActivityType;
import eu.europa.ec.fisheries.schema.movementrules.movement.v1.MovementActivityTypeType;
import eu.europa.ec.fisheries.schema.movementrules.movement.v1.MovementComChannelType;
import eu.europa.ec.fisheries.schema.movementrules.movement.v1.MovementPoint;
import eu.europa.ec.fisheries.schema.movementrules.movement.v1.MovementSourceType;
import eu.europa.ec.fisheries.schema.movementrules.movement.v1.MovementTypeType;
import eu.europa.ec.fisheries.schema.movementrules.movement.v1.RawMovementType;

public class MovementMapper {

    private MovementMapper() {}
    
    public static RawMovementType mapMovementBaseTypeToRawMovementType(MovementBaseType movementBaseType) {
        RawMovementType rawMovementType = new RawMovementType();
        if (movementBaseType.getAssetId() != null) {
            AssetId assetId = new AssetId();
            if (movementBaseType.getAssetId().getAssetType() != null) {
                assetId.setAssetType(AssetType.fromValue(movementBaseType.getAssetId().getAssetType().value()));
            }
            assetId.getAssetIdList().addAll(mapAssetIdList(movementBaseType.getAssetId().getAssetIdList()));
            rawMovementType.setAssetId(assetId);
        }
        if (movementBaseType.getMobileTerminalId() != null) {
            MobileTerminalType mobileTerminalType = new MobileTerminalType();
            mobileTerminalType.setConnectId(movementBaseType.getMobileTerminalId().getConnectId());
            mobileTerminalType.setGuid(movementBaseType.getMobileTerminalId().getGuid());
            mobileTerminalType.getMobileTerminalIdList().addAll(mapMobileTerminalIdList(movementBaseType.getMobileTerminalId().getMobileTerminalIdList()));
            rawMovementType.setMobileTerminal(mobileTerminalType);
        }
        if (movementBaseType.getComChannelType() != null) {
            rawMovementType.setComChannelType(MovementComChannelType.fromValue(movementBaseType.getComChannelType().value()));
        }
        if (movementBaseType.getSource() != null) {
            rawMovementType.setSource(MovementSourceType.fromValue(movementBaseType.getSource().value()));
        }
        MovementPoint movementPoint = new MovementPoint();
        movementPoint.setLongitude(movementBaseType.getPosition().getLongitude());
        movementPoint.setLatitude(movementBaseType.getPosition().getLatitude());
        movementPoint.setAltitude(movementBaseType.getPosition().getAltitude());
        rawMovementType.setPosition(movementPoint);
        rawMovementType.setPositionTime(movementBaseType.getPositionTime());
        rawMovementType.setStatus(movementBaseType.getStatus());
        rawMovementType.setReportedSpeed(movementBaseType.getReportedSpeed());
        rawMovementType.setReportedCourse(movementBaseType.getReportedCourse());
        if (movementBaseType.getMovementType() != null) {
            rawMovementType.setMovementType(MovementTypeType.fromValue(movementBaseType.getMovementType().value()));
        }
        if (movementBaseType.getActivity() != null) {
            MovementActivityType movementActivityType = new MovementActivityType();
            movementActivityType.setCallback(movementBaseType.getActivity().getCallback());
            movementActivityType.setMessageId(movementBaseType.getActivity().getMessageId());
            if (movementBaseType.getActivity().getMessageType() != null) {
                movementActivityType.setMessageType(MovementActivityTypeType.fromValue(movementBaseType.getActivity().getMessageType().value()));
            }
            rawMovementType.setActivity(movementActivityType);
        }
        rawMovementType.setAssetName(movementBaseType.getAssetName());
        rawMovementType.setFlagState(movementBaseType.getFlagState());
        rawMovementType.setExternalMarking(movementBaseType.getExternalMarking());
        rawMovementType.setTripNumber(movementBaseType.getTripNumber());
        rawMovementType.setInternalReferenceNumber(movementBaseType.getInternalReferenceNumber());
        return rawMovementType;
    }
    
    public static List<eu.europa.ec.fisheries.schema.movementrules.asset.v1.AssetIdList> mapAssetIdList(
            List<eu.europa.ec.fisheries.schema.exchange.movement.asset.v1.AssetIdList> inList) {
        List<eu.europa.ec.fisheries.schema.movementrules.asset.v1.AssetIdList> outList = new ArrayList<>();
        for (eu.europa.ec.fisheries.schema.exchange.movement.asset.v1.AssetIdList inAssetId : inList) {
            eu.europa.ec.fisheries.schema.movementrules.asset.v1.AssetIdList outAssetId = new eu.europa.ec.fisheries.schema.movementrules.asset.v1.AssetIdList();
            AssetIdType idType = null;
            switch (inAssetId.getIdType()) {
                case CFR:
                    idType = AssetIdType.CFR;
                    break;
                case ID:
                    idType = AssetIdType.ID;
                    break;
                case IMO:
                    idType = AssetIdType.IMO;
                    break;
                case IRCS:
                    idType = AssetIdType.IRCS;
                    break;
                case MMSI:
                    idType = AssetIdType.MMSI;
                    break;
                case GUID:
                    idType = AssetIdType.GUID;
                    break;
            }
            outAssetId.setIdType(idType);
            outAssetId.setValue(inAssetId.getValue());
            outList.add(outAssetId);
        }
        return outList;
    }

    public static List<eu.europa.ec.fisheries.schema.movementrules.mobileterminal.v1.IdList> mapMobileTerminalIdList(
            List<eu.europa.ec.fisheries.schema.exchange.movement.mobileterminal.v1.IdList> inList) {
        List<eu.europa.ec.fisheries.schema.movementrules.mobileterminal.v1.IdList> outList = new ArrayList<>();
        for (eu.europa.ec.fisheries.schema.exchange.movement.mobileterminal.v1.IdList inId : inList) {
            eu.europa.ec.fisheries.schema.movementrules.mobileterminal.v1.IdList outId = new eu.europa.ec.fisheries.schema.movementrules.mobileterminal.v1.IdList();
            IdType idType = null;

            switch (inId.getType()) {
                case DNID:
                    idType = IdType.DNID;
                    break;
                case LES:
                    idType = IdType.LES;
                    break;
                case MEMBER_NUMBER:
                    idType = IdType.MEMBER_NUMBER;
                    break;
                case SERIAL_NUMBER:
                    idType = IdType.SERIAL_NUMBER;
                    break;
            }
            outId.setType(idType);
            outId.setValue(inId.getValue());
            outList.add(outId);
        }
        return outList;
    }
    
}
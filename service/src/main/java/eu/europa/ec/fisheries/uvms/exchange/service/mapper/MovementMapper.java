package eu.europa.ec.fisheries.uvms.exchange.service.mapper;

import java.util.ArrayList;
import java.util.List;

import org.dozer.DozerBeanMapper;

import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.rules.asset.v1.AssetIdType;
import eu.europa.ec.fisheries.schema.rules.mobileterminal.v1.IdType;

public class MovementMapper {

    private static final DozerBeanMapper mapper = new DozerBeanMapper();
    private static final MovementMapper INSTANCE = new MovementMapper();

    private MovementMapper() {
        mapper.setMappingFiles(getMapperFiles());
    }

    public static MovementMapper getInstance() {
        return INSTANCE;
    }

    private List<String> getMapperFiles() {
        List<String> files = new ArrayList<>();
        files.add("movementbasetype.xml");
        return files;
    }

    public DozerBeanMapper getMapper() {
        return mapper;
    }

    public static List<eu.europa.ec.fisheries.schema.rules.asset.v1.AssetIdList> mapAssetIdList(
            List<eu.europa.ec.fisheries.schema.exchange.movement.asset.v1.AssetIdList> inList) {
        List<eu.europa.ec.fisheries.schema.rules.asset.v1.AssetIdList> outList = new ArrayList<>();
        for (eu.europa.ec.fisheries.schema.exchange.movement.asset.v1.AssetIdList inAssetId : inList) {
            eu.europa.ec.fisheries.schema.rules.asset.v1.AssetIdList outAssetId = new eu.europa.ec.fisheries.schema.rules.asset.v1.AssetIdList();
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

    public static List<eu.europa.ec.fisheries.schema.rules.mobileterminal.v1.IdList> mapMobileTerminalIdList(
            List<eu.europa.ec.fisheries.schema.exchange.movement.mobileterminal.v1.IdList> inList) {
        List<eu.europa.ec.fisheries.schema.rules.mobileterminal.v1.IdList> outList = new ArrayList<>();
        for (eu.europa.ec.fisheries.schema.exchange.movement.mobileterminal.v1.IdList inId : inList) {
            eu.europa.ec.fisheries.schema.rules.mobileterminal.v1.IdList outId = new eu.europa.ec.fisheries.schema.rules.mobileterminal.v1.IdList();
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
                case SATELLITE_NUMBER:
                    idType = IdType.SATELLITE_NUMBER;
                    break;
            }
            outId.setType(idType);
            outId.setValue(inId.getValue());
            outList.add(outId);
        }
        return outList;
    }

    public static eu.europa.ec.fisheries.schema.rules.exchange.v1.PluginType mapPluginType(PluginType pluginType) {
        switch (pluginType) {
            case EMAIL:
                return eu.europa.ec.fisheries.schema.rules.exchange.v1.PluginType.EMAIL;
            case FLUX:
                return eu.europa.ec.fisheries.schema.rules.exchange.v1.PluginType.FLUX;
            case SATELLITE_RECEIVER:
                return eu.europa.ec.fisheries.schema.rules.exchange.v1.PluginType.SATELLITE_RECEIVER;
            case OTHER:
            default:
                return eu.europa.ec.fisheries.schema.rules.exchange.v1.PluginType.OTHER;
        }
    }
}

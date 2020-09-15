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
package eu.europa.ec.fisheries.uvms.exchange.service.bean;

import javax.ejb.Stateless;
import javax.inject.Inject;
import eu.europa.ec.fisheries.uvms.asset.client.AssetClient;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetIdentifier;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;

@Stateless
public class ExchangeAssetServiceBean {

	private static final Logger LOG = LoggerFactory.getLogger(ExchangeAssetServiceBean.class);

	@Inject
	private AssetClient assetClient;

	public Asset getAsset(String assetGuid) {
		try {

			LOG.trace("Asking asset for asset with guid " + assetGuid);
			AssetDTO assetDTO = assetClient.getAssetById(AssetIdentifier.GUID, assetGuid);
			return mapToAssetFromAssetDTO(assetDTO);

		}catch (Exception e){
			throw new RuntimeException(e);		//convert various asset exceptions to runtime
		}
	}

	public Asset mapToAssetFromAssetDTO(AssetDTO dto){
		Asset asset = new Asset();
		AssetId assetId = new AssetId();
		assetId.setGuid(dto.getId().toString());
		assetId.setType(AssetIdType.GUID);
		assetId.setValue(dto.getId().toString());
		asset.setAssetId(assetId);

		asset.setName(dto.getName());
		//Set more stuff in this thing when they are needed

		return asset;
	}

}
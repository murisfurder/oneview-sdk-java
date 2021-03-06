/*
 * (C) Copyright 2015-2016 Hewlett Packard Enterprise Development LP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hp.ov.sdk.rest.client.storage;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.hp.ov.sdk.OneViewClientSample;
import com.hp.ov.sdk.constants.ResourceCategory;
import com.hp.ov.sdk.dto.ResourceCollection;
import com.hp.ov.sdk.dto.storage.ConnectableStorageVolumeTemplate;
import com.hp.ov.sdk.dto.storage.StoragePool;
import com.hp.ov.sdk.dto.storage.StorageVolumeTemplate;
import com.hp.ov.sdk.dto.storage.TemplateProvisioningData;
import com.hp.ov.sdk.rest.client.OneViewClient;

/*
 * StorageVolumeTemplateClientSample is a sample program consumes the template that is defined over the storage pool
 * in HPE OneView. It invokes APIs of StorageVolumeTemplateClient which is in sdk library to perform GET/PUT/POST/DELETE
 * operations on storage volume template resource
 */
public class StorageVolumeTemplateClientSample {

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageVolumeTemplateClientSample.class);

    // These are variables to be defined by user
    // ================================
    private static final String STORAGE_VOLUME_TEMPLATE_NAME = "Storage_Volume_Template_Sample";
    private static final String STORAGE_VOLUME_TEMPLATE_NAME_UPDATED = STORAGE_VOLUME_TEMPLATE_NAME + "_Updated";
    private static final String STORAGE_VOLUME_TEMPLATE_CAPACITY = "235834383322";
    private static final String STORAGE_VOLUME_TEMPLATE_PROVISION_TYPE = "Thin";
    // ================================

    private final StorageVolumeTemplateClient storageVolumeTemplateClient;
    private final StoragePoolClient storagePoolClient;
    private final StorageSystemClient storageSystemClient;

    public StorageVolumeTemplateClientSample() {
        OneViewClient oneViewClient = new OneViewClientSample().getOneViewClient();

        this.storageVolumeTemplateClient = oneViewClient.storageVolumeTemplate();
        this.storagePoolClient = oneViewClient.storagePool();
        this.storageSystemClient = oneViewClient.storageSystem();
    }

    private void getStorageVolumeTemplateById() {
        StorageVolumeTemplate template = this.storageVolumeTemplateClient.getByName(STORAGE_VOLUME_TEMPLATE_NAME).get(0);

        template = this.storageVolumeTemplateClient.getById(template.getResourceId());

        LOGGER.info("StorageVolumeTemplate object returned to client : " + template.toJsonString());
    }

    private void getAllStorageVolumeTemplates() {
        ResourceCollection<StorageVolumeTemplate> storageVolumeTemplates = this.storageVolumeTemplateClient.getAll();

        LOGGER.info("StorageVolumeTemplates returned to client : " + storageVolumeTemplates.toJsonString());
    }

    private void getStorageVolumeTemplateByName() {
        StorageVolumeTemplate storageVolumeTemplate = this.storageVolumeTemplateClient.getByName(
                STORAGE_VOLUME_TEMPLATE_NAME).get(0);

        LOGGER.info("StorageVolumeTemplate object returned to client : " + storageVolumeTemplate.toJsonString());
    }

    private void createStorageVolumeTemplate() {
        StorageVolumeTemplate storageVolumeTemplate = buildStorageVolumeTemplate();

        StorageVolumeTemplate created = storageVolumeTemplateClient.create(storageVolumeTemplate);

        LOGGER.info("StorageVolumeTemplate object returned to client : " + created.toJsonString());
    }

    private void updateStorageVolumeTemplate() {
        StorageVolumeTemplate storageVolumeTemplate = this.storageVolumeTemplateClient.getByName(
                STORAGE_VOLUME_TEMPLATE_NAME).get(0);

        storageVolumeTemplate.setType(ResourceCategory.RC_STORAGE_VOLUME_TEMPLATE);
        storageVolumeTemplate.setType(ResourceCategory.RC_STORAGE_VOLUME_TEMPLATE_V200);
        storageVolumeTemplate.setName(STORAGE_VOLUME_TEMPLATE_NAME_UPDATED);

        StorageVolumeTemplate updated = storageVolumeTemplateClient.update(storageVolumeTemplate.getResourceId(),
                storageVolumeTemplate);

        LOGGER.info("StorageVolumeTemplate object returned to client : " + updated.toJsonString());
    }

    private void deleteStorageVolumeTemplate() {
        StorageVolumeTemplate storageVolumeTemplate = this.storageVolumeTemplateClient.getByName(
                STORAGE_VOLUME_TEMPLATE_NAME_UPDATED).get(0);

        String response = this.storageVolumeTemplateClient.delete(storageVolumeTemplate.getResourceId());

        LOGGER.info("Response returned to client : " + response);
    }

    private void getConnectableStorageVolumeTemplates() {
        ResourceCollection<ConnectableStorageVolumeTemplate> connectableVolumeTemplates
                = this.storageVolumeTemplateClient.getConnectableVolumeTemplates();

        LOGGER.info("ConnectableStorageVolumeTemplate returned to client : " + connectableVolumeTemplates.toJsonString());
    }

    private StorageVolumeTemplate buildStorageVolumeTemplate() {
        String storageSystemUri = storageSystemClient.getByName(StorageSystemClientSample.STORAGE_SYSTEM_NAME).get(0).getUri();

        ResourceCollection<StoragePool> storagePools = storagePoolClient.getByName(
                StoragePoolClientSample.STORAGE_POOL_NAME);

        List<StoragePool> filteredPools = this.getFilteredPoolsByStoragesystemUri(storagePools, storageSystemUri);

        String storagePoolUri = filteredPools.get(0).getUri();

        StorageVolumeTemplate storageVolumeTemplate = new StorageVolumeTemplate();

        storageVolumeTemplate.setName(STORAGE_VOLUME_TEMPLATE_NAME);
        storageVolumeTemplate.setState("Normal");
        storageVolumeTemplate.setDescription("Example Template");
        storageVolumeTemplate.setStateReason("None");
        storageVolumeTemplate.setStorageSystemUri(storageSystemUri);
        storageVolumeTemplate.setSnapshotPoolUri(storagePoolUri); //v200

        TemplateProvisioningData provisioning = new TemplateProvisioningData();

        provisioning.setProvisionType(STORAGE_VOLUME_TEMPLATE_PROVISION_TYPE);
        provisioning.setShareable(true);
        provisioning.setCapacity(STORAGE_VOLUME_TEMPLATE_CAPACITY);
        provisioning.setStoragePoolUri(storagePoolUri);
        storageVolumeTemplate.setProvisioning(provisioning);

        storageVolumeTemplate.setType(ResourceCategory.RC_STORAGE_VOLUME_TEMPLATE); //v120
        storageVolumeTemplate.setType(ResourceCategory.RC_STORAGE_VOLUME_TEMPLATE_V200); //v200

        return storageVolumeTemplate;
    }

    private List<StoragePool> getFilteredPoolsByStoragesystemUri(ResourceCollection<StoragePool> storagePools, final String storageSystemUri) {

        List<StoragePool> filteredPools = storagePools.getMembers(new Predicate<StoragePool>() {

            @Override
            public boolean apply(StoragePool input) {
                return (storageSystemUri.equalsIgnoreCase(input.getStorageSystemUri()));
            }
        });

        return filteredPools;
    }

    public static void main(final String[] args) throws Exception {
        StorageVolumeTemplateClientSample client = new StorageVolumeTemplateClientSample();

        client.createStorageVolumeTemplate();

        client.getStorageVolumeTemplateById();
        client.getAllStorageVolumeTemplates();
        client.getStorageVolumeTemplateByName();
        client.getConnectableStorageVolumeTemplates();
        client.updateStorageVolumeTemplate();
        client.deleteStorageVolumeTemplate();
    }

}

/*
 * (C) Copyright 2016 Hewlett Packard Enterprise Development LP
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
package com.hpe.i3s.dto.deployment.artifactsbundle;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class BuildPlanArtifact implements Serializable {

    private static final long serialVersionUID = 1L;

    private String bpID;
    private String buildPlanName;
    private String description;
    private String planScriptName;
    private boolean readOnly;

    /**
     * @return the bpID
     */
    public String getBpID() {
        return bpID;
    }

    /**
     * @param bpID the bpID to set
     */
    public void setBpID(String bpID) {
        this.bpID = bpID;
    }

    /**
     * @return the buildPlanName
     */
    public String getBuildPlanName() {
        return buildPlanName;
    }

    /**
     * @param buildPlanName the buildPlanName to set
     */
    public void setBuildPlanName(String buildPlanName) {
        this.buildPlanName = buildPlanName;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the planScriptName
     */
    public String getPlanScriptName() {
        return planScriptName;
    }

    /**
     * @param planScriptName the planScriptName to set
     */
    public void setPlanScriptName(String planScriptName) {
        this.planScriptName = planScriptName;
    }

    /**
     * @return the readOnly
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * @param readOnly the readOnly to set
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}

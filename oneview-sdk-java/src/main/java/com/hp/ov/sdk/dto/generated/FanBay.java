/*******************************************************************************
 * // (C) Copyright 2014-2015 Hewlett-Packard Development Company, L.P.
 *******************************************************************************/
package com.hp.ov.sdk.dto.generated;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
        "bayNumber", "devicePresence", "deviceRequired", "status",
        "state", "model", "partNumber", "sparePartNumber"
})
public class FanBay implements Serializable
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    @JsonProperty("bayNumber")
    private Integer bayNumber;
    @JsonProperty("devicePresence")
    private FanBay.DevicePresence devicePresence;
    @JsonProperty("deviceRequired")
    private Boolean deviceRequired;
    @JsonProperty("status")
    private FanBay.Status status;
    @JsonProperty("state")
    private FanBay.State state;
    @JsonProperty("model")
    private String model;
    @JsonProperty("partNumber")
    private String partNumber;
    @JsonProperty("sparePartNumber")
    private String sparePartNumber;

    /**
     * 
     * @return The bayNumber
     */
    @JsonProperty("bayNumber")
    public Integer getBayNumber()
    {
        return bayNumber;
    }

    /**
     * 
     * @param bayNumber
     *        The bayNumber
     */
    @JsonProperty("bayNumber")
    public void setBayNumber(final Integer bayNumber)
    {
        this.bayNumber = bayNumber;
    }

    /**
     * 
     * @return The devicePresence
     */
    @JsonProperty("devicePresence")
    public FanBay.DevicePresence getDevicePresence()
    {
        return devicePresence;
    }

    /**
     * 
     * @param devicePresence
     *        The devicePresence
     */
    @JsonProperty("devicePresence")
    public void setDevicePresence(final FanBay.DevicePresence devicePresence)
    {
        this.devicePresence = devicePresence;
    }

    /**
     * 
     * @return The deviceRequired
     */
    @JsonProperty("deviceRequired")
    public Boolean getDeviceRequired()
    {
        return deviceRequired;
    }

    /**
     * 
     * @param deviceRequired
     *        The deviceRequired
     */
    @JsonProperty("deviceRequired")
    public void setDeviceRequired(final Boolean deviceRequired)
    {
        this.deviceRequired = deviceRequired;
    }

    /**
     * 
     * @return The status
     */
    @JsonProperty("status")
    public FanBay.Status getStatus()
    {
        return status;
    }

    /**
     * 
     * @param status
     *        The status
     */
    @JsonProperty("status")
    public void setStatus(final FanBay.Status status)
    {
        this.status = status;
    }

    /**
     * 
     * @return The state
     */
    @JsonProperty("state")
    public FanBay.State getState()
    {
        return state;
    }

    /**
     * 
     * @param state
     *        The state
     */
    @JsonProperty("state")
    public void setState(final FanBay.State state)
    {
        this.state = state;
    }

    /**
     * 
     * @return The model
     */
    @JsonProperty("model")
    public String getModel()
    {
        return model;
    }

    /**
     * 
     * @param model
     *        The model
     */
    @JsonProperty("model")
    public void setModel(final String model)
    {
        this.model = model;
    }

    /**
     * 
     * @return The partNumber
     */
    @JsonProperty("partNumber")
    public String getPartNumber()
    {
        return partNumber;
    }

    /**
     * 
     * @param partNumber
     *        The partNumber
     */
    @JsonProperty("partNumber")
    public void setPartNumber(final String partNumber)
    {
        this.partNumber = partNumber;
    }

    /**
     * 
     * @return The sparePartNumber
     */
    @JsonProperty("sparePartNumber")
    public String getSparePartNumber()
    {
        return sparePartNumber;
    }

    /**
     * 
     * @param sparePartNumber
     *        The sparePartNumber
     */
    @JsonProperty("sparePartNumber")
    public void setSparePartNumber(final String sparePartNumber)
    {
        this.sparePartNumber = sparePartNumber;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(bayNumber).append(devicePresence)
                .append(deviceRequired).append(status).append(state)
                .append(model).append(partNumber).append(sparePartNumber)
                .toHashCode();
    }

    @Override
    public boolean equals(final Object other)
    {
        if (other == this)
        {
            return true;
        }
        if ((other instanceof FanBay) == false)
        {
            return false;
        }
        final FanBay rhs = ((FanBay) other);
        return new EqualsBuilder().append(bayNumber, rhs.bayNumber)
                .append(devicePresence, rhs.devicePresence)
                .append(deviceRequired, rhs.deviceRequired)
                .append(status, rhs.status).append(state, rhs.state)
                .append(model, rhs.model).append(partNumber, rhs.partNumber)
                .append(sparePartNumber, rhs.sparePartNumber).isEquals();
    }

    @Generated("org.jsonschema2pojo")
    public static enum DevicePresence
    {

        PresenceNoOp ("PresenceNoOp"),
        PresenceUnknown ("PresenceUnknown"),
        Absent (
                "Absent"),
        Present ("Present"),
        Subsumed ("Subsumed");
        private final String value;
        private static Map<String, FanBay.DevicePresence> constants = new HashMap<String, FanBay.DevicePresence>();

        static
        {
            for (final FanBay.DevicePresence c : values())
            {
                constants.put(c.value, c);
            }
        }

        private DevicePresence(final String value)
        {
            this.value = value;
        }

        @JsonValue
        @Override
        public String toString()
        {
            return this.value;
        }

        @JsonCreator
        public static FanBay.DevicePresence fromValue(final String value)
        {
            final FanBay.DevicePresence constant = constants.get(value);
            if (constant == null)
            {
                throw new IllegalArgumentException(value);
            }
            else
            {
                return constant;
            }
        }

    }

    @Generated("org.jsonschema2pojo")
    public static enum State
    {

        Misplaced ("Misplaced"),
        Missing ("Missing"),
        Degraded ("Degraded"),
        Failed (
                "Failed"),
        OK ("OK");
        private final String value;
        private static Map<String, FanBay.State> constants = new HashMap<String, FanBay.State>();

        static
        {
            for (final FanBay.State c : values())
            {
                constants.put(c.value, c);
            }
        }

        private State(final String value)
        {
            this.value = value;
        }

        @JsonValue
        @Override
        public String toString()
        {
            return this.value;
        }

        @JsonCreator
        public static FanBay.State fromValue(final String value)
        {
            final FanBay.State constant = constants.get(value);
            if (constant == null)
            {
                throw new IllegalArgumentException(value);
            }
            else
            {
                return constant;
            }
        }

    }

    @Generated("org.jsonschema2pojo")
    public static enum Status
    {

        Unknown ("Unknown"),
        OK ("OK"),
        Disabled ("Disabled"),
        Warning ("Warning"),
        Critical (
                "Critical");
        private final String value;
        private static Map<String, FanBay.Status> constants = new HashMap<String, FanBay.Status>();

        static
        {
            for (final FanBay.Status c : values())
            {
                constants.put(c.value, c);
            }
        }

        private Status(final String value)
        {
            this.value = value;
        }

        @JsonValue
        @Override
        public String toString()
        {
            return this.value;
        }

        @JsonCreator
        public static FanBay.Status fromValue(final String value)
        {
            final FanBay.Status constant = constants.get(value);
            if (constant == null)
            {
                throw new IllegalArgumentException(value);
            }
            else
            {
                return constant;
            }
        }

    }

}

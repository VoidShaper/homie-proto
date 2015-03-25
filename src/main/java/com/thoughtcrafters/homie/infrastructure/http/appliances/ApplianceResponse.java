package com.thoughtcrafters.homie.infrastructure.http.appliances;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.thoughtcrafters.homie.application.FullAppliance;
import com.thoughtcrafters.homie.domain.appliances.ApplianceState;
import com.thoughtcrafters.homie.domain.appliances.ApplianceType;
import com.thoughtcrafters.homie.domain.appliances.properties.ApplianceProperty;
import com.thoughtcrafters.homie.domain.appliances.properties.EnumProperty;
import com.thoughtcrafters.homie.domain.appliances.properties.IntegerProperty;
import com.thoughtcrafters.homie.domain.appliances.properties.PropertyType;
import org.apache.commons.lang.NotImplementedException;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplianceResponse {

    @JsonProperty
    @NotBlank
    private final String self;

    @JsonProperty
    @NotBlank
    private final String id;

    @JsonProperty
    @NotBlank
    private final String name;

    @JsonProperty
    @NotBlank
    private final ApplianceType type;

    @JsonProperty
    @NotBlank
    private final ApplianceState state;

    @JsonProperty
    @NotNull
    private final Map<String, AppliancePropertyBody> properties;

    @JsonProperty
    private final RoomWithAppliance room;

    private ApplianceResponse(String self,
                              String id,
                              String name,
                              ApplianceType type,
                              ApplianceState state,
                              Map<String, AppliancePropertyBody> properties,
                              RoomWithAppliance room) {
        this.self = self;
        this.id = id;
        this.name = name;
        this.type = type;
        this.state = state;
        this.properties = properties;
        this.room = room;
    }

    public static ApplianceResponse fromFullAppliance(FullAppliance fullAppliance) {

        Map<String, AppliancePropertyBody> properties =
                fullAppliance.appliance().properties().stream()
                             .collect(toMap(ApplianceProperty::name,
                                            AppliancePropertyBody::fromApplianceProperty));

        return new ApplianceResponse("/appliances/" + fullAppliance.appliance().id().uuid(),
                                     fullAppliance.appliance().id().uuid().toString(),
                                     fullAppliance.appliance().name(),
                                     fullAppliance.appliance().type(),
                                     fullAppliance.appliance().state(),
                                     properties,
                                     RoomWithAppliance.roomFromFullAppliance(fullAppliance));
    }

    public static class EnumPropertyBody extends AppliancePropertyBody<Enum> {
        @JsonProperty
        private List<Enum> availableValues;

        private EnumPropertyBody(Enum value,
                                 String description,
                                 PropertyType type,
                                 boolean editable,
                                 List<Enum> availableValues) {
            super(value, description, type, editable);
            this.availableValues = availableValues;
        }
    }

    public static class IntegerPropertyBody extends AppliancePropertyBody<Integer> {
        @JsonProperty
        private Integer max;

        @JsonProperty
        private Integer min;

        private IntegerPropertyBody(Integer value,
                                    String description,
                                    PropertyType type,
                                    boolean editable,
                                    Integer min,
                                    Integer max) {
            super(value, description, type, editable);
            this.min = min;
            this.max = max;
        }
    }

    public static class AppliancePropertyBody<T> {
        @JsonProperty
        @NotNull
        private T value;

        @JsonProperty
        private String description;

        @JsonProperty
        @NotNull
        private PropertyType type;

        @JsonProperty
        private boolean editable;

        private AppliancePropertyBody(T value, String description, PropertyType type, boolean editable) {
            this.value = value;
            this.description = description;
            this.type = type;
            this.editable = editable;
        }

        private static AppliancePropertyBody fromApplianceProperty(ApplianceProperty applianceProperty) {
            switch (applianceProperty.type()) {
                case INTEGER:
                    IntegerProperty integerProperty = (IntegerProperty) applianceProperty;
                    return new IntegerPropertyBody(integerProperty.value(),
                                                   integerProperty.description(),
                                                   integerProperty.type(),
                                                   integerProperty.editable(),
                                                   integerProperty.min(),
                                                   integerProperty.max());
                case ENUM:
                    EnumProperty enumProperty = (EnumProperty) applianceProperty;
                    return new EnumPropertyBody(enumProperty.value(),
                                                enumProperty.description(),
                                                enumProperty.type(),
                                                enumProperty.editable(),
                                                enumProperty.availableValues());
                default:
                    throw new NotImplementedException("Cannot yet serialize appliance property of type " + applianceProperty.type());
            }

        }
    }

    public static class RoomWithAppliance {
        @JsonProperty
        @NotBlank
        private String id;

        @JsonProperty
        @NotBlank
        private String self;

        @JsonProperty
        @NotBlank
        private String name;

        private RoomWithAppliance(String id, String self, String name) {
            this.id = id;
            this.self = self;
            this.name = name;
        }

        private static RoomWithAppliance roomFromFullAppliance(FullAppliance fullAppliance) {
            return fullAppliance.appliance().roomId().isPresent() ?
                    new RoomWithAppliance(fullAppliance.appliance().roomId().get().uuid().toString(),
                                          "/rooms/" + fullAppliance.appliance().roomId().get().uuid(),
                                          fullAppliance.roomName().orElse(null))
                    : null;
        }
    }
}

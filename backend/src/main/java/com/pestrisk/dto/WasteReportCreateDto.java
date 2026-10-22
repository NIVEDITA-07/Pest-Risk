package com.pestrisk.dto;

import com.pestrisk.enums.WasteQuantity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class WasteReportCreateDto extends RiskAssessmentRequestDto {

    @NotBlank(message = "Ward/area is required")
    @Size(max = 100, message = "Ward name cannot exceed 100 characters")
    private String ward;

    @NotBlank(message = "Location description is required")
    @Size(max = 1000, message = "Location description cannot exceed 1000 characters")
    private String locationDescription;

    public WasteReportCreateDto() {
        super();
    }

    public WasteReportCreateDto(
            String ward,
            String locationDescription,
            WasteQuantity wasteQuantity,
            Integer wasteDurationDays,
            Boolean stagnantWater,
            Boolean mosquitoSightings,
            Boolean rodentSightings,
            Boolean strayAnimalSightings,
            Boolean unsegregatedWaste) {
        super(wasteQuantity, wasteDurationDays, stagnantWater, mosquitoSightings, rodentSightings, strayAnimalSightings, unsegregatedWaste);
        this.ward = ward;
        this.locationDescription = locationDescription;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }
}

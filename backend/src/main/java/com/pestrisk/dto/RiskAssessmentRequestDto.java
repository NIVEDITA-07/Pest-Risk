package com.pestrisk.dto;

import com.pestrisk.enums.WasteQuantity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class RiskAssessmentRequestDto {

    @NotNull(message = "Waste quantity is required (LOW, MEDIUM, HIGH)")
    private WasteQuantity wasteQuantity;

    @NotNull(message = "Waste duration in days is required")
    @Min(value = 0, message = "Waste duration cannot be negative")
    private Integer wasteDurationDays;

    @NotNull(message = "Stagnant water flag is required")
    private Boolean stagnantWater;

    @NotNull(message = "Mosquito sightings flag is required")
    private Boolean mosquitoSightings;

    @NotNull(message = "Rodent sightings flag is required")
    private Boolean rodentSightings;

    @NotNull(message = "Stray animal sightings flag is required")
    private Boolean strayAnimalSightings;

    @NotNull(message = "Unsegregated waste flag is required")
    private Boolean unsegregatedWaste;

    public RiskAssessmentRequestDto() {
    }

    public RiskAssessmentRequestDto(
            WasteQuantity wasteQuantity,
            Integer wasteDurationDays,
            Boolean stagnantWater,
            Boolean mosquitoSightings,
            Boolean rodentSightings,
            Boolean strayAnimalSightings,
            Boolean unsegregatedWaste) {
        this.wasteQuantity = wasteQuantity;
        this.wasteDurationDays = wasteDurationDays;
        this.stagnantWater = stagnantWater;
        this.mosquitoSightings = mosquitoSightings;
        this.rodentSightings = rodentSightings;
        this.strayAnimalSightings = strayAnimalSightings;
        this.unsegregatedWaste = unsegregatedWaste;
    }

    public WasteQuantity getWasteQuantity() {
        return wasteQuantity;
    }

    public void setWasteQuantity(WasteQuantity wasteQuantity) {
        this.wasteQuantity = wasteQuantity;
    }

    public Integer getWasteDurationDays() {
        return wasteDurationDays;
    }

    public void setWasteDurationDays(Integer wasteDurationDays) {
        this.wasteDurationDays = wasteDurationDays;
    }

    public Boolean getStagnantWater() {
        return stagnantWater;
    }

    public void setStagnantWater(Boolean stagnantWater) {
        this.stagnantWater = stagnantWater;
    }

    public Boolean getMosquitoSightings() {
        return mosquitoSightings;
    }

    public void setMosquitoSightings(Boolean mosquitoSightings) {
        this.mosquitoSightings = mosquitoSightings;
    }

    public Boolean getRodentSightings() {
        return rodentSightings;
    }

    public void setRodentSightings(Boolean rodentSightings) {
        this.rodentSightings = rodentSightings;
    }

    public Boolean getStrayAnimalSightings() {
        return strayAnimalSightings;
    }

    public void setStrayAnimalSightings(Boolean strayAnimalSightings) {
        this.strayAnimalSightings = strayAnimalSightings;
    }

    public Boolean getUnsegregatedWaste() {
        return unsegregatedWaste;
    }

    public void setUnsegregatedWaste(Boolean unsegregatedWaste) {
        this.unsegregatedWaste = unsegregatedWaste;
    }
}

package com.vietnam.pji.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicalHistoryRequestDTO {

    private String medicalHistory;

    private String process;

    private Boolean isAllergy;

    private String allergyNote;

    private Boolean isDrug;

    private String drugNote;

    private Boolean isAlcohol;

    private String alcoholNote;

    private Boolean isSmoking;

    private String smokingNote;

    private Boolean isOther;

    private String otherNote;
}

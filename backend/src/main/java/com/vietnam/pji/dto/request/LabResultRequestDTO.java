package com.vietnam.pji.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.Map;

import com.vietnam.pji.dto.response.Measurement;

@Getter
@Setter
public class LabResultRequestDTO {

    @NotNull(message = "episodeId must not be null")
    private Long episodeId;

    private Measurement esr;
    private Measurement wbcBlood;
    private Measurement neut;
    private Measurement mono;
    private Measurement rbc;
    private Measurement rdw;
    private Measurement ig;
    private Measurement leu;
    private Measurement mcv;
    private Measurement mch;
    private Measurement crp;
    private Measurement dimer;
    private Measurement alphaDefensin;
    private Measurement serumIl6;
    private Measurement synovialWbc;
    private Measurement synovialPmn;
    private Map<String, Object> biochemicalData;

}

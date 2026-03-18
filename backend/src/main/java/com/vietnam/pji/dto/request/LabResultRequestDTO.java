package com.vietnam.pji.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
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
    private Measurement lymph;
    private Measurement eos;
    private Measurement baso;
    private Measurement rbc;
    private Measurement hgb;
    private Measurement hct;
    private Measurement rdw;
    private Measurement ig;
    private Measurement mcv;
    private Measurement mch;
    private Measurement mchc;
    private Measurement crp;
    private Measurement synovialWbc;
    private Measurement synovialPmn;
    private Map<String, Object> biochemicalData;
}

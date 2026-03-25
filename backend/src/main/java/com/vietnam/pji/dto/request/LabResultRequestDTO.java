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
    // data for biochemicalData
    // {
    // "alb": {
    // "unit": "g/L",
    // "value": 38.5
    // },
    // "alt": {
    // "unit": "U/L",
    // "value": 25
    // },
    // "ast": {
    // "unit": "U/L",
    // "value": 30
    // },
    // "clo": {
    // "unit": "mmol/L",
    // "value": 102
    // },
    // "ure": {
    // "unit": "%",
    // "value": 6.5
    // },
    // "eGFR": {
    // "unit": "mL/min/1.73m2",
    // "value": 95
    // },
    // "kali": {
    // "unit": "mmol/L",
    // "value": 4.1
    // },
    // "hba1c": {
    // "unit": "%",
    // "value": 5.6
    // },
    // "natri": {
    // "unit": "mmol/L",
    // "value": 138
    // },
    // "albumin": {
    // "unit": "g/L",
    // "value": 41
    // },
    // "glucose": {
    // "unit": "mmol/L",
    // "value": 5.2
    // },
    // "creatinine": {
    // "unit": "µmol/L",
    // "value": 86
    // }
    // }
}

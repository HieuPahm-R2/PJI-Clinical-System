package com.vietnam.pji.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LabResultRequestDTO {

    @NotNull(message = "episodeId must not be null")
    private Long episodeId;

    private Integer esr;
    private BigDecimal wbcBlood;
    private BigDecimal neut;
    private BigDecimal mono;
    private BigDecimal lymph;
    private BigDecimal eos;
    private BigDecimal baso;
    private BigDecimal rbc;
    private Integer hgb;
    private BigDecimal hct;
    private BigDecimal rdw;
    private BigDecimal ig;
    private BigDecimal mcv;
    private BigDecimal mch;
    private Integer mchc;
    private BigDecimal crp;
    private Integer synovialWbc;
    private BigDecimal synovialPmn;
    private String biochemicalData;
}

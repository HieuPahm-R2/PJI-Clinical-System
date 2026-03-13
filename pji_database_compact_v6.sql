-- ==========================================
-- PJI DATABASE - COMPACT CDS SCHEMA V1
-- Purpose:
-- 1) Keep core clinical source-of-truth tables simple
-- 2) Replace fragmented AI recommendation tables with snapshot/run/item/review/version model
-- 3) Support unlimited AI runs per episode
-- 4) Support doctor review + final confirmed treatment plan
-- 5) Support timeline UI: recommendation history + AI vs confirmed plan
-- ==========================================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ==========================================
-- 1. ENUMS
-- ==========================================
DO $$ BEGIN
    CREATE TYPE gender_type AS ENUM ('MALE', 'FEMALE', 'OTHER');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
    CREATE TYPE sample_result_status AS ENUM ('NOT_PERFORMED', 'PENDING', 'NO_GROWTH', 'POSITIVE', 'CONTAMINATED', 'FINAL_NEGATIVE');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
    CREATE TYPE ai_run_trigger_type AS ENUM ('MANUAL_GENERATE', 'AUTO_REFRESH', 'DATA_CHANGED', 'DOCTOR_REQUEST', 'REVIEW_REQUEST');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
    CREATE TYPE ai_run_status AS ENUM ('SUCCESS', 'FAILED', 'PARTIAL','QUEUED', 'PROCESSING', 'TIMEOUT');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
    CREATE TYPE recommendation_item_category AS ENUM (
        'DIAGNOSTIC_TEST',
        'SYSTEMIC_ANTIBIOTIC',
        'LOCAL_ANTIBIOTIC',
        'SURGERY_PROCEDURE',
    );
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
    CREATE TYPE review_status_type AS ENUM ('ACCEPTED', 'MODIFIED', 'REJECTED', 'SAVED_DRAFT');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
    CREATE TYPE treatment_plan_status AS ENUM ('DRAFT', 'CONFIRMED', 'SUPERSEDED', 'CANCELLED');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

-- ==========================================
-- 2. USERS / RBAC (minimal)
-- ==========================================
CREATE TABLE IF NOT EXISTS permissions (
    id          BIGSERIAL PRIMARY KEY,
    api_path    VARCHAR(255) UNIQUE NOT NULL,
    method      VARCHAR(255),
    module      VARCHAR(255),
    name        VARCHAR(255),
    created_by  VARCHAR(255),
    created_at  TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_by  VARCHAR(255),
    updated_at  TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS role_permissions (
    role_id       BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id BIGINT NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);
CREATE TABLE IF NOT EXISTS roles (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(50) UNIQUE NOT NULL,
    description     TEXT,
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
     created_by          VARCHAR(255) NOT NULL,
     updated_by          VARCHAR(255) NOT NULL,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS users (
    id              BIGSERIAL PRIMARY KEY,
    role_id         BIGINT REFERENCES roles(id),
    email           VARCHAR(255) UNIQUE NOT NULL,
    password   VARCHAR(255) NOT NULL,
    fullname       VARCHAR(255) NOT NULL,
    phone           VARCHAR(20),
    department      VARCHAR(100),
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    last_login_at   TIMESTAMPTZ,
     created_by          VARCHAR(255),
     updated_by          VARCHAR(255),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- 3. CORE CLINICAL SOURCE OF TRUTH
-- ==========================================
CREATE TABLE IF NOT EXISTS patients (
    id                  BIGSERIAL PRIMARY KEY,
    patient_code        VARCHAR(50) UNIQUE,
    full_name           VARCHAR(150) NOT NULL,
    date_of_birth       DATE NOT NULL,
    gender              gender_type NOT NULL DEFAULT 'UNKNOWN',
    identity_card       VARCHAR(50),
	insurance_number VARCHAR(50),
	insurance_expired DATE,
    nationality      VARCHAR(50),
    ethnicity        VARCHAR(50),
    phone            VARCHAR(20),
    career           VARCHAR(50),
    subject          VARCHAR(50),
    address             TEXT,
    relative_info       JSONB,
    created_by          VARCHAR(255) NOT NULL,
    updated_by          VARCHAR(255) NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS pji_episodes (
    id                  BIGSERIAL PRIMARY KEY,
    patient_id          BIGINT NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    admission_date      DATE NOT NULL,
    discharge_date      DATE,
    department          VARCHAR(255),
    treatment_days      INT,
    direct              VARCHAR(50),
    reason              TEXT,
    referral_source     VARCHAR(255), -- nơi giới thiệu
    status              VARCHAR(100) NOT NULL,
    result              VARCHAR(100) NOT NULL,
    created_by          VARCHAR(255) NOT NULL,
    updated_by          VARCHAR(255) NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS medical_histories (
    episode_id      BIGINT PRIMARY KEY REFERENCES pji_episodes(id) ON DELETE CASCADE,
	medical_history TEXT,
    process         TEXT,
    antibiotic_history TEXT,
    is_allergy      BOOLEAN,
    allergy_note    VARCHAR,
    is_drug         BOOLEAN,
    drug_note       VARCHAR,
    is_alcohol      BOOLEAN,
    alcohol_note    VARCHAR,
    is_smoking      BOOLEAN,
    smoking_note    VARCHAR,
    is_other        BOOLEAN,
    other_note      VARCHAR,
    created_by      VARCHAR(255) NOT NULL,
    updated_by       VARCHAR(255) NOT NULL,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS clinical_records (
    id                          BIGSERIAL PRIMARY KEY,
    episode_id                  BIGINT NOT NULL REFERENCES pji_episodes(id) ON DELETE CASCADE,
    recorded_at                 TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    illness_onset_date          DATE,
    blood_pressure              VARCHAR(20),
    bmi                         NUMERIC(5,2),
	fever BOOLEAN,
    pain BOOLEAN,
    erythema BOOLEAN, -- có ban đỏ
    swelling BOOLEAN, -- sưng tấy
    sinus_tract BOOLEAN, -- có đường rò xoang
    suspected_infection_type infection_type, -- loại nhiễm trùng nghi ngờ
    hematogenous_suspected BOOLEAN, -- nghi ngờ lây truyền qua đường máu
    implant_stability implant_stability_type, -- Đánh giá độ ổn định của cấy ghép
    soft_tissue VARCHAR(100), -- tình trạng mô mềm
    pmma_allergy BOOLEAN,
    prosthesis_joint VARCHAR(50), -- khớp giả

    days_since_index_arthroplasty INT, -- số ngày kể từ lần phẫu thuật thay khớp ban đầu
    notations                   TEXT, -- Khám bệnh toàn thân
    created_by      VARCHAR(255) NOT NULL,
    updated_by       VARCHAR(255) NOT NULL,
    created_at                  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS surgeries (
    id                          BIGSERIAL PRIMARY KEY,
    episode_id                  BIGINT NOT NULL REFERENCES pji_episodes(id) ON DELETE CASCADE,
    surgery_date                DATE NOT NULL,
    surgery_type                VARCHAR(255) NOT NULL,
    findings                    TEXT,
    created_by                  BIGINT REFERENCES users(id),
    updated_by                  BIGINT REFERENCES users(id),
    created_at                  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS lab_results (
    id                          BIGSERIAL PRIMARY KEY,
    episode_id                  BIGINT NOT NULL REFERENCES pji_episodes(id) ON DELETE CASCADE,
    -- Chỉ số máu (viêm hệ thống)
    esr           INT,                       -- Tốc độ máu lắng (mm/h)
    wbc_blood     DECIMAL(10,2),             -- Bạch cầu máu
    neut          DECIMAL(5,2),
    mono          DECIMAL(4,1),              -- bạch cầu đơn nhân
    rbc           DECIMAL(5,2),              -- tổng số tế bào hồng cầu
    ig            DECIMAL(5,2),
    mcv           DECIMAL(5,2),
    mch           DECIMAL(5,2),
    dimer NUMERIC(10,2), -- một xét nghiệm máu đo lường các mảnh protein
	serum_il6 NUMERIC(10,2), -- một cytokine tiền viêm quan trọng
	alpha_defensin VARCHAR(50), -- alpha-defensin trong huyết thanh
    egfr INT, -- đánh giá chức năng thận
    -- Chỉ số dịch khớp (viêm tại chỗ)
    crp           DECIMAL(10,2),
    synovial_wbc  INT,
    synovial_pmn  DECIMAL(5,2),
    -- Các chỉ số sinh hóa (glucose, ure, creatinine, alt, ast, alb, natri, clo, kali, hba1c...)
    biochemical_data JSONB,
    created_by                  BIGINT REFERENCES users(id),
    updated_by                  BIGINT REFERENCES users(id),
    created_at                  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE image_results (
    id            BIGSERIAL PRIMARY KEY,
    episode_id    BIGINT REFERENCES pji_episodes(id) ON DELETE CASCADE,
    type          VARCHAR(50),               -- 'X-RAY', 'MRI', 'CT', 'ULTRASOUND'
    findings      TEXT,
    file_metadata JSONB,                     -- {'path': 's3://...',}
    -- Audit fields
    created_at    TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    created_by    BIGINT REFERENCES users(id),
    updated_by    BIGINT REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS culture_results (
    id                          BIGSERIAL PRIMARY KEY,
    episode_id                  BIGINT NOT NULL REFERENCES pji_episodes(id) ON DELETE CASCADE,
    sample_type                 VARCHAR(100), -- Dịch khớp, Mô quanh khớp giả, Bản thân khớp giả (Explant)
    incubation_days             INT,
    name               VARCHAR(255),
    result_status               sample_result_status NOT NULL DEFAULT 'PENDING',
    gram_type                   VARCHAR(20),
    notes                       TEXT,
    created_by                  BIGINT REFERENCES users(id),
    updated_by                  BIGINT REFERENCES users(id),
    created_at                  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sensitivity_results (
    id                          BIGSERIAL PRIMARY KEY,
    culture_id                  BIGINT NOT NULL REFERENCES culture_results(id) ON DELETE CASCADE,
    antibiotic_name             VARCHAR(100) NOT NULL,
    mic_value                   VARCHAR(20),
    sensitivity_code            VARCHAR(10),
    created_by                  BIGINT REFERENCES users(id),
    updated_by                  BIGINT REFERENCES users(id),
    created_at                  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- 4. AI / CDS LAYER (COMPACT)
-- ==========================================
-- Snapshot of normalized case input at the exact time AI runs.
CREATE TABLE IF NOT EXISTS case_clinical_snapshots (
    id                              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    episode_id                      BIGINT NOT NULL REFERENCES pji_episodes(id) ON DELETE CASCADE,
    snapshot_no                     INT NOT NULL, -- bệnh nhân có thể có nhiều lần AI chạy
    snapshot_data_json              JSONB NOT NULL, -- Toàn bộ dữ liệu lâm sàng lúc đó (xét nghiệm, triệu chứng...) lưu dạng JSON
    data_completeness_score         NUMERIC(5,2),
    created_by                      BIGINT REFERENCES users(id),
    created_at                      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (episode_id, snapshot_no)
);

-- One record = one complete AI execution.
CREATE TABLE IF NOT EXISTS ai_recommendation_runs (
    id                              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    episode_id                      BIGINT NOT NULL REFERENCES pji_episodes(id) ON DELETE CASCADE,
    snapshot_id                     UUID NOT NULL REFERENCES case_clinical_snapshots(id) ON DELETE CASCADE,
    run_no                          INT NOT NULL,
    trigger_type                    ai_run_trigger_type NOT NULL,
    status                          ai_run_status NOT NULL DEFAULT 'SUCCESS',
    model_name                      VARCHAR(100),
    model_version                   VARCHAR(100),
    assessment_json                 JSONB,  -- suspicion level, predicted diagnosis summary, triage level
    explanation_json                JSONB,  -- clinical reasons, missing information, rationale summary
    warnings_json                   JSONB,  -- safety warnings, red flags, renal adjustment warning...
    latency_ms                      INT,
    error_message                   TEXT,
    created_by                      BIGINT REFERENCES users(id),
    created_at                      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (episode_id, run_no)
);

-- Flexible recommendation blocks generated within one run.
CREATE TABLE IF NOT EXISTS ai_recommendation_items (
    id                              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    run_id                          UUID NOT NULL REFERENCES ai_recommendation_runs(id) ON DELETE CASCADE,
    category                        recommendation_item_category NOT NULL,
    title                           VARCHAR(255) NOT NULL,
    priority_order                  INT NOT NULL DEFAULT 1,
    is_primary                      BOOLEAN NOT NULL DEFAULT FALSE,
    item_json                       JSONB NOT NULL,
    created_at                      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- RAG / evidence references attached to run or specific item.
CREATE TABLE IF NOT EXISTS ai_rag_citations (
    id                              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    run_id                          UUID NOT NULL REFERENCES ai_recommendation_runs(id) ON DELETE CASCADE,
    item_id                         UUID REFERENCES ai_recommendation_items(id) ON DELETE CASCADE,
    source_type                     VARCHAR(50),
    source_title                    TEXT,
    source_uri                      TEXT,
    snippet                         TEXT,
    relevance_score                 NUMERIC(6,4),
    created_at                      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Doctor reviews a specific AI run.
CREATE TABLE IF NOT EXISTS doctor_recommendation_reviews (
    id                              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    episode_id                      BIGINT NOT NULL REFERENCES pji_episodes(id) ON DELETE CASCADE,
    run_id                          UUID NOT NULL REFERENCES ai_recommendation_runs(id) ON DELETE CASCADE,
    review_status                   review_status_type NOT NULL,
    review_note                     TEXT,
    modification_json               JSONB,
    rejection_reason           TEXT,
    reviewed_by                     BIGINT NOT NULL REFERENCES users(id),
    created_at                      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Final doctor-confirmed treatment plan, versioned.
CREATE TABLE IF NOT EXISTS treatment_plan_versions (
    id                              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    episode_id                      BIGINT NOT NULL REFERENCES pji_episodes(id) ON DELETE CASCADE,
    source_run_id                   UUID REFERENCES ai_recommendation_runs(id) ON DELETE SET NULL,
    source_review_id                UUID REFERENCES doctor_recommendation_reviews(id) ON DELETE SET NULL,
    version_no                      INT NOT NULL,
    is_current                      BOOLEAN NOT NULL DEFAULT TRUE,
    status                          treatment_plan_status NOT NULL DEFAULT 'CONFIRMED',
    regimen_json                    JSONB NOT NULL, -- Phác đồ điều trị cụ thể
    clinical_rationale              TEXT, -- giải thích tại sao chọn phác đồ nà
    confirmed_by                    BIGINT REFERENCES users(id),
    confirmed_at                    TIMESTAMPTZ,
    created_at                      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (episode_id, version_no)
);
-- ==========================================
-- 6. AI CHAT SYSTEM
-- ==========================================
CREATE TABLE ai_chat_sessions (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT REFERENCES users(id),
    episode_id  BIGINT REFERENCES pji_episodes(id),
    title       VARCHAR(255),
    is_archived BOOLEAN DEFAULT FALSE,
    created_at  TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ai_chat_messages (
    id              BIGSERIAL PRIMARY KEY,
    session_id      BIGINT REFERENCES ai_chat_sessions(id) ON DELETE CASCADE,
    role            VARCHAR(15),             -- 'user', 'assistant', 'system'
    content         TEXT NOT NULL,
    tokens_used     INT,
    latency_ms      INT,
    references_json JSONB,
    created_at      TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);


-- ==========================================
-- 5. INDEXES
-- ==========================================
CREATE INDEX IF NOT EXISTS idx_patients_full_name ON patients(full_name);
CREATE INDEX IF NOT EXISTS idx_patients_identity_card ON patients(identity_card);

CREATE INDEX IF NOT EXISTS idx_episodes_patient_id ON pji_episodes(patient_id);
CREATE INDEX IF NOT EXISTS idx_episodes_status ON pji_episodes(status);
CREATE INDEX IF NOT EXISTS idx_episodes_department ON pji_episodes(department);

CREATE INDEX IF NOT EXISTS idx_clinical_records_episode_time ON clinical_records(episode_id, recorded_at DESC);
CREATE INDEX IF NOT EXISTS idx_lab_results_episode_time ON lab_results(episode_id, collected_at DESC);
CREATE INDEX IF NOT EXISTS idx_culture_results_episode_time ON culture_results(episode_id, collected_at DESC);
CREATE INDEX IF NOT EXISTS idx_sensitivity_results_culture_id ON sensitivity_results(culture_id);

CREATE INDEX IF NOT EXISTS idx_snapshots_episode_id ON case_clinical_snapshots(episode_id);
CREATE INDEX IF NOT EXISTS idx_snapshots_input_hash ON case_clinical_snapshots(normalized_input_hash);
CREATE INDEX IF NOT EXISTS idx_snapshots_json_gin ON case_clinical_snapshots USING GIN(snapshot_data_json);

CREATE INDEX IF NOT EXISTS idx_ai_runs_episode_id ON ai_recommendation_runs(episode_id);
CREATE INDEX IF NOT EXISTS idx_ai_runs_snapshot_id ON ai_recommendation_runs(snapshot_id);
CREATE INDEX IF NOT EXISTS idx_ai_runs_created_at ON ai_recommendation_runs(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_ai_runs_assessment_gin ON ai_recommendation_runs USING GIN(assessment_json);

CREATE INDEX IF NOT EXISTS idx_ai_items_run_id ON ai_recommendation_items(run_id);
CREATE INDEX IF NOT EXISTS idx_ai_items_category ON ai_recommendation_items(category);
CREATE INDEX IF NOT EXISTS idx_ai_items_json_gin ON ai_recommendation_items USING GIN(item_json);

CREATE INDEX IF NOT EXISTS idx_ai_citations_run_id ON ai_rag_citations(run_id);
CREATE INDEX IF NOT EXISTS idx_ai_citations_item_id ON ai_rag_citations(item_id);

CREATE INDEX IF NOT EXISTS idx_reviews_episode_id ON doctor_recommendation_reviews(episode_id);
CREATE INDEX IF NOT EXISTS idx_reviews_run_id ON doctor_recommendation_reviews(run_id);
CREATE INDEX IF NOT EXISTS idx_reviews_status ON doctor_recommendation_reviews(review_status);

CREATE INDEX IF NOT EXISTS idx_plan_versions_episode_id ON treatment_plan_versions(episode_id);
CREATE INDEX IF NOT EXISTS idx_plan_versions_current ON treatment_plan_versions(episode_id, is_current);
CREATE UNIQUE INDEX IF NOT EXISTS uq_treatment_plan_current_per_episode
    ON treatment_plan_versions(episode_id)
    WHERE is_current = TRUE;

CREATE INDEX IF NOT EXISTS idx_compare_logs_episode_id ON recommendation_compare_logs(episode_id);
CREATE INDEX IF NOT EXISTS idx_compare_logs_runs ON recommendation_compare_logs(left_run_id, right_run_id);

-- ==========================================
-- 6. HELPFUL COMMENTS
-- ==========================================
COMMENT ON TABLE ai_recommendation_runs IS 'One row = one complete AI/CDS execution for an episode.';
COMMENT ON TABLE ai_recommendation_items IS 'Flexible recommendation blocks belonging to one AI run.';
COMMENT ON TABLE treatment_plan_versions IS 'Doctor-confirmed versioned treatment plans; only one current plan per episode.';
COMMENT ON TABLE case_clinical_snapshots IS 'Normalized input snapshot used by AI at a specific point in time.';

-- ==========================================
-- 7. WHAT TO DEPRECATE FROM OLD SCHEMA
-- ==========================================
-- Recommended to deprecate/remove gradually:
-- 1) ai_predictions
-- 2) ai_recommendations (old thin header version)
-- 3) rec_antibiolocal_details
-- 4) rec_antibiosystem_details
-- 5) rec_surgery_details
-- 6) rec_surgery_steps
-- 7) plan_antibiolocal_details
-- 8) plan_antibiosystem_details
-- 9) plan_surgery_details
-- 10) plan_surgery_steps
-- Replace all above with:
-- case_clinical_snapshots + ai_recommendation_runs + ai_recommendation_items
-- + doctor_recommendation_reviews + treatment_plan_versions


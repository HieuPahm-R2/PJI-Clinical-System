# PJI Clinical Decision Support System – Hướng dẫn cho AI Agent đọc hiểu luồng hoạt động hệ thống

## 1) Mục tiêu tài liệu
- hệ thống đang quản lý đối tượng gì,
- dữ liệu đi qua các phân hệ như thế nào,
- bảng nào là bảng gốc, bảng nào là bảng phát sinh,
- luồng tạo khuyến nghị của AI,
- luồng bác sĩ review và chốt phác đồ,
- cách agent nên đọc dữ liệu để trả lời đúng ngữ cảnh.

> Lưu ý: tài liệu này được suy luận từ database schema và danh sách API/permission hiện có, nên phản ánh **luồng nghiệp vụ ở mức dữ liệu và hệ thống**, không phải chi tiết implementation source code.

---

## 2) Bức tranh tổng thể hệ thống

Đây là hệ thống hỗ trợ chẩn đoán và gợi ý điều trị cho ca **PJI (Periprosthetic Joint Infection)**.

Luồng tổng thể:

1. Người dùng nội bộ đăng nhập hệ thống.
2. Tạo **bệnh nhân**.
3. Tạo một **đợt điều trị / episode** cho bệnh nhân.
4. Trong từng episode, nhập các nhóm dữ liệu lâm sàng:
   - tiền sử bệnh,
   - khám lâm sàng,
   - phẫu thuật trước đó,
   - xét nghiệm,
   - chẩn đoán hình ảnh,
   - cấy vi sinh,
   - kháng sinh đồ.
5. Hệ thống tổng hợp dữ liệu của episode thành một **clinical snapshot**.
6. Từ snapshot đó, AI tạo một **recommendation run**.
7. AI sinh ra:
   - đánh giá ca bệnh,
   - giải thích,
   - cảnh báo,
   - danh sách recommendation items,
   - citation / nguồn RAG.
8. Bác sĩ review kết quả AI:
   - chấp nhận,
   - sửa,
   - từ chối,
   - lưu bản nháp.
9. Sau review, hệ thống chốt thành **treatment plan version**.
10. Người dùng có thể tiếp tục chat với AI theo đúng episode hoặc theo đúng run AI trước đó.

---

## 3) Kiến trúc dữ liệu mức cao

Có thể chia schema thành 6 miền chính:

### A. Identity & Authorization
- `users`
- `roles`
- `permissions`
- `role_permissions`

### B. Hồ sơ bệnh nhân và episode
- `patients`
- `pji_episodes`

### C. Dữ liệu y khoa gốc trong mỗi episode
- `medical_histories`
- `clinical_records`
- `surgeries`
- `lab_results`
- `image_results`
- `culture_results`
- `sensitivity_results`

### D. Lớp snapshot trung gian cho AI
- `case_clinical_snapshots`

### E. Lớp AI recommendation
- `ai_recommendation_runs`
- `ai_recommendation_items`
- `ai_rag_citations`
- `doctor_recommendation_reviews`
- `treatment_plan_versions`

### F. Lớp hội thoại AI
- `ai_chat_sessions`
- `ai_chat_messages`

---

## 4) Định nghĩa thực thể cốt lõi

## 4.1 `patients` – hồ sơ bệnh nhân gốc

Đây là bảng gốc chứa thông tin hành chính của bệnh nhân.

Thông tin tiêu biểu:
- `patient_code`
- `full_name`
- `date_of_birth`
- `gender`
- `identity_card`
- `insurance_number`
- `address`
- `relative_info` (JSONB)

### Ý nghĩa nghiệp vụ
Một bệnh nhân có thể có nhiều lần vào viện hoặc nhiều đợt theo dõi PJI khác nhau.

### Quan hệ
- 1 `patients` -> N `pji_episodes`

---

## 4.2 `pji_episodes` – một ca/đợt điều trị PJI

Đây là **thực thể trung tâm** của toàn bộ hệ thống.

Thông tin tiêu biểu:
- `patient_id`
- `admission_date`
- `discharge_date`
- `department`
- `treatment_days`
- `direct`
- `reason`
- `referral_source`
- `status`
- `result`

### Ý nghĩa nghiệp vụ
Mọi dữ liệu y khoa và AI đều nên được hiểu **theo episode**, không phải chỉ theo patient.

### Quan hệ
- N `pji_episodes` -> 1 `patients`
- 1 `pji_episodes` -> 1 `medical_histories` (theo thiết kế hiện tại gần như 1-1)
- 1 `pji_episodes` -> N `clinical_records`
- 1 `pji_episodes` -> N `surgeries`
- 1 `pji_episodes` -> N `lab_results`
- 1 `pji_episodes` -> N `image_results`
- 1 `pji_episodes` -> N `culture_results`
- 1 `pji_episodes` -> N `case_clinical_snapshots`
- 1 `pji_episodes` -> N `ai_recommendation_runs`
- 1 `pji_episodes` -> N `doctor_recommendation_reviews`
- 1 `pji_episodes` -> N `treatment_plan_versions`
- 1 `pji_episodes` -> N `ai_chat_sessions`

> Khi agent cần trả lời về “ca bệnh hiện tại”, đơn vị chuẩn là `episode_id`.

---

## 5) Nhóm dữ liệu y khoa gốc trong episode

## 5.1 `medical_histories`

Chứa tiền sử bệnh và các yếu tố nền:
- `medical_history`
- `process`
- `antibiotic_history`
- dị ứng,
- thuốc,
- rượu,
- hút thuốc,
- yếu tố khác.

### Vai trò
Đây là lớp thông tin nền để AI hiểu bối cảnh bệnh nhân, nguy cơ và yếu tố ảnh hưởng điều trị.

### Lưu ý
Khóa chính là `episode_id`, nghĩa là hiện tại thiết kế đang thiên về **mỗi episode có một bản medical history chính**.

---

## 5.2 `clinical_records`

Chứa dữ liệu khám lâm sàng và nhận định chính:
- ngày ghi nhận,
- huyết áp, BMI,
- sốt, đau, ban đỏ, sưng, đường rò,
- loại nhiễm trùng nghi ngờ (`infection_type`),
- nghi ngờ đường máu,
- độ vững implant (`implant_stability_type`),
- mô mềm,
- dị ứng PMMA,
- khớp liên quan,
- số ngày từ lần thay khớp ban đầu,
- ghi chú.

### Vai trò
Đây là bảng mô tả **trạng thái lâm sàng thực tế của ca bệnh** và thường là input cực kỳ quan trọng cho AI suy luận.

### Lưu ý cho agent
Nếu có nhiều `clinical_records` trong cùng 1 episode:
- ưu tiên bản mới nhất theo `recorded_at` hoặc `updated_at`,
- nhưng vẫn nên xem lịch sử nếu người dùng hỏi diễn tiến.

---

## 5.3 `surgeries`

Lưu lịch sử phẫu thuật trong episode:
- `surgery_date`
- `surgery_type`
- `findings`

### Vai trò
Cho biết bệnh nhân đã trải qua can thiệp nào trong đợt điều trị này hoặc trong phạm vi hồ sơ episode đang theo dõi.

### Cách hiểu đúng
Bảng này nên được hiểu là:
- lịch sử phẫu thuật liên quan episode,
- không phải recommendation phẫu thuật của AI,
- recommendation phẫu thuật của AI nằm ở `ai_recommendation_items`.

---

## 5.4 `lab_results`

Chứa xét nghiệm dạng JSONB, ví dụ:
- `esr`
- `wbc_blood`
- `neut`
- `crp`
- `dimer`
- `serum_il6`
- `alpha_defensin`
- `synovial_wbc`
- `synovial_pmn`
- `biochemical_data`

### Vai trò
Đây là dữ liệu cận lâm sàng định lượng.

### Cách đọc đúng
Mỗi chỉ số thường có dạng:
```json
{
  "value": 18.0,
  "unit": "mm"
}
```

Riêng `biochemical_data` là object tổng hợp nhiều chỉ số con.

### Quy tắc cho agent
- Không chỉ lấy `value`, phải luôn giữ cùng `unit`.
- Khi tóm tắt, nên chuẩn hóa theo mẫu: `CRP = 3.2 mg/L`.
- Nếu nhiều bản lab trong cùng episode, ưu tiên bản mới nhất, nhưng có thể mô tả xu hướng nếu user hỏi.

---

## 5.5 `image_results`

Chứa kết quả chẩn đoán hình ảnh:
- `type` (ví dụ X-ray, CT)
- `findings`
- `file_metadata` (JSONB, thường có URL/tên file)

### Vai trò
Lưu kết quả đọc hình ảnh và metadata file.

### Gợi ý cho agent
- Ưu tiên `findings` để trả lời nghiệp vụ.
- `file_metadata` chỉ dùng khi cần liên kết file/hình.

---

## 5.6 `culture_results`

Chứa kết quả cấy:
- `sample_type`
- `incubation_days`
- `name` (vi khuẩn/tác nhân)
- `result_status`
- `gram_type`
- `notes`
- `antibioticed`
- `days_off_antibio`

### Vai trò
Đây là bảng vi sinh chính để xác định tác nhân gây bệnh.

### Cách hiểu
Một episode có thể có nhiều mẫu cấy khác nhau.

### Lưu ý cho agent
- Phải phân biệt:
  - `PENDING`
  - `NO_GROWTH`
  - `POSITIVE`
  - `CONTAMINATED`
  - `FINAL_NEGATIVE`
- Không nên kết luận tác nhân nếu kết quả chưa final hoặc bị contaminated.

---

## 5.7 `sensitivity_results`

Chứa kháng sinh đồ, gắn với `culture_id`:
- `antibiotic_name`
- `mic_value`
- `sensitivity_code`

### Vai trò
Đây là lớp chi tiết đi kèm với từng mẫu cấy dương tính.

### Quan hệ
- N `sensitivity_results` -> 1 `culture_results`

### Quy tắc cho agent
Khi người dùng hỏi “kháng sinh nào nhạy”, agent phải:
1. tìm `culture_results` phù hợp,
2. rồi lấy toàn bộ `sensitivity_results` theo `culture_id`.

---

## 6) Snapshot layer – lớp trung gian giữa dữ liệu gốc và AI

## 6.1 `case_clinical_snapshots`

Trường chính:
- `episode_id`
- `snapshot_no`
- `snapshot_data_json`
- `data_completeness_score`
- `created_by`
- `created_at`

### Vai trò
Đây là **điểm chụp tổng hợp dữ liệu tại một thời điểm** để gửi sang AI.

Thay vì để AI đọc trực tiếp rời rạc từ nhiều bảng, hệ thống gom lại thành một object snapshot.

### Ý nghĩa cực kỳ quan trọng
Snapshot là:
- đầu vào ổn định cho AI,
- cơ sở tái lập recommendation,
- cách đóng băng trạng thái dữ liệu ở thời điểm chạy AI.

### Quan hệ
- 1 `pji_episodes` -> N `case_clinical_snapshots`
- 1 `case_clinical_snapshots` -> N `ai_recommendation_runs`

### Quy tắc cho agent
Khi phân tích một AI run:
- phải xem nó được sinh từ snapshot nào,
- không được mặc định AI run luôn phản ánh dữ liệu mới nhất của episode.

---

## 7) AI recommendation layer

## 7.1 `ai_recommendation_runs`

Đây là bản ghi mỗi lần AI chạy gợi ý.

Trường chính:
- `episode_id`
- `snapshot_id`
- `run_no`
- `trigger_type`
- `status`
- `model_name`
- `model_version`
- `assessment_json`
- `explanation_json`
- `warnings_json`
- `latency_ms`
- `error_message`
- `created_by`
- `created_at`

### Ý nghĩa
Mỗi lần người dùng bấm generate hoặc hệ thống auto refresh dữ liệu, một `run` mới được tạo.

### Trạng thái chạy
Enum `ai_run_status`:
- `SUCCESS`
- `FAILED`
- `PARTIAL`
- `QUEUED`
- `PROCESSING`
- `TIMEOUT`

### Trigger type
Enum `ai_run_trigger_type`:
- `MANUAL_GENERATE`
- `AUTO_REFRESH`
- `DATA_CHANGED`
- `DOCTOR_REQUEST`
- `REVIEW_REQUEST`

### Vai trò của các cột JSON
- `assessment_json`: kết luận/nhận định tổng quát
- `explanation_json`: giải thích lập luận AI
- `warnings_json`: cảnh báo / giới hạn / nguy cơ

### Quy tắc cho agent
- Không chỉ nhìn `status = SUCCESS`; vẫn cần xem `warnings_json`.
- `run_no` là số thứ tự trong phạm vi `episode_id`.
- Khi có nhiều run, thường run mới nhất là quan trọng nhất, nhưng run cũ cần để so sánh tiến hóa gợi ý.

---

## 7.2 `ai_recommendation_items`

Đây là danh sách item chi tiết do AI sinh ra cho mỗi run.

Trường chính:
- `run_id`
- `category`
- `title`
- `priority_order`
- `is_primary`
- `item_json`

### Category enum
- `DIAGNOSTIC_TEST`
- `SYSTEMIC_ANTIBIOTIC`
- `LOCAL_ANTIBIOTIC`
- `SURGERY_PROCEDURE`

### Ý nghĩa
`ai_recommendation_runs` là “header”.
`ai_recommendation_items` là “detail line items”.

### Cách hiểu
Ví dụ trong một run:
- item 1: xét nghiệm đề nghị làm thêm,
- item 2: kháng sinh toàn thân,
- item 3: kháng sinh tại chỗ,
- item 4: thủ thuật/phẫu thuật.

### Quy tắc cho agent
- Sắp xếp theo `priority_order`.
- `is_primary = true` thường là item chính.
- Nội dung chi tiết thực nằm trong `item_json`, không chỉ ở `title`.

---

## 7.3 `ai_rag_citations`

Trường chính:
- `run_id`
- `item_id`
- `source_type`
- `source_title`
- `source_uri`
- `snippet`
- `relevance_score`

### Vai trò
Đây là bằng chứng / citation mà hệ RAG dùng để hỗ trợ cho output AI.

### Ý nghĩa nghiệp vụ
Giúp bác sĩ hoặc hệ thống audit biết:
- AI đang dựa vào nguồn nào,
- nguồn đó liên kết tới item nào,
- mức độ liên quan ra sao.

### Quy tắc cho agent
Khi người dùng hỏi:
- “AI dựa trên nguồn nào?”
- “Vì sao AI đề xuất như vậy?”
thì phải đọc bảng này cùng với `ai_recommendation_items`.

---

## 8) Review của bác sĩ và phác đồ chính thức

## 8.1 `doctor_recommendation_reviews`

Trường chính:
- `episode_id`
- `run_id`
- `review_status`
- `review_note`
- `modification_json`
- `rejection_reason`
- `reviewed_by`
- `created_at`
- `updated_at`

### Ý nghĩa
Đây là phản hồi của bác sĩ đối với một AI run.

### `review_status`
- `ACCEPTED`
- `MODIFIED`
- `REJECTED`
- `SAVED_DRAFT`

### Diễn giải
- `ACCEPTED`: bác sĩ đồng ý.
- `MODIFIED`: bác sĩ chỉnh sửa đề xuất AI.
- `REJECTED`: bác sĩ bác bỏ.
- `SAVED_DRAFT`: chưa chốt.

### Quy tắc cho agent
Khi user hỏi “bác sĩ đã quyết định gì?”, phải ưu tiên đọc bảng này trước, không được chỉ đọc output AI.

---

## 8.2 `treatment_plan_versions`

Đây là lớp chốt phác đồ điều trị cuối cùng.

Trường chính:
- `episode_id`
- `source_run_id`
- `source_review_id`
- `version_no`
- `is_current`
- `status`
- `regimen_json`
- `clinical_rationale`
- `confirmed_by`
- `confirmed_at`

### Vai trò
Đây là **bản kế hoạch điều trị chính thức có version**.

### Ý nghĩa
Sau khi review AI, hệ thống không nên ghi đè một bản duy nhất, mà lưu version để theo dõi thay đổi theo thời gian.

### `status`
- `DRAFT`
- `CONFIRMED`
- `SUPERSEDED`
- `CANCELLED`

### Quy tắc cho agent
- Khi cần lấy phác đồ hiện hành, ưu tiên:
  - `is_current = true`
  - và `status = CONFIRMED` nếu có.
- Nếu user hỏi lịch sử thay đổi phác đồ, lấy toàn bộ version theo `version_no`.

---

## 9) Chat với AI

## 9.1 `ai_chat_sessions`

Trường chính:
- `user_id`
- `run_id`
- `current_item_id`
- `chat_type`
- `episode_id`
- `title`
- `is_archived`
- `created_at`

### Vai trò
Mỗi session chat AI gắn với một người dùng, một episode, và có thể gắn với một AI run cụ thể.

### Cách hiểu
Có 2 kiểu ngữ cảnh chat phổ biến:
- chat theo ca bệnh (`episode_id`),
- chat xoay quanh một recommendation run (`run_id`),
- thậm chí thu hẹp vào một recommendation item (`current_item_id`).

### Quy tắc cho agent
Khi trả lời trong chat:
- ngữ cảnh phải ưu tiên `episode_id`,
- nếu `run_id` có mặt thì câu trả lời nên bám vào output AI của run đó,
- nếu `current_item_id` có mặt thì nên trả lời xoay quanh item tương ứng.

---

## 9.2 `ai_chat_messages`

Trường chính:
- `session_id`
- `role`
- `content`
- `tokens_used`
- `context_json`
- `latency_ms`
- `references_json`
- `created_at`

### Vai trò
Lưu toàn bộ hội thoại theo session.

### Quy tắc cho agent
- `role` thường là user / assistant / system.
- `context_json` có thể chứa context đã bơm cho model ở thời điểm đó.
- `references_json` có thể là citation hoặc nguồn hỗ trợ cho câu trả lời.

---

## 10) Identity và phân quyền

## 10.1 `users`
Thông tin người dùng nội bộ:
- email,
- password,
- fullname,
- role_id,
- department,
- avatar,
- refresh_token,
- status,
- is_active.

## 10.2 `roles`
Nhóm quyền, ví dụ admin / doctor / staff.

## 10.3 `permissions`
Danh sách permission theo API:
- `api_path`
- `method`
- `module`
- `name`

## 10.4 `role_permissions`
Bảng nối role và permission.

### Ý nghĩa cho agent
Agent không nhất thiết cần dùng phân quyền để suy luận y khoa, nhưng cần hiểu:
- hệ thống backend tổ chức nghiệp vụ theo module API,
- các module này phản ánh khá rõ các phân hệ nghiệp vụ thực tế.

---

## 11) Sơ đồ quan hệ nghiệp vụ rút gọn

```text
users -> roles -> role_permissions -> permissions

patients
  -> pji_episodes
      -> medical_histories
      -> clinical_records
      -> surgeries
      -> lab_results
      -> image_results
      -> culture_results
            -> sensitivity_results
      -> case_clinical_snapshots
            -> ai_recommendation_runs
                  -> ai_recommendation_items
                        -> ai_rag_citations
                  -> doctor_recommendation_reviews
                  -> ai_chat_sessions
                        -> ai_chat_messages
      -> treatment_plan_versions
```

> Ghi chú: `ai_chat_sessions` còn gắn trực tiếp với `users` và `pji_episodes`.

---

## 12) Luồng hoạt động chuẩn của hệ thống

## 12.1 Luồng nhập liệu ca bệnh

1. Tạo bệnh nhân trong `patients`.
2. Tạo episode trong `pji_episodes`.
3. Nhập:
   - `medical_histories`
   - `clinical_records`
   - `surgeries`
   - `lab_results`
   - `image_results`
   - `culture_results`
   - `sensitivity_results`

Kết quả: episode có đủ dữ liệu y khoa để đánh giá.

---

## 12.2 Luồng sinh recommendation AI

1. Hệ thống đọc dữ liệu y khoa của `episode_id`.
2. Gom các dữ liệu hiện tại vào `snapshot_data_json`.
3. Tạo `case_clinical_snapshots`.
4. Tạo `ai_recommendation_runs` gắn với snapshot đó.
5. Khi AI trả kết quả:
   - cập nhật `assessment_json`
   - cập nhật `explanation_json`
   - cập nhật `warnings_json`
   - set `status`
6. Tạo nhiều `ai_recommendation_items`.
7. Nếu có RAG, lưu `ai_rag_citations`.

---

## 12.3 Luồng bác sĩ review AI

1. Bác sĩ mở AI run.
2. Xem assessment, explanation, warnings, items, citations.
3. Thực hiện review:
   - accept,
   - modify,
   - reject,
   - save draft.
4. Lưu vào `doctor_recommendation_reviews`.
5. Nếu cần chốt phác đồ chính thức:
   - tạo `treatment_plan_versions`
   - gắn `source_run_id`
   - gắn `source_review_id`
   - set `is_current = true`

---

## 12.4 Luồng cập nhật điều trị theo version

1. Khi có thay đổi lớn trong nhận định hoặc đáp ứng điều trị:
   - dữ liệu gốc trong episode được cập nhật,
   - snapshot mới được tạo,
   - AI run mới được tạo,
   - review mới được tạo,
   - treatment plan version mới được sinh.
2. Version cũ có thể bị `SUPERSEDED`.
3. Version hiện tại là bản có `is_current = true`.

---

## 12.5 Luồng chat với AI

1. Người dùng mở chat theo episode.
2. Hệ thống tạo `ai_chat_sessions`.
3. Tin nhắn được lưu ở `ai_chat_messages`.
4. Nếu chat dựa trên recommendation run cụ thể:
   - `run_id` được gắn vào session.
5. Nếu chat xoáy vào một item:
   - `current_item_id` được dùng để neo ngữ cảnh.

---

## 13) Thứ tự ưu tiên dữ liệu khi agent cần trả lời về một ca bệnh

Khi nhận câu hỏi về một `episode`, agent nên đọc theo thứ tự sau:

### Mức 1 – định danh ca
1. `patients`
2. `pji_episodes`

### Mức 2 – dữ liệu y khoa gốc
3. `medical_histories`
4. `clinical_records`
5. `surgeries`
6. `lab_results`
7. `image_results`
8. `culture_results`
9. `sensitivity_results`

### Mức 3 – dữ liệu AI
10. `case_clinical_snapshots`
11. `ai_recommendation_runs`
12. `ai_recommendation_items`
13. `ai_rag_citations`

### Mức 4 – quyết định của bác sĩ
14. `doctor_recommendation_reviews`
15. `treatment_plan_versions`

### Mức 5 – lịch sử hỏi đáp
16. `ai_chat_sessions`
17. `ai_chat_messages`

---

## 14) Quy tắc suy luận quan trọng cho AI agent

## 14.1 Episode là đơn vị nghiệp vụ trung tâm
Không được trộn dữ liệu giữa các episode của cùng một bệnh nhân.

## 14.2 Snapshot mới không đồng nghĩa run mới nhất là đúng tuyệt đối
Run AI luôn phản ánh trạng thái dữ liệu tại thời điểm snapshot được tạo.

## 14.3 Recommendation AI không phải quyết định cuối cùng
Quyết định cuối cần ưu tiên:
1. `doctor_recommendation_reviews`
2. `treatment_plan_versions`

## 14.4 Kết quả cấy và kháng sinh đồ phải đọc theo cặp
Không được kết luận kháng sinh nếu chưa map đúng `culture_results` -> `sensitivity_results`.

## 14.5 Lab JSONB phải giữ nguyên unit
Không được tóm tắt số liệu mà bỏ đơn vị.

## 14.6 Dữ liệu nhiều bản ghi phải ưu tiên bản mới nhất nhưng không bỏ lịch sử
Đặc biệt áp dụng cho:
- `clinical_records`
- `lab_results`
- `culture_results`
- `treatment_plan_versions`
- `ai_recommendation_runs`

## 14.7 Nếu recommendation bị bác sĩ sửa
Phải xem `modification_json` thay vì chỉ lặp lại item AI nguyên gốc.

---

## 15) Truy vấn nghiệp vụ mẫu mà agent nên hiểu

### 15.1 “Tóm tắt ca bệnh hiện tại”
Cần tổng hợp:
- thông tin bệnh nhân,
- episode hiện tại,
- clinical record mới nhất,
- lab mới nhất,
- image findings mới nhất,
- culture/sensitivity mới nhất,
- treatment plan current.

### 15.2 “AI đã gợi ý gì?”
Cần lấy:
- run mới nhất hoặc run được chỉ định,
- assessment/explanation/warnings,
- items theo priority,
- citations nếu có.

### 15.3 “Bác sĩ có đồng ý với AI không?”
Cần lấy:
- review mới nhất theo run hoặc theo episode,
- `review_status`,
- `review_note`,
- `modification_json`,
- `rejection_reason`.

### 15.4 “Phác đồ hiện tại là gì?”
Cần lấy:
- `treatment_plan_versions`
- filter `is_current = true`
- đọc `regimen_json` và `clinical_rationale`

### 15.5 “Kháng sinh nào phù hợp theo vi sinh?”
Cần lấy:
- culture dương tính mới nhất hoặc culture được chỉ định,
- toàn bộ sensitivity của culture đó,
- sau đó mới liên hệ với treatment plan hiện hành.

---

## 16) Gợi ý xây context cho LLM/AI agent

Khi build context cho model, nên theo cấu trúc:

```json
{
  "patient": {},
  "episode": {},
  "medical_history": {},
  "latest_clinical_record": {},
  "surgeries": [],
  "latest_lab_result": {},
  "latest_image_results": [],
  "culture_results": [
    {
      "culture": {},
      "sensitivities": []
    }
  ],
  "latest_snapshot": {},
  "latest_ai_run": {
    "assessment": {},
    "explanation": {},
    "warnings": {},
    "items": [],
    "citations": []
  },
  "latest_doctor_review": {},
  "current_treatment_plan": {}
}
```

### Lợi ích
- model dễ hiểu,
- giảm lặp,
- tách rõ dữ liệu gốc và dữ liệu AI,
- tránh nhầm recommendation với quyết định cuối.

---

## 17) Những bẫy dễ hiểu sai

### Bẫy 1
`surgeries` không phải đề xuất mổ của AI.

### Bẫy 2
`ai_recommendation_items` không phải phác đồ cuối cùng.

### Bẫy 3
`ai_chat_messages` không phải dữ liệu lâm sàng gốc.

### Bẫy 4
`medical_histories` có thể chứa text tự do, chất lượng dữ liệu không đồng đều.

### Bẫy 5
`culture_results` và `sensitivity_results` phải đọc cùng nhau.

### Bẫy 6
`status` của episode khác `status` của AI run và khác `status` của treatment plan.

---

## 18) Đề xuất contract tư duy cho AI agent

Khi được hỏi về hệ thống này, agent nên tuân thủ:

1. Xác định `episode_id` trước.
2. Phân tách rõ:
   - dữ liệu gốc,
   - dữ liệu snapshot,
   - output AI,
   - quyết định bác sĩ,
   - phác đồ cuối.
3. Nếu có mâu thuẫn:
   - ưu tiên quyết định bác sĩ và treatment plan hơn output AI.
4. Khi trả lời:
   - nêu dữ liệu nào là observed fact,
   - dữ liệu nào là AI suggestion,
   - dữ liệu nào là final confirmed plan.

---

## 19) Kết luận ngắn gọn

Hệ thống này có kiến trúc rất rõ theo 4 tầng:

- **Tầng hồ sơ bệnh án**: patient + episode + dữ liệu y khoa gốc
- **Tầng chuẩn hóa cho AI**: clinical snapshot
- **Tầng AI**: recommendation run + items + citations
- **Tầng quyết định lâm sàng**: doctor review + treatment plan versions

Nếu AI agent hiểu đúng bốn tầng này, agent sẽ:
- không nhầm dữ liệu gốc với dữ liệu AI sinh ra,
- không nhầm gợi ý với quyết định cuối,
- và có thể trả lời đúng theo từng ca bệnh.

---

## 20) Tóm tắt 1 dòng cho agent

> Episode là trung tâm; dữ liệu y khoa gốc tạo ra snapshot; snapshot sinh AI run; bác sĩ review AI run; treatment plan version là quyết định điều trị chính thức cuối cùng.

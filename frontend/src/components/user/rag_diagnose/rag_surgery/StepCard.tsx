import { RiskLevel, SurgeryStatus, SurgeryStep } from '@/types/types';
import React, { useState } from 'react'


const ChevronDown = ({ open }: { open: boolean }) => (
    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5"
        style={{ transform: open ? "rotate(180deg)" : "rotate(0deg)", transition: "transform 0.2s" }}>
        <path d="M6 9l6 6 6-6" />
    </svg>
);
const EditIcon = () => (
    <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7" />
        <path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z" />
    </svg>
);
const DeleteIcon = () => (
    <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <polyline points="3 6 5 6 21 6" /><path d="M19 6l-1 14a2 2 0 01-2 2H8a2 2 0 01-2-2L5 6" />
        <path d="M10 11v6M14 11v6" /><path d="M9 6V4a1 1 0 011-1h4a1 1 0 011 1v2" />
    </svg>
);

const ClockIcon = () => (
    <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <circle cx="12" cy="12" r="10" /><polyline points="12 6 12 12 16 14" />
    </svg>
);
const AlertIcon = () => (
    <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <path d="M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z" />
        <line x1="12" y1="9" x2="12" y2="13" /><line x1="12" y1="17" x2="12.01" y2="17" />
    </svg>
);
const UserIcon = () => (
    <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2" /><circle cx="12" cy="7" r="4" />
    </svg>
);
const TagIcon = () => (
    <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <path d="M20.59 13.41l-7.17 7.17a2 2 0 01-2.83 0L2 12V2h10l8.59 8.59a2 2 0 010 2.82z" />
        <line x1="7" y1="7" x2="7.01" y2="7" />
    </svg>
);
const riskColors: Record<RiskLevel, { bg: string; text: string; dot: string }> = {
    "Thấp": { bg: "rgba(34,197,94,0.12)", text: "#4ade80", dot: "#22c55e" },
    "Trung bình": { bg: "rgba(251,191,36,0.12)", text: "#fbbf24", dot: "#f59e0b" },
    "Cao": { bg: "rgba(248,113,113,0.12)", text: "#f87171", dot: "#ef4444" },
};

const statusColors: Record<SurgeryStatus, { bg: string; text: string }> = {
    "Bắt buộc": { bg: "rgba(96,165,250,0.15)", text: "#60a5fa" },
    "Đề xuất": { bg: "rgba(167,139,250,0.15)", text: "#a78bfa" },
    "Tùy chọn": { bg: "rgba(148,163,184,0.15)", text: "#94a3b8" },
};
function MetaTag({ icon, label, value }: { icon: React.ReactNode; label: string; value: string }) {
    return (
        <div style={{
            background: "rgba(30,41,59,0.8)", borderRadius: "7px",
            padding: "7px 10px", display: "flex", flexDirection: "column", gap: "3px",
        }}>
            <span style={{ fontSize: "10px", color: "#64748b", display: "flex", alignItems: "center", gap: "4px", textTransform: "uppercase", letterSpacing: "0.05em" }}>
                {icon} {label}
            </span>
            <span style={{ fontSize: "12px", color: "#cbd5e1", fontWeight: 500 }}>{value}</span>
        </div>
    );
}
const StepCard = ({ step, onEdit, onDelete }: {
    step: SurgeryStep;
    onEdit: (id: number) => void;
    onDelete: (id: number) => void;
}) => {
    const [expanded, setExpanded] = useState(false);
    const risk = riskColors[step.risk];
    const status = statusColors[step.status];
    return (
        <div style={{
            background: "rgba(15,23,42,0.6)",
            border: "1px solid rgba(100,116,139,0.25)",
            borderRadius: "10px",
            overflow: "hidden",
            transition: "border-color 0.2s, box-shadow 0.2s",
        }}
            onMouseEnter={e => {
                (e.currentTarget as HTMLDivElement).style.borderColor = "rgba(96,165,250,0.4)";
                (e.currentTarget as HTMLDivElement).style.boxShadow = "0 0 0 1px rgba(96,165,250,0.1)";
            }}
            onMouseLeave={e => {
                (e.currentTarget as HTMLDivElement).style.borderColor = "rgba(100,116,139,0.25)";
                (e.currentTarget as HTMLDivElement).style.boxShadow = "none";
            }}
        >
            {/* Header row */}
            <div style={{ display: "flex", alignItems: "center", padding: "10px 12px", gap: "10px", cursor: "pointer" }}
                onClick={() => setExpanded(!expanded)}>
                {/* Step badge */}
                <div style={{
                    minWidth: "28px", height: "28px", borderRadius: "8px",
                    background: "linear-gradient(135deg, #1e40af, #3b82f6)",
                    display: "flex", alignItems: "center", justifyContent: "center",
                    fontSize: "11px", fontWeight: 700, color: "#fff", fontFamily: "monospace",
                    boxShadow: "0 2px 8px rgba(59,130,246,0.3)",
                }}>
                    {String(step.step).padStart(2, "0")}
                </div>

                {/* Title */}
                <span style={{ flex: 1, fontSize: "13px", fontWeight: 600, color: "#e2e8f0", letterSpacing: "-0.01em" }}>
                    {step.name}
                </span>

                {/* Badges */}
                <div style={{ display: "flex", gap: "6px", alignItems: "center" }}>
                    <span style={{
                        fontSize: "10px", fontWeight: 600, padding: "2px 8px", borderRadius: "20px",
                        background: status.bg, color: status.text, letterSpacing: "0.03em",
                    }}>{step.status}</span>
                    <span style={{
                        fontSize: "10px", fontWeight: 600, padding: "2px 8px", borderRadius: "20px",
                        background: risk.bg, color: risk.text, letterSpacing: "0.03em",
                        display: "flex", alignItems: "center", gap: "4px",
                    }}>
                        <span style={{ width: "5px", height: "5px", borderRadius: "50%", background: risk.dot, display: "inline-block" }} />
                        {step.risk}
                    </span>
                </div>

                {/* Actions */}
                <div style={{ display: "flex", gap: "4px", marginLeft: "4px" }}>
                    <button onClick={e => { e.stopPropagation(); onEdit(step.id); }} style={{
                        background: "rgba(234,179,8,0.1)", border: "1px solid rgba(234,179,8,0.2)",
                        color: "#eab308", borderRadius: "6px", padding: "4px 6px", cursor: "pointer",
                        display: "flex", alignItems: "center", transition: "all 0.15s",
                    }}
                        onMouseEnter={e => (e.currentTarget.style.background = "rgba(234,179,8,0.2)")}
                        onMouseLeave={e => (e.currentTarget.style.background = "rgba(234,179,8,0.1)")}
                    ><EditIcon /></button>
                    <button onClick={e => { e.stopPropagation(); onDelete(step.id); }} style={{
                        background: "rgba(239,68,68,0.1)", border: "1px solid rgba(239,68,68,0.2)",
                        color: "#ef4444", borderRadius: "6px", padding: "4px 6px", cursor: "pointer",
                        display: "flex", alignItems: "center", transition: "all 0.15s",
                    }}
                        onMouseEnter={e => (e.currentTarget.style.background = "rgba(239,68,68,0.2)")}
                        onMouseLeave={e => (e.currentTarget.style.background = "rgba(239,68,68,0.1)")}
                    ><DeleteIcon /></button>
                    <div style={{ color: "#64748b", display: "flex", alignItems: "center" }}>
                        <ChevronDown open={expanded} />
                    </div>
                </div>
            </div>

            {/* Expanded detail */}
            {expanded && (
                <div style={{
                    borderTop: "1px solid rgba(100,116,139,0.2)",
                    padding: "12px 14px",
                    display: "flex", flexDirection: "column", gap: "10px",
                    background: "rgba(2,6,23,0.4)",
                }}>
                    {/* Description */}
                    <p style={{ fontSize: "12.5px", color: "#94a3b8", lineHeight: "1.6", margin: 0 }}>
                        {step.description}
                    </p>

                    {/* Meta grid */}
                    <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "8px" }}>
                        <MetaTag icon={<ClockIcon />} label="Thời gian mổ" value={step.duration} />
                        <MetaTag icon={<TagIcon />} label="Thời điểm thực hiện" value={step.timing} />
                        <MetaTag icon={<UserIcon />} label="Phẫu thuật viên" value={step.surgeon} />
                    </div>

                    {/* Notes */}
                    <div style={{
                        background: "rgba(251,191,36,0.07)", border: "1px solid rgba(251,191,36,0.18)",
                        borderLeft: "3px solid #fbbf24", borderRadius: "6px",
                        padding: "8px 10px", display: "flex", gap: "8px", alignItems: "flex-start",
                    }}>
                        <span style={{ color: "#fbbf24", marginTop: "1px", flexShrink: 0 }}><AlertIcon /></span>
                        <span style={{ fontSize: "12px", color: "#cbd5e1", lineHeight: "1.55" }}>
                            <strong style={{ color: "#fde68a", marginRight: "4px" }}>Lưu ý:</strong>
                            {step.notes}
                        </span>
                    </div>
                </div>
            )}
        </div>
    )
}

export default StepCard
import React, { useState } from 'react'

const ProtocolField = ({ label, value, onChange, accent }: {
    label: string;
    value: string;
    onChange: (value: string) => void;
    accent: string;
}) => {
    const [editing, setEditing] = useState(false);
    return (
        <div style={{
            background: "rgba(233, 236, 243, 0.7)", border: "1px solid rgba(100,116,139,0.2)",
            borderRadius: "8px", padding: "8px 10px", cursor: "pointer",
            borderTop: `2px solid ${accent}`,
            transition: "border-color 0.2s",
        }}
            onClick={() => setEditing(true)}
        >
            <div style={{ fontSize: "12px", color: "#475569", textTransform: "uppercase", letterSpacing: "0.06em", marginBottom: "4px" }}>
                {label}
            </div>
            {editing ? (
                <input
                    autoFocus
                    defaultValue={value}
                    onBlur={e => { onChange(e.target.value); setEditing(false); }}
                    onKeyDown={e => 
                        { if (e.key === "Enter") { 
                            onChange((e.target as HTMLInputElement).value); 
                            setEditing(false);
                         } 
                    }}
                    style={{
                        width: "100%", background: "transparent", border: "none", outline: "none",
                        fontSize: "12px", color: "black", fontWeight: 500,
                    }}
                />
            ) : (
                <div style={{ fontSize: "12px", color: "black", fontWeight: 500, lineHeight: "1.3" }}>
                    {value}
                </div>
            )}
        </div>
    );
}

export default ProtocolField
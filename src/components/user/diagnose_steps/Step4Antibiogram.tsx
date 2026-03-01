import React from 'react';
import { Button } from 'antd';
import { SaveOutlined } from '@ant-design/icons';

interface Step4Props {
    onNext: () => void;
    onPrev: () => void;
}

export const Step4Antibiogram: React.FC<Step4Props> = ({ onNext, onPrev }) => {
    // Mock data for Antibiogram
    const antibiotics = [
        { name: 'Oxacillin', mic: '>=4', interpretation: 'R' },
        { name: 'Cefoxitin Screen', mic: 'Positive', interpretation: 'R' },
        { name: 'Vancomycin', mic: '<=0.5', interpretation: 'S' },
        { name: 'Daptomycin', mic: '<=0.5', interpretation: 'S' },
        { name: 'Linezolid', mic: '1', interpretation: 'S' },
        { name: 'Clindamycin', mic: '>4', interpretation: 'R' },
        { name: 'Erythromycin', mic: '>8', interpretation: 'R' },
        { name: 'Rifampin', mic: '<=0.5', interpretation: 'S' },
        { name: 'Trimethoprim/Sulfamethoxazole', mic: '<=10', interpretation: 'S' },
        { name: 'Gentamicin', mic: '<=0.5', interpretation: 'S' }
    ];

    const getInterpretationColor = (val: string) => {
        if (val === 'R') return 'bg-red-100 text-red-700 border-red-200';
        if (val === 'S') return 'bg-green-100 text-green-700 border-green-200';
        if (val === 'I') return 'bg-yellow-100 text-yellow-700 border-yellow-200';
        return 'bg-slate-100 text-slate-700 border-slate-200';
    };

    return (
        <div className="flex flex-col h-full bg-slate-50 relative pb-24">
            <header className="bg-white border-b border-slate-200 px-8 py-5 flex items-center justify-between z-10 flex-shrink-0">
                <div>
                    <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Bảng Kháng Sinh Đồ</h1>
                    <p className="text-slate-500 text-sm mt-1">Kết quả định danh vi khuẩn và mức độ nhạy cảm tự động</p>
                </div>
            </header>

            <div className="flex-1 overflow-y-auto p-8">
                <div className="max-w-5xl mx-auto space-y-6">
                    <section className="bg-white rounded-xl shadow-sm border border-slate-200 p-6">
                        <div className="flex justify-between items-center mb-6 pb-4 border-b border-slate-100">
                            <div>
                                <h3 className="font-bold text-slate-800 text-base">Vi khuẩn phân lập: <span className="text-red-600">Staphylococcus aureus</span> (MRSA)</h3>
                                <p className="text-xs text-slate-500 mt-1">Mẫu cấy: Dịch khớp gối - Ngày cấy: 2026-02-25</p>
                            </div>
                            <div className="flex items-center gap-2 text-xs">
                                <div className="flex items-center gap-1"><span className="w-3 h-3 rounded bg-green-100 border border-green-200"></span>S (Susceptible)</div>
                                <div className="flex items-center gap-1"><span className="w-3 h-3 rounded bg-yellow-100 border border-yellow-200"></span>I (Intermediate)</div>
                                <div className="flex items-center gap-1"><span className="w-3 h-3 rounded bg-red-100 border border-red-200"></span>R (Resistant)</div>
                            </div>
                        </div>

                        <div className="overflow-hidden border border-slate-200 rounded-lg">
                            <table className="w-full text-sm text-left">
                                <thead className="bg-slate-50 text-slate-700 font-semibold border-b border-slate-200">
                                    <tr>
                                        <th className="px-4 py-3 border-r border-slate-200">Tên kháng sinh</th>
                                        <th className="px-4 py-3 border-r border-slate-200 w-32 text-center">MIC (µg/mL)</th>
                                        <th className="px-4 py-3 border-r border-slate-200 w-32 text-center">Biện luận</th>
                                        <th className="px-4 py-3 text-center">Ghi chú</th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-slate-200">
                                    {antibiotics.map((ab, idx) => (
                                        <tr key={idx} className="hover:bg-slate-50/50">
                                            <td className="px-4 py-2 font-medium text-slate-800 border-r border-slate-200">{ab.name}</td>
                                            <td className="px-4 py-2 border-r border-slate-200 text-center text-slate-600 font-mono">{ab.mic}</td>
                                            <td className="px-4 py-2 border-r border-slate-200 text-center">
                                                <span className={`inline-flex items-center justify-center font-bold text-xs w-8 h-6 rounded border ${getInterpretationColor(ab.interpretation)}`}>
                                                    {ab.interpretation}
                                                </span>
                                            </td>
                                            <td className="px-4 py-2 text-slate-500 text-center">
                                                {ab.name === 'Rifampin' && <span className="text-xs italic text-blue-600">Khuyến cáo phối hợp trị màng sinh học</span>}
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    </section>
                </div>
            </div>

            {/* Fixed Footer with buttons */}
            <div className="fixed bottom-0 left-72 right-0 bg-white border-t border-slate-200 p-4 px-8 flex items-center justify-between z-20 shadow-[0_-4px_6px_-1px_rgba(0,0,0,0.05)]">
                <button onClick={onPrev} className="px-6 py-3 text-sm font-medium text-slate-600 hover:text-slate-900 transition-colors flex items-center gap-2 border border-slate-200 rounded-lg bg-white">
                    <span className="material-symbols-outlined text-[18px]">arrow_back</span> Quay lại
                </button>
                <div className="flex gap-3">
                    <button className="flex items-center gap-2 rounded-lg bg-blue-50 px-6 py-3 text-sm font-bold text-blue-600 hover:bg-blue-100 transition-colors">
                        <SaveOutlined /> Lưu nháp
                    </button>
                    <button onClick={onNext} className="flex items-center gap-2 rounded-lg bg-primary px-6 py-3 text-sm font-bold text-white hover:bg-blue-600 shadow-lg shadow-blue-500/20 transition-all active:scale-95">
                        Tính toán phác đồ AI <span className="material-symbols-outlined text-[18px]">auto_awesome</span>
                    </button>
                </div>
            </div>
        </div>
    );
};

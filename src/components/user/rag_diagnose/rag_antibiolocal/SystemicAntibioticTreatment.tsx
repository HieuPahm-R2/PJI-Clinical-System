import React from 'react';
import { Input } from 'antd';
import { DeleteOutlined, EditOutlined, PlusOutlined } from '@ant-design/icons';

type PhaseColor = 'blue' | 'emerald';

interface TreatmentPhase {
    id: string;
    phaseNumber: number;
    weekLabel: string;
    routeLabel: string;
    drugName: string;
    dosage: string;
    duration: string;
    color: PhaseColor;
}

type EditablePhaseField = 'weekLabel' | 'routeLabel' | 'drugName' | 'dosage' | 'duration';

interface SystemicAntibioticTreatmentProps {
    phases: TreatmentPhase[];
    editingPhaseId: string | null;
    onPhaseFieldChange: (id: string, field: EditablePhaseField, value: string) => void;
    onToggleEditPhase: (id: string) => void;
    onAddPhase: () => void;
}

export const SystemicAntibioticTreatment: React.FC<SystemicAntibioticTreatmentProps> = ({
    phases,
    editingPhaseId,
    onPhaseFieldChange,
    onToggleEditPhase,
    onAddPhase,
}) => {
    return (
        <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
            <div className="p-4 border-b border-slate-100 flex justify-between items-center bg-slate-50/50">
                <h3 className="text-lg font-bold text-slate-900">Điều trị kháng sinh toàn thân</h3>
            </div>
            <div className="p-4 relative">
                <div className="absolute left-[39px] top-6 bottom-6 w-0.5 bg-slate-200 z-0"></div>

                {phases.map((phase, index) => {
                    const isLast = index === phases.length - 1;
                    const isEditing = editingPhaseId === phase.id;

                    const colorClasses =
                        phase.color === 'blue'
                            ? {
                                circle: 'bg-blue-400',
                                cardBg: 'bg-blue-50',
                                cardBorder: 'border-blue-100',
                                tagBg: 'bg-blue-600/10',
                                tagText: 'text-blue-600',
                            }
                            : {
                                circle: 'bg-emerald-600',
                                cardBg: 'bg-emerald-50',
                                cardBorder: 'border-emerald-100',
                                tagBg: 'bg-emerald-600/10',
                                tagText: 'text-emerald-600',
                            };

                    return (
                        <div
                            key={phase.id}
                            className={`relative z-10 flex gap-6 group ${!isLast ? 'mb-8' : ''}`}
                        >
                            <div className="flex flex-col items-center">
                                <div
                                    className={`w-8 h-8 rounded-full ${colorClasses.circle} text-white flex items-center justify-center font-bold text-sm shadow ring-4 ring-white`}
                                >
                                    {index + 1}
                                </div>
                                {isEditing ? (
                                    <Input
                                        size="small"
                                        placeholder="Tuần điều trị"
                                        value={phase.weekLabel}
                                        onChange={e =>
                                            onPhaseFieldChange(
                                                phase.id,
                                                'weekLabel',
                                                e.target.value
                                            )
                                        }
                                        className="mt-2 text-[10px] font-semibold text-center uppercase border-none bg-transparent text-slate-700 px-0"
                                    />
                                ) : (
                                    <div className="mt-2 text-[10px] font-semibold text-center uppercase text-slate-700 px-1">
                                        {phase.weekLabel || 'Tuần điều trị'}
                                    </div>
                                )}
                            </div>
                            <div
                                className={`flex-1 ${colorClasses.cardBg} border ${colorClasses.cardBorder} rounded-xl p-5 relative transition-all hover:shadow-md`}
                            >
                                <div className="absolute top-0 right-4 flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                                    <button
                                        title="Hỏi AI"
                                        className="p-1.5 text-slate-400 hover:text-purple-600 hover:bg-white rounded-md transition-colors flex items-center"
                                    >
                                        <span className="material-symbols-outlined text-[18px]">
                                            psychology
                                        </span>
                                    </button>

                                    <button
                                        title={isEditing ? 'Đóng chỉnh sửa' : 'Chỉnh sửa'}
                                        onClick={() => onToggleEditPhase(phase.id)}
                                        className="p-1.5 text-slate-400 hover:text-yellow-600 hover:bg-white rounded-md transition-colors"
                                    >
                                        <EditOutlined className="text-sm" />
                                    </button>
                                    <button
                                        title="Xóa"
                                        className="p-1.5 text-slate-400 hover:text-red-600 hover:bg-white rounded-md transition-colors"
                                    >
                                        <DeleteOutlined className="text-sm" />
                                    </button>
                                </div>
                                <div className="flex items-center gap-2 mb-3">
                                    {isEditing ? (
                                        <Input
                                            size="small"
                                            placeholder="Loại điều trị"
                                            value={phase.routeLabel}
                                            onChange={e =>
                                                onPhaseFieldChange(
                                                    phase.id,
                                                    'routeLabel',
                                                    e.target.value
                                                )
                                            }
                                            className={`text-[10px] font-bold px-2 py-0.5 rounded mt-2 uppercase border-none ${colorClasses.tagBg} ${colorClasses.tagText}`}
                                        />
                                    ) : (
                                        <span
                                            className={`text-[10px] font-bold px-2 py-0.5 rounded uppercase ${colorClasses.tagBg} ${colorClasses.tagText}`}
                                        >
                                            {phase.routeLabel || 'Loại điều trị'}
                                        </span>
                                    )}
                                    {isEditing ? (
                                        <Input
                                            size="middle"
                                            placeholder="Tên thuốc / phác đồ"
                                            value={phase.drugName}
                                            onChange={e =>
                                                onPhaseFieldChange(
                                                    phase.id,
                                                    'drugName',
                                                    e.target.value
                                                )
                                            }
                                            className="font-bold text-slate-900 text-lg border-none bg-transparent px-0 mt-2"
                                        />
                                    ) : (
                                        <h4 className="font-bold text-slate-900 text-lg">
                                            {phase.drugName || 'Tên thuốc / phác đồ'}
                                        </h4>
                                    )}
                                </div>
                                <div className="grid grid-cols-2 gap-4 text-sm">
                                    <div>
                                        <p className="text-slate-500 text-[10px] uppercase font-bold mb-0.5">
                                            Liều lượng
                                        </p>
                                        {isEditing ? (
                                            <Input
                                                size="small"
                                                placeholder="Nhập liều lượng"
                                                value={phase.dosage}
                                                onChange={e =>
                                                    onPhaseFieldChange(
                                                        phase.id,
                                                        'dosage',
                                                        e.target.value
                                                    )
                                                }
                                                className="font-semibold text-slate-800"
                                            />
                                        ) : (
                                            <p className="font-semibold text-slate-800">
                                                {phase.dosage || '—'}
                                            </p>
                                        )}
                                    </div>
                                    <div>
                                        <p className="text-slate-500 text-[10px] uppercase font-bold mb-0.5">
                                            Thời gian
                                        </p>
                                        {isEditing ? (
                                            <Input
                                                size="small"
                                                placeholder="Nhập thời gian"
                                                value={phase.duration}
                                                onChange={e =>
                                                    onPhaseFieldChange(
                                                        phase.id,
                                                        'duration',
                                                        e.target.value
                                                    )
                                                }
                                                className="font-semibold text-slate-800"
                                            />
                                        ) : (
                                            <p className="font-semibold text-slate-800">
                                                {phase.duration || '—'}
                                            </p>
                                        )}
                                    </div>
                                </div>
                            </div>
                        </div>
                    );
                })}

                <div className="mt-6">
                    <button
                        type="button"
                        onClick={onAddPhase}
                        className="w-full border-2 border-dashed border-slate-300 hover:border-blue-400 rounded-xl py-3 flex items-center justify-center text-sm font-medium text-slate-500 hover:text-blue-600 bg-slate-50/40 transition-colors"
                    >
                        <PlusOutlined className="mr-2" />
                        Thêm phase điều trị mới
                    </button>
                </div>
            </div>
        </div>
    );
};

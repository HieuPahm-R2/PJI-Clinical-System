import React from 'react';
import { Button, Input } from 'antd';
import { SendOutlined, InfoCircleOutlined, DeleteOutlined, EditOutlined, PlusOutlined } from '@ant-design/icons';
import { usePatient } from '../../../context/PatientContext';

interface Step5Props {
    onPrev: () => void;
}

export const Step5TreatmentPlan: React.FC<Step5Props> = ({ onPrev }) => {
    return (
        <div className="flex flex-col h-full relative">
            <header className="bg-white border-b border-slate-200 px-6 py-4 flex items-center justify-between z-10 flex-shrink-0">
                <div>
                    <h1 className="text-xl font-bold text-slate-900 tracking-tight flex items-center gap-2">
                        <span className="material-symbols-outlined text-green-400">psychology</span>
                        Gợi ý Phác đồ Phẫu thuật & Kháng sinh
                    </h1>
                </div>
                <div className="flex items-center gap-3 z-10">
                    <button onClick={onPrev} className="px-4 py-2 text-sm font-medium text-slate-300 hover:text-white transition-colors bg-slate-800 border border-slate-600 hover:border-slate-500 rounded-lg">Quay lại</button>
                </div>
            </header>

            {/* Dark Theme Container */}
            <div className="flex-1 overflow-hidden p-6 flex gap-6 text-slate-200">

                {/* Left Panel: Treatment Plan Draft */}
                <div className="flex-1 bg-white border border-slate-200 rounded-xl flex flex-col overflow-hidden shadow-2xl">
                    <div className="p-4 border-b border-slate-200 flex justify-between items-center bg-slate-50/50">
                        <h3 className="font-bold text-slate-900">Phác đồ gợi ý [DRAFT]</h3>
                        <Button size="small" type="primary" className="bg-slate-700 hover:!bg-slate-600 border-none flex items-center gap-1.5 text-xs">
                            Xác nhận <span className="material-symbols-outlined text-[14px]">save</span>
                        </Button>
                    </div>

                    <div className="flex-1 overflow-y-auto p-4 space-y-4">
                        <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
                            <div className="p-6 border-b border-slate-100 flex justify-between items-center bg-slate-50/50">
                                <h3 className="text-lg font-bold text-slate-900">Liệu trình điều trị dự kiến</h3>
                            </div>
                            <div className="p-6 relative">
                                <div className="absolute left-[39px] top-6 bottom-6 w-0.5 bg-slate-200 z-0"></div>

                                {/* Phase 1 */}
                                <div className="relative z-10 flex gap-6 mb-8 group">
                                    <div className="flex flex-col items-center">
                                        <div className="w-8 h-8 rounded-full bg-blue-600 text-white flex items-center justify-center font-bold text-sm shadow ring-4 ring-white">1</div>
                                        <div className="mt-2 text-xs font-semibold text-slate-500 uppercase">Tuần 1-2</div>
                                    </div>
                                    <div className="flex-1 bg-blue-50 border border-blue-100 rounded-xl p-5 relative transition-all hover:shadow-md">
                                        <div className="absolute top-4 right-4 flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                                            <button title="Hỏi AI" className="p-1.5 text-slate-400 hover:text-purple-600 hover:bg-white rounded-md transition-colors flex items-center">
                                                <span className="material-symbols-outlined text-[18px]">psychology</span>
                                            </button>
                                            <button title="Thêm thuốc" className="p-1.5 text-slate-400 hover:text-blue-600 hover:bg-white rounded-md transition-colors">
                                                <PlusOutlined className="text-sm" />
                                            </button>
                                            <button title="Chỉnh sửa" className="p-1.5 text-slate-400 hover:text-yellow-600 hover:bg-white rounded-md transition-colors">
                                                <EditOutlined className="text-sm" />
                                            </button>
                                            <button title="Xóa" className="p-1.5 text-slate-400 hover:text-red-600 hover:bg-white rounded-md transition-colors">
                                                <DeleteOutlined className="text-sm" />
                                            </button>
                                        </div>
                                        <div className="flex items-center gap-2 mb-3">
                                            <span className="bg-blue-600/10 text-blue-600 text-[10px] font-bold px-2 py-0.5 rounded uppercase">Điều trị tiêm tĩnh mạch</span>
                                            <h4 className="font-bold text-slate-900 text-lg">Daptomycin</h4>
                                        </div>
                                        <div className="grid grid-cols-2 gap-4 text-sm">
                                            <div>
                                                <p className="text-slate-500 text-[10px] uppercase font-bold mb-0.5">Liều lượng</p>
                                                <p className="font-semibold text-slate-800">6-8 mg/kg IV</p>
                                            </div>
                                            <div>
                                                <p className="text-slate-500 text-[10px] uppercase font-bold mb-0.5">Thời gian</p>
                                                <p className="font-semibold text-slate-800">2-4 tuần</p>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                {/* Phase 2 */}
                                <div className="relative z-10 flex gap-6 group">
                                    <div className="flex flex-col items-center">
                                        <div className="w-8 h-8 rounded-full bg-emerald-600 text-white flex items-center justify-center font-bold text-sm shadow ring-4 ring-white">2</div>
                                        <div className="mt-2 text-xs font-semibold text-slate-500 uppercase">Tuần 3-6</div>
                                    </div>
                                    <div className="flex-1 bg-emerald-50 border border-emerald-100 rounded-xl p-5 relative transition-all hover:shadow-md">
                                        <div className="absolute top-4 right-4 flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                                            <button title="Hỏi AI" className="p-1.5 text-slate-400 hover:text-purple-600 hover:bg-white rounded-md transition-colors flex items-center">
                                                <span className="material-symbols-outlined text-[18px]">psychology</span>
                                            </button>
                                            <button title="Thêm thuốc" className="p-1.5 text-slate-400 hover:text-emerald-600 hover:bg-white rounded-md transition-colors">
                                                <PlusOutlined className="text-sm" />
                                            </button>
                                            <button title="Chỉnh sửa" className="p-1.5 text-slate-400 hover:text-yellow-600 hover:bg-white rounded-md transition-colors">
                                                <EditOutlined className="text-sm" />
                                            </button>
                                            <button title="Xóa" className="p-1.5 text-slate-400 hover:text-red-600 hover:bg-white rounded-md transition-colors">
                                                <DeleteOutlined className="text-sm" />
                                            </button>
                                        </div>
                                        <div className="flex items-center gap-2 mb-3">
                                            <span className="bg-emerald-600/10 text-emerald-600 text-[10px] font-bold px-2 py-0.5 rounded uppercase">Điều trị uống duy trì</span>
                                            <h4 className="font-bold text-slate-900 text-lg">Rifampin + Levofloxacin</h4>
                                        </div>
                                        <div className="grid grid-cols-2 gap-4 text-sm">
                                            <div>
                                                <p className="text-slate-500 text-[10px] uppercase font-bold mb-0.5">Liều lượng</p>
                                                <p className="font-semibold text-slate-800">900mg/ngày</p>
                                            </div>
                                            <div>
                                                <p className="text-slate-500 text-[10px] uppercase font-bold mb-0.5">Thời gian</p>
                                                <p className="font-semibold text-slate-800">12 tuần</p>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                            </div>
                        </div>
                        {/* Section: Surgery */}
                        <div className="border border-slate-300 rounded-lg p-3">
                            <div className="flex items-center gap-2 mb-3 text-slate-900 font-bold border-b border-slate-700 pb-2">
                                <span className="material-symbols-outlined text-[18px]">surgical</span> PHẪU THUẬT
                            </div>
                            <div className="space-y-2">
                                <div className="flex justify-between items-center hover:bg-slate-700/50 p-2 rounded group">
                                    <span className="text-sm"><span className="text-blue-400 font-mono mr-2">Bước 1:</span>Tháo khớp + đặt spacer</span>
                                    <div className="hidden group-hover:flex gap-2">
                                        <EditOutlined className="text-yellow-500 hover:text-yellow-400 cursor-pointer" />
                                        <DeleteOutlined className="text-red-500 hover:text-red-400 cursor-pointer" />
                                    </div>
                                </div>
                                <div className="flex justify-between items-center hover:bg-slate-700/50 p-2 rounded group">
                                    <span className="text-sm"><span className="text-blue-400 font-mono mr-2">Bước 2:</span>Thay khớp lại (tuần 8+)</span>
                                    <div className="hidden group-hover:flex gap-2">
                                        <EditOutlined className="text-yellow-500 hover:text-yellow-400 cursor-pointer" />
                                        <DeleteOutlined className="text-red-500 hover:text-red-400 cursor-pointer" />
                                    </div>
                                </div>
                            </div>
                            <div className="mt-3 flex justify-end">
                                <span className="text-xs text-slate-500 hover:text-white cursor-pointer transition-colors flex items-center gap-1">
                                    [+ Thêm bước]
                                </span>
                            </div>
                        </div>

                        {/* Section: Systemic Antibiotics */}
                        <div className="border-l-2 border-green-500 border-t border-r border-b border-slate-700 rounded-lg p-3 bg-slate-800 /80">
                            <div className="flex items-center gap-2 mb-3 text-green-400 font-bold border-b border-slate-700 pb-2">
                                <span className="material-symbols-outlined text-[18px]">vaccines</span> KHÁNG SINH TOÀN THÂN
                            </div>
                            <div className="space-y-2">
                                <div className="flex justify-between items-center hover:bg-slate-700/50 p-2 rounded group">
                                    <span className="text-sm"><span className="text-slate-500 mr-2">1.</span>Daptomycin 6mg/kg IV</span>
                                    <div className="hidden group-hover:flex gap-3">
                                        <EditOutlined className="text-yellow-500 hover:text-yellow-400 cursor-pointer" />
                                        <DeleteOutlined className="text-red-500 hover:text-red-400 cursor-pointer" />
                                        <span className="material-symbols-outlined text-[16px] text-blue-400 cursor-pointer" title="Hỏi AI tại sao">sms</span>
                                    </div>
                                </div>
                                <div className="flex justify-between items-center hover:bg-slate-700/50 p-2 rounded group">
                                    <span className="text-sm"><span className="text-slate-500 mr-2">2.</span>Rifampin + Cipro PO</span>
                                    <div className="hidden group-hover:flex gap-3">
                                        <EditOutlined className="text-yellow-500 hover:text-yellow-400 cursor-pointer" />
                                        <DeleteOutlined className="text-red-500 hover:text-red-400 cursor-pointer" />
                                        <span className="material-symbols-outlined text-[16px] text-blue-400 cursor-pointer" title="Hỏi AI tại sao">sms</span>
                                    </div>
                                </div>
                            </div>
                            <div className="mt-3 flex justify-end">
                                <span className="text-xs text-slate-500 hover:text-white cursor-pointer transition-colors flex items-center gap-1">
                                    [+ Thêm thuốc]
                                </span>
                            </div>
                        </div>

                        {/* Section: Local Antibiotics */}
                        <div className="border border-slate-700 rounded-lg p-3">
                            <div className="flex items-center gap-2 mb-3 text-blue-300 font-bold border-b border-slate-700 pb-2">
                                <span className="material-symbols-outlined text-[18px]">science</span> KHÁNG SINH TẠI CHỖ
                            </div>
                            <div className="space-y-2">
                                <div className="flex justify-between items-center hover:bg-slate-700/50 p-2 rounded group">
                                    <span className="text-sm">Vancomycin trong cement spacer</span>
                                    <div className="hidden group-hover:flex gap-3">
                                        <EditOutlined className="text-yellow-500 hover:text-yellow-400 cursor-pointer" />
                                        <DeleteOutlined className="text-red-500 hover:text-red-400 cursor-pointer" />
                                        <span className="material-symbols-outlined text-[16px] text-blue-400 cursor-pointer" title="Hỏi AI tại sao">sms</span>
                                    </div>
                                </div>
                            </div>
                        </div>

                    </div>
                </div>

                {/* Right Panel: AI Chat */}

                <div className="w-96 bg-slate-800 border border-slate-700 rounded-xl flex flex-col overflow-hidden shadow-2xl">
                    <div className="bg-slate-50 rounded-xl border border-slate-200 flex flex-col ">
                        <div className="p-4 border-b border-slate-200 flex items-center justify-between">
                            <div className="flex items-center gap-2">
                                <span className="material-symbols-outlined text-purple-600">smart_toy</span>
                                <h3 className="font-bold text-slate-900 text-sm">Cơ sở bằng chứng (RAG)</h3>
                            </div>
                            <span className="text-[10px] font-bold uppercase bg-purple-100 text-purple-700 px-2 py-0.5 rounded">Tạo bởi AI</span>
                        </div>
                        <div className="p-5 flex-1 space-y-4">
                            <div>
                                <p className="text-xs font-bold text-slate-400 uppercase tracking-wide mb-2">Trích dẫn hướng dẫn</p>
                                <blockquote className="text-sm text-slate-700 italic border-l-2 border-primary pl-3 leading-relaxed">
                                    Lorem ipsum dolor sit amet, consectetur adipisicing elit. Porro modi cupiditate sunt maiores? Impedit ratione enim magnam vel! Dolores, veniam? Non blanditiis veniam adipisci, quos amet ad earum aliquam alias.
                                </blockquote>
                                <div className="mt-2 flex items-center gap-1.5">
                                    <span className="material-symbols-outlined text-xs text-slate-400">menu_book</span>
                                    <a href="#" className="text-xs font-medium text-primary hover:underline">Hướng dẫn đồng thuận ICM 2018</a>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="p-4 border-b border-slate-700 flex items-center gap-2 bg-slate-800/50">
                        <span className="material-symbols-outlined text-blue-400">forum</span>
                        <h3 className="font-bold text-slate-100">Hỏi AI về phác đồ</h3>
                    </div>

                    <div className="flex-1 overflow-y-auto p-4 space-y-4 text-sm font-mono">
                        {/* User message */}
                        <div className="flex flex-col gap-1 items-end">
                            <span className="text-xs text-slate-400">Dr. Nam:</span>
                            <div className="bg-slate-700 text-green-400 p-2.5 rounded-lg rounded-tr-none max-w-[85%] border border-green-900/50">
                                "Sao không DAIR?"
                            </div>
                        </div>

                        {/* AI response */}
                        <div className="flex flex-col gap-1 items-start">
                            <span className="text-xs text-primary font-bold flex items-center gap-1">
                                <span className="material-symbols-outlined text-[14px]">auto_awesome</span> AI:
                            </span>
                            <div className="bg-slate-700/50 border-l-2 border-green-500 text-slate-300 p-3 rounded-r-lg rounded-bl-lg max-w-[90%] space-y-2">
                                <p className="text-green-300 font-sans leading-relaxed">"DAIR không phù hợp vì onset LATE {'>'} 3 tháng. Theo guideline, việc giữ lại khớp nhân tạo có tỉ lệ thất bại rất cao đối với nhiễm trùng mạn tính."</p>
                                <div className="bg-slate-800/80 p-2 rounded border border-slate-600 flex items-center gap-2 mt-2">
                                    <InfoCircleOutlined className="text-slate-400" />
                                    <span className="text-xs text-slate-400">ICM 2018 §6.2</span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div className="p-3 border-t border-slate-700 bg-slate-800">
                        <Input
                            placeholder="[Hỏi tiếp...]"
                            className="bg-slate-900 border-slate-700 text-slate-200 placeholder-slate-500 font-mono p-2 hover:border-slate-500 focus:border-green-500"
                            suffix={<SendOutlined className="text-slate-500 hover:text-green-400 cursor-pointer" />}
                        />
                    </div>
                </div>

            </div>
        </div>
    );
};

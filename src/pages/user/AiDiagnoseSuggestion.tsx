import React, { useState, useEffect } from 'react';
import { Steps, Button } from 'antd';
import { Step1PatientSelection } from '../../components/user/diagnose_steps/Step1PatientSelection';
import { PatientIntake } from '../../components/user/rag_diagnose/PatientIntake';
import { MedicalHistoryPage } from '../../components/user/rag_diagnose/MedicalHistory';
import { ClinicalAssessmentPage } from '../../components/user/rag_diagnose/ClinicalAssessment';
import { Step4Antibiogram } from '../../components/user/diagnose_steps/Step4Antibiogram';
import { Step5TreatmentPlan } from '../../components/user/diagnose_steps/Step5TreatmentPlan';

const AiDiagnosisSuggestion = () => {
    const [currentStep, setCurrentStep] = useState(() => {
        const saved = localStorage.getItem('pji_currentStep');
        return saved ? parseInt(saved, 10) : 0;
    });
    const [isCreatingNewPatient, setIsCreatingNewPatient] = useState(() => {
        const saved = localStorage.getItem('pji_isCreatingNewPatient');
        return saved ? JSON.parse(saved) : false;
    });

    useEffect(() => {
        localStorage.setItem('pji_currentStep', currentStep.toString());
    }, [currentStep]);

    useEffect(() => {
        localStorage.setItem('pji_isCreatingNewPatient', JSON.stringify(isCreatingNewPatient));
    }, [isCreatingNewPatient]);

    const next = () => setCurrentStep(prev => prev + 1);
    const prev = () => setCurrentStep(prev => prev - 1);

    const steps = [
        {
            title: 'Tạo hồ sơ bệnh án',
            content: isCreatingNewPatient ? (
                <div className="h-full flex flex-col relative w-full">
                    <PatientIntake
                        onNext={() => { setIsCreatingNewPatient(false); next(); }}
                        onCancel={() => setIsCreatingNewPatient(false)}
                    />
                </div>
            ) : (
                <Step1PatientSelection
                    onNext={next}
                    onCreateNew={() => setIsCreatingNewPatient(true)}
                />
            ),
        },
        {
            title: 'Nhập tiền sử bệnh',
            content: <MedicalHistoryPage onNext={next} onPrev={prev} />,
        },
        {
            title: 'Lâm sàng & cận lâm sàng',
            content: <ClinicalAssessmentPage onNext={next} onPrev={prev} />,
        },
        {
            title: 'Bảng kháng sinh đồ',
            content: <Step4Antibiogram onNext={next} onPrev={prev} />,
        },
        {
            title: 'Gợi ý phác đồ',
            content: <Step5TreatmentPlan onPrev={prev} />,
        },
    ];

    const items = steps.map((item) => ({ key: item.title, title: item.title }));

    return (
        <div className="flex flex-col h-full bg-slate-50 relative w-full overflow-hidden">
            {/* Header Breadcrumb / Steps */}
            <div className="bg-white px-8 py-5 border-b border-slate-200 shadow-sm z-10">
                <div className="mb-2 text-sm text-slate-500 font-medium">
                    Homepage / Hỗ trợ chẩn đoán & tạo phác đồ / <span className="text-primary">Step {currentStep + 1}</span>
                </div>
                <Steps
                    current={currentStep}
                    items={items}
                    className="mt-4 custom-steps"
                    size="small"
                />
            </div>

            {/* Content Area */}
            <div className="flex-1 overflow-y-auto relative w-full">
                {steps[currentStep].content}
            </div>
        </div>
    );
};

export default AiDiagnosisSuggestion;
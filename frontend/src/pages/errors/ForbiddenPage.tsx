import { Button, Result } from 'antd';
import { useNavigate } from 'react-router-dom';
const Error403 = () => {
    const navigate = useNavigate();
    return (
        <>
            <Result
                status="403"
                title="403 Error"
                subTitle="Xin lỗi, Bạn không có quyền truy cập vào địa chỉ này!"
                extra={<Button type="primary" onClick={() => navigate("/")}>Quay Lại</Button>}
            />
        </>
    )
}
export default Error403;
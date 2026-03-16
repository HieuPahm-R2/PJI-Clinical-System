import { Button, Result } from 'antd';
import { useNavigate } from 'react-router-dom';
const Error404 = () => {
    const navigate = useNavigate()
    return (
        <Result
            status="404"
            title="404 Error"
            subTitle="Xin Lỗi, Địa chỉ bạn tìm không tồn tại!"
            extra={<Button type="primary" onClick={() => navigate('/')}>Quay Lại</Button>}
        />
    )
}
export default Error404;
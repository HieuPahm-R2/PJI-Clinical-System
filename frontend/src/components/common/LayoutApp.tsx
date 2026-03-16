import { useAppDispatch, useAppSelector } from "@/redux/hook";
import { setRefreshTokenAction } from "@/redux/slice/accountSlice";
import { message } from "antd";
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

interface IProps {
    children: React.ReactNode
}
const LayoutApp = (props: IProps) => {
    const isRefreshToken = useAppSelector(state => state.account.isRefreshToken);
    const errorRefreshToken = useAppSelector(state => state.account.errorRefreshToken);
    const navigate = useNavigate();
    const dispatch = useAppDispatch();

    useEffect(() => {
        console.log('[LayoutApp] useEffect fired - isRefreshToken:', isRefreshToken, 'error:', errorRefreshToken)
        if (isRefreshToken === true) {
            console.log('[LayoutApp] refresh token error -> navigating to /login')
            localStorage.removeItem('access_token')
            message.error(errorRefreshToken)
            dispatch(setRefreshTokenAction({ status: false, message: "yêu cầu đăng nhập!" }))
            navigate("/login");
        }
    }, [isRefreshToken]);
    return (
        <>
            {props.children}
        </>
    )
}
export default LayoutApp
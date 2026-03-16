import Error403 from "@/pages/errors/ForbiddenPage"
import { useSelector } from "react-redux"
import { Navigate, useLocation } from "react-router-dom"


const RoleCheck = (props) => {
    const isAdmin = window.location.pathname.startsWith("/admin")
    const user = useSelector((state: any) => state.account.user)
    const userRole = user?.role?.name
    console.log(userRole)
    if ((isAdmin && (userRole === 'ADMIN' || userRole === 'DOCTOR' || userRole === 'NURSE' || userRole === 'RECEPTIONIST'))
        || !isAdmin && (userRole === 'USER' || userRole === 'ADMIN' || userRole === 'DOCTOR' || userRole === 'NURSE' || userRole === 'RECEPTIONIST')) {
        return (<>{props.children}</>)
    } else {
        return (<Error403 />)
    }
}

const ProtectedRoute = (props) => {
    const isAuthenticated = useSelector((state: any) => state.account.isAuthenticated)
    const location = useLocation();
    return (
        <>
            {isAuthenticated === true ?
                <>
                    <RoleCheck>{props.children}</RoleCheck>
                </> : <Navigate to="/login" replace={true} />
            }
        </>
    )
}
export default ProtectedRoute;
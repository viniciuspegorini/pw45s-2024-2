import { useContext } from "react";
import { useLocation, Navigate, Outlet } from "react-router-dom";

export function RequireAuth() {  
  const authenticated = true;
  const location = useLocation();

  return authenticated ? (
    <Outlet />
  ) : authenticated ? (
    <Navigate to="/unauthorized" state={{ from: location }} replace />
  ) : (
    <Navigate to="/login" state={{ from: location }} replace />
  );
}

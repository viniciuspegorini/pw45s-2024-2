// Criar dentro de src a pasta context > hooks > useAuth.ts

import { useState } from "react";
import { AuthenticatedUser, AuthenticationResponse } from "../../commons/types";
import { api } from "../../lib/axios";

export function useAuth() {
    const [authenticated, setAuthenticated] = useState(false);
    const [authenticatedUser, setAuthenticatedUser] = useState<AuthenticatedUser>();
    const [loading, setLoading] = useState(false);

    function handleLogin(response: AuthenticationResponse) {
        localStorage.setItem("token", response.token);
        localStorage.setItem("user", JSON.stringify(response.user));

        api.defaults.headers.common["Authorization"] = `Bearer ${response.token}`;
        setAuthenticatedUser(response.user);
        setAuthenticated(true);
    }

    function handleLogout() {
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        setAuthenticatedUser(undefined);
        setAuthenticated(false);

        api.defaults.headers.common["Authorization"] = "";	
    }

    return {
        authenticated,
        authenticatedUser,
        loading,
        handleLogin,
        handleLogout
    }
}
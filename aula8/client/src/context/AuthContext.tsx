import { createContext, ReactNode } from 'react';
import { AuthenticatedUser, AuthenticationResponse} from '../commons/types';
import { useAuth } from './hooks/useAuth';

interface AuthContextType {
  authenticated: boolean;
  authenticatedUser?: AuthenticatedUser;
  loading: boolean;
  handleLogin: (response: AuthenticationResponse) => void;
  handleLoginSocial: (idToken: string) => void;
  handleLogout: () => void;
}

interface AuthProviderProps {
  children: ReactNode
}

export const AuthContext = createContext({} as AuthContextType);

export function AuthProvider({ children }: AuthProviderProps) {
  const {authenticated, authenticatedUser, loading, handleLogin, handleLoginSocial, handleLogout} = useAuth();

  return (
    <AuthContext.Provider value={{loading, authenticated, authenticatedUser, handleLogin, handleLoginSocial, handleLogout}}>
      {children}
    </AuthContext.Provider>
  )
}
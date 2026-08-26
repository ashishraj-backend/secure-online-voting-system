import React from 'react'
import { Navigate } from 'react-router-dom'

export default function ProtectedRoute({ children, requireAdmin=false }){
  const token = sessionStorage.getItem('token')
  if (!token) return <Navigate to="/login" replace />
  // simple admin check: token payload may contain role; we keep it simple here
  if (requireAdmin){
    try{
      const payload = JSON.parse(atob(token.split('.')[1]))
      if (!payload.roles || !payload.roles.includes('ROLE_ADMIN')) return <Navigate to="/" replace />
    }catch(e){ return <Navigate to="/" replace /> }
  }
  return children
}

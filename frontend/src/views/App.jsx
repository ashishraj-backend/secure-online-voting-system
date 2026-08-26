import React, {useEffect, useState} from 'react'
import LiveResults from '../widgets/LiveResults'
import { Routes, Route, Link } from 'react-router-dom'
import Login from './Login'
import Register from './Register'
import Dashboard from './Dashboard'
import ElectionPage from './ElectionPage'
import AdminDashboard from './AdminDashboard'
import ProtectedRoute from '../components/ProtectedRoute'

export default function App(){
  return (
    <div style={{padding:20,fontFamily:'sans-serif'}}>
      <nav style={{marginBottom:20}}>
        <Link to="/">Home</Link> | <Link to="/login">Login</Link> | <Link to="/register">Register</Link> | <Link to="/dashboard">Dashboard</Link> | <Link to="/admin">Admin</Link>
      </nav>
      <Routes>
        <Route path="/" element={<div><h1>Secure Voting — Demo</h1><p>Use the links to navigate.</p></div>} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/dashboard" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
        <Route path="/election/:id" element={<ProtectedRoute><ElectionPage /></ProtectedRoute>} />
        <Route path="/admin" element={<ProtectedRoute requireAdmin={true}><AdminDashboard /></ProtectedRoute>} />
      </Routes>
    </div>
  )
}

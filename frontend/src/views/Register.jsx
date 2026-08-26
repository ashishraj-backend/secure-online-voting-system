import React, {useState} from 'react'
import {useNavigate} from 'react-router-dom'
import api from '../services/api'

export default function Register(){
  const [name,setName]=useState('')
  const [email,setEmail]=useState('')
  const [password,setPassword]=useState('')
  const [error,setError]=useState(null)
  const nav = useNavigate();

  async function submit(e){
    e.preventDefault(); setError(null);
    try{
      const res = await api.register(name,email,password);
      sessionStorage.setItem('token', res.token);
      nav('/dashboard');
    }catch(err){ setError(err.body?.message || 'Registration failed'); }
  }

  return (
    <div style={{maxWidth:400, margin:'2rem auto'}}>
      <h2>Register</h2>
      {error && <div style={{color:'red'}}>{error}</div>}
      <form onSubmit={submit}>
        <div><input placeholder="Full name" value={name} onChange={e=>setName(e.target.value)} required/></div>
        <div><input placeholder="Email" value={email} onChange={e=>setEmail(e.target.value)} required/></div>
        <div><input type="password" placeholder="Password" value={password} onChange={e=>setPassword(e.target.value)} required/></div>
        <div><button type="submit">Register</button></div>
      </form>
    </div>
  )
}

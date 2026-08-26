import React, {useEffect, useState} from 'react'
import api from '../services/api'
import {Link,useNavigate} from 'react-router-dom'

export default function Dashboard(){
  const [elections,setElections]=useState([])
  const [error,setError]=useState(null)
  const nav = useNavigate();
  const token = sessionStorage.getItem('token');

  useEffect(()=>{
    if(!token) return nav('/login');
    api.listElections(token).then(setElections).catch(e=>setError(e.body?.message||String(e)));
  },[])

  function logout(){ sessionStorage.removeItem('token'); nav('/login'); }

  return (
    <div style={{padding:20}}>
      <div style={{display:'flex',justifyContent:'space-between'}}>
        <h1>Voter Dashboard</h1>
        <div><button onClick={logout}>Logout</button></div>
      </div>
      {error && <div style={{color:'red'}}>{error}</div>}
      <ul>
        {elections.map(e=> <li key={e.id}><Link to={'/election/'+e.id}>{e.title} — {e.status}</Link></li>)}
      </ul>
    </div>
  )
}

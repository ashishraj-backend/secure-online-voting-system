import React, {useState} from 'react'
import api from '../services/api'

export default function AdminDashboard(){
  const token = sessionStorage.getItem('token');
  const [title,setTitle]=useState('');
  const [desc,setDesc]=useState('');
  const [msg,setMsg]=useState(null);

  async function create(e){
    e.preventDefault(); setMsg(null);
    try{
      const r = await api.createElection({title,description:desc}, token);
      setMsg('Created ' + r.id);
    }catch(err){ setMsg('Create failed'); }
  }

  return (
    <div style={{padding:20}}>
      <h1>Admin</h1>
      <form onSubmit={create}>
        <div><input placeholder="Title" value={title} onChange={e=>setTitle(e.target.value)} required/></div>
        <div><textarea placeholder="Description" value={desc} onChange={e=>setDesc(e.target.value)} /></div>
        <div><button type="submit">Create Election</button></div>
      </form>
      {msg && <div>{msg}</div>}
    </div>
  )
}

import React, {useEffect, useState} from 'react'
import {useParams} from 'react-router-dom'
import api from '../services/api'
import LiveResults from '../widgets/LiveResults'

export default function ElectionPage(){
  const {id} = useParams();
  const [election,setElection]=useState(null)
  const [authToken,setAuthToken]=useState(null)
  const [message,setMessage]=useState(null)
  const token = sessionStorage.getItem('token');

  useEffect(()=>{ if(token) api.getElection(id, token).then(setElection).catch(e=>setMessage('Error loading')); },[id])

  async function requestAuth(){
    try{
      const r = await api.requestAuthorization(id, token);
      setAuthToken(r.token);
      setMessage('Authorization granted (short-lived)');
    }catch(e){ setMessage(e.body?.message||'Auth error'); }
  }

  async function vote(candidateId){
    try{
      await api.castVote(id, candidateId, authToken, token);
      setMessage('Vote submitted');
    }catch(e){ setMessage(e.body?.message || 'Vote failed'); }
  }

  if (!election) return <div>Loading...</div>

  return (
    <div style={{padding:20}}>
      <h2>{election.title}</h2>
      <p>{election.description}</p>
      <div>
        <button onClick={requestAuth}>Request Voting Authorization</button>
        {authToken && <div>Authorization ready</div>}
        {message && <div style={{color:'green'}}>{message}</div>}
      </div>
      <h3>Candidates</h3>
      <ul>
        {election.candidates?.map(c => (
          <li key={c.id}>{c.name} <button onClick={()=>vote(c.id)}>Vote</button></li>
        ))}
      </ul>
      <LiveResults electionId={id} />
    </div>
  )
}

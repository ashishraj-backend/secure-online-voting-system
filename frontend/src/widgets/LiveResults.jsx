import React, {useEffect, useState} from 'react'
import SockJS from 'sockjs-client'
import Stomp from 'stompjs'

const API_BASE = process.env.REACT_APP_API_BASE || 'http://localhost:8080';

export default function LiveResults({electionId}){
  const [results,setResults] = useState([])

  useEffect(()=>{
    const sock = new SockJS(API_BASE + '/ws')
    const client = Stomp.over(sock)
    client.connect({}, ()=>{
      client.subscribe(`/topic/election.${electionId}.results`, (msg)=>{
        try{ const payload = JSON.parse(msg.body); setResults(payload) }catch(e){}
      })
    })
    return ()=>{ try{ client.disconnect(); }catch(e){} }
  },[electionId])

  return (
    <div>
      <h2>Live Results</h2>
      <ul>
        {results.map(r=> <li key={r.candidateId}>{r.candidateName} — {r.votes} — {r.percentage.toFixed(1)}%</li>)}
      </ul>
    </div>
  )
}

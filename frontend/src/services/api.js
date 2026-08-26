const API_BASE = process.env.REACT_APP_API_BASE || 'http://localhost:8080';

async function request(path, {method='GET', body, token, headers={}}={}){
  const opts = { method, headers: {...headers} };
  if (body) { opts.body = JSON.stringify(body); opts.headers['Content-Type']='application/json'; }
  if (token) opts.headers['Authorization'] = 'Bearer ' + token;
  const res = await fetch(API_BASE + path, opts);
  const text = await res.text();
  let data = null;
  try { data = text ? JSON.parse(text) : null; } catch(e){ data = text; }
  if (!res.ok) throw {status: res.status, body: data};
  return data;
}

export async function register(name, email, password){
  return request('/api/auth/register', {method:'POST', body: {name,email,password}});
}

export async function login(email, password){
  return request('/api/auth/login', {method:'POST', body: {email,password}});
}

export async function listElections(token){
  return request('/api/elections', {token});
}

export async function getElection(id, token){
  return request('/api/elections/'+id, {token});
}

export async function requestAuthorization(electionId, token){
  return request('/api/elections/'+electionId+'/authorization', {method:'POST', token});
}

export async function castVote(electionId, candidateId, authToken, token){
  return request('/api/elections/'+electionId+'/votes', {method:'POST', body: {candidateId}, token, headers: {'Authorization-Token': authToken}});
}

export async function createElection(election, token){
  return request('/api/admin/elections', {method:'POST', body: election, token});
}

export default {register, login, listElections, getElection, requestAuthorization, castVote, createElection};

const computation=import.meta.env.VITE_COMPUTATION_API_URL??'http://localhost:8082/api/v1/computations';const optimization=import.meta.env.VITE_OPTIMIZATION_API_URL??'http://localhost:8083/api/v1/optimizations';const rsa=import.meta.env.VITE_RSA_API_URL??'http://localhost:8084/api/v1/rsa/workflows';
async function post(url:string,body:unknown){const r=await fetch(url,{method:'POST',headers:{'content-type':'application/json'},body:JSON.stringify(body)});if(!r.ok)throw new Error(await r.text());return r.json()}
export const createWorkflow=async(networkId:string)=>{const r=await fetch(`${rsa}?networkId=${encodeURIComponent(networkId)}`,{method:'POST'});if(!r.ok)throw new Error(await r.text());return r.json()}
export const runLoadFlow=(networkId:string)=>post(`${computation}/load-flow`,{networkId,dc:false,parameters:{}})
export const runSecurity=(networkId:string,contingencies:unknown[])=>post(`${computation}/security-analysis`,{networkId,contingencies})
export const runSensitivity=(networkId:string,monitoredElements:string[],variables:string[])=>post(`${computation}/sensitivity-analysis`,{networkId,monitoredElements,variables})
export const runRao=(input:unknown)=>post(`${optimization}/rao`,input)
export async function runRaoProfile(networkId:string,timestamp:string,file:File,snapshot:unknown){const body=new FormData();body.append('networkId',networkId);body.append('timestamp',timestamp);body.append('profilePackage',file);body.append('snapshot',JSON.stringify(snapshot));const r=await fetch(`${optimization}/rao/profile-package`,{method:'POST',body});if(!r.ok)throw new Error(await r.text());return r.json()}
export const applyActions=(workflowId:string,actions:unknown[])=>post(`${rsa}/${workflowId}/actions`,actions)
export const rerun=(networkId:string,contingencies:unknown[])=>post(`${computation}/rerun`,{networkId,contingencies})
export const compare=(workflowId:string,before:Record<string,number>,after:Record<string,number>)=>post(`${rsa}/comparison`,{workflowId,before,after})

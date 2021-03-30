import React, { useState, useEffect } from 'react'
import { useParams, useLocation, useHistory } from 'react-router-dom'
import { Button, InlineLoading }  from 'carbon-components-react' 
import ResultsCard from './ResultsCard'
// import Error from './Error'

let baseUrl = (id, {start, reportingDate}) => { return `https://proxy-orcin.vercel.app/?url=${encodeURIComponent(
    `http://45.33.84.72:8095/fhir/Measure/${id}/$evaluate-measure?periodStart=${start}&periodEnd=${reportingDate}&_format=json`
    )}`}

export default function Results(){

    let {id} = useParams()
    let history = useHistory()
    let args = new URLSearchParams(useLocation().search);
    let reportingDate = args.get('reportingDate');
    let _start = new Date(reportingDate)
    _start.setFullYear(_start.getFullYear() - 1)
    let start = _start.getFullYear() + "-" + String(_start.getMonth() + 1).padStart(2, 0) + "-" + String(_start.getDate()).padStart(2, 0)
    
    let reportingPeriod = {
        start: start,
        end: reportingDate
    } 

    console.log(reportingDate)
    console.log(start)


    let [results, setResults] = useState('')
    let [error, setError] = useState('')

    let exportJSON = () => {
        const blob = new Blob([JSON.stringify(results)], { type: "application/json" });
        const link = document.createElement("a");

        link.download = "MeasureReport.json";
        link.href = window.URL.createObjectURL(blob);
        link.dataset.downloadurl = ["text/json", link.download, link.href].join(":");

        const evt = new MouseEvent("click", {
            view: window,
            bubbles: true,
            cancelable: true,
        });
        link.dispatchEvent(evt);
    }

    useEffect(() => {
        
        async function fetchData(){
            let response = await fetch(baseUrl(id, {start, reportingDate}), {headers:{'Content-Type':'application/json'}}) 
            let data = await response.json()
            // console.log(data)
            if(typeof data.group[0].measureScore == 'undefined'){
                console.log("No Data")
                setError(true);
            }
            setResults(data)
        }
        fetchData()
        
    },[id, start, reportingDate])

    return (
        <>
        {
            (results)  ? <><ResultsCard results={results} reportingPeriod={reportingPeriod}/></>:
            // (results && !results.group[0].measureScore )?
            // <Error error="Measure Score is 0 or null"/>
            // :
            <InlineLoading status={results ? 'finished': 'active'} description="Evaluating Measure.."/>
        }
        <br/>
        <Button kind="tertiary" onClick={e => {history.push('/#')}} style={{float:'left'}} >Go back</Button>
        
        {
            (results) ?  <Button kind='secondary' style={{float:'right'}} onClick={e => {exportJSON()}} >Export JSON</Button> : ""
        }
        
        </>
    )
}
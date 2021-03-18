import React, { useState, useEffect } from 'react'
import { useParams } from 'react-router-dom'
import { InlineLoading }  from 'carbon-components-react' 
import ResultsCard from './ResultsCard'

let baseUrl = (id) => { return `https://proxy-orcin.vercel.app/?url=http://45.79.249.220:8888/fhir/Measure/${id}/$evaluate-measure%3FperiodStart=2018-01-01&periodEnd=2018-12-31&_format=json`}

export default function Results(){

    let {id} = useParams()

    let [results, setResults] = useState('')

    useEffect(() => {
        async function fetchData(){
            let response = await fetch(baseUrl(id), {headers:{'Content-Type':'application/json'}}) 
            let data = await response.json()
            console.log(data)
            setResults(data)
        }
        fetchData()
    },[id])

    return (
        <>
        {
            results ?
            <>
                <ResultsCard results={results} />
            </>
            
            :
            <InlineLoading description="Evaluating Measure.."/>

        }

        </>
    )
}
import { Tile }  from 'carbon-components-react' 


export default function ResultsCard({results, reportingPeriod}) {


    return (
        <>
            <Tile>
                <h4><u>{results.measure}</u></h4>
                <br/>
                
                <p>From: <b>{reportingPeriod.start}</b>;  To: <b>{reportingPeriod.end}</b></p>
                <br/>

                {results.group[0].population.map((population) => {
                   return <p key={String(population.code.coding[0].code)}>{population.code.coding[0].display}: {population.count}</p>
                })}
            
                <br/>
                <hr/>
                <h5>
					Measure Score: {results.group[0].measureScore ? (results.group[0].measureScore.value).toFixed(2):  "0" }
				</h5>
            </Tile>
        </>
    )
}
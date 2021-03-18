import { Tile }  from 'carbon-components-react' 


export default function ResultsCard({results}) {


    return (
        <>
            <Tile>
                <h4><u>{results.measure}</u></h4>
            </Tile>
            <br/>
            <Tile>
                {results.group[0].population.map((population) => {
                   return <p>{population.code.coding[0].display}: {population.count}</p>
                })}
            
                <hr/>
                <h5>
					Measure Score: {(results.group[0].measureScore.value).toFixed(2)}
				</h5>
            </Tile>
        </>
    )
}
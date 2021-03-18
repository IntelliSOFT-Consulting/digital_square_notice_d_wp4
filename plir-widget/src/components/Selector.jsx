import React, { useState, useEffect } from 'react'
import { useHistory } from 'react-router-dom'
import { Dropdown, Button, DatePicker, DatePickerInput } from 'carbon-components-react'

let url = 'https://proxy-orcin.vercel.app/?url=http://45.79.249.220:8888/fhir/Measure?_format=json'

export default function Selector() {

    let [selected, setSelected] = useState('')
    let [indicators, setIndicators] = useState([])
    
    let history = useHistory()

    let generateResults = () => {
        history.push(`/results/${selected}`)
    }

    useEffect(() => {
        async function getData() {
            let response = await fetch(url)
            let data = await response.json()
            let res = []
            data.entry.forEach(entry => {
                res.push({ id: entry.resource.id, title: entry.resource.title })
            });
            setIndicators(res)
        }
        getData()
    }, [])


    return (
        <>
            <Dropdown
                id="default"
                titleText="Select an indicator or a FHIR Measure Resource"
                // helperText="Click to generate results"
                label="Select item from list"
                items={indicators}
                itemToString={(item) => (item ? item.title : '')}
                onChange={e => { setSelected(e.selectedItem.id) }}
            />
            <br />
            {
                selected &&
                <DatePicker className="bx--col-lg-4 bx--col-sm-4" datePickerType="single">
                    <DatePickerInput id="reportingDate"
                        placeholder="mm/dd/yyyy"
                        labelText="Reporting Date"
                    />
                </DatePicker>
            }

            {   selected &&
                <>
                    <br />
                    <Button kind="secondary" type="button" onClick={e => { console.log(e); generateResults() }}>
                        Next
                            </Button>
                </>
            }
        </>
    )
}
library TxPVLS version '0.0.1'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers

codesystem "SNOMEDCT:2017-09": 'http://snomed.info/sct' version 'http://snomed.info/sct/731000124108/version/201709'
codesystem "CIEL": 'https://openconceptlab.org/orgs/CIEL/sources/CIEL'


code "Human immunodeficiency virus viral load (procedure)": '315124004' from "SNOMEDCT:2017-09" display 'Human immunodeficiency virus viral load (procedure)'
code "HIV viral load": '856AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA' from "CIEL" display 'HIV viral load'


parameter "Measurement Period" Interval<DateTime>
    default Interval[@2001-01-01T00:00:00.0, @2002-01-01T00:00:00.0)
/*
    Assuming: 1  - All patients are active on care.
    
    TX_PVLS - Definition:
        Percentage of ART patients with a suppressed viral load (VL) result (<1000 copies/ml) 
        documented in the medical or laboratory records/laboratory information systems (LIS) 
        within the past 12 months


    union [Observation: "HIV VIRAL LOAD"] )
*/

context Patient

define "Initial Population":
    [Observation: "HIV viral load"] VL
        where FHIRHelpers.ToDateTime(VL."issued") during "Measurement Period"
            and VL."value" is not null

define "Suppressed VL Results":
    "Initial Population" VL
        where VL."value" < 1000 'copies/mL'

define Numerator:
    "Suppressed VL Results"

define Denominator:
    "Initial Population"


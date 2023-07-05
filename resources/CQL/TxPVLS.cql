library TxPVLS version '0.0.1'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers

codesystem "SNOMEDCT:2017-09": 'http://snomed.info/sct' version 'http://snomed.info/sct/731000124108/version/201709'

code "Human immunodeficiency virus viral load (procedure)": '315124004' from "SNOMEDCT:2017-09" display 'Human immunodeficiency virus viral load (procedure)'

parameter "Measurement Period" Interval<DateTime>
    default Interval[@2020-01-01T00:00:00.0, @2020-12-31T00:00:00.0)

/*
    Assuming: 1  - All patients are active on care.
*/

context Patient

define "Initial Population":
    [Observation: "Human immunodeficiency virus viral load (procedure)"] VL
        where FHIRHelpers.ToDateTime(VL."issued") during "Measurement Period"
            and VL."value" is not null

define SuppressedViralLoadResults:
    "Initial Population" VL
        where VL."value" < 1000

define Numerator:
    "SuppressedViralLoadResults"

define Denominator:
    "Initial Population"


// import { ExtensionSlot } from '@openmrs/esm-react-utils';
import React from 'react';
// import Widget from './TxPVLS/App'

// export default () => {
//   // const [slotName, setSlotName] = React.useState('');
//   return (
//     <div style={{ margin: '4em' }}>
//       <br/>
//       <h1 style={{'textAlign':"center"}}>TxPVLS Report</h1>
//       {/* <input value={slotName} onChange={e => setSlotName(e.currentTarget.value)} /> */}
//       {/* <ExtensionSlot extensionSlotName={slotName} key={slotName} /> */}
//       <Widget/>
//     </div>
//   );
// };





// 2???

import Selector from './TxPVLS/components/Selector'
import Results from './TxPVLS/components/Results'
import { HashRouter as Router, Route } from 'react-router-dom'


function Widget() {

  return (
    <div className="bx--grid">
       <br/><br/><br/><br/><br/>
       <h1 style={{'textAlign':"center"}}>TxPVLS Report</h1>
       <p></p>
      <div className="bx--row" >
        <div className="bx--col-lg-4 bx--col-md-4 bx--col-sm-4" style={{margin:"auto"}}>
          <Router>
              <Route exact path="/" component={Selector} />
              <Route path="/results/:id" children={<Results/>} />
          </Router>
        </div>
      </div>
    </div>
  );
}

export default Widget;

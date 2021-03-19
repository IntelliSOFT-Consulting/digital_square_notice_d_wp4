import Selector from './components/Selector'
import Results from './components/Results'
import { HashRouter as Router, Route } from 'react-router-dom'


function App() {

  return (
    <div className="bx--grid">
      <div className="bx--row">
        <div className="bx--col-lg-4 bx--col-md-4 bx--col-sm-4">
          <Router>
              <Route exact path="/" component={Selector} />
              <Route path="/results/:id" children={<Results/>} />
          </Router>
        </div>
      </div>
    </div>
  );
}

export default App;
